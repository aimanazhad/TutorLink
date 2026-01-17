@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tutorlink.ui.theme

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Tutor(val uid: String, val name: String, val courses: List<String>)

@Composable
fun AppointmentStudent(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedTutor by remember { mutableStateOf<Tutor?>(null) }

    Scaffold(
        topBar = { AppointmentStudentTopBar() },
        bottomBar = { StudentDashBottomBar(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            AppointmentForm(
                selectedTutor = selectedTutor,
                onTutorSelected = { selectedTutor = it },
                onSuccess = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Appointment submitted successfully")
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
            Spacer(modifier = Modifier.width(56.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
fun AppointmentForm(
    selectedTutor: Tutor?,
    onTutorSelected: (Tutor) -> Unit,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var tutors by remember { mutableStateOf<List<Tutor>>(emptyList()) }
    var isLoadingTutors by remember { mutableStateOf(true) }
    val context = LocalContext.current

    var availableCourses by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        isLoadingTutors = true
        db.collection("users").whereEqualTo("role", "Tutor").get()
            .addOnSuccessListener { result ->
                val tutorList = result.documents.mapNotNull { doc ->
                    val uid = doc.id
                    val name = doc.getString("fullName") ?: ""
                    @Suppress("UNCHECKED_CAST")
                    val courses = doc.get("Course Code") as? List<String> ?: emptyList()
                    if (name.isNotEmpty()) Tutor(uid, name, courses) else null
                }
                tutors = tutorList
                isLoadingTutors = false
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Firebase Error: ${exception.message}", Toast.LENGTH_LONG).show()
                isLoadingTutors = false
            }
    }

    val studentCountOptions = listOf("1", "2", "3", "4", "5+")
    var course by remember { mutableStateOf<String?>(null) }
    var studentCount by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }

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
        AppDropdown(
            label = "Select Tutor",
            options = tutors.map { it.name },
            selectedOption = selectedTutor?.name,
            onOptionSelected = { tutorName ->
                val foundTutor = tutors.find { it.name == tutorName }
                if (foundTutor != null) {
                    onTutorSelected(foundTutor)
                    availableCourses = foundTutor.courses
                    course = null // Reset course selection
                }
            },
            isLoading = isLoadingTutors
        )

        Text("Course Code", fontWeight = FontWeight.SemiBold)
        AppDropdown(
            label = "Select Course",
            options = availableCourses,
            selectedOption = course,
            onOptionSelected = { selectedCourse -> course = selectedCourse },
            enabled = selectedTutor != null
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedDate ?: "Select Date", color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Time:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                Card(
                    modifier = Modifier.clickable { timePickerDialog.show() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedTime ?: "Select Time", color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Text("How Many Students", fontWeight = FontWeight.SemiBold)
        AppDropdown(label = "Select Student Count", options = studentCountOptions, selectedOption = studentCount, onOptionSelected = { selectedStudentCount -> studentCount = selectedStudentCount })

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (selectedTutor != null && course != null && studentCount != null && selectedDate != null && selectedTime != null) {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        val studentName = if (currentUser.displayName.isNullOrEmpty()) "Student User" else currentUser.displayName
                        val appointment = hashMapOf(
                            "tutorId" to selectedTutor.uid,
                            "tutorName" to selectedTutor.name,
                            "studentId" to currentUser.uid,
                            "studentName" to studentName,
                            "course" to course,
                            "date" to selectedDate,
                            "time" to selectedTime,
                            "studentCount" to studentCount,
                            "status" to "pending"
                        )
                        db.collection("appointments").add(appointment)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { onError() }
                    } else {
                        Toast.makeText(context, "You must be logged in to book an appointment", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    onError()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(text = "Submit", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AppDropdown(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = enabled && expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption ?: "",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            enabled = enabled,
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = enabled && expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (isLoading) {
                DropdownMenuItem(
                    text = { Text("Loading...") },
                    enabled = false,
                    onClick = {}
                )
            } else if (options.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No options available") },
                    enabled = false,
                    onClick = {}
                )
            } else {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
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
