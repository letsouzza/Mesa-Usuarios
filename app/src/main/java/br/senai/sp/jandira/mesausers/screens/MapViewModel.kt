package br.senai.sp.jandira.mesausers.screens

import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private var _fusedLocationClient: FusedLocationProviderClient? = null
    var currentLocation: Location? = null
        private set

    var state by mutableStateOf(MapState())
        private set

    init {
        state = state.copy(
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                compassEnabled = true,
                myLocationButtonEnabled = false,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true,
                rotationGesturesEnabled = true,
                tiltGesturesEnabled = true
            ),
            markers = listOf(
                MapMarker(
                    position = LatLng(-23.5505, -46.6333), // São Paulo
                    title = "Restaurante Bom Sabor",
                    snippet = "Aberto até 22h"
                ),
                MapMarker(
                    position = LatLng(-23.5534, -46.6316), // Near São Paulo
                    title = "Mercado do Zé",
                    snippet = "Alimentos não perecíveis"
                ),
                MapMarker(
                    position = LatLng(-23.5478, -46.6352), // Near São Paulo
                    title = "Padaria Pão Quentinho",
                    snippet = "Pães e doces"
                )
            )
        )
    }

    fun initializeLocationClient(context: Context) {
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        getLastKnownLocation()
    }

    fun getLastKnownLocation() {
        viewModelScope.launch {
            try {
                _fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
                    location?.let {
                        currentLocation = it
                        // You can update the camera position here if needed
                    }
                }
            } catch (e: SecurityException) {
                Log.e("MapViewModel", "Error getting location: ${e.message}")
            }
        }
    }

    fun checkLocationPermission(context: Context): Boolean {
        return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED ||
                context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}

data class MapState(
    val properties: MapProperties = MapProperties(),
    val uiSettings: MapUiSettings = MapUiSettings(),
    val markers: List<MapMarker> = emptyList()
)

data class MapMarker(
    val position: LatLng,
    val title: String = "",
    val snippet: String = ""
)
