package com.example.sergiitb_pr04_maps_app

sealed class Routes(val route:String) {
    object MapScreen:Routes("mapscreen")
    object MenuScreen:Routes("menuscreen")
    object CreateMarker:Routes("createmarker")
}