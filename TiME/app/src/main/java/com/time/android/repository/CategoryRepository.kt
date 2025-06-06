package com.time.android.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.time.android.model.Category
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val categoryCollection = db.collection("categories")

    suspend fun addCategory(category: Category) {
        categoryCollection.add(category).await()
    }

    suspend fun getCategories(): List<Category> {
        return categoryCollection.get().await().toObjects(Category::class.java)
    }

    suspend fun deleteCategory(id: String) {
        categoryCollection.document(id).delete().await()
    }
}
