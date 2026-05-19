package com.example.tercertaller.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tercertaller.data.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserState(
    val usuario: Usuario? = null,
    val isLoading: Boolean = false,
    val photoUri: Uri? = null,
    val errorMessage: String? = null,
    val showErrorMessage: Boolean = false,
    val saveSuccess: Boolean = false
)

interface UserService {
    fun crearUsuario(usuario: Usuario, photoUri: Uri?, onResult: (Throwable?) -> Unit)
    fun cargarUsuario()
    fun actualizarUsuario(nombre: String, telefono: String, photoUri: Uri?)
    fun cargarFoto()
}

class UserViewModel : ViewModel(), UserService {

    private val _uiState = MutableStateFlow(UserState())
    val uiState = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val database = Firebase.database
    private val storage = Firebase.storage

    override fun crearUsuario(usuario: Usuario, photoUri: Uri?, onResult: (Throwable?) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            _uiState.value = _uiState.value.copy(errorMessage = "Usuario no autenticado")
            onResult(Exception("Usuario no autenticado"))
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, showErrorMessage = false)

        fun guardarUsuario() {
            database.reference
                .child("users")
                .child(uid)
                .setValue(usuario)
                .addOnFailureListener { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showErrorMessage = true,
                        errorMessage = error.localizedMessage
                    )
                    onResult(error)
                }
                .addOnSuccessListener {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onResult(null)
                }
        }

        if (photoUri != null) {
            storage.reference.child("users/$uid/pf.jpg")
                .putFile(photoUri)
                .addOnFailureListener { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showErrorMessage = true,
                        errorMessage = error.localizedMessage
                    )
                    onResult(error)
                }
                .addOnSuccessListener {
                    guardarUsuario()
                }
        } else {
            guardarUsuario()
        }
    }

    fun dismissErrorMessage() {
        _uiState.update { it.copy(showErrorMessage = false) }
    }

    fun setSaveSuccess(saveSuccess: Boolean) {
        _uiState.update { it.copy(saveSuccess = saveSuccess) }
    }

    override fun cargarUsuario() {
        val uid = auth.currentUser?.uid ?: run {
            _uiState.value = _uiState.value.copy(errorMessage = "Usuario no autenticado")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, showErrorMessage = false)

        database.reference.child("users").child(uid).get()
            .addOnFailureListener { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showErrorMessage = true,
                    errorMessage = error.localizedMessage
                )
            }
            .addOnSuccessListener { snapshot ->
                val usuarioBD = snapshot.getValue(Usuario::class.java)
                if (usuarioBD != null) {
                    Log.d("UserViewModel", "Usuario cargado: $usuarioBD")
                    _uiState.value = _uiState.value.copy(isLoading = false, usuario = usuarioBD)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showErrorMessage = true,
                        errorMessage = "No se pudo cargar el perfil del usuario"
                    )
                }
            }
        cargarFoto()
    }

    override fun cargarFoto() {
        val uid = auth.currentUser?.uid ?: run {
            _uiState.value = _uiState.value.copy(errorMessage = "Usuario no autenticado")
            return
        }

        storage.reference.child("users/$uid/pf.jpg").downloadUrl
            .addOnFailureListener { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showErrorMessage = true,
                    errorMessage = error.localizedMessage
                )
            }
            .addOnSuccessListener { uri ->
                _uiState.value = _uiState.value.copy(isLoading = false, photoUri = uri)
            }
    }

    override fun actualizarUsuario(nombre: String, telefono: String, photoUri: Uri?) {
        val uid = auth.currentUser?.uid ?: run {
            _uiState.value = _uiState.value.copy(errorMessage = "Usuario no autenticado")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, showErrorMessage = false)

        fun guardarUsuario() {
            val actualizacion = mapOf(
                "nombre" to nombre,
                "telefono" to telefono
            )

            database.reference
                .child("users")
                .child(uid)
                .updateChildren(actualizacion)
                .addOnFailureListener { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showErrorMessage = true,
                        errorMessage = error.localizedMessage
                    )
                }
                .addOnSuccessListener {
                    val usuarioActual = _uiState.value.usuario
                    val usuarioActualizado = (usuarioActual ?: Usuario()).copy(
                        nombre = nombre,
                        telefono = telefono
                    )

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        usuario = usuarioActualizado,
                        saveSuccess = true,
                        photoUri = photoUri ?: _uiState.value.photoUri
                    )
                }
        }

        if (photoUri != null && photoUri.scheme != "http" && photoUri.scheme != "https") {
            storage.reference.child("users/$uid/pf.jpg")
                .putFile(photoUri)
                .addOnFailureListener { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showErrorMessage = true,
                        errorMessage = error.localizedMessage
                    )
                }
                .addOnSuccessListener {
                    guardarUsuario()
                }
        } else {
            guardarUsuario()
        }

    }

}
