package com.example.sergiitb_pr04_maps_app

sealed class Routes(val route:String) {
    object MapScreen:Routes("mapscreen")
    object MenuScreen:Routes("menuscreen")
    object AddMarkerScreen:Routes("addmarkerscreen")
    object ListMarkersScreen:Routes("listmarkscreen")
    object Camara:Routes("camara")

    object TakePhotoScreen:Routes("takephoto")
    object EditMarker:Routes("editmarker")
    object LogScreen:Routes("logscreen")
    object RegisterScreen:Routes("registerscreen")

    object ProfileScreen:Routes("profilescreen")


}