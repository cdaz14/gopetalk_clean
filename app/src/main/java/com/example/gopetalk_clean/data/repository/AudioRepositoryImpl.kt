package com.example.gopetalk_clean.data.repository

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.example.gopetalk_clean.data.api.AudioService
import com.example.gopetalk_clean.data.audio.WebSocketManager
import com.example.gopetalk_clean.domain.repository.AudioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRepositoryImpl @Inject constructor(
    private val audioService: AudioService,
    private val webSocketManager: WebSocketManager
) : AudioRepository {

    private var userTalking = false
    private var audioCollectionJob: Job? = null

    init {
        startAudioCollection()
    }

    private fun startAudioCollection() {
        audioCollectionJob = CoroutineScope(Dispatchers.IO).launch {
            webSocketManager.incomingAudio.collect { data ->
                playReceivedAudio(data)
            }
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override suspend fun startSending() {
        if (!userTalking) {
            userTalking = true
            audioService.startRecording { data ->
                webSocketManager.sendAudio(data)
            }
        }
    }

    override suspend fun stopSending() {
        userTalking = false
        audioService.stopRecording()
    }

    override fun playReceivedAudio(data: ByteArray) {
        audioService.playAudio(data)
    }

    override fun isUserTalking(): Boolean = userTalking

    fun cleanup() {
        audioCollectionJob?.cancel()
        audioCollectionJob = null
        userTalking = false
        audioService.stopRecording()
        audioService.stopAudio()
    }
}