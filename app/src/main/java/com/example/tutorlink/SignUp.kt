package com.example.tutorlink

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var matricNo by remember { mutableStateOf("") }
    var phoneNo by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.tutorlink__1_),
            contentDescription = "Logo",
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(24.dp))
                .padding(bottom = 24.dp)
        )

        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        SignUpTextField(value = fullName, onValueChange = { fullName = it }, label = "Full Name", icon = Icons.Default.Person)
        Spacer(modifier = Modifier.height(16.dp))
        SignUpTextField(value = email, onValueChange = { email = it }, label = "Email", icon = Icons.Default.Email)
        Spacer(modifier = Modifier.height(16.dp))
        SignUpTextField(value = password, onValueChange = { password = it }, label = "Password", icon = Icons.Default.Lock, isPassword = true)
        Spacer(modifier = Modifier.height(16.dp))
        SignUpTextField(value = matricNo, onValueChange = { matricNo = it }, label = "Matric No", icon = Icons.Default.Badge)
        Spacer(modifier = Modifier.height(16.dp))
        SignUpTextField(value = phoneNo, onValueChange = { phoneNo = it }, label = "Phone No", icon = Icons.Default.Phone)

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "I am a...", fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SignUpRoleSelectionCard(
                text = "Tutor",
                selected = selectedRole == "Tutor",
                onClick = { selectedRole = "Tutor" },
                modifier = Modifier.weight(1f)
            )
            SignUpRoleSelectionCard(
                text = "Student",
                selected = selectedRole == "Student",
                onClick = { selectedRole = "Student" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty() && fullName.isNotEmpty() && selectedRole != null) {
                    isLoading = true
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                val user = hashMapOf(
                                    "fullName" to fullName,
                                    "email" to email,
                                    "matricNo" to matricNo,
                                    "phoneNo" to phoneNo,
                                    "role" to selectedRole
                                )
                                if (userId != null) {
                                    db.collection("users").document(userId).set(user)
                                        .addOnSuccessListener {
                                            isLoading = false
                                            Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show()
                                            navController.navigate("login")
                                        }
                                        .addOnFailureListener { e ->
                                            isLoading = false
                                            Toast.makeText(context, "Firestore Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                }
                            } else {
                                isLoading = false
                                Toast.makeText(context, "Auth Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    if (selectedRole == null) {
                        Toast.makeText(context, "Please select a role", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            else Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Log In")
        }
    }
}

@Composable
fun SignUpTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector, isPassword: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpRoleSelectionCard(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent

    Card(
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(width = if (selected) 2.dp else 1.dp, color = borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.outline
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
