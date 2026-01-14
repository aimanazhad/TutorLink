package com.example.tutorlink.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tutorlink.R

// Data class to represent a single review
data class Review(
    val tutorName: String,
    val courseCode: String,
    val rating: Int,
    val comment: String,
    val reviewerName: String,
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateReview(navController: NavController) {
    // Sample list of reviews. In a real app, you would fetch this from a ViewModel.
    val reviews = remember {
        listOf(
            Review(
                tutorName = "Dr. Smith",
                courseCode = "435 OOP",
                rating = 5,
                comment = "Excellent teaching, very clear and helpful.",
                reviewerName = "John Doe",
                date = "Jun 10, 2024"
            ),
            Review(
                tutorName = "Prof. Jane",
                courseCode = "402 Programming I",
                rating = 4,
                comment = "Great course, but a bit fast-paced.",
                reviewerName = "Jane Smith",
                date = "Jun 9, 2024"
            ),
            Review(
                tutorName = "Dr. Smith",
                courseCode = "435 OOP",
                rating = 4,
                comment = "Very good, but sometimes hard to follow.",
                reviewerName = "Peter Jones",
                date = "Jun 8, 2024"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "REVIEW",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.tutorlink__1_),
                        contentDescription = "Logo",
                        modifier = Modifier.padding(start = 16.dp).size(36.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFDECEE) // Light pinkish color from image
                )
            )
        },
        bottomBar = {
            TutorDashBottomBar(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF9F9F9)) // Light gray background
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(reviews) { review ->
                ReviewCard(review = review)
            }
        }
    }
}

@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            StarRatingDisplay(rating = review.rating)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${review.tutorName}, ${review.courseCode}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = review.comment,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEEEEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Reviewer",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = review.reviewerName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = review.date,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun StarRatingDisplay(rating: Int) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Star",
                tint = if (index < rating) Color(0xFFFFD700) else Color(0xFFE0E0E0),
                modifier = Modifier.size(24.dp)
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
