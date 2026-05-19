package com.example.tercertaller.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tercertaller.R
import com.example.tercertaller.ui.components.CampoForm
import com.example.tercertaller.viewmodels.AuthViewModel
import com.example.tercertaller.viewmodels.EditProfileViewModel
import com.example.tercertaller.viewmodels.UserViewModel

@Composable
fun EditPerfilScreen(
    editViewModel: EditProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val current = LocalContext.current
    val userUiState by userViewModel.uiState.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()
    val editUiState by editViewModel.uiState.collectAsState()
    val isLoading = userUiState.isLoading || authUiState.isLoading
    val saveSuccess = userUiState.saveSuccess && (authUiState.saveSuccess && editUiState.isPasswordChangeEnabled || !editUiState.isPasswordChangeEnabled)

    LaunchedEffect(Unit) {
        editViewModel.setIsPasswordChangeEnabled(false)
        editViewModel.cleanPassword()
    }

    var hasInitialized by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(userUiState.usuario) {
        if (userUiState.usuario != null && !hasInitialized) {
            userUiState.usuario?.let { user ->
                editViewModel.onNombreChange(user.nombre)
                editViewModel.onTelefonoChange(user.telefono)
                hasInitialized = true
            }
        }
    }

    LaunchedEffect(authUiState.showErrDialog){
        if (authUiState.showErrDialog){
            Toast.makeText(
                current,
                authUiState.errorMessage ?: "Error desconocido",
                Toast.LENGTH_SHORT).show()
            authViewModel.dismissErrorMessage()
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
                .padding(bottom = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón de regreso alineado a la izquierda
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.descripcion_volver),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.mi_perfil),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
            Text(
                text = stringResource(id = R.string.actualiza_info),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
            )
        }

        // ── Bottom card: formulario ────────────────────────────────────────
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
                    text = stringResource(id = R.string.info_personal),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(id = R.string.modifica_campos),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                CampoForm(
                    label = stringResource(id = R.string.label_nombre),
                    error = if (editUiState.isNombreError) stringResource(id = R.string.error_nombre) else "",
                    value = editUiState.nombre,
                    onValueChange = { newValue ->
                        editViewModel.onNombreChange(newValue)
                        userViewModel.setSaveSuccess(false)
                        authViewModel.setSaveSuccess(false)
                    },
                    placeholder = stringResource(id = R.string.placeholder_nombre),
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
                    label = stringResource(id = R.string.label_email),
                    isEmailOnEdit = true,
                    value = editUiState.email,
                    onValueChange = { },
                    placeholder = stringResource(id = R.string.placeholder_email),
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
                    label = stringResource(id = R.string.label_telefono),
                    error = if (editUiState.isTelefonoError) stringResource(id = R.string.error_telefono) else "",
                    value = editUiState.telefono,
                    onValueChange = { newValue ->
                        editViewModel.onTelefonoChange(newValue)
                        userViewModel.setSaveSuccess(false)
                        authViewModel.setSaveSuccess(false)
                    },
                    placeholder = stringResource(id = R.string.placeholder_telefono),
                    keyboardType = KeyboardType.Phone,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                if(editUiState.isPasswordChangeEnabled) {
                    CampoForm(
                        label = stringResource(id = R.string.label_password),
                        error = if (editUiState.isPasswordError) stringResource(id = R.string.error_password) else "",
                        value = editUiState.password,
                        onValueChange = { newValue ->
                            editViewModel.onPasswordChange(newValue)
                            userViewModel.setSaveSuccess(false)
                            authViewModel.setSaveSuccess(false)
                        },
                        placeholder = stringResource(id = R.string.placeholder_nueva_password),
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
                } else {
                    // Botón para habilitar cambio de contraseña
                    OutlinedButton(
                        onClick = { editViewModel.setIsPasswordChangeEnabled(true) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                    ) {
                        Text(
                            text = stringResource(id = R.string.cambiar_password),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // ── Mensaje de éxito ───────────────────────────────────────
                if (saveSuccess) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.perfil_actualizado),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ── Botón guardar ──────────────────────────────────────────
                Button(
                    onClick = {
                        if (editViewModel.datosValidos()){
                            if(editUiState.isPasswordChangeEnabled)
                                authViewModel.updatePassword(editUiState.password)
                            userViewModel.actualizarUsuario(editUiState.nombre, editUiState.telefono)
                        }


                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.boton_guardar_cambios),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onNavigateBack) {
                    Text(
                        text = stringResource(id = R.string.boton_cancelar),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}



