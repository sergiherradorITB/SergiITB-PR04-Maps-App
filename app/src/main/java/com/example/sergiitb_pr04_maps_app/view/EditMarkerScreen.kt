package com.example.sergiitb_pr04_maps_app.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.sergiitb_pr04_maps_app.viewmodel.MapViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.example.sergiitb_pr04_maps_app.MyDrawer
import com.example.sergiitb_pr04_maps_app.Routes


@Composable
fun EditMarkerScreen(navigationController: NavHostController, mapViewModel: MapViewModel) {
    val marker by mapViewModel.editingMarkers.observeAsState()
    var editedTitle by remember { mutableStateOf(marker?.title ?: "") }
    var editedSnippet by remember { mutableStateOf(marker?.snippet ?: "") }
    var editedPhoto by remember { mutableStateOf(marker?.photo) }
    var showTakePhotoScreen by remember { mutableStateOf(false) }

    MyDrawer(navController = navigationController, mapViewModel = mapViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Mostrar la imagen del marcador
            editedPhoto?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para editar la foto del marcador
            Button(
                onClick = {
                    showTakePhotoScreen = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Photo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto para editar el título
            OutlinedTextField(
                value = editedTitle,
                onValueChange = { editedTitle = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto para editar el fragmento
            OutlinedTextField(
                value = editedSnippet,
                onValueChange = { editedSnippet = it },
                label = { Text("Snippet") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para guardar los cambios
            Button(
                onClick = {
                    marker?.apply {
                        modificarTitle(editedTitle)
                        modificarSnippet(editedSnippet)
                        editedPhoto?.let { modificarPhoto(it) }
                    }
                    navigationController.navigate(Routes.ListMarkersScreen.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
            Button(
                onClick = {
                    navigationController.navigate(Routes.ListMarkersScreen.route)
                    marker?.let { mapViewModel.removeMarker(it) }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Borrar")
            }
        }

        // Mostrar la pantalla de captura de foto si showTakePhotoScreen es verdadero
        if (showTakePhotoScreen) {
            TakePhotoScreen(
                navigationController = navigationController,
                mapViewModel = mapViewModel,
                onPhotoCaptured = { photo ->
                    // Aquí puedes manejar la foto capturada, por ejemplo, actualizar el estado o realizar otras acciones necesarias
                    editedPhoto = photo
                    showTakePhotoScreen = false
                }
            )
        }

    }
}