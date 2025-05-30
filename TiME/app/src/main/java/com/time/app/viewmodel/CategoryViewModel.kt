package com.time.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.time.app.model.Category
import com.time.app.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val repository = CategoryRepository()
    private val db = FirebaseFirestore.getInstance()


    // State flow for categories
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    // Load categories from repository and update state
    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repository.getCategories() // This should return a list of categories
        }
    }

    // Add a new category and optionally sync to Firestore
    fun addCategory(category: Category, autoSync: Boolean = false) {
        viewModelScope.launch {
            repository.addCategory(category)
            loadCategories() // Refresh categories
            if (autoSync) saveCategoryToFirestore(category) // Sync to Firestore if needed
        }
    }

    // Delete a category by id, and optionally remove from Firestore
    fun deleteCategory(id: String) {
        viewModelScope.launch {
            repository.deleteCategory(id) // Delete from repository
            deleteFromFirestore(id) // Delete from Firestore
            loadCategories() // Refresh categories
        }
    }

    // Save a new category to Firestore
    fun saveCategoryToFirestore(category: Category) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(uid)
            .collection("categories").document(category.id)
            .set(category.toMap()) // Convert category to map for Firestore
    }

    // Delete a category from Firestore
    fun deleteFromFirestore(id: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(uid)
            .collection("categories").document(id)
            .delete() // Delete from Firestore
    }

}
