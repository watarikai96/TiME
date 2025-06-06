import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.time.android.model.ExecutableSession
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import com.time.android.model.SessionStateSnapshot
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import java.io.IOException

object SessionStateSerializer : Serializer<SessionStateSnapshot> {
    override val defaultValue: SessionStateSnapshot = SessionStateSnapshot(
        queue = emptyList(),
        currentIndex = 0,
        progress = 0f,
        isRunning = false,
        isPaused = false,
        breakStartedAt = null,
        currentBreakDuration = 0L
    )

    override suspend fun readFrom(input: InputStream): SessionStateSnapshot {
        val rawBytes = input.readBytes() // read once
        val jsonText = rawBytes.decodeToString()

        if (jsonText.isBlank()) {
            return defaultValue // handle empty file gracefully
        }

        return try {
            Json.decodeFromString<SessionStateSnapshot>(jsonText).let { snapshot ->
                if (snapshot.breakStartedAt == null && snapshot.currentBreakDuration == 0L) {
                    snapshot.copy(
                        breakStartedAt = null,
                        currentBreakDuration = 0L
                    )
                } else {
                    snapshot
                }
            }
        } catch (e: SerializationException) {
            try {
                val legacySnapshot = Json.decodeFromString<LegacySessionStateSnapshot>(jsonText)
                legacySnapshot.toCurrentFormat()
            } catch (e2: Exception) {
                throw CorruptionException("Failed to read SessionStateSnapshot (both current and legacy)", e2)
            }
        } catch (e: Exception) {
            throw CorruptionException("Failed to read SessionStateSnapshot", e)
        }
    }


    override suspend fun writeTo(t: SessionStateSnapshot, output: OutputStream) {
        try {
            val json = Json {
                encodeDefaults = true
                ignoreUnknownKeys = true
            }.encodeToString(SessionStateSnapshot.serializer(), t)
            output.write(json.encodeToByteArray())
        } catch (e: Exception) {
            throw IOException("Failed to write SessionStateSnapshot", e)
        }
    }

    // Legacy snapshot class for migration
    @Serializable
    private data class LegacySessionStateSnapshot(
        val queue: List<ExecutableSession>,
        val currentIndex: Int,
        val progress: Float,
        val isRunning: Boolean,
        val isPaused: Boolean
    ) {
        fun toCurrentFormat(): SessionStateSnapshot {
            return SessionStateSnapshot(
                queue = queue,
                currentIndex = currentIndex,
                progress = progress,
                isRunning = isRunning,
                isPaused = isPaused,
                breakStartedAt = null,
                currentBreakDuration = 0L
            )
        }
    }
}