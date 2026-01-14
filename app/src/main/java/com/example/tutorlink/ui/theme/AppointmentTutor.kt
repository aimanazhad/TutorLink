package com.example.tutorlink.ui.theme

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Class
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tutorlink.R

// Data class to represent an appointment request
data class AppointmentRequest(
    val id: Int,
    val className: String,
    val classDescription: String,
    val time: String,
    val studentName: String,
    val courseCode: String,
    val studentCount: Int,
    val dateTime: String
)

@Composable
fun AppointmentTutor(navController: NavController) {
    val context = LocalContext.current
    
    // Sample data for appointment requests
    val appointmentRequests = remember {
        listOf(
            AppointmentRequest(
                id = 1,
                className = "Mathematics",
                classDescription = "Calculus I session",
                time = "9:41 AM",
                studentName = "Ahmad",
                courseCode = "MATH101",
                studentCount = 1,
                dateTime = "2024-08-10, 10:00 AM"
            ),
            AppointmentRequest(
                id = 2,
                className = "Physics",
                classDescription = "Newtonian Mechanics",
                time = "11:30 AM",
                studentName = "Sarah",
                courseCode = "PHY202",
                studentCount = 1,
                dateTime = "2024-08-11, 12:00 PM"
            )
        )
    }
    
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        bottomBar = { TutorDashBottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF80DEEA)), // Using a consistent color from the app
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Appointment (Tutor)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.tutorlink__1_),
                contentDescription = "TutorLink Logo",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(80.dp)
            )

            // "Announcement" Title
            Text(
                text = "Announcement",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            )

            // List of appointment requests
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                itemsIndexed(appointmentRequests) { index, request ->
                    AppointmentRequestCard(
                        request = request,
                        highlighted = selectedIndex == index,
                        onClick = { selectedIndex = index }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Details and Action Buttons for selected request
            val selectedRequest = selectedIndex?.let { appointmentRequests[it] }
            if (selectedRequest != null) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Student: ${selectedRequest.studentName}", fontWeight = FontWeight.Bold)
                    Text(text = "Course code: ${selectedRequest.courseCode}")
                    Text(text = "How many student: ${selectedRequest.studentCount}")
                    Text(text = "Date, Time: ${selectedRequest.dateTime}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { 
                        Toast.makeText(context, "Appointment for ${selectedRequest.studentName} Approved", Toast.LENGTH_SHORT).show()
                        selectedIndex = null // Deselect after action
                    }) {
                        Text("APPROVE")
                    }
                    Button(
                        onClick = { 
                            Toast.makeText(context, "Appointment for ${selectedRequest.studentName} Rejected", Toast.LENGTH_SHORT).show()
                            selectedIndex = null // Deselect after action
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)) // A clearer Red
                    ) {
                        Text("REJECT")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AppointmentRequestCard(request: AppointmentRequest, highlighted: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) Color(0xFFF0F0F0) else Color.White // Light grey for highlight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon for the class
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Class, contentDescription = "Class Icon", tint = Color.Gray)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = request.className, fontWeight = FontWeight.Bold)
                    Text(text = request.classDescription, fontSize = 14.sp, color = Color.Gray)
                }
            }
            Text(text = request.time, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentTutorPreview() {
    TutorLINKTheme {
        AppointmentTutor(rememberNavController())
    }
}
