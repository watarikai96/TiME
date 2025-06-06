package com.time.android.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import java.util.UUID

@Keep
@Serializable
data class HyperFocus(
    val sessions: List<HyperFocusSession> = emptyList(),
    val shortBreakDuration: Int = 5,
    val shortBreakFrequency: Int = 1,
    val longBreakDuration: Int = 20,
    val longBreakFrequency: Int = 4
)

@Keep
@Serializable
data class HyperFocusSession(
    val title: String = "",
    val durationMinutes: Int = 25,
    val iconName: String = "DefaultIcon",
    val categoryId: String = "",
    val categoryColor: Long = 0xFFCCCCCC
)

@Serializable
data class ExecutableSession(
    val title: String,
    val duration: Int, // total duration in minutes
    val type: SessionType,
    val categoryId: String? = null,
    val iconName: String? = null,
    val scheduledStart: Long,
    val originalStart: Long,
    var manuallyOverridden: Boolean = false,
    val isBreak: Boolean,
    val categoryColor: Long? = null,

    val breakWindows: List<BreakWindow> = emptyList(),

    var pauseStartTime: Long? = null,
    var isPaused: Boolean = false,
    var isActive: Boolean = false,

    // UI-state fields for rendering card status
    val isCompleted: Boolean = false,
    var isCancelled: Boolean = false,
    val progress: Float = 0f,
    val elapsedTime: Int = 0,       // minutes
    val remainingTime: Int = duration,
    val focusDuration: Int = duration,
    val breakDuration: Int = 0,
    val planTitle: String? = null,
    var actualDuration: Int? = null,        // in minutes
    var completedAt: Long? = null,         // timestamp
    val id: String = UUID.randomUUID().toString()
)


@Serializable
data class BreakWindow(
    val start: Long,
    val end: Long
)


@Serializable
enum class SessionType {
    WORK, SHORT_BREAK, LONG_BREAK
}
