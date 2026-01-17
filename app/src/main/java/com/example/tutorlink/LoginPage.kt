package com.example.tutorlink

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tutorlink.ui.theme.TutorLINKTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavController) {
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val isStudentRoleSelected = selectedRole == "Student"

    // Google Sign-In Setup
    // Hardcoded web_client_id from google-services.json to fix 'default_web_client_id' unresolved reference
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("336704939872-cs1q8qnma4mtsrqq2rq9d321kfpfbg23.apps.googleusercontent.com") 
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context as Activity, gso)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                
                isLoading = true
                auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        val userId = firebaseUser?.uid
                        
                        // Check if user exists in Firestore, if not create basic profile
                        if (userId != null) {
                            db.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    if (!document.exists()) {
                                        val user = hashMapOf(
                                            "fullName" to (firebaseUser.displayName ?: ""),
                                            "email" to (firebaseUser.email ?: ""),
                                            "matricNo" to "",
                                            "phoneNo" to "",
                                            "role" to "Student" // Default role for Google login
                                        )
                                        db.collection("users").document(userId).set(user)
                                            .addOnCompleteListener {
                                                isLoading = false
                                                navController.navigate("student_dash")
                                            }
                                    } else {
                                        isLoading = false
                                        navController.navigate("student_dash")
                                    }
                                }
                                .addOnFailureListener {
                                    isLoading = false
                                    navController.navigate("student_dash")
                                }
                        }
                    } else {
                        isLoading = false
                        Toast.makeText(context, "Firebase Auth Failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: ApiException) {
                isLoading = false
                Toast.makeText(context, "Google Sign In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            isLoading = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.tutorlink__1_),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .padding(bottom = 24.dp)
            )

            Text(
                text = "Welcome Back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to continue",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "I am a...", fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoleSelectionCard(
                    text = "Tutor",
                    selected = selectedRole == "Tutor",
                    onClick = {
                        selectedRole = "Tutor"
                        navController.navigate("tutor_login")
                    },
                    modifier = Modifier.weight(1f)
                )
                RoleSelectionCard(
                    text = "Student",
                    selected = isStudentRoleSelected,
                    onClick = { selectedRole = "Student" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "Email Icon") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = isStudentRoleSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = "Lock Icon") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = isStudentRoleSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    navController.navigate("student_dash")
                                } else {
                                    Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isStudentRoleSelected && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text(text = "Log In", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "OR", color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { 
                    isLoading = true
                    launcher.launch(googleSignInClient.signInIntent) 
                },
                enabled = isStudentRoleSelected && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                if (isLoading && selectedRole == "Student" && email.isEmpty()) {
                     CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sign in with Google", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Don't have an account? Sign Up")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionCard(
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

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    TutorLINKTheme {
        LoginPage(rememberNavController())
    }
}
