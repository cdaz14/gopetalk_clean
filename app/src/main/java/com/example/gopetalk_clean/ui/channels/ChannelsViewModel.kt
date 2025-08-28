package com.example.gopetalk_clean.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gopetalk_clean.domain.usecase.GetChannelsUseCase
import com.example.gopetalk_clean.domain.usecase.GetChannelUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val getChannelUsersUseCase: GetChannelUsersUseCase
) : ViewModel() {

    private val _channels = MutableLiveData<List<String>>()

    private val _channelUsers = MutableLiveData<List<String>>()
    val channelUsers: LiveData<List<String>> get() = _channelUsers

    private val _currentChannel = MutableLiveData<String>()
    val currentChannel: LiveData<String> get() = _currentChannel
    private val _uiState = MutableStateFlow(ChannelUiState())

    val uiState: StateFlow<ChannelUiState> = _uiState


    fun loadChannels() {
        viewModelScope.launch {
            _channels.value = getChannelsUseCase()
        }
    }

    fun loadChannelUsers(canal: String) {
        viewModelScope.launch {
            _channelUsers.value = getChannelUsersUseCase(canal)
        }
    }

    fun fetchChannels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            try {
                val result = getChannelsUseCase()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    channels = result,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error desconocido al cargar canales"
                )
            }
        }
    }

    fun changeChannel(canal: String) {
        _currentChannel.value = canal
        loadChannelUsers(canal)
    }

    fun disconnectChannel() {
        _currentChannel.value = ""
        _channelUsers.value = emptyList()
    }
}
data class ChannelUiState(
    val isLoading: Boolean = false,
    val channels: List<String> = emptyList(),
    val errorMessage: String? = null
)
