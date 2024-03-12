package com.example.sergiitb_pr04_maps_app.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sergiitb_pr04_maps_app.model.Categoria
import com.example.sergiitb_pr04_maps_app.model.MarkerSergi
import com.example.sergiitb_pr04_maps_app.viewmodel.MapViewModel

@Composable
fun AddMarkerScreen(
    mapViewModel: MapViewModel,
    onCloseBottomSheet: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var snippet by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Categoria?>(null) }

    val categories: List<Categoria> by mapViewModel.categories.observeAsState(emptyList())

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
                        texto = categoria.name // Actualizar el texto al seleccionar una categoría
                    })
                }
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
                val markerToAdd = MarkerSergi(latLng, title, snippet, categoryToAdd)
                mapViewModel.addMarker(markerToAdd)
                onCloseBottomSheet()
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Add Marker")
    }
    MyDialog(show) { show = false }
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
