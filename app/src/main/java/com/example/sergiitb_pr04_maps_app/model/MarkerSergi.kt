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

/*class MarkerSergi {
    var userId:String? = null
    var position: LatLng = LatLng(0.0,0.0)
    var title: String
    var snippet: String
    var category: Categoria // Referencia a la categoría
    var photo: Bitmap? = null

    constructor(
        userId:String?,
        position: LatLng,
        title: String,
        snippet: String,
        categoria: Categoria,
        photo: Bitmap?
    ) {
        this.userId = userId
        this.position = position
        this.title = title
        this.snippet = snippet
        this.category = categoria
        this.photo = photo
    }

    constructor(
        position: LatLng,
        title: String,
        snippet: String,
        categoria: Categoria,
        photo: Bitmap?
    ) {
        this.position = position
        this.title = title
        this.snippet = snippet
        this.category = categoria
        this.photo = photo
    }

    constructor(
        userId: String?,
        latitude: Double,
        longitude: Double,
        title: String,
        snippet: String,
        nombreCategoria:String,
        colorCategoria:Color,
        photo: Bitmap?
    ) {
        this.userId = userId
        this.position = LatLng(latitude, longitude)
        this.title = title
        this.snippet = snippet
        this.category = Categoria(nombreCategoria,colorCategoria)
        this.photo = photo
    }

    // modificarter para la posición
    fun modificarPosition(newPosition: LatLng) {
        position = newPosition
    }

    // pillarter para la posición
    fun pillarPosition(): LatLng? {
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
    fun pillarPhoto(): Bitmap? {
        return photo
    }
}

 */