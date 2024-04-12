package com.example.sergiitb_pr04_maps_app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.sergiitb_pr04_maps_app.MyDrawer
import com.example.sergiitb_pr04_maps_app.Routes
import com.example.sergiitb_pr04_maps_app.viewmodel.MapViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileScreen(navController: NavController, mapViewModel: MapViewModel) {
    MyDrawer(navController = navController, mapViewModel = mapViewModel) {
        val imageUrl: String by mapViewModel.imageUrlForUser.observeAsState("")
        val loggedUser: String by mapViewModel.loggedUser.observeAsState("")
        val userName = loggedUser.split("@")[0]

        mapViewModel.getProfileImageUrlForUser()

        if (!mapViewModel.userLogged()){
            mapViewModel.signOut(context = LocalContext.current, navController)
        }

        // Mostrar la pantalla de captura de foto si showTakePhotoScreen es verdadero
        if (mapViewModel.showTakePhotoScreen) {
            Box(
                modifier = Modifier.fillMaxSize() // Esto hará que la pantalla de captura de foto ocupe toda la pantalla
            ) {
                TakePhotoScreen(
                    navigationController = navController,
                    mapViewModel = mapViewModel,
                    onPhotoCaptured = { photo ->
                        mapViewModel.modificarEditedPhoto(photo)
                        mapViewModel.modificarShowTakePhotoScreen(false)
                    }
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "User: $userName",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(200.dp)
                        .clip(CircleShape) // Clip con CircleShape
                ) {
                    GlideImage(
                        model = imageUrl,
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }

                // Botón para editar la foto del marcador
                Button(
                    onClick = {
                        mapViewModel.modificarShowTakePhotoScreen(true)
                    },
                    modifier = Modifier.fillMaxWidth(0.8f).padding(top=20.dp)
                ) {
                    Text("Edit Photo")
                }

                Button(
                    onClick = {
                        mapViewModel.updateUser()
                    },
                    modifier = Modifier.fillMaxWidth(0.8f).padding(top=5.dp)
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}
