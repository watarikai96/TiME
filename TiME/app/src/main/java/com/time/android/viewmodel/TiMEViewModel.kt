package com.time.android.viewmodel

import com.time.android.repository.TiMERepository
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.time.android.model.TiME
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class TiMEViewModel : ViewModel() {

    private val repository = TiMERepository()
    private val firestore = FirebaseFirestore.getInstance()


    private val _timeList = mutableStateListOf<TiME>()
    val timeList: List<TiME> get() = _timeList

    val todaySessions: List<TiME>
        get() {
            val (start, end) = getTodayBounds()
            val now = System.currentTimeMillis()
            return _timeList.filter { entry ->
                val startsToday = entry.startTime in start..end
                val isRunningNow = now in entry.startTime..entry.endTime && !entry.isCompleted
                startsToday || isRunningNow
            }
        }

    private fun getTodayBounds(): Pair<Long, Long> {
        val today = LocalDate.now()
        val zoneId = ZoneId.systemDefault()
        val startOfDay = today.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDay = today.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
        return Pair(startOfDay, endOfDay)
    }

    init {
        fetchAllTimes()
    }

    private fun fetchAllTimes() {
        firestore.collection("time_entries")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val updatedList = snapshot.documents.mapNotNull { doc ->
                    val raw = doc.data ?: return@mapNotNull null
                    val mapped = TiME.fromMap(doc.id, raw)
                    Log.d("TiME_RAWDOC", "Doc ${doc.id} = $raw")  // optional
                    mapped
                }

                _timeList.clear()
                _timeList.addAll(updatedList)
            }
    }

    fun add(time: TiME) {
        viewModelScope.launch {
            repository.add(time)
            saveToUserNode(time)
        }
    }

    fun update(partialUpdate: TiME) {
        viewModelScope.launch {
            val current = _timeList.find { it.id == partialUpdate.id }

            if (current != null) {
                val merged = current.copy(
                    title = partialUpdate.title,
                    description = partialUpdate.description,
                    startTime = partialUpdate.startTime,
                    endTime = partialUpdate.endTime,
                    notes = partialUpdate.notes,
                    subtasks = partialUpdate.subtasks,
                    isCompleted = partialUpdate.isCompleted,
                    isPaused = partialUpdate.isPaused,
                    pauseStartTime = partialUpdate.pauseStartTime,
                    totalPausedDuration = partialUpdate.totalPausedDuration,
                    breakTimeTotal = partialUpdate.breakTimeTotal // Ensure breakTimeTotal is updated
                )

                repository.update(merged.id, merged)
                saveToUserNode(merged) // Save the updated session to Firestore

                val index = _timeList.indexOfFirst { it.id == merged.id }
                if (index != -1) _timeList[index] = merged.copy()
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repository.delete(id)
            deleteFromUserNode(id)
        }
    }

    fun getFocusStreak(): Int {
        val zoneId = ZoneId.systemDefault()
        val now = LocalDate.now()

        var streak = 0
        for (i in 0..29) {
            val day = now.minusDays(i.toLong())
            val start = day.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val end = day.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1

            val hasFocus = _timeList.any {
                !it.isBreak && it.startTime in start..end
            }

            if (hasFocus) streak++
            else break
        }
        return streak
    }

    private fun saveToUserNode(time: TiME) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userSessions = firestore.collection("users").document(uid).collection("sessions")

        val docRef = if (time.id.isBlank()) userSessions.document() else userSessions.document(time.id)
        val timeWithId = time.copy(id = docRef.id)

        docRef.set(timeWithId.toMap())
    }

    private fun deleteFromUserNode(id: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .collection("sessions").document(id)
            .delete()
    }

}



