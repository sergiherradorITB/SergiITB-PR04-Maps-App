package com.example.sergiitb_pr04_maps_app.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng

class MarkerSergi {
    val position: LatLng
    val title: String
    val snippet: String
    var category: Categoria // Referencia a la categoría

    constructor(position: LatLng, title: String, snippet: String) {
        this.position = position
        this.title = title
        this.snippet = snippet
        this.category = Categoria("Defaul", Icons.Filled.Person, Color.White)
    }

    constructor(position: LatLng, title: String, snippet: String, categoria: Categoria) : this(
        position,
        title,
        snippet
    ) {
        this.category = categoria
    }

}