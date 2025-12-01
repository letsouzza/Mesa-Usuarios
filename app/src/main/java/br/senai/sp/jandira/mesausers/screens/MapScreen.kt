package br.senai.sp.jandira.mesausers.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.screens.components.BarraDeTitulo
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    navController: NavHostController? = null
) {
    val context = LocalContext.current
    
    // Initialize location client when the screen is first composed
    LaunchedEffect(Unit) {
        viewModel.initializeLocationClient(context)
    }
    
    // State for location permission
    var hasLocationPermission by remember { 
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    // State for showing permission rationale dialog
    var showPermissionRationale by remember { mutableStateOf(false) }
    
    // Launcher for requesting location permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        hasLocationPermission = fineLocationGranted || coarseLocationGranted
        
        if (!hasLocationPermission) {
            showPermissionRationale = true
        }
    }
    
    // Request location permission when composable is first launched
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    
    // Permission rationale dialog
    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Permissão de Localização Necessária") },
            text = { 
                Text("Este aplicativo precisa da permissão de localização para mostrar sua posição no mapa. Por favor, conceda a permissão nas configurações do aplicativo.") 
            },
            confirmButton = {
                TextButton(
                    onClick = { showPermissionRationale = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
    
    // Default location (São Paulo coordinates)
    val defaultLocation = LatLng(-23.5505, -46.6333)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }
    
    // Map UI settings
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = hasLocationPermission
        )
    }
    
    // Map properties
    val mapProperties = remember {
        MapProperties(
            isMyLocationEnabled = hasLocationPermission,
            mapType = MapType.NORMAL
        )
    }
    
    // Main UI
    Scaffold(
        topBar = {
        },
        bottomBar = {
            navController?.let { BarraInferior(it) }
        },
        floatingActionButton = {
            if (hasLocationPermission) {
                FloatingActionButton(
                    onClick = {
                        viewModel.currentLocation?.let { location ->
                            val newPosition = LatLng(location.latitude, location.longitude)
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(newPosition, 15f)
                        } ?: run {
                            // If location is not available, request it
                            viewModel.getLastKnownLocation()
                        }
                    },
                    modifier = Modifier.padding(bottom = 70.dp),
                    containerColor = Color(0xFF1B4227),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Minha localização"
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = viewModel.state.properties,
                uiSettings = viewModel.state.uiSettings.copy(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = true
                ),
                onMapClick = { },
                onMapLongClick = { },
                onMyLocationButtonClick = { false },
                onMyLocationClick = { },
                onPOIClick = { },
                onMapLoaded = { },
            ) {
                // Add markers from viewModel
                viewModel.state.markers.forEach { marker ->
                    Marker(
                        state = MarkerState(position = marker.position),
                        title = marker.title,
                        snippet = marker.snippet,
                        onInfoWindowClick = { },
                        onInfoWindowClose = { },
                        onInfoWindowLongClick = { },
                        onClick = { false },
                        tag = null
                    )
                }
            }
        }
    }
}
