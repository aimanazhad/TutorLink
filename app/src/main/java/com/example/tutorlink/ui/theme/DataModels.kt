package com.example.tutorlink.ui.theme

import androidx.compose.ui.graphics.vector.ImageVector
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

// Single source of truth for the Appointment data model
data class Appointment(
    val id: String = "",
    val tutorId: String = "",
    val tutorName: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val course: String = "",
    val date: String = "",
    val time: String = "",
    val studentCount: String = "",
    val status: String = ""
)

// Data class for bottom navigation items
data class Screen(val route: String, val title: String, val icon: ImageVector)
