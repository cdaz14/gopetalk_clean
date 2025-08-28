package com.example.gopetalk_clean.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gopetalk_clean.domain.usecase.GetChannelUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelUsersViewModel @Inject constructor(
    private val getChannelUsersUseCase: GetChannelUsersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChannelUsersUiState())
    val uiState: StateFlow<ChannelUsersUiState> = _uiState

    fun fetchChannelUsers(channelId: String, channelName: String) {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                channelName = channelName
            )
            try {
                val result = getChannelUsersUseCase(channelId)
                _uiState.value = _uiState.value.copy(
                    users = result,
                    userCount = result.size,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error desconocido al cargar usuarios"
                )
            }
        }
    }
}


data class ChannelUsersUiState(
    val channels: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val users: List<String> = emptyList(),
    val channelName: String? = null,
    val userCount: Int = 0,
    val errorMessage: String? = null
)


