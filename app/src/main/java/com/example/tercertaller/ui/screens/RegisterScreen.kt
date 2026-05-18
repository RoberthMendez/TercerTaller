package com.example.tercertaller.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.tercertaller.viewmodels.RegisterViewModel
import com.example.tercertaller.ui.components.CampoForm

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onIniciarSesion: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Mostrar toast cuando hay error
    LaunchedEffect(uiState.showErrDialog) {
        if (uiState.showErrDialog) {
            Toast.makeText(
                context,
                uiState.errorMessage ?: "Unknown error",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.dismissErrorDialog()
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
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.descripcion_icono_app),
                    modifier = Modifier.padding(12.dp).fillMaxSize()
                )
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
                    error = if (uiState.isNombreError) stringResource(R.string.error_nombre) else "",
                    value = uiState.nombre,
                    onValueChange = viewModel::onNombreChange,
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
                    error = if (uiState.isEmailError) stringResource(R.string.error_email) else "",
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
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
                    value = uiState.password,
                    error = if (uiState.isPassError) stringResource(R.string.error_password) else "",
                    onValueChange = viewModel::onPasswordChange,
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
                    error = if (uiState.isTelefonoError) stringResource(R.string.error_telefono) else "",
                    value = uiState.telefono,
                    onValueChange = viewModel::onTelefonoChange,
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
                    enabled = !uiState.isEmailError && !uiState.isPassError && !uiState.isNombreError && !uiState.isTelefonoError,
                    onClick = {
                        if (uiState.email.isNotEmpty() && uiState.password.isNotEmpty() && !uiState.isEmailError && !uiState.isPassError) {
                            viewModel.registrarUsuario() { error ->
                                if (error == null) {
                                    onIniciarSesion()
                                }
                            }
                        }
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

}
