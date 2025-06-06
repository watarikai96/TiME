package com.time.android.model
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class Category(
    @DocumentId
    val id: String = "",

    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("color")
    @set:PropertyName("color")
    var color: Long = 0xFFCCCCCC // Hex color stored as Long

) {
    // Converts this object to a map for Firestore save
    @Exclude
    fun toMap(): Map<String, Any> = mapOf(
        "name" to name,
        "color" to color
    )

    companion object {
        // Rebuild from Firestore document
        fun fromMap(id: String, map: Map<String, Any?>): Category? {
            val name = map["name"] as? String ?: return null
            val color = map["color"] as? Long ?: (map["color"] as? Number)?.toLong() ?: 0xFFCCCCCC
            return Category(id = id, name = name, color = color)
        }
    }
}


