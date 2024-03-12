package com.example.sergiitb_pr04_maps_app.viewmodel

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
import com.google.android.gms.maps.model.LatLng
import kotlin.math.exp

class MapViewModel : ViewModel() {
    var expanded by mutableStateOf(false)
        private set
    fun modifyExpanded(valorNuevo:Boolean){
        expanded = valorNuevo
    }

    fun pillarExpanded():Boolean{
        return expanded
    }

    private var position = LatLng(41.4534265, 2.1837151)
    fun changePosition(positionNueva:LatLng){
        position = positionNueva
    }

    fun getPosition():LatLng{
        return position
    }

    // LiveData para la lista de marcadores
    private val _markers = MutableLiveData<MutableList<MarkerSergi>>()
    val markers: LiveData<MutableList<MarkerSergi>> = _markers

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

    private val _categories = MutableLiveData<MutableList<Categoria>>()
    val categories: LiveData<MutableList<Categoria>> = _categories

    init {
        // Inicializar la lista de categorías si es necesario
        if (_categories.value == null) {
            _categories.value = mutableListOf(
                Categoria("Info", Icons.Filled.Info, Color.Green),
                Categoria("Likes", Icons.Filled.ThumbUp, Color.Yellow),
                Categoria("Favoritos", Icons.Filled.Star, Color.Cyan)
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
}