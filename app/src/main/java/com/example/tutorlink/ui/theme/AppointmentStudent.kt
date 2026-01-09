package com.example.tutorlink.ui.theme

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tutorlink.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AppointmentStudent(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { AppointmentStudentTopBar() },
        bottomBar = { StudentDashBottomBar(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            AppointmentForm(
                onSuccess = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Appointment Success")
                        navController.navigate("student_dash") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                onError = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Please fill all fields!")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentStudentTopBar() {
    TopAppBar(
        title = {
            Text(
                "APPOINTMENT",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.tutorlink__1_),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 16.dp)
            )
        },
        actions = {
            // Invisible spacer to push the title to a more centered position
            Spacer(modifier = Modifier.width(56.dp)) // 40dp icon + 16dp padding
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFFDECEE)
        )
    )
}

@Composable
fun AppointmentForm(onSuccess: () -> Unit, onError: () -> Unit) {
    val tutorCourseMap = remember {
        mapOf(
            "Mr Ahmad" to listOf("435 OOP", "402 Programming I"),
            "Mr Wan Ikhwan" to listOf("435 OOP"),
            "Miss Fazlin" to listOf("429 Computer Architecture")
        )
    }
    val tutorOptions = remember { tutorCourseMap.keys.toList() }
    val studentCountOptions = listOf("1", "2", "3", "4", "5+")

    var tutor by remember { mutableStateOf<String?>(null) }
    var course by remember { mutableStateOf<String?>(null) }
    var studentCount by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }

    var availableCourses by remember { mutableStateOf<List<String>>(emptyList()) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = dateFormat.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val amPm = if (hourOfDay < 12) "AM" else "PM"
            val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
            selectedTime = String.format(Locale.US, "%d:%02d %s", hour, minute, amPm)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false // 12-hour format
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Tutor's name", fontWeight = FontWeight.SemiBold)
        CustomDropdown(
            label = "Select Tutor",
            options = tutorOptions,
            selectedOption = tutor,
            onOptionSelected = { selectedTutor ->
                tutor = selectedTutor
                course = null // Reset course selection
                availableCourses = tutorCourseMap[selectedTutor] ?: emptyList()
            }
        )

        Text("Course Code", fontWeight = FontWeight.SemiBold)
        CustomDropdown(
            label = "Select Course",
            options = availableCourses,
            selectedOption = course,
            onOptionSelected = { course = it },
            enabled = tutor != null
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Date:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                Card(
                    modifier = Modifier.clickable { datePickerDialog.show() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedDate ?: "Select Date", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Time:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                Card(
                    modifier = Modifier.clickable { timePickerDialog.show() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedTime ?: "Select Time", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Text("How Many Students", fontWeight = FontWeight.SemiBold)
        CustomDropdown(label = "Select Student Count", options = studentCountOptions, selectedOption = studentCount, onOptionSelected = { studentCount = it })

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (tutor != null && course != null && studentCount != null && selectedDate != null && selectedTime != null) {
                    onSuccess()
                } else {
                    onError()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(text = "Submit", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentStudentPreview() {
    TutorLINKTheme {
        AppointmentStudent(rememberNavController())
    }
}
