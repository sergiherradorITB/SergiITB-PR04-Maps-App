package com.example.sergiitb_pr04_maps_app.view

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sergiitb_pr04_maps_app.MainActivity
import com.example.sergiitb_pr04_maps_app.MyDrawerWithFloatingButton
import com.example.sergiitb_pr04_maps_app.model.Categoria
import com.example.sergiitb_pr04_maps_app.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch


@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(navController: NavController, mapViewModel: MapViewModel) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    mapViewModel.setSelectedCategory(null) // Establecer la categoría seleccionada como nula
    val marcadores by mapViewModel.markers.observeAsState(emptyList())

    MyDrawerWithFloatingButton(
        navController = navController,
        mapViewModel = mapViewModel,
        content = {
            val permissionState =
                rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
            LaunchedEffect(Unit) {
                permissionState.launchPermissionRequest()
            }
            if (permissionState.status.isGranted) {
                val context = LocalContext.current
                val fusedLocationProviderClient =
                    remember { LocationServices.getFusedLocationProviderClient(context) }
                var lastKnownLocation by remember { mutableStateOf<Location?>(null) }
                var deviceLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
                val cameraPositionState =
                    rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
                    }

                val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)
                locationResult.addOnCompleteListener(context as MainActivity) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        deviceLatLng =
                            LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
                        mapViewModel.changePosition(deviceLatLng)
                    } else {
                        Log.e("Error", "Exception: %s", task.exception)
                    }
                }
                Box() {
                    Column {
                        val categories: List<Categoria> by mapViewModel.categories.observeAsState(
                            emptyList()
                        )
                        val selectedCategory by mapViewModel.selectedCategory.observeAsState(null)

                        var texto by remember { mutableStateOf("Selecciona que quieres mostrar") }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
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
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Opción para mostrar todos los marcadores
                                DropdownMenuItem(
                                    text = { Text(text = "Mostrar Todos") },
                                    onClick = {
                                        mapViewModel.setSelectedCategory(null) // Establecer la categoría seleccionada como nula
                                        mapViewModel.modifyExpandedMapa(false)
                                        texto =
                                            "Mostrar Todos" // Actualizar el texto al seleccionar la opción "Mostrar Todos"
                                    })

                                // Opciones para las categorías
                                categories.forEach { categoria ->
                                    DropdownMenuItem(
                                        text = { Text(text = categoria.name) },
                                        onClick = {
                                            mapViewModel.setSelectedCategory(categoria)
                                            mapViewModel.modifyExpandedMapa(false)
                                            texto =
                                                categoria.name // Actualizar el texto al seleccionar una categoría
                                        })
                                }
                            }
                        }


                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.Start
                        ) {
                            GoogleMap(
                                modifier = Modifier.fillMaxHeight(),
                                cameraPositionState = cameraPositionState,
                                onMapLongClick = {
                                    mapViewModel.changePosition(it)
                                    showBottomSheet = true
                                },
                                properties = MapProperties(
                                    isMyLocationEnabled = true,
                                    isIndoorEnabled = true,
                                    isBuildingEnabled = true,
                                    isTrafficEnabled = true
                                )
                            )
                            {
                                if (showBottomSheet) {
                                    ModalBottomSheet(
                                        onDismissRequest = {
                                            showBottomSheet = false
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
                                                            showBottomSheet = false
                                                        }
                                                    }
                                            }
                                        )
                                    }
                                }
                                // Filtrar marcadores por categoría seleccionada
                                val markersToShow = if (selectedCategory != null) {
                                    mapViewModel.getMarkersByCategory(selectedCategory!!) // si no es nulo filtro por categoria
                                } else {
                                    marcadores // Si es nulo cojo el valor de marcadores
                                }

                                markersToShow.forEach { marker ->
                                    Marker(
                                        state = MarkerState(marker.position),
                                        title = marker.title,
                                        snippet = marker.snippet,
                                    )
                                }
                            }

                            ExtendedFloatingActionButton(
                                text = { Text("Show bottom sheet") },
                                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                                onClick = { showBottomSheet = true },
                                modifier = Modifier.padding(16.dp)
                            )
                        }


                    }
                    Button(
                        onClick = {
                            showBottomSheet = true
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
            } else {
                Text(text = "Need permision")
            }
        })


}

fun resetearParametros(mapViewModel: MapViewModel) {
    mapViewModel.modifyTitle("")
    mapViewModel.modifySnippet("")
    mapViewModel.modifySelectedCategory(null)
    mapViewModel.modifyPhotoBitmap(null)
    mapViewModel.modifyPhotoTaken(false)
    mapViewModel.modifyShowGuapo(false)
}