package com.example.tercertaller.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import com.example.tercertaller.data.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterUiState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val telefono: String = "",
    var isEmailError: Boolean = false,
    var isPassError: Boolean = false,
    var isNombreError: Boolean = false,
    var isTelefonoError: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val currentUser: FirebaseUser? = null,
    var showErrDialog: Boolean = false
)
interface AccountService {
    fun register(email: String, password: String, onResult: (Throwable?) -> Unit)
    /*fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun register(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun forgotPassword(email: String, onResult: (Throwable?) -> Unit)
    fun signOut()*/
}

class RegisterViewModel : ViewModel(), AccountService {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    val auth = Firebase.auth

    init {
        val currentUser = auth.currentUser
        _uiState.value = _uiState.value.copy(
            isAuthenticated = currentUser != null,
            currentUser = currentUser
        )
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value) }
        if (value.isBlank()) {
            _uiState.update { it.copy(isNombreError = true) }
        } else {
            _uiState.update { it.copy(isNombreError = false) }

        }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
        if (value.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            _uiState.update { it.copy(isEmailError = true) }
        } else {
            _uiState.update { it.copy(isEmailError = false) }
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
        if (value.isEmpty() || value.length < 6) {
            _uiState.update { it.copy(isPassError = true) }
        } else {
            _uiState.update { it.copy(isPassError = false) }
        }
    }

    fun onTelefonoChange(value: String) {
        _uiState.update { it.copy(telefono = value) }
        if (value.isBlank() || !value.matches(Regex("^[+]?[0-9]{10,15}\$"))) {
            _uiState.update { it.copy(isTelefonoError = true) }
        } else {
            _uiState.update { it.copy(isTelefonoError = false) }
        }
    }

    fun registrarUsuario(onResult: (Throwable?) -> Unit) {
        val state = uiState.value
        register(state.email, state.password) { error ->
            if (error != null) {
                _uiState.update { it.copy(showErrDialog = true) }
                onResult(error)
            } else {
                onResult(null)
            }
        }
        val nuevoUsuario = Usuario(
            nombre = state.nombre,
            email = state.email,
            password = state.password,
            telefono = state.telefono,
            enLinea = false,
            ubicacion = Pair(0.0, 0.0)
        )

        println("Usuario registrado: $nuevoUsuario")
    }

    fun dismissErrorDialog() {
        _uiState.update { it.copy(showErrDialog = false) }
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

}