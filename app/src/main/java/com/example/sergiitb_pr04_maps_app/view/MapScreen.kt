package com.example.sergiitb_pr04_maps_app.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sergiitb_pr04_maps_app.MainActivity
import com.example.sergiitb_pr04_maps_app.MyDrawer
import com.example.sergiitb_pr04_maps_app.model.Categoria
import com.example.sergiitb_pr04_maps_app.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
    val showBottomSheet by mapViewModel.showBottomSheet.observeAsState(initial = false)
    val marcadores by mapViewModel.markers.observeAsState(emptyList())
    val texto: String by mapViewModel.textoDropdown.observeAsState("Mostrar Todos")
    val isLoading: Boolean by mapViewModel.isLoadingMarkers.observeAsState(initial = false)
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
        MyDrawer(
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
                    Box {
                        Column {
                            val categories: List<Categoria> by mapViewModel.categories.observeAsState(
                                emptyList()
                            )

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
                                            mapViewModel.modifyExpandedMapa(false)
                                            mapViewModel.pillarTodosMarkers()
                                            mapViewModel.modificarTextoDropdown("Mostrar Todos")
                                        })

                                    // Opciones para las categorías
                                    categories.forEach { categoria ->
                                        DropdownMenuItem(
                                            text = { Text(text = categoria.name) },
                                            onClick = {
                                                mapViewModel.pillarTodosMarkersCategoria(categoria.name)
                                                mapViewModel.modifyExpandedMapa(false)
                                                mapViewModel.modificarTextoDropdown(categoria.name)
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
                                        mapViewModel.modificarEditingPosition(it)
                                        mapViewModel.modificarShowBottomSheet(true)
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
                                            // tonalElevation =  BottomSheetDefaults.SheetPeekHeight,
                                            // modifier = Modifier.fillMaxSize(),
                                            onDismissRequest = {
                                                mapViewModel.modificarShowBottomSheet(false)
                                            },
                                            sheetState = sheetState
                                        ) {
                                            AddMarkerScreen(
                                                mapViewModel = mapViewModel,
                                                navController,
                                                onCloseBottomSheet = {
                                                    resetearParametros(mapViewModel)
                                                    scope.launch { sheetState.hide() }
                                                        .invokeOnCompletion {
                                                            if (!sheetState.isVisible) {
                                                                mapViewModel.modificarShowBottomSheet(
                                                                    false
                                                                )
                                                            }
                                                        }
                                                }
                                            )
                                        }
                                    }

                                    marcadores.forEach { marker ->
                                        Marker(
                                            state = MarkerState(
                                                LatLng(
                                                    marker.latitude,
                                                    marker.longitude
                                                )
                                            ), // no los muestra en la posicion D:
                                            title = marker.title,
                                            snippet = marker.snippet,
                                            icon = BitmapDescriptorFactory.defaultMarker(
                                                when (marker.category.name) {
                                                    "Favoritos" -> BitmapDescriptorFactory.HUE_CYAN
                                                    "Likes" -> BitmapDescriptorFactory.HUE_YELLOW
                                                    "Info" -> BitmapDescriptorFactory.HUE_GREEN
                                                    else -> BitmapDescriptorFactory.HUE_RED
                                                }
                                            )
                                            /* SI PONGO YO LAS FOTOS SALEN GIGANTES.fromResource(
                                            when (marker.category.name) {
                                                "Favoritos" -> R.drawable.uwu
                                                "Likes" -> R.drawable.favoritemarker
                                                "Info" -> R.drawable.informarker
                                                else -> R.drawable.uwu
                                            } */
                                        )
                                    }
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
                } else {
                    // Text(text = "Need permision")
                    PermissionDeclinedScreenMap()
                }
            })
    }
}

@Composable
fun PermissionDeclinedScreenMap() {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Permission required", fontWeight = FontWeight.Bold)
        Text(text = "This app needs access to your position")
        Button(onClick = { openAppSettings(context as Activity) }) {
            Text(text = "Accept")
        }
    }
}

fun resetearParametros(mapViewModel: MapViewModel) {
    mapViewModel.modifyTitle("")
    mapViewModel.modifySnippet("")
    mapViewModel.modifySelectedCategory(null)
    mapViewModel.modifyPhotoBitmap(null)
    mapViewModel.modifyPhotoTaken(false)
    mapViewModel.modifyShowGuapo(false)
}