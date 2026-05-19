package com.example.tercertaller.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val showErrDialog: Boolean = false,
    val isRegister: Boolean = false,
    val saveSuccess: Boolean = false
)
interface AccountService {
    fun register(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun eliminarCuenta(onResult: (Throwable?) -> Unit)
    fun authenticate(email: String, password: String)

    fun singOut()
    fun getEmail() : String
    fun updatePassword(newPassword: String)
    /*fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun register(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun forgotPassword(email: String, onResult: (Throwable?) -> Unit)
    fun signOut()*/
}

class AuthViewModel: ViewModel(), AccountService {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    val auth = Firebase.auth

    init {
        val currentUser = auth.currentUser
        _uiState.value = _uiState.value.copy(
            isAuthenticated = currentUser != null,
            currentUser = currentUser
        )
    }

    fun registrarUsuario(email: String, password: String) {
        register(email, password) { error ->
            if (error != null) {
                _uiState.update {
                    it.copy(
                        showErrDialog = true,
                        errorMessage = error.localizedMessage
                    )
                }
            }
        }
    }

    fun dismissErrorMessage() {
        _uiState.update { it.copy(showErrDialog = false) }
    }

    fun setSaveSuccess(saveSuccess: Boolean) {
        _uiState.update { it.copy(saveSuccess = saveSuccess) }
    }

    override fun register(
        email: String,
        password: String,
        onResult: (Throwable?) -> Unit
    ) {
        // Modo cargando + mensaje de error null
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)


        // Función de auth de Firebase para crear un nuevo usuario con email y contraseña
        /*
        *  Si falla (OnFailureListener), se actualiza el estado para mostrar el error y se llama a onResult con la excepción.
        *  Si tiene éxito (OnSuccessListener), se obtiene el usuario actual, se actualiza el estado para indicar que la autenticación fue exitosa
        *  y se llama a onResult con null (sin error).
        * */

        auth.createUserWithEmailAndPassword(email, password).addOnFailureListener {
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = it.localizedMessage)
            onResult(it)
        }.addOnSuccessListener{
            val user = auth.currentUser
            _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true, currentUser = user)
            onResult(null)
        }
    }

    override fun eliminarCuenta(onResult: (Throwable?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            user.delete().addOnFailureListener {
                onResult(it)
            }.addOnSuccessListener {
                _uiState.value = _uiState.value.copy(isAuthenticated = false, currentUser = null)
                onResult(null)
            }
        } else {
            onResult(Exception("No hay usuario autenticado"))
        }
    }

    override fun authenticate(email: String, password: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        auth.signInWithEmailAndPassword(email, password).addOnFailureListener {
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = it.localizedMessage, showErrDialog = true)
        }.addOnSuccessListener {
            val user = auth.currentUser
            _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true, currentUser = user)
        }
    }

    override fun singOut() {
        auth.signOut()
        _uiState.value = _uiState.value.copy(isAuthenticated = false, currentUser = null)
    }

    override fun getEmail(): String {
        val user = auth.currentUser
        return user?.email ?: ""
    }

    override fun updatePassword(newPassword: String) {
        val user = auth.currentUser
        if (user != null) {
            user.updatePassword(newPassword).addOnFailureListener {
                _uiState.value = _uiState.value.copy(errorMessage = it.localizedMessage, showErrDialog = true)
            }.addOnSuccessListener {
                _uiState.value = _uiState.value.copy(saveSuccess = true)
            }
        } else {
            _uiState.value = _uiState.value.copy(errorMessage = "Usuario no autenticado", showErrDialog = true)
        }
    }

}