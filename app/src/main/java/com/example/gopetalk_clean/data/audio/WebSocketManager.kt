package com.example.gopetalk_clean.data.audio

import android.util.Log
import com.example.gopetalk_clean.di.NetworkModule
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import okio.ByteString.Companion.toByteString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val client: OkHttpClient,
    @NetworkModule.WebSocketUrl private val url: String
) : WebSocketListener() {

    private var webSocket: WebSocket? = null
    private val _incomingAudio = MutableSharedFlow<ByteArray>()
    val incomingAudio: SharedFlow<ByteArray> = _incomingAudio

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job + CoroutineExceptionHandler { _, throwable ->
        Log.e("WebSocket", "Error en coroutine: ${throwable.message}")
    })

    fun connect(channel: String, userId: String, token: String) {
        disconnect()

        val request = Request.Builder()
            .url("$url?channel=$channel&userId=$userId")
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, this)
        Log.d("WebSocket", "Conectando a: $channel")
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnected")
        webSocket = null
    }

    fun sendAudio(data: ByteArray) {
        webSocket?.let { ws ->
            if (ws.send(data.toByteString())) {
                Log.d("WebSocket", "Audio enviado: ${data.size} bytes")
            }
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WebSocket", "Conexion abierta")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        scope.launch {
            _incomingAudio.emit(bytes.toByteArray())
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocket", "Texto recibido: $text")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocket", "Cerrando conexion: $code - $reason")
        webSocket.close(1000, null)
        this.webSocket = null
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocket", "WebSocket cerrado: $code - $reason")
        this.webSocket = null
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocket", "Error WebSocket: ${t.message}", t)
        this.webSocket = null
    }

    @Suppress ("UNUSED")
    fun close() {
        disconnect()
        job.cancel() // ← FORMA CORRECTA DE CANCELAR
    }
}