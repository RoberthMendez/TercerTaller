package com.example.tercertaller.viewmodels

import androidx.lifecycle.ViewModel
import com.example.tercertaller.data.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UsersUiState(
    val usuarios: Map<String, Usuario> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
interface UsersService {
    fun fetchUsuarios()
}
class UsersViewModel: ViewModel(), UsersService {
    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val database = Firebase.database
    private var usersListener: ValueEventListener? = null

    override fun fetchUsuarios() {
        if (usersListener != null) return // Evitar duplicar el listener

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        val uid = auth.currentUser?.uid ?: return
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Crear mapa de uid -> Usuario, extrayendo uid de las claves del snapshot
                val usuariosMap = snapshot.children.mapNotNull { child ->
                    val usuarioUid = child.key ?: return@mapNotNull null
                    val usuario = child.getValue(Usuario::class.java) ?: return@mapNotNull null
                    // Excluir el usuario actual
                    if (usuarioUid == uid) null else usuarioUid to usuario
                }.toMap()

                // Filtrar solo usuarios en línea
                val usuariosEnLineaMap = usuariosMap.filterValues { it.enLinea }

                _uiState.value = _uiState.value.copy(usuarios = usuariosEnLineaMap, isLoading = false, errorMessage = null)
            }

            override fun onCancelled(error: DatabaseError) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
            }
        }
        usersListener = listener
        database.reference.child("users").addValueEventListener(listener)
    }

    override fun onCleared() {
        super.onCleared()
        // Es muy importante remover el listener para evitar fugas de memoria
        usersListener?.let {
            database.reference.child("users").removeEventListener(it)
        }
    }
}