package com.time.android.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.time.android.model.SessionStateSnapshot
import kotlinx.coroutines.flow.firstOrNull

val Context.sessionStateDataStore: DataStore<SessionStateSnapshot> by dataStore(
    fileName = "hyperfocus_session_state.json",
    serializer = SessionStateSerializer
)
class SessionStateDataStore(private val context: Context) {
    private val dataStore: DataStore<SessionStateSnapshot> = context.sessionStateDataStore
    suspend fun saveSessionState(state: SessionStateSnapshot) {
        dataStore.updateData { state }
    }
    suspend fun loadSessionState(): SessionStateSnapshot? {
        return try {
            dataStore.data.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
    suspend fun clear() {
        dataStore.updateData {
            SessionStateSnapshot(emptyList(), 0, 0f, false, false, null)
        }
    }
}