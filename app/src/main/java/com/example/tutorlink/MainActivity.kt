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
import com.example.tutorlink.ui.theme.AppointmentTutor
import com.example.tutorlink.ui.theme.ConfessRate
import com.example.tutorlink.ui.theme.EditStudent
import com.example.tutorlink.ui.theme.EditTutor
import com.example.tutorlink.ui.theme.ProfileStudent
import com.example.tutorlink.ui.theme.ProfileTutor
import com.example.tutorlink.ui.theme.RateReview
import com.example.tutorlink.ui.theme.SplashScreen
import com.example.tutorlink.ui.theme.StudentDash
import com.example.tutorlink.ui.theme.TutorDash
import com.example.tutorlink.ui.theme.TutorLINKTheme
import com.example.tutorlink.ui.theme.TutorLogin

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
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginPage(navController) } // Assuming LoginPage exists
        composable("tutor_login") { TutorLogin(navController) }
        composable("tutor_dash") { TutorDash(navController) }
        composable("student_dash") { StudentDash(navController) }
        composable("profile_student") { ProfileStudent(navController) }
        composable("edit_student") { EditStudent(navController) }
        composable("profile_tutor") { ProfileTutor(navController) }
        composable("edit_tutor") { EditTutor(navController) }
        composable("appointment_student") { AppointmentStudent(navController) }
        composable("appointment_tutor") { AppointmentTutor(navController) }
        composable("confess_rate") { ConfessRate(navController) }
        composable("rate_review") { RateReview(navController) }
    }
}
