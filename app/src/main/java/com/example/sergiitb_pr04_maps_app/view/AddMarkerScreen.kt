package com.example.sergiitb_pr04_maps_app.view

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.sergiitb_pr04_maps_app.model.Categoria
import com.example.sergiitb_pr04_maps_app.model.MarkerSergi
import com.example.sergiitb_pr04_maps_app.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddMarkerScreen(
    mapViewModel: MapViewModel,
    onCloseBottomSheet: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var snippet by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Categoria?>(null) }
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var photoTaken by remember { mutableStateOf(false) } // Nuevo estado

    val context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            CameraController.IMAGE_CAPTURE
        }
    }

    val categories: List<Categoria> by mapViewModel.categories.observeAsState(emptyList())
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA)
    Column(Modifier.fillMaxSize()) {


        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
        if (permissionState.status.isGranted) {
            // Mostrar la vista de captura de foto si no se ha tomado ninguna foto
            if (!photoTaken) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())
                    IconButton(
                        onClick = {
                            controller.cameraSelector =
                                if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                        },
                        modifier = Modifier.offset(16.dp, 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Switch camera"
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {
                                takePhoto(context, controller) { photo ->
                                    photoBitmap = photo
                                    photoTaken = true // Actualizar el estado cuando se toma la foto
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Take Photo"
                            )
                        }
                    }
                }
            } else {
                // Mostrar la vista de agregar marcador después de tomar una foto
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = snippet,
                        onValueChange = { snippet = it },
                        label = { Text("Snippet") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    var texto by remember { mutableStateOf("Selecciona una categoría") }

                    Box(
                        modifier = Modifier
                            //.background(Color(255f / 255, 0f / 255, 238f / 255, 0.1f))
                            .fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = texto,
                            onValueChange = { /* No permitimos cambios directos aquí */ },
                            enabled = false,
                            readOnly = true,
                            modifier = Modifier
                                .clickable { mapViewModel.modifyExpanded(true) }
                                .fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = mapViewModel.pillarExpanded(),
                            onDismissRequest = { mapViewModel.modifyExpanded(false) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            categories.forEach { categoria ->
                                DropdownMenuItem(text = { Text(text = categoria.name) }, onClick = {
                                    selectedCategory = categoria
                                    mapViewModel.modifyExpanded(false)
                                    texto =
                                        categoria.name // Actualizar el texto al seleccionar una categoría
                                })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    var show by remember { mutableStateOf(false) }

                    Button(
                        onClick = {
                            if (selectedCategory == null) {
                                show = true
                            } else {
                                val categoryToAdd = selectedCategory!!
                                val latLng = mapViewModel.getPosition()
                                val photo = photoBitmap
                                val markerToAdd =
                                    photo?.let {
                                        MarkerSergi(
                                            latLng,
                                            title,
                                            snippet,
                                            categoryToAdd,
                                            it
                                        )
                                    }
                                if (markerToAdd != null) {
                                    mapViewModel.addMarker(markerToAdd)
                                }
                                onCloseBottomSheet()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Marker")
                    }
                    MyDialog(show) { show = false }
                }
            }
        } else {
            Text(text = "Peru")
        }
    }
}


@Composable
fun CameraPreview(controller: LifecycleCameraController, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(factory = {
        PreviewView(it).apply {
            this.controller = controller
            controller.bindToLifecycle(lifecycleOwner)
        }
    }, modifier = modifier)
}

private fun takePhoto(
    context: Context, controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                onPhotoTaken(image.toBitmap())
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Error taken photo", exception)
            }
        }
    )
}

@Composable
fun MyDialog(show: Boolean, onDismiss: () -> Unit) {
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Pon una categoria")
            }
        }
    }
}
