package com.example.gopetalk_clean.di

import com.example.gopetalk_clean.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideConnectToChannelUseCase(webSocketManager: com.example.gopetalk_clean.data.audio.WebSocketManager): ConnectToChannelUseCase
            = ConnectToChannelUseCase(webSocketManager)

    @Provides
    @Singleton
    fun provideDisconnectFromChannelUseCase(repository: com.example.gopetalk_clean.domain.repository.ChannelRepository): DisconnectFromChannelUseCase
            = DisconnectFromChannelUseCase(repository)

    @Provides
    @Singleton
    fun provideGetChannelsUseCase(repository: com.example.gopetalk_clean.domain.repository.ChannelRepository): GetChannelsUseCase
            = GetChannelsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetChannelUsersUseCase(repository: com.example.gopetalk_clean.domain.repository.ChannelRepository): GetChannelUsersUseCase
            = GetChannelUsersUseCase(repository)

    @Provides
    @Singleton
    fun provideLoginUseCase(repository: com.example.gopetalk_clean.domain.repository.AuthRepository): LoginUseCase
            = LoginUseCase(repository)

    @Provides
    @Singleton
    fun provideLogoutUseCase(repository: com.example.gopetalk_clean.domain.repository.AuthRepository): LogoutUseCase
            = LogoutUseCase(repository)

    @Provides
    @Singleton
    fun provideRegisterUseCase(repository: com.example.gopetalk_clean.domain.repository.AuthRepository): RegisterUseCase
            = RegisterUseCase(repository)

    @Provides
    @Singleton
    fun provideSendAudioUseCase(repository: com.example.gopetalk_clean.domain.repository.AudioRepository): SendAudioUseCase
            = SendAudioUseCase(repository)

    @Provides
    @Singleton
    fun provideStopAudioUseCase(repository: com.example.gopetalk_clean.domain.repository.AudioRepository): StopAudioUseCase
            = StopAudioUseCase(repository)

    @Provides
    @Singleton
    fun providePlayAudioUseCase(repository: com.example.gopetalk_clean.domain.repository.AudioRepository): PlayAudioUseCase
            = PlayAudioUseCase(repository)
}