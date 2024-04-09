package com.example.sergiitb_pr04_maps_app.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color


data class MarkerSergi(
    var markerId:String?,
    var latitude:Double,
    var longitude:Double,
    var title:String,
    var snippet:String,
    var category: Categoria,
    var photo:Bitmap?,
    var photoReference:String?
){
    constructor():this(null,0.0,0.0,"","",Categoria("",Color.Black),null,null)

    // Método para modificar el título
    fun modificarTitle(newTitle: String) {
        title = newTitle
    }

    // Método para modificar el fragmento
    fun modificarSnippet(newSnippet: String) {
        snippet = newSnippet
    }

    // Método para modificar la foto
    fun modificarPhoto(newPhoto: Bitmap) {
        photo = newPhoto
    }

    fun modificarPhotoReference(newReference: String) {
        photoReference = newReference
    }
}