package com.example.tercertaller.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tercertaller.data.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserState(
    val usuario: Usuario? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showErrorMessage: Boolean = false,
    val saveSuccess: Boolean = false
)

interface UserService{
    fun crearUsuario(usuario: Usuario, onResult: (Throwable?) -> Unit)
    fun cargarUsuario()
    fun actualizarUsuario(nombre: String, telefono: String)

    /*fun loadUserProfile()
    fun updateUserProfile(usuario: Usuario, photoUri: Uri)
    fun createUserProfile(usuario: Usuario, photoUri: Uri)
    fun deleteUserProfile()*/
}
class UserViewModel: ViewModel(), UserService{

    private val _uiState = MutableStateFlow(UserState())
    val uiState = _uiState.asStateFlow()

    val auth = Firebase.auth
    val database = Firebase.database



    override fun crearUsuario(usuario: Usuario, onResult: (Throwable?) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            _uiState.value = _uiState.value.copy(errorMessage = "Usuario no autenticado")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, showErrorMessage = false)

        database.reference
            .child("users")
            .child(uid)
            .setValue(usuario)
            .addOnFailureListener { error ->
                _uiState.value = _uiState.value.copy(isLoading = false, showErrorMessage = true, errorMessage = error.localizedMessage)
                onResult(error)
            }
            .addOnSuccessListener {
                _uiState.value = _uiState.value.copy(isLoading = false)
                onResult(null)
            }

        /*storage.reference.child("users/$uid/pf.jpg")
            .putFile(photoUri)
            .addOnFailureListener { error ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.localizedMessage)
            }
            .addOnSuccessListener{
                database.reference
                    .child("users")
                    .child(uid)
                    .setValue(usuario)
                    .addOnFailureListener { error ->
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.localizedMessage)
                    }
                    .addOnSuccessListener {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
            }*/

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
                _uiState.value = _uiState.value.copy(isLoading = false, showErrorMessage = true, errorMessage = error.localizedMessage)
            }
            .addOnSuccessListener { snapshot ->
                val usuarioBD = snapshot.getValue(Usuario::class.java)
                if (usuarioBD != null) {
                    Log.d("UserViewModel", "Usuario cargado: $usuarioBD")

                    _uiState.value = _uiState.value.copy(isLoading = false, usuario = usuarioBD)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, showErrorMessage = true, errorMessage = "No se pudo cargar el perfil del usuario")
                }
            }
    }
    override fun actualizarUsuario(nombre: String, telefono: String) {
        val uid = auth.currentUser?.uid ?: run {
            _uiState.value = _uiState.value.copy(errorMessage = "Usuario no autenticado")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, showErrorMessage = false)

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
                    saveSuccess = true
                )
            }
    }

     /*override fun loadUserProfile() {
         // Implementar carga de perfil de usuario desde Firebase Realtime Database
     }

     override fun updateUserProfile(usuario: Usuario, photoUri: Uri) {
         // Implementar actualización de perfil de usuario en Firebase Realtime Database y Storage
     }

     override fun createUserProfile(usuario: Usuario, photoUri: Uri) {
         // Implementar creación de perfil de usuario en Firebase Realtime Database y Storage
     }

     override fun deleteUserProfile() {
         // Implementar eliminación de perfil de usuario en Firebase Realtime Database y Storage
     }*/

}
