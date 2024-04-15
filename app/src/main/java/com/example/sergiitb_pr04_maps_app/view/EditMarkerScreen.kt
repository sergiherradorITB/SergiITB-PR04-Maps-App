package com.example.sergiitb_pr04_maps_app.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.sergiitb_pr04_maps_app.MyDrawer
import com.example.sergiitb_pr04_maps_app.Routes
import com.example.sergiitb_pr04_maps_app.model.Categoria
import com.example.sergiitb_pr04_maps_app.model.MarkerSergi


@Composable
fun EditMarkerScreen(navigationController: NavHostController, mapViewModel: MapViewModel) {
    val marker by mapViewModel.editingMarkers.observeAsState()

    mapViewModel.modificarEditedTitle(marker!!.title)
    mapViewModel.modificarEditedSnippet(marker!!.snippet)
    mapViewModel.modificarCategoryName(marker!!.category.name)

    val textoDropdown: String by mapViewModel.textoDropdown.observeAsState("Mostrar Todos")
    val categories: List<Categoria> by mapViewModel.categories.observeAsState(emptyList())

    MyDrawer(navController = navigationController, mapViewModel = mapViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // DropdownMenu para las opciones de filtrado
            Box {
                OutlinedTextField(
                    value = textoDropdown,
                    onValueChange = { /* No permitimos cambios directos aquí */ },
                    enabled = false,
                    readOnly = true,
                    modifier = Modifier
                        .clickable { mapViewModel.modifyExpandedMapa(true) }
                        .fillMaxWidth()
                )

                DropdownMenu(
                    expanded = mapViewModel.pillarExpandedMapa(),
                    onDismissRequest = { mapViewModel.modifyExpandedMapa(false) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                ) {
                    // Opciones para las categorías
                    categories.forEach { categoria ->
                        DropdownMenuItem(text = { Text(text = categoria.name) }, onClick = {
                            mapViewModel.modificarCategoryName(categoria.name)
                            mapViewModel.modifyExpandedMapa(false)
                            mapViewModel.modificarTextoDropdown(categoria.name)
                        })
                    }
                }
            }

            // Mostrar la imagen del marcador
            mapViewModel.editedPhoto?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(333.dp)
                        .padding(top = 10.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Botón para editar la foto del marcador
            Button(
                onClick = {
                    mapViewModel.modificarShowTakePhotoScreen(true)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Photo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto para editar el título
            OutlinedTextField(
                value = mapViewModel.editedTitle,
                onValueChange = { mapViewModel.modificarEditedTitle(it) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de texto para editar el fragmento
            OutlinedTextField(
                value = mapViewModel.editedSnippet,
                onValueChange = { mapViewModel.modificarEditedSnippet(it) },
                label = { Text("Snippet") },
                modifier = Modifier.fillMaxWidth()
            )

            // Botón para guardar los cambios

            Button(
                onClick = {
                    marker?.apply {
                        modificarTitle(mapViewModel.editedTitle)
                        modificarSnippet(mapViewModel.editedSnippet)
                        if (mapViewModel.editedPhoto != null) {
                            modificarPhoto(mapViewModel.editedPhoto!!)
                        }
                        modificarCategoria(mapViewModel.editedCategoryName)
                        mapViewModel.updateMarker(this) // Pasar el marcador actual después de aplicar los cambios
                    }
                    navigationController.navigate(Routes.ListMarkersScreen.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }

            // Botón para borrar
            Button(
                onClick = {
                    mapViewModel.modificarShowDialog(true)
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Borrar")
            }

            Row {
                Text(text = "Latitud: ${marker!!.latitude.toString().take(10)}")
                Text(text = "Longitud: ${marker!!.longitude.toString().take(10)}")
            }
        }

        // Mostrar la pantalla de captura de foto si showTakePhotoScreen es verdadero
        if (mapViewModel.showTakePhotoScreen) {
            TakePhotoScreen(
                navigationController = navigationController,
                mapViewModel = mapViewModel,
                onPhotoCaptured = { photo ->
                    mapViewModel.modificarEditedPhoto(photo)
                    mapViewModel.modificarShowTakePhotoScreen(false)
                }
            )
        }
        marker?.let {
            MyDialogConfirmErase(
                navigationController,
                it,
                mapViewModel,
                mapViewModel.showDialog
            ) { mapViewModel.modificarShowDialog(false) }
        }
    }
}

@Composable
fun MyDialogConfirmErase(
    navigationController: NavController,
    marker: MarkerSergi,
    mapViewModel: MapViewModel,
    show: Boolean,
    onDismiss: () -> Unit
) {
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Estas seguro que quieres borrarlo?")
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 13.dp),
                ) {
                    Button(onClick = {
                        onDismiss()
                        navigationController.navigate(Routes.ListMarkersScreen.route)
                        marker.markerId?.let { mapViewModel.deleteMarker(it) }
                    }) {
                        Text(text = "Sí")
                    }
                    Button(onClick = {
                        onDismiss()
                    }) {
                        Text(text = "No")
                    }
                }

            }
        }
    }
}