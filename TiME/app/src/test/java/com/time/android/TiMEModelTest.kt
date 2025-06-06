package com.time.android

import com.time.android.model.TiME
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TiMEModelTest {

    @Test
    fun createSession_isValid() {
        val session = TiME(
            id = "abc123",
            title = "Study Session",
            description = "Reading textbook",
            category = "cat001",
            categoryName = "Study",
            startTime = 1000L,
            endTime = 5000L,
            isCompleted = false,
            isPaused = false,
            tags = listOf("urgent", "math"),
            notes = "Start with chapter 1"
        )

        assertEquals("Study Session", session.title)
        assertEquals("Study", session.categoryName)
        assertFalse(session.isCompleted)
        assertEquals(listOf("urgent", "math"), session.tags)
    }

    @Test
    fun updatePause_correctlyUpdatesBreakTime() {
        val original = TiME(
            id = "pause001",
            title = "Deep Work",
            breakTimeTotal = 3000L
        )

        val updated = original.updatePause(startTime = 1000L, endTime = 4000L)

        // Should add 3000ms more to breakTimeTotal
        assertEquals(6000L, updated.breakTimeTotal)
        assertEquals("Deep Work", updated.title)
    }
}
