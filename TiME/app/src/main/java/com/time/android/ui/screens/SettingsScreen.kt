package com.time.android.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.twotone.CloudSync
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.MailOutline
import androidx.compose.material.icons.twotone.Palette
import androidx.compose.material.icons.twotone.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.time.android.datastore.SettingsDataStore
import com.time.android.ui.components.AboutTiMEModal
import com.time.android.ui.components.AnimatedClockLogo
import com.time.android.ui.components.TimeFormatOption
import com.time.android.ui.components.TimeFormatSelector
import com.time.android.ui.theme.QuietCraftAboutButton
import com.time.android.ui.theme.QuietCraftElevatedButton
import com.time.android.ui.theme.QuietCraftSettingActionButton
import com.time.android.ui.theme.QuietCraftSettingToggleButton
import com.time.android.ui.theme.QuietCraftSignOutButton
import com.time.android.ui.theme.QuietCraftSyncButton
import com.time.android.ui.theme.QuietCraftTheme
import com.time.android.ui.theme.ThemeManager
import com.time.android.ui.theme.ThemeMode
import com.time.android.ui.theme.ThemePresets
import com.time.android.ui.theme.getAccentColorName
import com.time.android.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch

//Settings Screen

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onSignOut: () -> Unit,
    viewModel: CategoryViewModel = viewModel()

) {
    val context = LocalContext.current



    //TimeFormatOption
    var showTimeFormatModal by remember { mutableStateOf(false) }
    var selectedFormat by rememberSaveable { mutableStateOf(TimeFormatOption.HOURS_MINUTES) }


    //Theme manager

    var isThemeModalOpen by remember { mutableStateOf(false) }

    var showAboutTiMEModal by remember { mutableStateOf(false) }


    var isCategoryModalOpen by remember { mutableStateOf(false) }
    var autoMoveMissed by rememberSaveable { mutableStateOf(false) }

    val user = Firebase.auth.currentUser
    var currentUser by remember { mutableStateOf(user) }
    var userName by remember { mutableStateOf<String?>(null) }

    val dataStore = remember { SettingsDataStore(context) }
    val autoSyncFlow = dataStore.autoSyncFlow.collectAsState(initial = true)
    var autoSyncEnabled by remember { mutableStateOf(autoSyncFlow.value) }



    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    LaunchedEffect(currentUser) {
        currentUser?.let { u ->
            Firebase.firestore.collection("users")
                .document(u.uid)
                .collection("profile")
                .document("main")
                .get()
                .addOnSuccessListener { doc ->
                    userName = doc.getString("name")
                }
        }
    }


    LaunchedEffect(autoSyncEnabled) {
        dataStore.setAutoSync(autoSyncEnabled)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (currentUser == null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                AuthBlock(
                    onLoginSuccess = { currentUser = it },
                    onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Text(
                text = "Hello, ${userName ?: "User"}!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 32.sp
                )
            )

            // Settings Toggles Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                QuietCraftTheme.QuietCraftSettingToggleButton(
                    icon = Icons.Outlined.Tune,
                    title = "Auto-Move Missed Tasks",
                    checked = autoMoveMissed,
                    onToggle = { autoMoveMissed = it }
                )

                QuietCraftTheme.QuietCraftSettingToggleButton(
                    icon = Icons.TwoTone.CloudSync,
                    title = "Auto-Sync to Cloud",
                    checked = autoSyncEnabled,
                    onToggle = { autoSyncEnabled = it }
                )




                // Theme Button
                val accentColor by ThemeManager.accentColorFlow(context)
                    .collectAsState(initial = Color(0xFF7C4DFF))
                val currentAccentName = getAccentColorName(accentColor)

                QuietCraftTheme.QuietCraftSettingActionButton(
                    icon = Icons.TwoTone.Palette,
                    title = "Current Theme",
                    subtitle = currentAccentName,
                    trailingPreview = {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                        )
                    },
                    onClick = { isThemeModalOpen = true }
                )


                // Time Format Button
                QuietCraftTheme.QuietCraftSettingActionButton(
                    icon = Icons.TwoTone.Schedule,
                    title = "Time Format",
                    subtitle = selectedFormat.label,
                    trailingPreview = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    onClick = { showTimeFormatModal = true }
                )


                // Categories Button

                QuietCraftTheme.QuietCraftElevatedButton(
                    label = "Manage Categories",
                    icon = Icons.Default.Category,
                    onClick = { isCategoryModalOpen = true }
                )


                // Sync Button
                QuietCraftTheme.QuietCraftSyncButton(
                    onClick = { /* Sync Now */ }
                )


                // About Button
                QuietCraftTheme.QuietCraftAboutButton(
                    onClick = { showAboutTiMEModal = true }
                )


                // Sign Out Button
                if (currentUser != null) {
                    QuietCraftTheme.QuietCraftSignOutButton(
                        onClick = {
                            Firebase.auth.signOut()
                            currentUser = null
                            onSignOut()
                        }
                    )
                }
            }

            // Footer Section
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth()
            ) {
                AnimatedClockLogo(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Crafted with",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Cursive,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    Icon(
                        imageVector = Icons.TwoTone.Favorite,
                        contentDescription = "Heart",
                        tint = Color.Red.copy(alpha = 0.8f),
                        modifier = Modifier
                            .size(28.dp)
                            .padding(horizontal = 2.dp)
                    )

                    Text(
                        text = "by",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Cursive,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                    )

                    Text(
                        text = "TiME",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Modals
        if (isThemeModalOpen) {
            ModalBottomSheet(
                onDismissRequest = { isThemeModalOpen = false },
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                tonalElevation = 8.dp
            ) {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                val selectedThemeMode by ThemeManager.themeModeFlow(context)
                    .collectAsState(initial = ThemeMode.System)
                val selectedAccentColor by ThemeManager.accentColorFlow(context)
                    .collectAsState(initial = Color(0xFF7C4DFF))

                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "Choose Theme Mode",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ThemeMode.entries.forEach { mode ->
                            FilterChip(
                                selected = selectedThemeMode == mode,
                                onClick = {
                                    scope.launch {
                                        ThemeManager.setThemeMode(context, mode)
                                    }
                                },
                                label = { Text(mode.name) }
                            )
                        }
                    }

                    Text(
                        text = "Accent Color",
                        style = MaterialTheme.typography.titleMedium
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val selectedColor =
                            remember(selectedAccentColor) { mutableStateOf(selectedAccentColor) }

                        ThemePresets.forEach { (_, color) ->
                            val isSelected = color == selectedColor.value

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onBackground
                                        else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedColor.value = color
                                        scope.launch {
                                            ThemeManager.setAccentColor(context, color)
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }

        if (isCategoryModalOpen) {
            CategoryManagerScreen(
                isModalOpen = isCategoryModalOpen,
                onDismiss = { isCategoryModalOpen = false }
            )
        }

        if (showTimeFormatModal) {
            ModalBottomSheet(
                onDismissRequest = { showTimeFormatModal = false },
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                tonalElevation = 8.dp
            ) {
                TimeFormatSelector(
                    selected = selectedFormat,
                    onSelect = {
                        selectedFormat = it
                        showTimeFormatModal = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }



        if (showAboutTiMEModal) {
            AboutTiMEModal(onDismiss = { showAboutTiMEModal = false })
        }
    }


}




// Authentication Block
@Composable
fun AuthBlock(
    onLoginSuccess: (FirebaseUser) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }

    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var lastUnverifiedUser by remember { mutableStateOf<FirebaseUser?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Show Snackbar
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Column(modifier = modifier) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth(),
            snackbar = {
                Snackbar(
                    shape = RoundedCornerShape(12.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Text(it.visuals.message)
                }
            }
        )

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                if (isSignUp) "Create Account" else "Sign In",
                style = MaterialTheme.typography.titleMedium
            )

            if (isSignUp) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Button(
                onClick = {
                    val auth = Firebase.auth
                    isLoading = true
                    if (isSignUp) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { result ->
                                val user = result.user!!
                                val profile = mapOf(
                                    "email" to user.email,
                                    "name" to name,
                                    "createdAt" to FieldValue.serverTimestamp()
                                )
                                Firebase.firestore.collection("users")
                                    .document(user.uid)
                                    .collection("profile")
                                    .document("main")
                                    .set(profile)

                                user.sendEmailVerification()
                                snackbarMessage = "Verification email sent to ${user.email}. Please verify before logging in."
                                Firebase.auth.signOut()
                            }
                            .addOnFailureListener {
                                snackbarMessage = "Sign up failed: ${it.message}"
                            }
                            .addOnCompleteListener { isLoading = false }
                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                val user = it.user!!
                                if (user.isEmailVerified) {
                                    onLoginSuccess(user)
                                } else {
                                    user.sendEmailVerification()
                                    snackbarMessage = "Email not verified. Verification link sent again to ${user.email}."
                                    lastUnverifiedUser = user
                                    Firebase.auth.signOut()
                                }
                            }
                            .addOnFailureListener {
                                snackbarMessage = "Login failed: ${it.message}"
                            }
                            .addOnCompleteListener { isLoading = false }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(if (isSignUp) "Sign Up" else "Sign In")
            }

            TextButton(
                onClick = { isSignUp = !isSignUp },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (isSignUp) "Already have an account?" else "Create an account")
            }

            if (lastUnverifiedUser != null) {
                TextButton(
                    onClick = {
                        lastUnverifiedUser?.sendEmailVerification()
                        snackbarMessage = "Verification email resent to ${lastUnverifiedUser?.email}."
                    },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(Icons.TwoTone.MailOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Resend Verification Email", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}
