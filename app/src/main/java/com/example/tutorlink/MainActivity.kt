package com.example.tutorlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tutorlink.ui.theme.AppointmentStudent
import com.example.tutorlink.ui.theme.ConfessRate
import com.example.tutorlink.ui.theme.EditStudent
import com.example.tutorlink.ui.theme.ProfileStudent
import com.example.tutorlink.ui.theme.RateReview
import com.example.tutorlink.ui.theme.StudentDash
import com.example.tutorlink.ui.theme.TutorLINKTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TutorLINKTheme {
                TutorLinkApp()
            }
        }
    }
}

@Composable
fun TutorLinkApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginPage(navController) }
        composable("student_dash") { StudentDash(navController) }
        composable("profile_student") { ProfileStudent(navController) }
        composable("appointment_student") { AppointmentStudent(navController) }
        composable("confess_rate") { ConfessRate(navController) }
        composable("rate_review") { RateReview(navController) }
        composable("edit_student") { EditStudent(navController) }
    }
}
