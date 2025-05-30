import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.time.app.model.TiME
import kotlinx.coroutines.tasks.await

class TiMERepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("time_entries")

    suspend fun add(time: TiME) {
        val docRef = collection.add(time).await()
        val timeWithId = time.copy(id = docRef.id)
        collection.document(docRef.id).set(timeWithId, SetOptions.merge()).await()
    }

    suspend fun update(id: String, time: TiME) {
        if (id.isBlank()) return
        collection.document(id).set(time, SetOptions.merge()).await()
    }

    suspend fun delete(id: String) {
        if (id.isBlank()) return
        collection.document(id).delete().await()
    }

    suspend fun getAll(): List<TiME> {
        val snapshot = collection.get().await()
        return snapshot.documents.mapNotNull {
            it.toObject(TiME::class.java)?.copy(id = it.id)
        }
    }
}
