package com.example.tutorlink.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tutorlink.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(3000L) // Delay for 3 seconds
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Top decorative bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFC88080)))
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White))
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFC88080)))
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White))
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFC88080)))
            }
            
            Spacer(modifier = Modifier.weight(1f))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.tutorlink__1_),
                contentDescription = "TutorLink Logo",
                modifier = Modifier.height(150.dp)
            )
            
            // App Name
            Text(
                text = "TUTORLINK",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00C89C)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Slogan
            Text(
                text = "CONNECTING STUDENTS &\nTUTORS IN ONE PLACE",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    TutorLINKTheme {
        SplashScreen(rememberNavController())
    }
}
