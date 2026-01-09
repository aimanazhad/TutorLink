
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

@Composable
fun StudentDash(navController: NavController) {
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
                text = "ANNOUNCEMENT",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
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

data class Announcement(
    val courseCode: String,
    val courseName: String,
    val details: String,
    val time: String,
    val icon: ImageVector,
    val iconColor: Color
)

@Composable
fun AnnouncementList() {
    val announcements = remember {
        listOf(
            Announcement("435 OOP", "Object Oriented Programming", "Room 302", "8:00 AM", Icons.Default.Star, Color(0xFF6A1B9A)),
            Announcement("402 Programming I", "Introduction to Programming", "Room 101", "10:00 AM", Icons.Default.Star, Color(0xFF6A1B9A)),
            Announcement("429 Computer Architecture", "Computer Systems", "Room 205", "1:00 PM", Icons.Default.Star, Color(0xFF6A1B9A))
        )
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(announcements) { announcement ->
            AnnouncementCard(announcement)
        }
    }
}

@Composable
fun AnnouncementCard(announcement: Announcement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(announcement.iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = announcement.icon,
                    contentDescription = "Course Icon",
                    tint = announcement.iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = announcement.courseCode, fontWeight = FontWeight.Bold)
                Text(text = "${announcement.courseName} - ${announcement.details}", fontSize = 14.sp, color = Color.Gray)
            }
            Text(text = announcement.time, fontSize = 12.sp, color = Color.Gray)
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
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color(0xFFE0B0FF) // Light purple for selected background
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
