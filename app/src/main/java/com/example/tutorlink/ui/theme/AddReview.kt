
package com.example.tutorlink.ui.theme

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReview(navController: NavController) {
    var selectedTutor by remember { mutableStateOf<String?>(null) }
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    val context = LocalContext.current

    val tutors = listOf("Dr. Smith", "Prof. Jane")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Review", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            CustomDropdown("Select a Tutor", tutors, selectedTutor, { selectedTutor = it })
            Spacer(modifier = Modifier.height(16.dp))

            Text("Rating", fontWeight = FontWeight.Medium, fontSize = 16.sp)
            StarRatingInput(rating = rating, onRatingChange = { rating = it })
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Write your review") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    Toast.makeText(context, "Review Submitted!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedTutor != null && rating > 0 && comment.isNotBlank()
            ) {
                Text("Submit Review", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StarRatingInput(rating: Int, onRatingChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        (1..5).forEach { index ->
            Icon(
                imageVector = if (index <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = if (index <= rating) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingChange(index) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddReviewPreview() {
    TutorLINKTheme {
        AddReview(rememberNavController())
    }
}
