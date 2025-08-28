package com.example.gopetalk_clean.domain.usecase

import com.example.gopetalk_clean.data.repository.ChannelRepositoryImpl
import com.example.gopetalk_clean.domain.repository.ChannelRepository
import jakarta.inject.Inject

class DisconnectFromChannelUseCase @Inject constructor(
    private val repository: ChannelRepository
) {

}