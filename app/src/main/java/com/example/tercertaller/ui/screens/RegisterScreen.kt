package com.example.tercertaller.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.tercertaller.R
import com.example.tercertaller.ui.components.CampoForm
import com.example.tercertaller.viewmodels.AuthViewModel
import com.example.tercertaller.viewmodels.RegisterViewModel
import com.example.tercertaller.viewmodels.UserViewModel
import java.io.File

@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    onIniciarSesion: () -> Unit = {}
) {
    val context = LocalContext.current

    val registerUiState by registerViewModel.uiState.collectAsState()
    val userUiState by userViewModel.uiState.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()

    var showImageDialog by rememberSaveable { mutableStateOf(false) }
    var cameraImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    fun createCameraUri(): Uri {
        val imagesDir = File(context.cacheDir, "profile_images").apply { mkdirs() }
        val imageFile = File(imagesDir, "profile_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            registerViewModel.onPhotoUriChange(uri)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let(registerViewModel::onPhotoUriChange)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createCameraUri()
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }


    // Mostrar toast cuando hay error
    LaunchedEffect(authUiState.showErrDialog) {
        if (authUiState.showErrDialog) {
            Toast.makeText(
                context,
                authUiState.errorMessage ?: "Unknown error",
                Toast.LENGTH_SHORT
            ).show()
            authViewModel.dismissErrorMessage()
        }
    }

    LaunchedEffect(authUiState.isAuthenticated) {
        if (authUiState.isAuthenticated) {
            userViewModel.crearUsuario(registerViewModel.getUsuario(), registerUiState.photoUri) { error ->
                if (error == null) onIniciarSesion()
            }
        }
    }

    LaunchedEffect(userUiState.showErrorMessage) {
        if (userUiState.showErrorMessage) {
            Toast.makeText(
                context,
                userUiState.errorMessage ?: "Unknown error",
                Toast.LENGTH_SHORT
            ).show()
            authViewModel.eliminarCuenta { error ->
                if (error != null) {
                    Toast.makeText(
                        context,
                        "Error al eliminar cuenta: ${error.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            userViewModel.dismissErrorMessage()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary),
                contentAlignment = Alignment.Center
            ) {
                if (registerUiState.photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(registerUiState.photoUri),
                        contentDescription = stringResource(R.string.foto_perfil),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = stringResource(R.string.descripcion_icono_app),
                        modifier = Modifier.padding(12.dp).fillMaxSize()
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.Center)
                        .clickable { showImageDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar imagen",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.bienvenido),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
            Text(
                text = stringResource(R.string.crea_cuenta),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.72f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.info_personal),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.completa_datos),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                CampoForm(
                    label = stringResource(R.string.label_nombre),
                    error = if (registerUiState.isNombreError) stringResource(R.string.error_nombre) else "",
                    value = registerUiState.nombre,
                    onValueChange = registerViewModel::onNombreChange,
                    placeholder = stringResource(R.string.placeholder_nombre),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CampoForm(
                    label = stringResource(R.string.label_email),
                    error = if (registerUiState.isEmailError) stringResource(R.string.error_email) else "",
                    value = registerUiState.email,
                    onValueChange = registerViewModel::onEmailChange,
                    placeholder = stringResource(R.string.placeholder_email),
                    keyboardType = KeyboardType.Email,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CampoForm(
                    label = stringResource(R.string.label_password),
                    value = registerUiState.password,
                    error = if (registerUiState.isPassError) stringResource(R.string.error_password) else "",
                    onValueChange = registerViewModel::onPasswordChange,
                    placeholder = stringResource(R.string.placeholder_password),
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CampoForm(
                    label = stringResource(R.string.label_telefono),
                    error = if (registerUiState.isTelefonoError) stringResource(R.string.error_telefono) else "",
                    value = registerUiState.telefono,
                    onValueChange = registerViewModel::onTelefonoChange,
                    placeholder = stringResource(R.string.placeholder_telefono),
                    keyboardType = KeyboardType.Phone,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    enabled = !registerUiState.isEmailError && !registerUiState.isPassError && !registerUiState.isNombreError && !registerUiState.isTelefonoError,
                    onClick = {
                        if (registerUiState.email.isNotEmpty() && registerUiState.password.isNotEmpty() && !registerUiState.isEmailError && !registerUiState.isPassError)
                            authViewModel.registrarUsuario(registerUiState.email, registerUiState.password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.boton_continuar),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }

    // AlertDialog para elegir origen de la imagen (solo UI, sin lógica de cámara/galería aún)
    if (showImageDialog) {
        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            title = {
                Text(text = "Seleccionar imagen")
            },
            text = {
                Text(text = "Elige una opción para establecer tu foto de perfil.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showImageDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text(text = "Abrir galería", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageDialog = false
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        val uri = createCameraUri()
                        cameraImageUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Text(text = "Tomar foto", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

}
