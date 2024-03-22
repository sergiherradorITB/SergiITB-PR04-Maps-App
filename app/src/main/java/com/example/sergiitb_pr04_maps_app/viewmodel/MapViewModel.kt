package com.example.sergiitb_pr04_maps_app.viewmodel

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sergiitb_pr04_maps_app.model.Categoria
import com.example.sergiitb_pr04_maps_app.model.MarkerSergi
import com.example.sergiitb_pr04_maps_app.model.Repository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream

class MapViewModel : ViewModel() {
    private val title = mutableStateOf("")
    private val snippet = mutableStateOf("")
    private val selectedCategoria = mutableStateOf<Categoria?>(null)
    private val photoBitmap = mutableStateOf<Bitmap?>(null)
    private val photoTaken = mutableStateOf(false)
    private val showGuapo = mutableStateOf(false)

    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPositionGranted = _cameraPermissionGranted

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied

    private val database = FirebaseFirestore.getInstance()


    // LiveData para la lista de marcadores
    private val _markers = MutableLiveData<MutableList<MarkerSergi>>()
    val markers: LiveData<MutableList<MarkerSergi>> = _markers


    fun addMarkerToDatabase(marker: MarkerSergi) {
        database.collection("markers")
            .add(
                hashMapOf(
                    "positionLatitude" to (marker.latitude),
                    "positionLongitude" to (marker.longitude),
                    "title" to marker.title,
                    "snippet" to marker.snippet,
                    // "foto" to marker.photo
                    )
            )
        println("Hola :( $marker")
    }

    var repository:Repository = Repository()

    private val _cargando = MutableLiveData(true)
    val cargando = _cargando

    fun pillarTodosMarkers(){
        repository.getMarkers().addSnapshotListener{value, error ->
            if (error != null){
                Log.e("Firestore error", error.message.toString())
                return@addSnapshotListener
            }
            val tempList = mutableListOf<MarkerSergi>()
            for (dc:DocumentChange in value?.documentChanges!!){
                if (dc.type == DocumentChange.Type.ADDED){
                    val newMarker = dc.document.toObject(MarkerSergi::class.java)
                    newMarker.userId = dc.document.id
                    tempList.add(newMarker)
                }
            }
            _markers.value = tempList
        }
        // _cargando.value = false
    }

    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun setShouldShowPermissionRationale(should: Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setShowPermissionDenied(denied: Boolean) {
        _showPermissionDenied
    }

    fun modifyTitle(newValue: String) {
        title.value = newValue
    }

    fun getTitle(): String {
        return title.value
    }

    fun modifySnippet(newValue: String) {
        snippet.value = newValue
    }

    fun getSnippet(): String {
        return snippet.value
    }

    fun modifySelectedCategory(newValue: Categoria?) {
        selectedCategoria.value = newValue
    }

    fun getSelectedCategory(): Categoria? {
        return selectedCategoria.value
    }

    fun modifyPhotoBitmap(newValue: Bitmap?) {
        photoBitmap.value = newValue
    }

    fun getPhotoBitmap(): Bitmap? {
        return photoBitmap.value
    }

    fun modifyPhotoTaken(newValue: Boolean) {
        photoTaken.value = newValue
    }

    fun getPhotoTaken(): Boolean {
        return photoTaken.value
    }

    fun modifyShowGuapo(newValue: Boolean) {
        showGuapo.value = newValue
    }

    fun getShowGuapo(): Boolean {
        return showGuapo.value
    }

    var expanded by mutableStateOf(false)
        private set

    fun modifyExpanded(valorNuevo: Boolean) {
        expanded = valorNuevo
    }

    fun pillarExpanded(): Boolean {
        return expanded
    }

    var expandedMapa by mutableStateOf(false)
        private set

    fun modifyExpandedMapa(valorNuevo: Boolean) {
        expandedMapa = valorNuevo
    }

    fun pillarExpandedMapa(): Boolean {
        return expandedMapa
    }

    private var position = LatLng(41.4534265, 2.1837151)
    fun changePosition(positionNueva: LatLng) {
        position = positionNueva
    }

    fun getPosition(): LatLng {
        return position
    }

    // Añadir marcador a la lista
    // Método para agregar un nuevo marcador
    fun addMarker(marker: MarkerSergi) {
        val currentList = _markers.value.orEmpty().toMutableList()
        currentList.add(marker)
        _markers.value = currentList
    }

    // Método para eliminar un marcador
    fun removeMarker(marker: MarkerSergi) {
        val currentList = _markers.value.orEmpty().toMutableList()
        currentList.remove(marker)
        _markers.value = currentList
    }

    // LiveData para la lista de marcadores
    private val _editingMarkers = MutableLiveData<MarkerSergi>()
    var editingMarkers: LiveData<MarkerSergi> = _editingMarkers

    fun setEditingMarkers(marker: MarkerSergi) {
        _editingMarkers.value = marker
    }

    /*fun editMarker(
        marker: MarkerSergi,
        editedTitle: String,
        editedSnippet: String,
        editedPhoto: Bitmap,
        editedCategory: Categoria
    ) {
        marker.apply {
            modificarTitle(editedTitle)
            modificarSnippet(editedSnippet)
            modificarPhoto(editedPhoto)
            modificarCategory(editedCategory)
        }
    }

     */

    private val _categories = MutableLiveData<MutableList<Categoria>>()
    val categories: LiveData<MutableList<Categoria>> = _categories

    init {
        // Inicializar la lista de categorías si es necesario
        if (_categories.value == null) {
            _categories.value = mutableListOf(
                Categoria("Info", Color.Green),
                Categoria("Likes", Color.Yellow),
                Categoria("Favoritos", Color.Cyan)
            )
        }
    }

    // Método para obtener la lista de categorías
    fun getCategories(): List<Categoria> {
        return _categories.value.orEmpty()
    }

    // Método para agregar una nueva categoría
    fun addCategory(category: Categoria) {
        val currentList = _categories.value.orEmpty().toMutableList()
        currentList.add(category)
        _categories.value = currentList
    }

    // Método para eliminar una categoría
    fun removeCategory(category: Categoria) {
        val currentList = _categories.value.orEmpty().toMutableList()
        currentList.remove(category)
        _categories.value = currentList
    }

    // LiveData para la categoría seleccionada
    private val _selectedCategory = MutableLiveData<Categoria?>()
    val selectedCategory: LiveData<Categoria?> = _selectedCategory

    // Método para establecer la categoría seleccionada
    fun setSelectedCategory(category: Categoria?) {
        _selectedCategory.value = category
    }

    // Método para obtener marcadores por categoría
    fun getMarkersByCategory(category: Categoria): List<MarkerSergi> {
        return _markers.value?.filter { it.category == category } ?: emptyList()
    }

    private var esPerModificar = false

    fun modificarEsPerModificar(boolean: Boolean) {
        esPerModificar = boolean
    }

    fun pillarEsPerModificar(): Boolean {
        return esPerModificar
    }
}
