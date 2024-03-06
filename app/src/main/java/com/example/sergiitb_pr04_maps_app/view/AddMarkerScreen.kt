package com.example.sergiitb_pr04_maps_app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sergiitb_pr04_maps_app.Routes
import com.example.sergiitb_pr04_maps_app.viewmodel.MapViewModel
import com.example.sergiitb_pr04_maps_app.viewmodel.MarkerSergi
import com.google.android.gms.maps.model.LatLng

@Composable
fun AddMarkerScreen(
    mapViewModel: MapViewModel,
    onCloseBottomSheet: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var snippet by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = title,
            onValueChange = {
                title = it
            },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = snippet,
            onValueChange = {
                snippet = it
            },
            label = { Text("Snippet") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Agregar el marcador al ViewModel
                val latLng = mapViewModel.getPosition()
                val markerToAdd = MarkerSergi(latLng, title, snippet)
                mapViewModel.addMarker(markerToAdd)
                // Navegar de regreso a la pantalla del mapa
                // navController.navigate(Routes.MapScreen.route)
                // Cerrar el BottomSheet
                onCloseBottomSheet()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Marker")
        }
    }
}


