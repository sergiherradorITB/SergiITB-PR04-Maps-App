package com.example.sergiitb_pr04_maps_app.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {
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

}

data class MarkerSergi(val position: LatLng, val title: String, val snippet: String)
