package com.example.tutorlink.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
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
fun StudentDash(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var userName by remember { mutableStateOf("User") }
    
    LaunchedEffect(auth.currentUser?.uid) {
        auth.currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userName = document.getString("fullName") ?: "User"
                    }
                }
        }
    }

    Scaffold(
        topBar = { StudentDashTopBar() },
        bottomBar = { StudentDashBottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Welcome, $userName!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "ANNOUNCEMENT",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )
            AnnouncementList()
        }
    }
}

@Composable
fun StudentDashTopBar() {
    var searchQuery by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFDECEE)) // A light pinkish color similar to the image
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.tutorlink__1_),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun StudentDashBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val items = listOf(
        "student_dash",
        "appointment_student",
        "confess_rate",
        "profile_student"
    )
    val icons = listOf(Icons.Default.Home, Icons.Rounded.CalendarToday, Icons.Default.Star, Icons.Default.Person)

    NavigationBar(
        containerColor = Color(0xFF00C89C), // Teal color
        contentColor = Color.White
    ) {
        items.forEachIndexed { index, route ->
            NavigationBarItem(
                icon = {
                    Icon(
                        icons[index],
                        contentDescription = route,
                        modifier = Modifier.size(28.dp)
                    )
                },
                selected = currentRoute == route,
                onClick = { 
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color(0xFFE0B0FF)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudentDashPreview() {
    TutorLINKTheme {
        StudentDash(rememberNavController())
    }
}
