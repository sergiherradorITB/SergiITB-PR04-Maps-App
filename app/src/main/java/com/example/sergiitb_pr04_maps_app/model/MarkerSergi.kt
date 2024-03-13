package com.example.sergiitb_pr04_maps_app.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.decodeResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.sergiitb_pr04_maps_app.R
import com.google.android.gms.maps.model.LatLng

class MarkerSergi {
    val position: LatLng
    val title: String
    val snippet: String
    var category: Categoria // Referencia a la categoría
    var photo:Bitmap


    constructor(position: LatLng, title: String, snippet: String, categoria: Categoria, photo:Bitmap) {
        this.position = position
        this.title = title
        this.snippet = snippet
        this.category = categoria
        this.photo = photo
    }
}