package com.example.data.ai

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.data.model.ChatMessage
import com.example.data.model.CompatibilityData
import com.example.data.model.GroundingSource
import com.example.data.model.HoroscopeData
import com.example.data.model.NatalChart
import com.example.data.model.ReframeResult
import com.example.data.model.ResourceSearchResult
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

private const val TAG = "FirebaseAiRepo"
private const val AI_TIMEOUT_MS = 60_000L

class FirebaseAiRepository(
    private val context: Context,
    private val offlineFallback: PlaceholderAiRepository = PlaceholderAiRepository()
) : AiRepository {

    override val isLiveAiEnabled: Boolean = true

    private val textModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "You are MindWell, a compassionate, trauma-informed, non-diagnostic wellness companion. " +
                        "Never claim to diagnose or replace medical care. If a user expresses suicidal intent or self-harm, " +
                        "immediately provide 988 Suicide & Crisis Lifeline, Crisis Text Line (HOME to 741741), and 911."
                )
            }
        )
    }

    private val imageModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-nano-banana-2.1",
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
            }
        )
    }

    private fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun isConnectivityFailure(e: Throwable): Boolean {
        return generateSequence(e) { it.cause }.any {
            it is UnknownHostException || it is IOException
        }
    }

    private suspend fun <T> runWithFallback(
        offlineBlock: suspend () -> T,
        liveBlock: suspend () -> T
    ): T {
        if (!isOnline()) return offlineBlock()
        return try {
            withTimeout(AI_TIMEOUT_MS) { liveBlock() }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (isConnectivityFailure(e) || !isOnline()) {
                Log.w(TAG, "Offline fallback triggered: ${e.message}")
                offlineBlock()
            } else {
                Log.e(TAG, "Firebase AI call failed", e)
                throw e
            }
        }
    }

    override suspend fun getDailyQuote(): String = runWithFallback(
        offlineBlock = { offlineFallback.getDailyQuote() },
        liveBlock = {
            textModel.generateContent(
                "Give one short, original, calming 1-sentence mindfulness quote (under 24 words) with no markdown."
            ).text?.trim().orEmpty().ifBlank { offlineFallback.getDailyQuote() }
        }
    )

    override suspend fun generateWeeklyDigest(
        moodSummary: String,
        habitCount: Int,
        copingTools: String
    ): String = runWithFallback(
        offlineBlock = { offlineFallback.generateWeeklyDigest(moodSummary, habitCount, copingTools) },
        liveBlock = {
            val prompt = "Write a warm, non-judgmental 3-bullet weekly wellness digest based on:\n" +
                "- 7-day mood summary: $moodSummary\n" +
                "- Habit check-ins completed: $habitCount\n" +
                "- Recent coping tools: ${copingTools.ifBlank { "breathing and journaling" }}\n" +
                "Keep under 90 words and highlight one gentle intention for tomorrow."
            textModel.generateContent(prompt).text?.trim().orEmpty()
                .ifBlank { offlineFallback.generateWeeklyDigest(moodSummary, habitCount, copingTools) }
        }
    )

    override suspend fun generateJournalPrompt(): String = runWithFallback(
        offlineBlock = { offlineFallback.generateJournalPrompt() },
        liveBlock = {
            textModel.generateContent(
                "Create one gentle, open-ended CBT/mindfulness journaling prompt (1-2 sentences)."
            ).text?.trim().orEmpty().ifBlank { offlineFallback.generateJournalPrompt() }
        }
    )

    override suspend fun generateReflectionFollowUp(draftContent: String): String = runWithFallback(
        offlineBlock = { offlineFallback.generateReflectionFollowUp(draftContent) },
        liveBlock = {
            val snippet = draftContent.trim().take(1200)
            val prompt = if (snippet.isBlank()) {
                "Give one gentle opening journaling question to help someone check in with their emotions."
            } else {
                "Based on this journal excerpt, offer 1-2 gentle, non-judgmental CBT follow-up reflection questions:\n\"$snippet\""
            }
            textModel.generateContent(prompt).text?.trim().orEmpty()
                .ifBlank { offlineFallback.generateReflectionFollowUp(draftContent) }
        }
    )

    override suspend fun analyzeJournalEntry(entry: String): JournalAnalysisResult = runWithFallback(
        offlineBlock = { offlineFallback.analyzeJournalEntry(entry) },
        liveBlock = {
            val schema = Schema.obj(
                properties = mapOf(
                    "sentiment" to Schema.string(description = "1-2 word emotional tone"),
                    "themes" to Schema.array(items = Schema.string(), description = "Up to 3 short theme labels"),
                    "analysis" to Schema.string(description = "Warm 2-3 sentence supportive CBT reflection")
                )
            )
            val structuredModel = Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                    responseSchema = schema
                }
            )
            val response = structuredModel.generateContent(
                "Reflect empathetically on this personal journal entry without diagnosing:\n${entry.take(2500)}"
            )
            val json = JSONObject(response.text ?: throw IllegalStateException("Empty analysis JSON"))
            val themesArr = json.optJSONArray("themes")
            val themes = (0 until (themesArr?.length() ?: 0)).mapNotNull {
                themesArr?.optString(it)?.takeIf { s -> s.isNotBlank() }
            }.take(3)
            JournalAnalysisResult(
                sentiment = json.optString("sentiment", "Balanced"),
                themes = themes.ifEmpty { listOf("Self-Reflection") },
                analysis = json.optString("analysis", "")
            )
        }
    )

    override fun streamChatReply(
        history: List<ChatMessage>,
        newMessage: String,
        userName: String?
    ): Flow<String> = flow {
        if (!isOnline()) {
            emitAll(offlineFallback.streamChatReply(history, newMessage, userName))
            return@flow
        }
        val chatHistory = history.takeLast(8).map { msg ->
            content(if (msg.sender == "user") "user" else "model") {
                text(msg.text.take(1500))
            }
        }
        val namePrefix = userName?.takeIf { it.isNotBlank() }?.let { "User name: $it. " } ?: ""
        val accumulated = StringBuilder()
        try {
            withTimeout(AI_TIMEOUT_MS) {
                val chat = textModel.startChat(history = chatHistory)
                chat.sendMessageStream("$namePrefix$newMessage").collect { chunk ->
                    val piece = chunk.text.orEmpty()
                    if (piece.isNotEmpty()) {
                        accumulated.append(piece)
                        emit(accumulated.toString())
                    }
                }
            }
            if (accumulated.isBlank()) {
                throw IllegalStateException("Gemini returned an empty response.")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (isConnectivityFailure(e) || !isOnline()) {
                emitAll(offlineFallback.streamChatReply(history, newMessage, userName))
            } else {
                Log.e(TAG, "Streaming chat failed", e)
                throw e
            }
        }
    }

    override suspend fun voiceCompanionReply(userSpeech: String, userName: String?): String = runWithFallback(
        offlineBlock = { offlineFallback.voiceCompanionReply(userSpeech, userName) },
        liveBlock = {
            val nameHint = userName?.takeIf { it.isNotBlank() }?.let { "Address $it warmly. " } ?: ""
            textModel.generateContent(
                "${nameHint}Respond in 2-3 calm, spoken-friendly sentences (no markdown) to what the user just said aloud: \"${userSpeech.take(800)}\""
            ).text?.trim().orEmpty().ifBlank { offlineFallback.voiceCompanionReply(userSpeech, userName) }
        }
    )

    override suspend fun analyzeThought(thought: String): ReframeResult = runWithFallback(
        offlineBlock = { offlineFallback.analyzeThought(thought) },
        liveBlock = {
            val schema = Schema.obj(
                properties = mapOf(
                    "distortion" to Schema.enumeration(
                        values = listOf(
                            "All-or-Nothing Thinking",
                            "Catastrophizing",
                            "Emotional Reasoning",
                            "Mind Reading",
                            "Fortune Telling",
                            "Personalization",
                            "Filtering",
                            "Should Statements"
                        ),
                        description = "Primary CBT cognitive distortion"
                    ),
                    "explanation" to Schema.string(description = "Brief 1-2 sentence explanation"),
                    "reframes" to Schema.array(
                        items = Schema.string(),
                        description = "3 balanced, compassionate alternative thoughts"
                    )
                )
            )
            val structuredModel = Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                    responseSchema = schema
                }
            )
            val response = structuredModel.generateContent(
                "Identify the primary CBT cognitive distortion and 3 balanced reframes for this thought:\n\"${thought.take(1500)}\""
            )
            val json = JSONObject(response.text ?: throw IllegalStateException("Empty reframe JSON"))
            val reframesArr = json.optJSONArray("reframes")
            val reframes = (0 until (reframesArr?.length() ?: 0)).mapNotNull {
                reframesArr?.optString(it)?.takeIf { s -> s.isNotBlank() }
            }
            ReframeResult(
                distortion = json.optString("distortion", "Emotional Reasoning"),
                explanation = json.optString("explanation", ""),
                reframes = reframes.ifEmpty { offlineFallback.analyzeThought(thought).reframes }
            )
        }
    )

    override suspend fun generateCopingStrategy(problem: String): String = runWithFallback(
        offlineBlock = { offlineFallback.generateCopingStrategy(problem) },
        liveBlock = {
            textModel.generateContent(
                "Create a structured, practical 3-step CBT/DBT coping strategy plan (Immediate Grounding, Short-Term Step, Self-Care/Support) for: \"${problem.take(1000)}\""
            ).text?.trim().orEmpty().ifBlank { offlineFallback.generateCopingStrategy(problem) }
        }
    )

    override suspend fun generateMeditationScript(topic: String, durationMinutes: Int): String = runWithFallback(
        offlineBlock = { offlineFallback.generateMeditationScript(topic, durationMinutes) },
        liveBlock = {
            textModel.generateContent(
                "Write a soothing, spoken-word $durationMinutes-minute guided mindfulness meditation script focused on '$topic' (~180 words, calm pacing)."
            ).text?.trim().orEmpty().ifBlank { offlineFallback.generateMeditationScript(topic, durationMinutes) }
        }
    )

    override suspend fun generateBedtimeStory(theme: String): String = runWithFallback(
        offlineBlock = { offlineFallback.generateBedtimeStory(theme) },
        liveBlock = {
            textModel.generateContent(
                "Write a gentle, peaceful bedtime sleep story (~180 words) set in: '$theme'. Slow sensory imagery, no conflict."
            ).text?.trim().orEmpty().ifBlank { offlineFallback.generateBedtimeStory(theme) }
        }
    )

    override suspend fun generateAffirmation(mood: String?, goals: List<String>): String = runWithFallback(
        offlineBlock = { offlineFallback.generateAffirmation(mood, goals) },
        liveBlock = {
            val contextHint = buildString {
                if (!mood.isNullOrBlank()) append("Current mood: $mood. ")
                if (goals.isNotEmpty()) append("Goals: ${goals.joinToString(", ")}.")
            }
            textModel.generateContent(
                "Write one grounded, first-person positive affirmation (under 18 words, no quotes). $contextHint"
            ).text?.trim()?.trim('"').orEmpty().ifBlank { offlineFallback.generateAffirmation(mood, goals) }
        }
    )

    override suspend fun generateCalmingImage(prompt: String): Bitmap {
        return withTimeout(AI_TIMEOUT_MS) {
            val response = imageModel.generateContent(
                "Serene, calming, painterly wellness artwork, soft atmospheric light, peaceful nature composition: ${prompt.take(400)}"
            )
            val imagePart = response.candidates.firstOrNull()
                ?.content?.parts?.filterIsInstance<ImagePart>()
                ?.firstOrNull()
            imagePart?.image ?: throw IllegalStateException("No image was returned by Gemini Nano Banana.")
        }
    }

    override suspend fun generateHoroscope(sign: String): HoroscopeData = runWithFallback(
        offlineBlock = { offlineFallback.generateHoroscope(sign) },
        liveBlock = {
            val schema = Schema.obj(
                properties = mapOf(
                    "mood" to Schema.string(),
                    "luckyColor" to Schema.string(),
                    "luckyNumber" to Schema.string(),
                    "love" to Schema.string(),
                    "career" to Schema.string(),
                    "wellness" to Schema.string(),
                    "quote" to Schema.string(),
                    "transits" to Schema.string()
                )
            )
            val structuredModel = Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                    responseSchema = schema
                }
            )
            val response = structuredModel.generateContent(
                "Generate a mindful, encouraging daily wellness reflection horoscope for zodiac sign $sign."
            )
            val json = JSONObject(response.text ?: throw IllegalStateException("Empty horoscope JSON"))
            HoroscopeData(
                mood = json.optString("mood", "Grounded"),
                luckyColor = json.optString("luckyColor", "Indigo"),
                luckyNumber = json.optString("luckyNumber", "7"),
                love = json.optString("love", ""),
                career = json.optString("career", ""),
                wellness = json.optString("wellness", ""),
                quote = json.optString("quote", ""),
                transits = json.optString("transits", "")
            )
        }
    )

    override suspend fun generateAstrologyDailyIntention(
        zodiacSign: String,
        lunarPhase: String
    ): String = runWithFallback(
        offlineBlock = { offlineFallback.generateAstrologyDailyIntention(zodiacSign, lunarPhase) },
        liveBlock = {
            textModel.generateContent(
                "Combine zodiac sign '$zodiacSign' and current lunar phase '$lunarPhase' into: 1 short first-person mindfulness intention and 1 gentle journaling prompt (under 55 words total)."
            ).text?.trim().orEmpty().ifBlank { offlineFallback.generateAstrologyDailyIntention(zodiacSign, lunarPhase) }
        }
    )

    override suspend fun generateNatalChart(
        birthDate: String,
        birthTime: String,
        birthLocation: String
    ): NatalChart = runWithFallback(
        offlineBlock = { offlineFallback.generateNatalChart(birthDate, birthTime, birthLocation) },
        liveBlock = {
            val schema = Schema.obj(
                properties = mapOf(
                    "sunSign" to Schema.string(),
                    "moonSign" to Schema.string(),
                    "risingSign" to Schema.string(),
                    "sunAnalysis" to Schema.string(),
                    "moonAnalysis" to Schema.string(),
                    "risingAnalysis" to Schema.string(),
                    "elementBalance" to Schema.string()
                )
            )
            val structuredModel = Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                    responseSchema = schema
                }
            )
            val response = structuredModel.generateContent(
                "Create a thoughtful wellness-oriented natal chart summary for birth date $birthDate, time $birthTime, location $birthLocation."
            )
            val json = JSONObject(response.text ?: throw IllegalStateException("Empty natal JSON"))
            NatalChart(
                sunSign = json.optString("sunSign", ""),
                moonSign = json.optString("moonSign", ""),
                risingSign = json.optString("risingSign", ""),
                sunAnalysis = json.optString("sunAnalysis", ""),
                moonAnalysis = json.optString("moonAnalysis", ""),
                risingAnalysis = json.optString("risingAnalysis", ""),
                elementBalance = json.optString("elementBalance", "")
            )
        }
    )

    override suspend fun generateCompatibility(sign1: String, sign2: String): CompatibilityData = runWithFallback(
        offlineBlock = { offlineFallback.generateCompatibility(sign1, sign2) },
        liveBlock = {
            val schema = Schema.obj(
                properties = mapOf(
                    "score" to Schema.integer(),
                    "summary" to Schema.string(),
                    "relationshipDynamics" to Schema.string(),
                    "challenges" to Schema.string()
                )
            )
            val structuredModel = Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                    responseSchema = schema
                }
            )
            val response = structuredModel.generateContent(
                "Describe mindful communication dynamics and compatibility (score 1-100) between $sign1 and $sign2."
            )
            val json = JSONObject(response.text ?: throw IllegalStateException("Empty compatibility JSON"))
            CompatibilityData(
                score = json.optInt("score", 78).coerceIn(1, 100),
                summary = json.optString("summary", ""),
                relationshipDynamics = json.optString("relationshipDynamics", ""),
                challenges = json.optString("challenges", "")
            )
        }
    )

    override suspend fun searchResources(
        query: String,
        latitude: Double?,
        longitude: Double?
    ): ResourceSearchResult = runWithFallback(
        offlineBlock = { offlineFallback.searchResources(query, latitude, longitude) },
        liveBlock = {
            val locContext = if (latitude != null && longitude != null) {
                " near coordinates (${"%.2f".format(latitude)}, ${"%.2f".format(longitude)})"
            } else ""
            val responseText = textModel.generateContent(
                "Provide practical, reputable mental health resource guidance and search tips for: '${query.take(300)}'$locContext. Always include 988 Lifeline for urgent help."
            ).text?.trim().orEmpty()
            ResourceSearchResult(
                text = responseText,
                sources = listOf(
                    GroundingSource("988 Suicide & Crisis Lifeline", "https://988lifeline.org/"),
                    GroundingSource("SAMHSA Treatment Locator", "https://findtreatment.gov/"),
                    GroundingSource("Psychology Today Directory", "https://www.psychologytoday.com/us/therapists")
                ),
                isSamplePreview = false
            )
        }
    )
}
