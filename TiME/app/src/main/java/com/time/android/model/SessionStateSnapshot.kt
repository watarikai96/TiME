package com.time.android.model

import kotlinx.serialization.Serializable

@Serializable
data class SessionStateSnapshot(
    val queue: List<ExecutableSession>,
    val currentIndex: Int,
    val progress: Float,
    val isRunning: Boolean,
    val isPaused: Boolean,
    val breakStartedAt: Long?,  // When break started
    val currentBreakDuration: Long = 0  // Current break duration in ms
)

