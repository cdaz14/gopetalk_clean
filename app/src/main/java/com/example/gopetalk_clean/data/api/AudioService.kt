package com.example.gopetalk_clean.data.api

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioService @Inject constructor() {

    private var recorder: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var isRecording = false
    private var isPlaying = false

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording(onData: (ByteArray) -> Unit) {
        stopRecording() // Cleanup primero

        val bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE, CHANNEL_CONFIG, ENCODING
        ).coerceAtLeast(MIN_BUFFER_SIZE)

        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            ENCODING,
            bufferSize
        ).takeIf { it.state == AudioRecord.STATE_INITIALIZED }

        recorder ?: throw IllegalStateException("AudioRecord no inicializado")

        recorder!!.startRecording()
        isRecording = true

        Thread {
            val buffer = ByteArray(bufferSize)
            while (isRecording) {
                val read = recorder!!.read(buffer, 0, buffer.size)
                if (read > 0) onData(buffer.copyOf(read))
            }
        }.start()
    }

    fun stopRecording() {
        isRecording = false
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    fun playAudio(data: ByteArray) {
        stopAudio() // Detener primero

        val minBuffer = AudioTrack.getMinBufferSize(
            SAMPLE_RATE, CHANNEL_OUT_CONFIG, ENCODING
        ).coerceAtLeast(MIN_BUFFER_SIZE)

        audioTrack = AudioTrack.Builder()
            .setAudioFormat(AudioFormat.Builder()
                .setEncoding(ENCODING)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(CHANNEL_OUT_CONFIG)
                .build())
            .setBufferSizeInBytes(minBuffer)
            .build()
            .apply { play() }

        audioTrack?.write(data, 0, data.size)
        isPlaying = true
    }

    fun stopAudio() {
        isPlaying = false
        audioTrack?.apply {
            stop()
            release()
        }
        audioTrack = null
    }

    companion object {
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val CHANNEL_OUT_CONFIG = AudioFormat.CHANNEL_OUT_MONO
        private const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
        private const val MIN_BUFFER_SIZE = 2048
    }
}
