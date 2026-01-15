package com.example.tutorlink.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tutorlink.R

@Composable
fun ProfileTutor(navController: NavController) {
    Scaffold(
        topBar = { ProfileTutorTopBar() },
        bottomBar = { TutorDashBottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PROFILE",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // User Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileDetailRow(label = "Name:", value = "Dr. Smith")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ProfileDetailRow(label = "Staff ID:", value = "T12345")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ProfileDetailRow(label = "Phone No:", value = "012-3456789")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ProfileDetailRow(label = "Status:", value = "Tutor")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Edit Profile Button
            Button(
                onClick = { navController.navigate("edit_tutor") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Edit Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Log Out Button
            OutlinedButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Text(text = "Log Out", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun ProfileTutorTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFDECEE))
            .padding(vertical = 8.dp, horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Image(
            painter = painterResource(id = R.drawable.tutorlink__1_),
            contentDescription = "Logo",
            modifier = Modifier.size(40.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileTutorPreview() {
    TutorLINKTheme {
        ProfileTutor(rememberNavController())
    }
}
