package com.example.tercertaller.viewmodels

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
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showErrorMessage: Boolean = false
)

interface UserService{
    fun createUserProfile(usuario: Usuario, onResult: (Throwable?) -> Unit)

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
    val storage = Firebase.storage



    override fun createUserProfile(usuario: Usuario, onResult: (Throwable?) -> Unit) {
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

}
