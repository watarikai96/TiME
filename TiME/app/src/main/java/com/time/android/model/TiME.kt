package com.time.android.model

import com.google.firebase.firestore.PropertyName

data class Subtask(
    val text: String = "",
    val isDone: Boolean = false
)
data class TiME(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",         // Stores the category ID
    val categoryName: String = "",     // For display
    val startTime: Long = 0L,
    val endTime: Long = 0L,

    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),
    val iconName: String = "Star",
    val isBreak: Boolean = false,
    val tags: List<String> = emptyList(),
    val notes: String = "",
    val manuallyAdjusted: Boolean = false,
    val repeat: String = "Once",
    val autoMove: Boolean = false,
    val breakTimeTotal: Long? = 0,

    @get:PropertyName("paused")
    @set:PropertyName("paused")
    var isPaused: Boolean = false,
    val pauseStartTime: Long? = null,
    val totalPausedDuration: Long = 0L,

    val subtasks: List<Subtask> = emptyList(),

    @get:PropertyName("isHyperFocusSession")
    @set:PropertyName("isHyperFocusSession")
    var isHyperFocusSession: Boolean = false,

    val sessionIndex: Int = -1,
    val totalSessions: Int = 0,
) {
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "title" to title,
            "description" to description,
            "category" to category,
            "categoryName" to categoryName,
            "startTime" to startTime,
            "endTime" to endTime,
            "isCompleted" to isCompleted,
            "isPaused" to isPaused,
            "totalPausedDuration" to totalPausedDuration,
            "createdAt" to createdAt,
            "iconName" to iconName,
            "isBreak" to isBreak,
            "tags" to tags,
            "notes" to notes,
            "manuallyAdjusted" to manuallyAdjusted,
            "repeat" to repeat,
            "autoMove" to autoMove,
            "subtasks" to subtasks.map { mapOf("text" to it.text, "isDone" to it.isDone) },
            "breakTimeTotal" to (breakTimeTotal ?: 0L),
            "isHyperFocusSession" to isHyperFocusSession,
            "sessionIndex" to sessionIndex,
            "totalSessions" to totalSessions
        )
        pauseStartTime?.let { map["pauseStartTime"] = it }
        return map
    }

    fun isRunning(): Boolean {
        val now = System.currentTimeMillis()
        return !this.isPaused && now in this.startTime..this.endTime && !this.isCompleted
    }

    fun updatePause(startTime: Long, endTime: Long): TiME {
        val breakTime = (endTime - startTime)
        val updatedBreakTimeTotal = (this.breakTimeTotal ?: 0L) + breakTime
        return this.copy(breakTimeTotal = updatedBreakTimeTotal)
    }

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): TiME? {
            return try {
                val subtasksList = (map["subtasks"] as? List<*>)?.mapNotNull {
                    val item = it as? Map<String, Any?>
                    val text = item?.get("text") as? String ?: return@mapNotNull null
                    val isDone = item["isDone"] as? Boolean ?: false
                    Subtask(text, isDone)
                } ?: emptyList()

                TiME(
                    id = id,
                    title = map["title"] as? String ?: "",
                    description = map["description"] as? String ?: "",
                    category = map["category"] as? String ?: "",
                    categoryName = map["categoryName"] as? String ?: "",
                    startTime = (map["startTime"] as? Number)?.toLong() ?: 0L,
                    endTime = (map["endTime"] as? Number)?.toLong() ?: 0L,
                    isCompleted = map["isCompleted"] as? Boolean ?: false,
                    isPaused = map["paused"] as? Boolean ?: false,
                    pauseStartTime = (map["pauseStartTime"] as? Number)?.toLong(),
                    totalPausedDuration = (map["totalPausedDuration"] as? Number)?.toLong() ?: 0L,
                    breakTimeTotal = (map["breakTimeTotal"] as? Number)?.toLong() ?: 0L,
                    createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                    iconName = map["iconName"] as? String ?: "Star",
                    isBreak = map["isBreak"] as? Boolean ?: false,
                    tags = map["tags"] as? List<String> ?: emptyList(),
                    notes = map["notes"] as? String ?: "",
                    manuallyAdjusted = map["manuallyAdjusted"] as? Boolean ?: false,
                    repeat = map["repeat"] as? String ?: "Once",
                    autoMove = map["autoMove"] as? Boolean ?: false,
                    subtasks = subtasksList
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

