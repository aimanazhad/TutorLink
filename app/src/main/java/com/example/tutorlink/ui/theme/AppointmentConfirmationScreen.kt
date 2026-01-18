@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tutorlink.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tutorlink.R
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentConfirmationScreen(navController: NavController, appointmentId: String) {
    val db = FirebaseFirestore.getInstance()
    var appointment by remember { mutableStateOf<Map<String, Any>?>(null) }

    LaunchedEffect(appointmentId) {
        db.collection("appointments").document(appointmentId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    appointment = document.data
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmation") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        if (appointment == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Appointment Confirmed!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Here is your booking confirmation:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Divider()
                        ConfirmationDetailRow(label = "Tutor", value = appointment!!["tutorName"] as? String ?: "")
                        ConfirmationDetailRow(label = "Course", value = appointment!!["course"] as? String ?: "")
                        ConfirmationDetailRow(label = "Date", value = appointment!!["date"] as? String ?: "")
                        ConfirmationDetailRow(label = "Time", value = appointment!!["time"] as? String ?: "")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Show this QR code to your tutor for verification:", textAlign = TextAlign.Center, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.qr),
                    contentDescription = "QR Code",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { 
                        navController.navigate("student_dash") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                     },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("DONE", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun ConfirmationDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
