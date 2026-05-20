package com.example.tercertaller.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.tercertaller.data.Usuario
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MapState(
    val usuarios: List<Usuario> = emptyList(),
    val lastLocation: LatLng = LatLng(0.0, 0.0),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val mapProperties: MapProperties = MapProperties(),
    val cameraPositionState: CameraPositionState = CameraPositionState(),
    val mapUiSettings: MapUiSettings = MapUiSettings(),
    val isCentered: Boolean = true,
    val mapLoaded: Boolean = false,
    val showMarker: Boolean = false,
    val photoUris: Map<String, Uri> = emptyMap(),
    val isImagesLoaded: Boolean = false,
    val loadedUris: List<Uri> = emptyList()
)
class MapaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapState())
    val uiState = _uiState.asStateFlow()

    init {
        val state = _uiState.value

        val mapPropertiesInit = MapProperties(
            isBuildingEnabled = true,
            isTrafficEnabled = false,
            mapType = MapType.NORMAL,
        )
        val mapUiSettingsInit = MapUiSettings(
            zoomGesturesEnabled = true,
            myLocationButtonEnabled = true
        )

        _uiState.value = state.copy(
            mapProperties = mapPropertiesInit,
            mapUiSettings = mapUiSettingsInit
        )
    }

    fun setCameraPosition(latLng: LatLng) {
        val state = _uiState.value
        val cameraPosition = CameraPosition.fromLatLngZoom(latLng, 15f)
        _uiState.value = state.copy(
            cameraPositionState = CameraPositionState(position = cameraPosition)
        )
    }

    fun setCentered(centered: Boolean) {
        val state = _uiState.value
        _uiState.value = state.copy(isCentered = centered)
    }

    fun setMapLoaded(loaded: Boolean) {
        val state = _uiState.value
        _uiState.value = state.copy(mapLoaded = loaded)
    }

    fun centrarMapa(latLng: LatLng) {
        setCameraPosition(latLng)
        setCentered(true)
    }

    @Suppress("unused")
    fun setShowMarker(show: Boolean) {
        val state = _uiState.value
        _uiState.value = state.copy(showMarker = show)
    }

    @Suppress("unused")
    fun addPhotoUriForUser(uid: String, uri: Uri) {
        val state = _uiState.value
        _uiState.value = state.copy(photoUris = state.photoUris + (uid to uri))
    }

    fun setImagesLoaded(loaded: Boolean) {
        val state = _uiState.value
        _uiState.value = state.copy(isImagesLoaded = loaded)
    }

    fun addLoadedUri(uri: Uri) {
        val state = _uiState.value
        if (uri !in state.loadedUris) {
            _uiState.value = state.copy(loadedUris = state.loadedUris + uri)
        }
    }

    @Suppress("unused")
    fun clearLoadedUris() {
        val state = _uiState.value
        _uiState.value = state.copy(loadedUris = emptyList())
    }

}