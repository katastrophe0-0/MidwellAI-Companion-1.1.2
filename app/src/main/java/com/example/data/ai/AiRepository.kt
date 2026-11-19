package com.example.data.ai

import android.graphics.Bitmap
import com.example.data.model.ChatMessage
import com.example.data.model.CompatibilityData
import com.example.data.model.HoroscopeData
import com.example.data.model.NatalChart
import com.example.data.model.ReframeResult
import com.example.data.model.ResourceSearchResult
import kotlinx.coroutines.flow.Flow

data class JournalAnalysisResult(
    val sentiment: String,
    val themes: List<String>,
    val analysis: String
)

interface AiRepository {
    val isLiveAiEnabled: Boolean

    suspend fun getDailyQuote(): String
    suspend fun generateWeeklyDigest(moodSummary: String, habitCount: Int, copingTools: String): String
    suspend fun generateJournalPrompt(): String
    suspend fun generateReflectionFollowUp(draftContent: String): String
    suspend fun analyzeJournalEntry(entry: String): JournalAnalysisResult
    fun streamChatReply(history: List<ChatMessage>, newMessage: String, userName: String?): Flow<String>
    suspend fun voiceCompanionReply(userSpeech: String, userName: String?): String
    suspend fun analyzeThought(thought: String): ReframeResult
    suspend fun generateCopingStrategy(problem: String): String
    suspend fun generateMeditationScript(topic: String, durationMinutes: Int): String
    suspend fun generateBedtimeStory(theme: String): String
    suspend fun generateAffirmation(mood: String? = null, goals: List<String> = emptyList()): String
    suspend fun generateCalmingImage(prompt: String): Bitmap
    suspend fun generateHoroscope(sign: String): HoroscopeData
    suspend fun generateAstrologyDailyIntention(zodiacSign: String, lunarPhase: String): String
    suspend fun generateNatalChart(birthDate: String, birthTime: String, birthLocation: String): NatalChart
    suspend fun generateCompatibility(sign1: String, sign2: String): CompatibilityData
    suspend fun searchResources(query: String, latitude: Double? = null, longitude: Double? = null): ResourceSearchResult
}
