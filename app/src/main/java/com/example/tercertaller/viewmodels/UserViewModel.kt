package com.example.tercertaller.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tercertaller.data.Ubicacion
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
    val saveSuccess: Boolean = false,
    val loadSuccess: Boolean = false
)

interface UserService {
    fun crearUsuario(usuario: Usuario, photoUri: Uri?, onResult: (Throwable?) -> Unit)
    fun cargarUsuario()
    fun actualizarUsuario(nombre: String, telefono: String, photoUri: Uri?)
    fun cargarFoto()
    fun clear()
    fun updateEnLinea(enLinea: Boolean)
    fun updatePosicion(latitud: Double, longitud: Double)
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

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, showErrorMessage = false, saveSuccess = false, loadSuccess = false)

        database.reference.child("users").child(uid).get()
            .addOnFailureListener { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showErrorMessage = true,
                    errorMessage = error.localizedMessage,
                    loadSuccess = false
                )
            }
            .addOnSuccessListener { snapshot ->
                val usuarioBD = snapshot.getValue(Usuario::class.java)
                if (usuarioBD != null) {
                    Log.d("UserViewModel", "Usuario cargado: $usuarioBD")
                    _uiState.value = _uiState.value.copy(isLoading = false, usuario = usuarioBD, loadSuccess = false)
                    cargarFoto()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showErrorMessage = true,
                        errorMessage = "No se pudo cargar el perfil del usuario",
                        loadSuccess = false
                    )
                }
                cargarFoto()
            }

    }

    override fun cargarFoto() {
        val uid = auth.currentUser?.uid ?: run {
            _uiState.value = _uiState.value.copy(errorMessage = "Usuario no autenticado", loadSuccess = true)
            return
        }

        Log.d("UserViewModel", "cargarFoto() iniciando para uid: $uid")
        storage.reference.child("users/$uid/pf.jpg").downloadUrl
            .addOnFailureListener { error ->
                Log.d("UserViewModel", "Error al cargar foto: ${error.localizedMessage}")
                // Si no hay foto, igualmente marca como loadSuccess = true para permitir navegar
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    photoUri = null,
                    loadSuccess = true
                )
            }
            .addOnSuccessListener { uri ->
                Log.d("UserViewModel", "Foto cargada exitosamente: $uri")
                // Establecer la URI primero
                _uiState.value = _uiState.value.copy(isLoading = false, photoUri = uri)

                // Luego marcar como completado
                _uiState.value = _uiState.value.copy(loadSuccess = true)
                Log.d("UserViewModel", "loadSuccess establecido a true con photoUri: $uri")
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

    override fun clear() {
        _uiState.value = UserState()
    }

    fun onPhotoUriChange(uri: Uri?) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    override fun updateEnLinea(enLinea: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        _uiState.update {
            val usuarioActual = it.usuario
            val usuarioActualizado = (usuarioActual ?: Usuario()).copy(enLinea = enLinea)
            it.copy(usuario = usuarioActualizado)
        }
        database.reference.child("users").child(uid).child("enLinea").setValue(enLinea)
    }

    override fun updatePosicion(latitud: Double, longitud: Double) {
        val uid = auth.currentUser?.uid ?: return
        _uiState.update {
            val usuarioActual = it.usuario
            val ubicacion = Ubicacion(latitud, longitud)
            val usuarioActualizado = (usuarioActual ?: Usuario()).copy(ubicacion = ubicacion)
            it.copy(usuario = usuarioActualizado)
        }
        val posicion = mapOf(
            "latitud" to latitud,
            "longitud" to longitud
        )
        database.reference.child("users").child(uid).child("posicion").setValue(posicion)
    }
}
