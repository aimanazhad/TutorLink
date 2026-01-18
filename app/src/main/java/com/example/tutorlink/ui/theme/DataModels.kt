package com.example.tutorlink.ui.theme

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

// Single source of truth for the Review data model
data class Review(
    val id: String = "",
    val tutorId: String = "",
    val tutorName: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val courseCode: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val timestamp: Timestamp = Timestamp.now()
)

// Single source of truth for the Tutor data model
data class Tutor(
    val uid: String = "",
    @get:PropertyName("fullName") @set:PropertyName("fullName") var name: String = "",
    val courses: List<String> = emptyList()
)
