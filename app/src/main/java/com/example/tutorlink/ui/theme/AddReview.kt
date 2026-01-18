package com.example.tutorlink.ui.theme

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var tutors by remember { mutableStateOf<List<Tutor>>(emptyList()) }
    var selectedTutor by remember { mutableStateOf<Tutor?>(null) }
    var availableCourses by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedCourse by remember { mutableStateOf<String?>(null) }
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var studentName by remember { mutableStateOf("") }

    // Fetch tutors
    LaunchedEffect(Unit) {
        db.collection("users").whereEqualTo("role", "Tutor").get()
            .addOnSuccessListener { result ->
                val tutorList = result.documents.mapNotNull { doc ->
                     doc.toObject(Tutor::class.java)?.copy(uid = doc.id)
                }
                tutors = tutorList
            }
    }

    // Fetch current student's name
    LaunchedEffect(auth.currentUser?.uid) {
        auth.currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        studentName = doc.getString("fullName") ?: "Anonymous"
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add a Review") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Review a Tutor", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            // Tutor Dropdown
            AppDropdown(
                label = "Select Tutor",
                options = tutors.map { it.name },
                selectedOption = selectedTutor?.name,
                onOptionSelected = { tutorName ->
                    val foundTutor = tutors.find { it.name == tutorName }
                    selectedTutor = foundTutor
                    if (foundTutor != null) {
                        availableCourses = foundTutor.courses
                        selectedCourse = null // Reset course selection
                    }
                }
            )

            // Course Dropdown
            AppDropdown(
                label = "Select Course",
                options = availableCourses,
                selectedOption = selectedCourse,
                onOptionSelected = { course -> selectedCourse = course },
                enabled = selectedTutor != null
            )


            // Star Rating
            Text("Your Rating", fontWeight = FontWeight.SemiBold)
            StarRating(rating = rating, onRatingChange = { newRating -> rating = newRating })


            // Comment Box
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Write your review") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            // Submit Button
            Button(
                onClick = {
                    val currentUser = auth.currentUser
                    if (selectedTutor == null) {
                        Toast.makeText(context, "Please select a tutor", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedCourse == null) {
                        Toast.makeText(context, "Please select a course", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (rating == 0) {
                        Toast.makeText(context, "Please provide a rating", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (comment.isBlank()) {
                        Toast.makeText(context, "Please write a comment", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (currentUser == null) {
                        Toast.makeText(context, "You must be logged in to post a review", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    val reviewData = hashMapOf(
                        "tutorId" to selectedTutor!!.uid,
                        "tutorName" to selectedTutor!!.name,
                        "studentId" to currentUser.uid,
                        "studentName" to studentName,
                        "courseCode" to selectedCourse!!,
                        "rating" to rating,
                        "comment" to comment.trim(),
                        "timestamp" to Timestamp.now()
                    )

                    db.collection("reviews").add(reviewData)
                        .addOnSuccessListener {
                            isLoading = false
                            Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            Toast.makeText(context, "Failed to submit review: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Submit Review", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
