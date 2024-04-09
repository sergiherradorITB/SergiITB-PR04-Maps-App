package com.example.sergiitb_pr04_maps_app.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.sergiitb_pr04_maps_app.R
import com.example.sergiitb_pr04_maps_app.Routes
import com.example.sergiitb_pr04_maps_app.model.Categoria
import com.example.sergiitb_pr04_maps_app.model.UserPrefs
import com.example.sergiitb_pr04_maps_app.viewmodel.MapViewModel
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun LoginScreen(navController: NavController, mapViewModel: MapViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val isLoading: Boolean by mapViewModel.isLoading.observeAsState(true)
    val goToNext: Boolean by mapViewModel.goToNext.observeAsState(false)
    val emailState: String by mapViewModel.emailState.observeAsState("")
    val passwordState: String by mapViewModel.passwordState.observeAsState("")
    val showDialogPass: Boolean by mapViewModel.showDialogPass.observeAsState(false)
    val passwordProblem: Boolean by mapViewModel.passwordProblem.observeAsState(false)
    val showDialogAuth: Boolean by mapViewModel.showDialogAuth.observeAsState(false)
    val emailProblem: Boolean by mapViewModel.emailDuplicated.observeAsState(false)
    val validLogin: Boolean by mapViewModel.validLogin.observeAsState(true)
    val passwordVisibility: Boolean by mapViewModel.passwordVisibility.observeAsState(false)

    val context = LocalContext.current
    val userPrefs = UserPrefs(context)
    val storedUserData = userPrefs.getUserData.collectAsState(initial = emptyList())

    if (storedUserData.value.isNotEmpty() && storedUserData.value[0] != ""
        && storedUserData.value[1] != "" && validLogin
    ) {
        mapViewModel.modifyProcessing(false)
        mapViewModel.login(storedUserData.value[0], storedUserData.value[1])
        if (goToNext) {
            navController.navigate(Routes.MenuScreen.route)
        }
    }

    if (!isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }
        if (goToNext) {
            navController.navigate(Routes.MenuScreen.route)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.itb_transformed),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = emailState,
                onValueChange = { mapViewModel.modificarEmailState(it) },
                label = { Text(text = "Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() })
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = passwordState,
                onValueChange = { mapViewModel.modificarPasswordState(it) },
                label = { Text(text = "Password") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (!passwordVisibility) {
                        Icons.Filled.VisibilityOff
                    } else {
                        Icons.Filled.Visibility
                    }
                    IconButton(onClick = { mapViewModel.cambiarPassVisibility(!passwordVisibility) }) {
                        Icon(imageVector = image, contentDescription = "Password visibility")
                    }
                },
                visualTransformation = if (passwordVisibility) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (passwordState.length < 6) {
                        mapViewModel.modificarShowDialogPass(true)
                        mapViewModel.modificarPasswordProblem(true)
                    } else if (emailState.contains("@")) {
                        mapViewModel.login(emailState, passwordState)
                        CoroutineScope(Dispatchers.IO).launch {
                            userPrefs.saveUserData(emailState, passwordState)
                        }
                    } else {
                        mapViewModel.modificarPasswordProblem(false)
                        mapViewModel.modificarShowDialogPass(true)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Login")
            }
            Button(
                onClick = {
                    if (passwordState.length < 6) {
                        mapViewModel.modificarShowDialogPass(true)
                        mapViewModel.modificarPasswordProblem(true)
                    } else if (emailState.contains("@")) {
                        mapViewModel.register(context, emailState, passwordState)
                    } else {
                        mapViewModel.modificarPasswordProblem(false)
                        mapViewModel.modificarShowDialogPass(true)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Register")
            }
        }
        MyDialogPasswordOrEmail(
            showDialogPass,
            passwordProblem
        ) { mapViewModel.modificarShowDialogPass(false) }

        MyDialogPasswordAuth(
            showDialogAuth,
            emailProblem
        ) { mapViewModel.modificarShowDialogAuth(false) }
    }
}


@Composable
fun MyDialogPasswordOrEmail(show: Boolean, password: Boolean, onDismiss: () -> Unit) {
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                if (password) {
                    Text(text = "La contraseña debe ser mínimo de 6 caracteres")
                } else {
                    Text(text = "El email es irróneo, necesitas mínimo el @")
                }
            }
        }
    }
}

@Composable
fun MyDialogPasswordAuth(show: Boolean, emailProblem: Boolean, onDismiss: () -> Unit) {
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                if (emailProblem) {
                    Text(
                        text = "Email ya registrado!!",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Credenciales incorrectas",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}