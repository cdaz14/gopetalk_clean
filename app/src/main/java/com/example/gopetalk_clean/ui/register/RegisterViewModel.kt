package com.example.gopetalk_clean.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gopetalk_clean.data.api.RegisterRequest
import com.example.gopetalk_clean.data.api.RegisterResponse
import com.example.gopetalk_clean.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
): ViewModel() {
    private val _registerState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerState: StateFlow<RegisterUiState> = _registerState

    fun register(   first_name: String,
                    last_name: String,
                    email: String,
                    password: String,
                    confirm_password: String){
        viewModelScope.launch {
            _registerState.value = RegisterUiState.Loading

            val response: Response<RegisterResponse> = registerUseCase(
                RegisterRequest(first_name,last_name,email,password,confirm_password)
            )

            if (response.isSuccessful && response.body() != null){
                val body = response.body()!!

               _registerState.value = RegisterUiState.Success(body)
            }else {
                val message = when (response.code()){
                    400 -> "datos invalidos o incompletos"
                    409 -> "el usuario ya existe"
                    else -> "error desconocido (${response.code()})"
                }
                _registerState.value = RegisterUiState.Error(message)
            }



        }
    }
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val data: RegisterResponse) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}