package com.example.sergiitb_pr04_maps_app.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.sergiitb_pr04_maps_app.MyDrawer
import com.example.sergiitb_pr04_maps_app.Routes
import com.example.sergiitb_pr04_maps_app.model.Categoria
import com.example.sergiitb_pr04_maps_app.model.MarkerSergi
import com.example.sergiitb_pr04_maps_app.viewmodel.MapViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListMarkersScreen(navController: NavController, mapViewModel: MapViewModel) {
    val lazyGridState = rememberLazyGridState()
    val categories: List<Categoria> by mapViewModel.categories.observeAsState(emptyList())
    val marcadores by mapViewModel.markers.observeAsState(emptyList())
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val showBottomSheet by mapViewModel.showBottomSheet.observeAsState(false)
    val texto: String by mapViewModel.textoDropdown.observeAsState("Mostrar Todos")

    val isLoading: Boolean by mapViewModel.isLoadingMarkers.observeAsState(initial = true)
    mapViewModel.pillarTodosMarkers()

    if (!mapViewModel.userLogged()) {
        mapViewModel.signOut(context = LocalContext.current, navController)
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
    } else {
        MyDrawer(navController = navController, mapViewModel = mapViewModel) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // DropdownMenu para las opciones de filtrado
                    Box {
                        OutlinedTextField(
                            value = texto,
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
                            // Opción para mostrar todos los marcadores
                            DropdownMenuItem(text = {
                                Text(text = "Mostrar Todos")
                            }, onClick = {
                                // mapViewModel.setSelectedCategory(null) // Establecer la categoría seleccionada como nula
                                mapViewModel.modifyExpandedMapa(false)
                                mapViewModel.pillarTodosMarkers()
                                mapViewModel.modificarTextoDropdown("Mostrar Todos")
                            })

                            // Opciones para las categorías
                            categories.forEach { categoria ->
                                DropdownMenuItem(text = { Text(text = categoria.name) }, onClick = {
                                    mapViewModel.pillarTodosMarkersCategoria(categoria.name)
                                    mapViewModel.modifyExpandedMapa(false)
                                    mapViewModel.modificarTextoDropdown(categoria.name) // Actualizar el texto al seleccionar una categoría
                                })
                            }
                        }
                    }

                    if (marcadores.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Oops, parece que \nestá vacío...", fontSize = 33.sp)
                            Text(text = "Puedes agregar uno ahora mismo haciendo click en el botón")
                            Button(
                                onClick = {
                                    mapViewModel.modificarShowBottomSheet(true)
                                },
                                modifier = Modifier
                                    .padding(top = 16.dp, bottom = 33.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Add, contentDescription = null) // Icono
                                }
                            }
                        }
                    } else {

                        // LazyVerticalGrid para los marcadores

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = lazyGridState,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(marcadores) { marker ->
                                LocationItem(marker, navController, mapViewModel)
                            }
                        }
                    }
                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                mapViewModel.modificarShowBottomSheet(false)
                            },
                            sheetState = sheetState
                        ) {
                            resetearParametros(mapViewModel)
                            AddMarkerScreen(
                                mapViewModel = mapViewModel,
                                navController,
                                onCloseBottomSheet = {
                                    scope.launch { sheetState.hide() }
                                        .invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                mapViewModel.modificarShowBottomSheet(false)
                                            }
                                        }
                                }
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        mapViewModel.modificarShowBottomSheet(true)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 33.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Add, contentDescription = null) // Icono
                    }
                }

            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LocationItem(
    marker: MarkerSergi,
    navController: NavController,
    mapViewModel: MapViewModel
) {
    Card(
        border = BorderStroke(
            2.dp,
            when (marker.category.name) {
                "Info" -> Color.Green
                "Likes" -> Color.Yellow
                "Favoritos" -> Color.Cyan
                else -> Color.Black
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .height(200.dp) // Especifica una altura fija para todas las cajas
    ) {
        Box(
            modifier = Modifier.fillMaxSize() // Modificador para que el contenido ocupe todo el espacio en la caja
        ) {
            if (marker.photoReference == ""){
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

            } else {
                GlideImage(
                    model = marker.photoReference,
                    contentDescription = "Image from Storage",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.33f)

                )
            }


            IconButton(
                onClick = { /*mapViewModel.removeMarker(marker)*/
                    mapViewModel.setEditingMarkers(marker)
                    mapViewModel.modificarEditedPhoto(null)
                    navController.navigate(Routes.EditMarker.route)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp) // Añade un poco de espacio alrededor del botón
            ) {
                Icon(
                    Icons.Filled.EditLocationAlt,
                    contentDescription = "Edit",
                    tint = Color.Black // Cambia el color de la X si lo necesitas
                )
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            ) {
                Text(
                    text = marker.title,
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontSize = 18.sp, // Tamaño de fuente más grande
                    fontWeight = FontWeight.Bold, // Texto remarcado
                    textAlign = TextAlign.Center // Alineación central
                )
                Text(text = marker.category.name)
            }
        }
    }
}