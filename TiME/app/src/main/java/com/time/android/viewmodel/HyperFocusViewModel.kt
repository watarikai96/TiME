package com.time.android.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.time.android.model.Category
import com.time.android.model.ExecutableSession
import com.time.android.model.HyperFocus
import com.time.android.model.HyperFocusPlanProfile
import com.time.android.model.HyperFocusSession
import com.time.android.model.SessionType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.time.android.datastore.SessionStateDataStore
import com.time.android.model.BreakWindow
import com.time.android.model.SessionStateSnapshot
import kotlinx.coroutines.Job
import java.time.LocalDate
import javax.inject.Inject

sealed class SessionAction {
    object Pause : SessionAction()
    object Resume : SessionAction()
    object End : SessionAction()
    object Delete : SessionAction()
    data class ChangeDuration(val minutes: Int) : SessionAction()
}


class HyperFocusViewModel @Inject constructor(

) : ViewModel() {

    private val db = Firebase.firestore
    private val userId: String?
        get() = Firebase.auth.currentUser?.uid

    var sessionCount = mutableIntStateOf(4)

    var breakStartedAt = mutableStateOf<Long?>(null)

    var currentBreakDuration = mutableLongStateOf(0L)
    private var breakTimerJob: Job? = null
    private var timerJob: Job? = null


    private fun archivePlanToCalendar() {
        val uid = userId ?: return
        val dateKey = LocalDate.now().toString()

        val archivedSessions = sessionQueue.map {
            mapOf(
                "title" to it.title,
                "duration" to it.duration,
                "isCompleted" to it.isCompleted,
                "isCancelled" to it.isCancelled,
                "actualDuration" to (it.actualDuration ?: 0),
                "breakDuration" to it.breakDuration,
                "categoryId" to it.categoryId,
                "iconName" to it.iconName.orEmpty(),
                "completedAt" to (it.completedAt ?: 0L)
            )
        }

        val archivedData = mapOf(
            "sessions" to archivedSessions,
            "archivedAt" to System.currentTimeMillis(),
            "planName" to selectedPlan?.name.orEmpty()
        )

        db.collection("users")
            .document(uid)
            .collection("completed_hyperfocus_plans")
            .document(dateKey)
            .set(archivedData)
    }


    // In HyperFocusViewModel
    val individualCategories = mutableStateListOf<Category?>()

    // In HyperFocusViewModel
    val individualIcons = mutableStateListOf<String>().apply {
        // Initialize with default icons if needed
        repeat(sessionCount.intValue) { add("DefaultIcon") }
    }


    val savedPlans = mutableStateListOf<HyperFocusPlanProfile>()
    var selectedPlan by mutableStateOf<HyperFocusPlanProfile?>(null)
    var isAddModalOpen = mutableStateOf(false)

    var sessionPlanName = mutableStateOf("")

    // Global vs Individual
    var isGlobalMode = mutableStateOf(true)

    // GLOBAL fields
    var globalSessionTitle = mutableStateOf("")
    var globalSessionDuration = mutableIntStateOf(25)
    var selectedIconName = mutableStateOf("DefaultIcon")

    // INDIVIDUAL fields
    var individualTitles = mutableStateListOf<String>()
    var individualDurations = mutableStateListOf<Int>()

    // Break settings
    var shortBreakDuration = mutableIntStateOf(5)
    var shortBreakFrequency = mutableIntStateOf(1)
    var longBreakDuration = mutableIntStateOf(20)
    var longBreakFrequency = mutableIntStateOf(4)

    var selectedCategory = mutableStateOf<Category?>(null)

    val sessionQueue = mutableStateListOf<ExecutableSession>()
    val currentSessionIndex = mutableIntStateOf(0)
    val isRunning = mutableStateOf(false)
    val isPaused = mutableStateOf(false)
    val progress = mutableFloatStateOf(0f)
    var isHyperFocusMode = mutableStateOf(false)


    lateinit var dataStore: SessionStateDataStore

    fun injectDataStore(context: Context) {
        dataStore = SessionStateDataStore(context.applicationContext)
    }



    fun loadSavedPlans() {
        val uid = userId
        if (uid.isNullOrBlank()) {
            Log.w("HyperFocusViewModel", "Cannot load plans — userId is null or blank.")
            return
        }

        db.collection("users")
            .document(uid)
            .collection("hyperfocus_plans")
            .get()
            .addOnSuccessListener { result ->
                savedPlans.clear()
                for (doc in result.documents) {
                    doc.toObject(HyperFocusPlanProfile::class.java)?.let { savedPlans += it }
                }
            }
            .addOnFailureListener { error ->
                Log.e("HyperFocusViewModel", "Failed to load plans: ${error.message}")
            }
    }


    fun saveCurrentGlobalPlan() {
        val uid = userId
        if (uid.isNullOrBlank()) {
            Log.w("HyperFocusViewModel", "Cannot save plan — userId is null or blank.")
            return
        }

        val category = selectedCategory.value ?: return

        val sessions = List(sessionCount.intValue) {
            HyperFocusSession(
                title = globalSessionTitle.value,
                durationMinutes = globalSessionDuration.intValue,
                categoryId = category.id,
                categoryColor = category.color,
                iconName = selectedIconName.value
            )
        }

        val plan = HyperFocus(
            sessions = sessions,
            shortBreakDuration = shortBreakDuration.intValue,
            shortBreakFrequency = shortBreakFrequency.intValue,
            longBreakDuration = longBreakDuration.intValue,
            longBreakFrequency = longBreakFrequency.intValue
        )

        val profile = HyperFocusPlanProfile(name = sessionPlanName.value, plan = plan)
        savedPlans += profile

        db.collection("users")
            .document(uid)
            .collection("hyperfocus_plans")
            .document(profile.id)
            .set(profile)
            .addOnSuccessListener {
                loadSavedPlans() //  Refresh list from Firebase after saving
            }
            .addOnFailureListener { error ->
                Log.e("HyperFocusViewModel", "Failed to save plan: ${error.message}")
            }
    }

    fun saveCurrentIndividualPlan() {
        val uid = userId
        if (uid.isNullOrBlank()) {
            Log.w("HyperFocusViewModel", "Cannot save plan — userId is null or blank.")
            return
        }

        if (individualTitles.size < sessionCount.intValue ||
            individualDurations.size < sessionCount.intValue ||
            individualCategories.size < sessionCount.intValue ||
            individualIcons.size < sessionCount.intValue
        ) {
            Log.w("HyperFocusViewModel", "Not enough individual session data")
            return
        }

        val sessions = List(sessionCount.intValue) { index ->
            HyperFocusSession(
                title = individualTitles[index],
                durationMinutes = individualDurations[index],
                categoryId = individualCategories[index]?.id ?: "",
                categoryColor = individualCategories[index]?.color ?: 0,
                iconName = individualIcons[index]
            )
        }

        val plan = HyperFocus(
            sessions = sessions,
            shortBreakDuration = shortBreakDuration.intValue,
            shortBreakFrequency = shortBreakFrequency.intValue,
            longBreakDuration = longBreakDuration.intValue,
            longBreakFrequency = longBreakFrequency.intValue
        )

        val profile = HyperFocusPlanProfile(name = sessionPlanName.value, plan = plan)
        savedPlans += profile

        // Save to Firestore
        db.collection("users")
            .document(uid)
            .collection("hyperfocus_plans")
            .document(profile.id)
            .set(profile)
            .addOnSuccessListener {
                loadSavedPlans() // Refresh list from Firebase after saving
            }
            .addOnFailureListener { error ->
                Log.e("HyperFocusViewModel", "Failed to save plan: ${error.message}")
            }
    }


    fun buildSessionQueue(plan: HyperFocus, planTitle: String): List<ExecutableSession> {
        val queue = mutableListOf<ExecutableSession>()
        var currentTime = System.currentTimeMillis()

        plan.sessions.forEachIndexed { index, session ->
            val workStart = currentTime
            val durationMs = session.durationMinutes * 60_000L
            val workEnd = workStart + durationMs

            //  Build Work Session
            queue += ExecutableSession(
                title = session.title,
                duration = session.durationMinutes,
                type = SessionType.WORK,
                categoryId = session.categoryId,
                iconName = session.iconName,
                scheduledStart = workStart,
                originalStart = workStart,
                isBreak = false,
                categoryColor = session.categoryColor,
                planTitle = planTitle
            )



            currentTime = workEnd

            if (index != plan.sessions.lastIndex) {
                val breakType = when {
                    (index + 1) % plan.longBreakFrequency == 0 -> SessionType.LONG_BREAK
                    (index + 1) % plan.shortBreakFrequency == 0 -> SessionType.SHORT_BREAK
                    else -> null
                }

                breakType?.let {
                    val breakDuration =
                        if (it == SessionType.LONG_BREAK) plan.longBreakDuration else plan.shortBreakDuration
                    val breakStart = currentTime
                    queue += ExecutableSession(
                        title = if (it == SessionType.SHORT_BREAK) "Short Break" else "Long Break",
                        duration = breakDuration,
                        type = it,
                        scheduledStart = breakStart,
                        originalStart = breakStart,
                        isBreak = true,
                        categoryColor = 0xFFE0E0E0,
                        planTitle = planTitle
                    )

                    currentTime += breakDuration * 60_000L
                }
            }
        }

        return queue
    }


    fun prepareQueueFromPlan(profile: HyperFocusPlanProfile) {
        selectedPlan = profile
        val queue = buildSessionQueue(
            plan = profile.plan,
            planTitle = profile.name //
        )
        sessionQueue.clear()
        sessionQueue += queue
        currentSessionIndex.intValue = 0
        isRunning.value = false
        isPaused.value = false
        progress.floatValue = 0f

        persistState()
    }


fun startTimer(duration: Int) {
    timerJob?.cancel() // Cancel existing job if running

    timerJob = viewModelScope.launch {
        val durationMs = duration * 60_000L
        var lastTick = System.currentTimeMillis()
        progress.floatValue = 0f

        while (progress.floatValue < 1f && isRunning.value) {
            if (!isPaused.value) {
                val now = System.currentTimeMillis()
                val elapsed = now - lastTick
                lastTick = now

                val progressIncrement = elapsed / durationMs.toFloat()
                progress.floatValue = (progress.floatValue + progressIncrement).coerceIn(0f, 1f)
                persistState()

                if (progress.floatValue >= 1f) {
                    markSessionAsCompleted(sessionQueue[currentSessionIndex.intValue].id)
                    endSession(manual = false)
                    break
                }
            }
            delay(1000)
        }
    }
}


    fun markSessionAsCompleted(sessionId: String) {
        val index = sessionQueue.indexOfFirst { it.id == sessionId }
        if (index == -1) return

        val session = sessionQueue[index]
        val now = System.currentTimeMillis()

        val totalElapsed = ((now - session.scheduledStart) / 60000).toInt()
        val breakDuration = (session.breakWindows.sumOf { it.end - it.start } / 60000).toInt()
        val focusTime = (totalElapsed - breakDuration).coerceAtLeast(0)

        sessionQueue[index] = session.copy(
            isCompleted = true,
            completedAt = now,
            actualDuration = totalElapsed,
            breakDuration = breakDuration,
            focusDuration = focusTime
        )

        if (index == currentSessionIndex.intValue) {
            if (index < sessionQueue.lastIndex) {
                // Start next session immediately
                currentSessionIndex.intValue++
                sessionQueue[currentSessionIndex.intValue] =
                    sessionQueue[currentSessionIndex.intValue].copy(
                        scheduledStart = now
                    )
                isRunning.value = true
                isPaused.value = false
                progress.floatValue = 0f
                startTimer(sessionQueue[currentSessionIndex.intValue].duration)
            } else {
                // Last session completed
                isRunning.value = false
                archivePlanToCalendar()
            }
        }
        persistState()
    }


    fun pauseSession() {
        val index = currentSessionIndex.intValue
        val session = sessionQueue.getOrNull(index) ?: return
        isPaused.value = true
        breakStartedAt.value = System.currentTimeMillis()

        sessionQueue[index] = session.copy(
            pauseStartTime = breakStartedAt.value
        )
        startBreakTimer()
        persistState()
    }


    fun resumeSession() {
        val index = currentSessionIndex.intValue
        val session = sessionQueue.getOrNull(index) ?: return
        val resumeTime = System.currentTimeMillis()

        val newBreakWindow = session.pauseStartTime?.let {
            BreakWindow(start = it, end = resumeTime)
        }

        val updatedBreaks: List<BreakWindow> = session.breakWindows + listOfNotNull(newBreakWindow)

        sessionQueue[index] = session.copy(
            pauseStartTime = null,
            breakWindows = updatedBreaks
        )

        isPaused.value = false
        breakStartedAt.value = null
        stopBreakTimer()
        persistState()
    }



    private fun startBreakTimer() {
        breakTimerJob?.cancel()
        breakTimerJob = viewModelScope.launch {
            while (isPaused.value) {
                breakStartedAt.value?.let { startTime ->
                    currentBreakDuration.longValue = System.currentTimeMillis() - startTime
                }
                delay(1000)
            }
        }
    }
    private fun stopBreakTimer() {
        breakTimerJob?.cancel()
        breakTimerJob = null
        currentBreakDuration.longValue = 0L
    }



    fun endSession(manual: Boolean = true) {
        val index = currentSessionIndex.intValue
        val session = sessionQueue.getOrNull(index) ?: return
        val now = System.currentTimeMillis()

        val totalElapsed = ((now - session.scheduledStart) / 60000).toInt()
        val breakDurationMillis: Long = session.breakWindows.sumOf { it.end - it.start }
        val breakDuration = (breakDurationMillis / 60000).toInt()
        val focusTime = (totalElapsed - breakDuration).coerceAtLeast(0)

        sessionQueue[index] = session.copy(
            isCompleted = !manual,
            isCancelled = manual,
            completedAt = now,
            actualDuration = totalElapsed,
            breakDuration = breakDuration,
            focusDuration = focusTime
        )

        stopBreakTimer()

        timerJob?.cancel() // Explicitly cancel the timer coroutine here

        if (index < sessionQueue.lastIndex) {
            var updatedStart = System.currentTimeMillis()
            for (i in (index + 1) until sessionQueue.size) {
                val updatedSession = sessionQueue[i]
                sessionQueue[i] = updatedSession.copy(
                    scheduledStart = updatedStart,
                    originalStart = updatedStart
                )
                updatedStart += updatedSession.duration * 60_000L
            }

            currentSessionIndex.intValue++
            isPaused.value = false
            isRunning.value = true
            progress.floatValue = 0f

            val nextSessionDuration = sessionQueue[currentSessionIndex.intValue].duration
            startTimer(nextSessionDuration)
        } else {
            isRunning.value = false
            isPaused.value = false
            progress.floatValue = 1f
            archivePlanToCalendar()
        }

        persistState()
    }







    fun deleteSession(index: Int) {
        if (index !in sessionQueue.indices) return

        sessionQueue.removeAt(index)

        var currentStart = sessionQueue.firstOrNull()?.scheduledStart ?: System.currentTimeMillis()
        sessionQueue.forEachIndexed { i, session ->
            val durationMs = session.duration * 60_000L
            sessionQueue[i] = session.copy(scheduledStart = currentStart)
            currentStart += durationMs
        }

        if (index <= currentSessionIndex.intValue && currentSessionIndex.intValue > 0) {
            currentSessionIndex.intValue--
        }

        persistState()
    }


    fun adjustSessionDuration(index: Int, newDuration: Int) {
        val session = sessionQueue.getOrNull(index) ?: return
        sessionQueue[index] = session.copy(duration = newDuration)

        // Cascade time updates from current session forward
        var currentStart = sessionQueue[index].scheduledStart
        for (i in index until sessionQueue.size) {
            val updatedDurationMs = sessionQueue[i].duration * 60_000L
            sessionQueue[i] = sessionQueue[i].copy(scheduledStart = currentStart)
            currentStart += updatedDurationMs
        }

        persistState()
    }


    fun deletePlan(profile: HyperFocusPlanProfile) {
        val uid = userId
        if (uid.isNullOrBlank()) {
            Log.w("HyperFocusViewModel", "Cannot delete plan — userId is null or blank.")
            return
        }

        savedPlans.remove(profile)
        db.collection("users")
            .document(uid)
            .collection("hyperfocus_plans")
            .document(profile.id)
            .delete()
    }


    fun persistState() {
        viewModelScope.launch {
            dataStore.saveSessionState(
                SessionStateSnapshot(
                    queue = sessionQueue.toList(),
                    currentIndex = currentSessionIndex.intValue,
                    progress = progress.floatValue,
                    isRunning = isRunning.value,
                    isPaused = isPaused.value,
                    breakStartedAt = breakStartedAt.value,
                    currentBreakDuration = currentBreakDuration.longValue
                )
            )
        }
    }

    fun restorePersistedState(context: Context) {

        viewModelScope.launch {
            val dataStore = SessionStateDataStore(context)
            val snapshot = dataStore.loadSessionState()

            snapshot?.let {
                sessionQueue.clear()
                sessionQueue.addAll(it.queue)
                currentSessionIndex.intValue = it.currentIndex
                progress.floatValue = it.progress
                isRunning.value = it.isRunning
                isPaused.value = it.isPaused

                if (it.isRunning && !it.isPaused) {
                    sessionQueue.getOrNull(it.currentIndex)?.let { session ->
                        startTimer(session.duration)
                    }
                }
            }
        }
    }


}


