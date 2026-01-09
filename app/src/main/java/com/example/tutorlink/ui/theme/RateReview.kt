package com.example.tutorlink.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateReview(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ADD REVIEW",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFDECEE)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            RateReviewForm(
                onSubmit = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Review Submitted!")
                        navController.popBackStack()
                    }
                },
                onError = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Please fill all fields!")
                    }
                }
            )
        }
    }
}

@Composable
fun RateReviewForm(onSubmit: () -> Unit, onError: () -> Unit) {
    val tutorCourseMap = remember {
        mapOf(
            "Mr Ahmad" to listOf("435 OOP", "402 Programming I"),
            "Mr Wan Ikhwan" to listOf("435 OOP"),
            "Miss Fazlin" to listOf("429 Computer Architecture")
        )
    }
    val tutorOptions = remember { tutorCourseMap.keys.toList() }

    var tutor by remember { mutableStateOf<String?>(null) }
    var course by remember { mutableStateOf<String?>(null) }
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    var availableCourses by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Tutor's ID", fontWeight = FontWeight.SemiBold)
        CustomDropdown(
            label = "Select Tutor",
            options = tutorOptions,
            selectedOption = tutor,
            onOptionSelected = { selectedTutor ->
                tutor = selectedTutor
                course = null // Reset course selection
                availableCourses = tutorCourseMap[selectedTutor] ?: emptyList()
            }
        )

        Text("Course Code", fontWeight = FontWeight.SemiBold)
        CustomDropdown(
            label = "Select Course",
            options = availableCourses,
            selectedOption = course,
            onOptionSelected = { course = it },
            enabled = tutor != null
        )

        Text("Rating", fontWeight = FontWeight.SemiBold)
        StarRatingSelector(rating = rating, onRatingChange = { rating = it })

        Text("Review", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = reviewText,
            onValueChange = { reviewText = it },
            label = { Text("Write your review") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (tutor != null && course != null && rating > 0 && reviewText.isNotBlank()) {
                    onSubmit()
                } else {
                    onError()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(text = "Submit Review", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StarRatingSelector(rating: Int, onRatingChange: (Int) -> Unit) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = if (i <= rating) "Full Star" else "Empty Star",
                tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onRatingChange(i) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RateReviewPreview() {
    TutorLINKTheme {
        RateReview(rememberNavController())
    }
}
