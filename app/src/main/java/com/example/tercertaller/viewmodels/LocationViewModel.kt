package com.example.tercertaller.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LocationState(
    val isUbicando: Boolean = false,
    val route: List<LatLng> = emptyList()
)
class LocationViewModel : ViewModel(){
    private val _uiState = MutableStateFlow(LocationState())
    val uiState = _uiState.asStateFlow()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    fun setup(context: Context){
        if (::fusedLocationClient.isInitialized) return

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context.applicationContext)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).apply {
            setMinUpdateIntervalMillis(200)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                _uiState.update { currentState ->
                    val newLastPos = LatLng(
                        locationResult.locations.last().latitude,
                        locationResult.locations.last().longitude
                    )

                    currentState.copy(
                        route = if (currentState.isUbicando) {
                            currentState.route.plus(newLastPos)
                        } else {
                            currentState.route
                        }
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        _uiState.update {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            it.copy(isUbicando = true)
        }
    }

    fun stopLocationUpdates() {
        _uiState.update {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            it.copy(isUbicando = false)
        }
    }

    fun clearRoute() {
        _uiState.update {
            it.copy(route = emptyList())
        }
    }
}