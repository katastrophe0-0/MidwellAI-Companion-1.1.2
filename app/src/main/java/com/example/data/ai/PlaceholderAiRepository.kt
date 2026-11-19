package com.example.data.ai

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import com.example.data.model.ChatMessage
import com.example.data.model.CompatibilityData
import com.example.data.model.GroundingSource
import com.example.data.model.HoroscopeData
import com.example.data.model.NatalChart
import com.example.data.model.ReframeResult
import com.example.data.model.ResourceSearchResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.abs

/**
 * Offline-first CBT/DBT template engine used when the device is offline or Gemini APIs are unavailable.
 * Contains 100% original, concise psychoeducational prompts, reframes, and safety responses.
 */
class PlaceholderAiRepository : AiRepository {

    override val isLiveAiEnabled: Boolean = false

    private val sampleQuotes = listOf(
        "Steadiness grows one slow breath and one kind choice at a time.",
        "You do not need to solve the whole week today; just care for the next small hour.",
        "Thoughts are passing weather patterns, not permanent facts about who you are.",
        "Rest is not a reward for exhaustion—it is essential maintenance for clarity."
    )

    private val cbtPrompts = listOf(
        "What thought kept looping today, and what single piece of evidence softens it?",
        "Notice where tension sits in your shoulders or jaw right now—what might your body be asking for?",
        "Name one small boundary or kindness you offered yourself (or want to offer) today.",
        "If a close friend carried what you are holding right now, what steady sentence would you tell them?"
    )

    private val sampleAffirmations = listOf(
        "I allow myself to take one steady breath at a time.",
        "My worth is not measured by urgency or perfection.",
        "I can feel discomfort and still remain grounded and safe.",
        "Each gentle step forward honors my well-being.",
        "I meet today with patience, curiosity, and self-kindness."
    )

    private fun containsCrisisSignal(text: String): Boolean {
        val lower = text.lowercase()
        val keywords = listOf(
            "suicide", "kill myself", "end my life", "want to die",
            "hurt myself", "self-harm", "self harm", "better off dead", "no reason to live"
        )
        return keywords.any { lower.contains(it) }
    }

    private fun crisisMessage(): String = """
        **Immediate Support Is Available**
        
        Your safety matters right now. Please connect with live, confidential human support immediately:
        
        - **US Suicide & Crisis Lifeline:** Call or text **988** (24/7, free, confidential)
        - **Crisis Text Line:** Text **HOME** to **741741**
        - **Emergency Services:** Call **911** or go to the nearest emergency room
        - **International:** Visit **findahelpline.com** for free local helplines
    """.trimIndent()

    override suspend fun getDailyQuote(): String {
        delay(120)
        return sampleQuotes.random()
    }

    override suspend fun generateWeeklyDigest(
        moodSummary: String,
        habitCount: Int,
        copingTools: String
    ): String {
        delay(150)
        return "[Offline Wellness Digest]\n" +
            "• Mood Pattern: $moodSummary\n" +
            "• Consistency: You logged $habitCount wellness check-ins across the last 7 days.\n" +
            "• Helpful Tools: ${copingTools.ifBlank { "Breathing & micro-journaling" }}\n" +
            "Gentle focus for tomorrow: Pair one brief midday pause with your highest-rated coping tool."
    }

    override suspend fun generateJournalPrompt(): String {
        delay(120)
        return "[Offline CBT Prompt] ${cbtPrompts.random()}"
    }

    override suspend fun generateReflectionFollowUp(draftContent: String): String {
        delay(150)
        val lower = draftContent.lowercase()
        return when {
            lower.isBlank() -> "[Offline Prompt] Start with three honest words about how your nervous system feels right now."
            lower.contains("anxious") || lower.contains("worry") || lower.contains("overwhelm") ->
                "[Offline CBT Reflection] Notice what part of this worry is within your control today versus outside it. What is one 5-minute anchor step?"
            lower.contains("tired") || lower.contains("exhaust") || lower.contains("sleep") ->
                "[Offline Reflection] Fatigue magnifies heavy thoughts. What can wait until tomorrow so you can protect recovery tonight?"
            else ->
                "[Offline CBT Reflection] As you reread what you shared, what emotion feels strongest underneath the story, and what would self-compassion look like next?"
        }
    }

    override suspend fun analyzeJournalEntry(entry: String): JournalAnalysisResult {
        delay(200)
        val lower = entry.lowercase()
        val sentiment = when {
            listOf("happy", "grateful", "calm", "peace", "good", "hope", "relief").any { lower.contains(it) } -> "Hopeful"
            listOf("anxious", "worry", "stress", "overwhelmed", "panic", "nervous").any { lower.contains(it) } -> "Anxious"
            listOf("sad", "tired", "heavy", "lonely", "down", "exhausted").any { lower.contains(it) } -> "Reflective / Heavy"
            else -> "Balanced"
        }
        val themes = buildList {
            if (lower.contains("work") || lower.contains("job") || lower.contains("deadline")) add("Work & Focus")
            if (lower.contains("sleep") || lower.contains("tired") || lower.contains("rest")) add("Rest & Energy")
            if (lower.contains("friend") || lower.contains("family") || lower.contains("partner")) add("Relationships")
            if (isEmpty()) add("Self-Reflection")
            add("Emotional Awareness")
        }.distinct().take(3)

        return JournalAnalysisResult(
            sentiment = sentiment,
            themes = themes,
            analysis = "[Offline CBT Summary]\n" +
                "Themes noticed: ${themes.joinToString(", ")}. " +
                "Pause for two slow exhales and notice whether any all-or-nothing assumptions showed up while writing."
        )
    }

    override fun streamChatReply(history: List<ChatMessage>, newMessage: String, userName: String?): Flow<String> = flow {
        if (containsCrisisSignal(newMessage)) {
            emit(crisisMessage())
            return@flow
        }
        val nameGreeting = userName?.takeIf { it.isNotBlank() }?.let { " $it" } ?: ""
        val reply = buildString {
            append("[Offline CBT/DBT Companion Response]\n\n")
            append("Thank you for checking in$nameGreeting. Feeling stretched by \"${newMessage.trim().take(80)}\" makes sense.\n\n")
            append("Three grounded steps for right now:\n")
            append("- **DBT TIPP Reset:** Splash cool water on your face or lengthen your exhale to 6 seconds.\n")
            append("- **Fact Check:** Separate what actually happened from what fear predicts might happen next.\n")
            append("- **Next Tiny Step:** Pick one comforting or low-effort action for the next 10 minutes.")
        }
        emit(reply)
    }

    override suspend fun voiceCompanionReply(userSpeech: String, userName: String?): String {
        delay(150)
        if (containsCrisisSignal(userSpeech)) {
            return "Your safety matters most right now. Please call or text 988 in the US for free 24/7 confidential support, or call 911 in an emergency."
        }
        val namePart = userName?.takeIf { it.isNotBlank() }?.let { "$it, " } ?: ""
        return "[Offline Companion] ${namePart}I heard what you shared. Drop your shoulders down away from your ears, inhale slowly for four counts, and exhale gently for six."
    }

    override suspend fun analyzeThought(thought: String): ReframeResult {
        delay(180)
        val lower = thought.lowercase()
        val distortion = when {
            lower.contains("always") || lower.contains("never") || lower.contains("every") -> "All-or-Nothing Thinking"
            lower.contains("ruin") || lower.contains("disaster") || lower.contains("worst") -> "Catastrophizing"
            lower.contains("should") || lower.contains("must") -> "Should Statements"
            lower.contains("think of me") || lower.contains("hate me") || lower.contains("judging") -> "Mind Reading"
            else -> "Emotional Reasoning"
        }
        return ReframeResult(
            distortion = distortion,
            explanation = "Offline CBT pattern check: Stress nudges thoughts toward rigid predictions ($distortion) rather than balanced middle ground.",
            reframes = listOf(
                "This feels uncomfortable right now, not permanent or unchangeable.",
                "One tough moment does not define my whole week or worth.",
                "I only need to focus on one realistic, steady next step."
            )
        )
    }

    override suspend fun generateCopingStrategy(problem: String): String {
        delay(150)
        if (containsCrisisSignal(problem)) return crisisMessage()
        return """
            [Offline DBT/CBT Coping Plan]
            
            ### Grounded Steps for: ${problem.trim().take(80)}
            
            **1. Body First (2 Minutes)**
            - Unclench your jaw, drop your shoulders, and take 4 cycles of 4-7-8 breathing.
            
            **2. Cognitive De-escalation (10 Minutes)**
            - Ask: *"What is one neutral fact about this situation right now?"*
            - Narrow your focus to the single next constructive step.
            
            **3. Care & Safety**
            - Check your Mental/Physical Battery and lean on your Safety Plan or a trusted person if distress persists.
        """.trimIndent()
    }

    override suspend fun generateMeditationScript(topic: String, durationMinutes: Int): String {
        delay(150)
        return "[Offline Meditation Script — ${durationMinutes}m for $topic]\n\n" +
            "Settle into a supportive seat and let your gaze soften. Bring awareness toward $topic. " +
            "Notice the cool air entering on your inhale and the gentle release on your exhale. " +
            "Whenever thoughts wander back to tasks, label them 'thinking' and return softly to your breath. " +
            "Rest here for this $durationMinutes-minute pause with zero pressure to fix anything."
    }

    override suspend fun generateBedtimeStory(theme: String): String {
        delay(150)
        return "[Offline Sleep Story — $theme]\n\n" +
            "Night settles softly over $theme. Distant sounds grow quiet like snow falling through tall pines. " +
            "Your breathing slows to match an unhurried tide. Everything on today's list can wait safely until morning. " +
            "Feel the steady weight of the mattress beneath you as calm rest gently arrives."
    }

    override suspend fun generateAffirmation(mood: String?, goals: List<String>): String {
        delay(120)
        val goalNote = goals.firstOrNull()?.let { " (Focus: $it)" } ?: ""
        return "${sampleAffirmations.random()}$goalNote"
    }

    override suspend fun generateCalmingImage(prompt: String): Bitmap {
        val width = 512
        val height = 512
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val seed = abs(prompt.hashCode())
        val colors = when (seed % 3) {
            0 -> intArrayOf(Color.parseColor("#312E81"), Color.parseColor("#4F46E5"), Color.parseColor("#CCFBF1"))
            1 -> intArrayOf(Color.parseColor("#0F172A"), Color.parseColor("#0D9488"), Color.parseColor("#E0E7FF"))
            else -> intArrayOf(Color.parseColor("#581C87"), Color.parseColor("#EC4899"), Color.parseColor("#FEF3C7"))
        }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), colors, null, Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

    override suspend fun generateHoroscope(sign: String): HoroscopeData {
        delay(150)
        return HoroscopeData(
            mood = "Steady & Reflective",
            luckyColor = "Deep Indigo",
            luckyNumber = "7",
            love = "[Offline Reflection] For $sign, honest pacing and listening foster calm connection today.",
            career = "Complete one anchored task before picking up new demands.",
            wellness = "Protect a 5-minute screen-free transition between work and rest.",
            quote = "Clarity arrives when your pace slows enough to hear yourself.",
            transits = "Offline reflection template active — connect online for live Gemini astrology."
        )
    }

    override suspend fun generateAstrologyDailyIntention(zodiacSign: String, lunarPhase: String): String {
        delay(120)
        return "[Offline Daily Intention — $zodiacSign • $lunarPhase]\n" +
            "Intention: \"I honor the pace of this $lunarPhase by releasing unnecessary urgency.\"\n" +
            "Journal Prompt: What one habit or thought am I ready to soften today?"
    }

    override suspend fun generateNatalChart(birthDate: String, birthTime: String, birthLocation: String): NatalChart {
        delay(150)
        return NatalChart(
            sunSign = "Sun ($birthDate)",
            moonSign = "Reflective Moon",
            risingSign = "Ascendant ($birthLocation)",
            sunAnalysis = "[Offline Summary] Core energy values steady autonomy and authentic expression.",
            moonAnalysis = "Emotional balance deepens through regular rest and honest reflection.",
            risingAnalysis = "Calm outward composure balanced by thoughtful observation.",
            elementBalance = "Offline natal summary — reconnect for full Gemini astrological synthesis."
        )
    }

    override suspend fun generateCompatibility(sign1: String, sign2: String): CompatibilityData {
        delay(150)
        return CompatibilityData(
            score = 80,
            summary = "[Offline Insight] $sign1 & $sign2 grow strongest through curiosity and clear emotional boundaries.",
            relationshipDynamics = "Complementary strengths help balance action with reflection.",
            challenges = "Pause under stress to clarify assumptions instead of mind-reading."
        )
    }

    override suspend fun searchResources(query: String, latitude: Double?, longitude: Double?): ResourceSearchResult {
        delay(150)
        return ResourceSearchResult(
            text = """
                [Offline Resource Guide for "$query"]
                
                - **Immediate 24/7 Support:** Call or text **988** (US Lifeline) or text **HOME** to **741741**.
                - **Local Clinicians:** Use the **Local Clinics** filter below to browse SAMHSA & Psychology Today directories.
            """.trimIndent(),
            sources = listOf(
                GroundingSource("988 Suicide & Crisis Lifeline", "https://988lifeline.org/"),
                GroundingSource("SAMHSA Treatment Locator", "https://findtreatment.gov/")
            ),
            isSamplePreview = true
        )
    }
}
