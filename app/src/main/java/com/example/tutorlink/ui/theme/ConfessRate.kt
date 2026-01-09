package com.example.tutorlink.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun ConfessRate(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { ConfessRateTopBar() },
        bottomBar = { StudentDashBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("rate_review") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Review", tint = Color.White)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        val reviews = remember {
            listOf(
                Review("Dr. Smith, 435 OOP", "Excellent teaching, very clear and helpful.", "John Doe", "Jun 10, 2024", 5),
                Review("Prof. Jane, 402 Programming I", "Great course, but a bit fast-paced.", "Jane Smith", "Jun 9, 2024", 4),
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(reviews) { review ->
                ReviewCard(review)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfessRateTopBar() {
    TopAppBar(
        title = {
            Text(
                "REVIEW",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.tutorlink__1_),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 16.dp)
            )
        },
        actions = {
            Spacer(modifier = Modifier.width(56.dp)) // To center the title
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFFDECEE)
        )
    )
}

data class Review(
    val title: String,
    val text: String,
    val reviewerName: String,
    val date: String,
    val rating: Int
)

@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StarRating(rating = review.rating)
            Spacer(modifier = Modifier.height(8.dp))
            Text(review.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(review.text, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Reviewer Icon", tint = Color.Gray)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(review.reviewerName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(review.date, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun StarRating(rating: Int) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = if (i <= rating) "Full Star" else "Empty Star",
                tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfessRatePreview() {
    TutorLINKTheme {
        ConfessRate(rememberNavController())
    }
}
