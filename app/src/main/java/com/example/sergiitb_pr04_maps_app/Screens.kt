package com.example.sergiitb_pr04_maps_app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(val route: String, val title: String) {

    sealed class DrawerScreens(
        route: String,
        val icon: ImageVector,
        title: String
    ) : Screens(route, title) {
        object Home : DrawerScreens(Routes.MenuScreen.route, Icons.Filled.Home, "Home")
        object Mapa : DrawerScreens(Routes.MapScreen.route, Icons.Filled.Home, "Mapa")
        object Listar : DrawerScreens(Routes.ListMarkersScreen.route, Icons.Filled.List, "Listar marcadores")

    }
}

val screensFromDrawer = listOf(
    Screens.DrawerScreens.Home,
    Screens.DrawerScreens.Mapa,
    Screens.DrawerScreens.Listar,
)