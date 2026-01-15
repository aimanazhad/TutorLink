package com.example.tutorlink.ui.theme

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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

@OptIn(ExperimentalMaterial3Api::class)
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
        bottomBar = { TutorDashBottomBar(navController) },
        topBar = { 
            TopAppBar(
                title = { Text("Appointments", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // "Announcement" Title
            Text(
                text = "Pending Requests",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            // List of appointment requests
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                itemsIndexed(appointmentRequests) { index, request ->
                    AppointmentRequestCard(
                        request = request,
                        highlighted = selectedIndex == index,
                        onClick = { selectedIndex = if (selectedIndex == index) null else index } // Toggle selection
                    )
                }
            }

            // Details and Action Buttons for selected request
            val selectedRequest = selectedIndex?.let { appointmentRequests[it] }
            if (selectedRequest != null) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider()
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("REQUEST DETAILS", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                        Text(text = "Student: ${selectedRequest.studentName}", fontWeight = FontWeight.Medium)
                        Text(text = "Course code: ${selectedRequest.courseCode}")
                        Text(text = "No. of Students: ${selectedRequest.studentCount}")
                        Text(text = "Date & Time: ${selectedRequest.dateTime}")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { 
                                Toast.makeText(context, "Appointment for ${selectedRequest.studentName} Approved", Toast.LENGTH_SHORT).show()
                                selectedIndex = null // Deselect after action
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("APPROVE", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { 
                                Toast.makeText(context, "Appointment for ${selectedRequest.studentName} Rejected", Toast.LENGTH_SHORT).show()
                                selectedIndex = null // Deselect after action
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Text("REJECT", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentRequestCard(request: AppointmentRequest, highlighted: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (highlighted) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    val borderColor = if (highlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, borderColor)
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
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Class, contentDescription = "Class Icon", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = request.className, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = request.classDescription, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(text = request.time, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
