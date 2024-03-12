package com.example.sergiitb_pr04_maps_app.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.sergiitb_pr04_maps_app.MyDrawer
import com.example.sergiitb_pr04_maps_app.viewmodel.MapViewModel


@Composable
fun MenuScreen(mapViewModel: MapViewModel, navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        MyDrawer(navController = navController, mapViewModel = mapViewModel, content = {
            Column (Modifier.fillMaxSize().background(Color.Black)){

            }
        }
        )
    }
}