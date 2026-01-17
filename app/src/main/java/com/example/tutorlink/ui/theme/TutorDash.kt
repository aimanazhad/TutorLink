package com.example.tutorlink.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tutorlink.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun TutorDash(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    
    LaunchedEffect(auth.currentUser?.uid) {
        auth.currentUser?.uid?.let { uid ->
            db.collection("appointments").whereEqualTo("tutorId", uid)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        val appointmentList = snapshot.documents.mapNotNull { doc ->
                            val data = doc.data
                            if (data != null) {
                                Appointment(doc.id, data)
                            } else {
                                null
                            }
                        }
                        appointments = appointmentList
                    }
                }
        }
    }

    Scaffold(
        topBar = { TutorDashTopBar() },
        bottomBar = { TutorDashBottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF0F4F8)) // Consistent background
        ) {
            Text(
                text = "MY APPOINTMENTS",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )
            TutorAppointmentList(appointments)
        }
    }
}

@Composable
fun TutorAppointmentList(appointments: List<Appointment>) {
    if (appointments.isEmpty()) {
        Text("You have no appointments.", modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(16.dp)) {
            items(appointments) { appointment ->
                TutorAppointmentCard(appointment)
            }
        }
    }
}

@Composable
fun TutorAppointmentCard(appointment: Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Student: ${appointment.data["studentName"]}", fontWeight = FontWeight.Bold)
                Text("Course: ${appointment.data["course"]}")
                Text("Date: ${appointment.data["date"]}")
            }
            Text(
                text = appointment.data["status"]?.toString()?.uppercase() ?: "N/A",
                color = when (appointment.data["status"]) {
                    "approved" -> Color(0xFF34A853)
                    "rejected" -> MaterialTheme.colorScheme.error
                    else -> Color.Gray
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorDashTopBar() {
    var searchQuery by remember { mutableStateOf("") }
    TopAppBar(
        title = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        navigationIcon = {
             Image(
                painter = painterResource(id = R.drawable.tutorlink__1_),
                contentDescription = "Logo",
                modifier = Modifier.padding(start = 16.dp).size(40.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFDECEE))
    )
}

@Composable
fun TutorDashBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val items = listOf(
        Screen("tutor_dash", "Home", Icons.Default.Home),
        Screen("appointment_tutor", "Appointments", Icons.Rounded.CalendarToday),
        Screen("rate_review", "Reviews", Icons.Default.Star),
        Screen("profile_tutor", "Profile", Icons.Default.Person)
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 8.dp
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        screen.icon,
                        contentDescription = screen.title,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = { Text(screen.title, fontSize = 12.sp) },
                selected = currentRoute == screen.route,
                onClick = { 
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}

data class Screen(val route: String, val title: String, val icon: ImageVector)

@Preview(showBackground = true)
@Composable
fun TutorDashPreview() {
    TutorLINKTheme {
        TutorDash(rememberNavController())
    }
}
