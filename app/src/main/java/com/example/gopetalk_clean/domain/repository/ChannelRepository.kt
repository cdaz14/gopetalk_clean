// domain/repository/ChannelRepository.kt
package com.example.gopetalk_clean.domain.repository

import retrofit2.Response

interface ChannelRepository {
 suspend fun getChannels(): List<String>
 suspend fun getChannelUsers(canal: String): List<String>
}
