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
    var position: LatLng
    var title: String
    var snippet: String
    var category: Categoria // Referencia a la categoría
    var photo:Bitmap

    constructor(position: LatLng, title: String, snippet: String, categoria: Categoria, photo:Bitmap) {
        this.position = position
        this.title = title
        this.snippet = snippet
        this.category = categoria
        this.photo = photo
    }

    // modificarter para la posición
    fun modificarPosition(newPosition: LatLng) {
        position = newPosition
    }

    // pillarter para la posición
    fun pillarPosition(): LatLng {
        return position
    }

    // modificarter para el título
    fun modificarTitle(newTitle: String) {
        title = newTitle
    }

    // pillarter para el título
    fun pillarTitle(): String {
        return title
    }

    // modificarter para el snippet
    fun modificarSnippet(newSnippet: String) {
        snippet = newSnippet
    }

    // pillarter para el snippet
    fun pillarSnippet(): String {
        return snippet
    }

    // modificarter para la categoría
    fun modificarCategory(newCategory: Categoria) {
        category = newCategory
    }

    // pillarter para la categoría
    fun pillarCategory(): Categoria {
        return category
    }

    // modificarter para la foto
    fun modificarPhoto(newPhoto: Bitmap) {
        photo = newPhoto
    }

    // pillarter para la foto
    fun pillarPhoto(): Bitmap {
        return photo
    }
}