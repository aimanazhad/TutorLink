package com.example.tutorlink.ui.theme

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Appointment(val id: String, val data: Map<String, Any>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentTutor(navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            db.collection("appointments")
                .whereEqualTo("tutorId", currentUser.uid)
                .whereEqualTo("status", "pending")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) { return@addSnapshotListener }
                    val appointmentList = snapshots?.map { Appointment(it.id, it.data) } ?: emptyList()
                    appointments = appointmentList
                }
        }
    }
    
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }

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
            Text(
                text = "Pending Requests",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(appointments) { appointment ->
                    AppointmentRequestCard(
                        appointment = appointment,
                        highlighted = selectedAppointment?.id == appointment.id,
                        onClick = { selectedAppointment = if (selectedAppointment?.id == appointment.id) null else appointment } // Toggle selection
                    )
                }
            }

            if (selectedAppointment != null) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider()
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("REQUEST DETAILS", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                        Text(text = "Student: ${selectedAppointment!!.data["studentName"]}", fontWeight = FontWeight.Medium)
                        Text(text = "Course: ${selectedAppointment!!.data["course"]}")
                        Text(text = "No. of Students: ${selectedAppointment!!.data["studentCount"]}")
                        Text(text = "Date & Time: ${selectedAppointment!!.data["date"]} at ${selectedAppointment!!.data["time"]}")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { 
                                db.collection("appointments").document(selectedAppointment!!.id).update("status", "approved")
                                Toast.makeText(context, "Appointment Approved", Toast.LENGTH_SHORT).show()
                                selectedAppointment = null 
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("APPROVE", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { 
                                db.collection("appointments").document(selectedAppointment!!.id).update("status", "rejected")
                                Toast.makeText(context, "Appointment Rejected", Toast.LENGTH_SHORT).show()
                                selectedAppointment = null
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
fun AppointmentRequestCard(appointment: Appointment, highlighted: Boolean, onClick: () -> Unit) {
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
                    Text(text = "${appointment.data["course"]}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = "with ${appointment.data["studentName"]}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(text = "${appointment.data["time"]}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
