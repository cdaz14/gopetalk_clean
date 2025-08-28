package com.example.gopetalk_clean.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gopetalk_clean.data.api.LoginRequest
import com.example.gopetalk_clean.data.api.LoginResponse
import com.example.gopetalk_clean.data.storage.SessionManager
import com.example.gopetalk_clean.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading

            val response: Response<LoginResponse> = loginUseCase(LoginRequest(email, password))

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!


                sessionManager.saveAccessToken(body.token)
                sessionManager.saveUserId(body.user_id)
                sessionManager.saveUserName(body.first_name)
                sessionManager.saveUserLastName(body.last_name)
                sessionManager.saveUserEmail(body.email)

                _loginState.value = LoginUiState.Success(body)
            } else {
                val message = when (response.code()) {
                    401 -> "Usuario o contraseña incorrectos"
                    404 -> "Usuario no encontrado"
                    else -> "Error desconocido (${response.code()})"
                }
                _loginState.value = LoginUiState.Error(message)
            }
        }
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val data: LoginResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
