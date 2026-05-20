package com.example.tercertaller.ui.components.main

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.example.tercertaller.R
import com.example.tercertaller.viewmodels.LocationViewModel
import com.example.tercertaller.viewmodels.MapaViewModel
import com.example.tercertaller.viewmodels.UserViewModel
import com.example.tercertaller.viewmodels.UsersViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberUpdatedMarkerState

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContenidoMapa(
    modifier: Modifier = Modifier,
    mapaViewModel: MapaViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    usersViewModel: UsersViewModel = viewModel()
) {
    val context = LocalContext.current

    val mapUiState by mapaViewModel.uiState.collectAsState()
    val locationUiState by locationViewModel.uiState.collectAsState()
    val userUiState by userViewModel.uiState.collectAsState()
    val usersUiState by usersViewModel.uiState.collectAsState()

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(userUiState.photoUri)
            .allowHardware(false)
            .build()
    )

    val state by painter.state.collectAsState()

    val imageLoaded = state is AsyncImagePainter.State.Success

    val multiplePermission = rememberMultiplePermissionsState(listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ))

    LaunchedEffect(Unit) {
        multiplePermission.launchMultiplePermissionRequest()
    }

    LaunchedEffect(multiplePermission.allPermissionsGranted) {
        if (multiplePermission.allPermissionsGranted) {
            locationViewModel.setup(context)
            locationViewModel.startLocationUpdates()
        }
    }

    LaunchedEffect(mapUiState.cameraPositionState.isMoving) {
        if (mapUiState.cameraPositionState.isMoving && mapUiState.cameraPositionState.cameraMoveStartedReason == com.google.maps.android.compose.CameraMoveStartedReason.GESTURE) {
            mapaViewModel.setCentered(false)
        }
    }

    LaunchedEffect(mapUiState.isCentered, locationUiState.route) {
        if (mapUiState.isCentered) {
            locationUiState.route.lastOrNull()?.let {
                mapaViewModel.setCameraPosition(it)
            }
        }
    }

    LaunchedEffect(locationUiState.route){
        if(userUiState.usuario?.enLinea ?: false) {
            locationUiState.route.lastOrNull()?.let { pos ->
                userViewModel.updateRecorrido(pos.latitude, pos.longitude)
            }
        }
    }

    LaunchedEffect(userUiState.usuario?.enLinea) {
        if (userUiState.usuario?.enLinea == false) {
            userViewModel.clearRecorrido()
        }
    }

    LaunchedEffect(Unit) {
        usersViewModel.fetchUsuarios()
    }

    LaunchedEffect(usersUiState.usuarios){
        Log.d("ContenidoMapa", "Usuarios actualizados: ${usersUiState.usuarios.size}")
        Log.d("ContenidoMapa", "Usuarios en línea: ${usersUiState.usuarios.values.map { it.nombre }}")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {

        if(!multiplePermission.allPermissionsGranted) {
            Text(
                text = stringResource(R.string.mapa_placeholder),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        } else {
            if (imageLoaded)
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = mapUiState.cameraPositionState,
                properties = mapUiState.mapProperties,
                uiSettings = mapUiState.mapUiSettings,
                onMapLoaded = {
                    mapaViewModel.setMapLoaded(true)
                }
            ){
                locationUiState.route.lastOrNull()?.let { last ->
                    if (userUiState.loadSuccess) {
                        MarkerComposable(
                            state = rememberUpdatedMarkerState(last),
                            anchor = Offset(0.5f, 0.5f),
                            zIndex = 1f
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (userUiState.photoUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(userUiState.photoUri)
                                                .allowHardware(false)
                                                .build()
                                        ),
                                        contentDescription = stringResource(R.string.foto_perfil),
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.AccountCircle,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                    }
                }

                val recorrido = userUiState.usuario?.recorrido
                val listaLatLng = recorrido?.values?.map { LatLng(it.latitud, it.longitud) } ?: emptyList()
                Polyline(
                    points = listaLatLng,
                    color = MaterialTheme.colorScheme.primary,
                    width = 6f,
                    zIndex = 0f
                )
            }


        }

        // Botón para centrar/recargar la vista
        if (multiplePermission.allPermissionsGranted) {
            FloatingActionButton(
                onClick = {
                    locationUiState.route.lastOrNull()?.let {
                        mapaViewModel.centrarMapa(it)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.MyLocation,
                    contentDescription = "Centrar mapa"
                )
            }
        }

    }
}
