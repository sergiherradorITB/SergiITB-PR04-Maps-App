package com.example.sergiitb_pr04_maps_app.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sergiitb_pr04_maps_app.MyDrawer
import com.example.sergiitb_pr04_maps_app.R
import com.example.sergiitb_pr04_maps_app.Routes
import com.example.sergiitb_pr04_maps_app.viewmodel.MapViewModel
import com.example.sergiitb_pr04_maps_app.viewmodel.MarkerSergi

@Composable
fun ListMarkersScreen(navController:NavController ,mapViewModel: MapViewModel) {
    val lazyGridState = rememberLazyGridState()
    val marcadores: List<MarkerSergi> by mapViewModel.markers.observeAsState(emptyList())

    MyDrawer(navController = navController, mapViewModel = mapViewModel) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = lazyGridState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {

            items(marcadores) { marker ->
                locationItem(marker, navController,mapViewModel)
            }
        }
    }

}

@Composable
fun locationItem(
    marker: MarkerSergi,
    navController: NavController,
    mapViewModel: MapViewModel
) {
    Card(
        border = BorderStroke(2.dp, Color.LightGray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.itb),
                contentDescription = "itb-defaultLogo",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.4f) // Ajusta la transparencia de la imagen
                    .clickable { mapViewModel.changePosition(marker.position)
                    navController.navigate(Routes.MapScreen.route)}
            )

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

                // Botón para eliminar el marcador
                Button(
                    onClick = { mapViewModel.removeMarker(marker) },
                    modifier = Modifier.align(Alignment.End) // Coloca el botón al final del Column
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}
