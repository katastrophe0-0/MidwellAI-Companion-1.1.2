package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class AppView {
    Dashboard,
    Journal,
    Quizzes,
    MoodTracker,
    Chatbot,
    Companion,
    Strategizer,
    Meditation,
    Breathing,
    ImageGenerator,
    VisionBoard,
    Resources,
    Emergency,
    Settings,
    CycleTracker,
    Astrology,
    Legal,
    Checkout,
    GratitudeJar,
    Soundscapes,
    ThoughtReframer,
    SleepStation,
    Somatic,
    Affirmations
}

@Serializable
enum class SubscriptionTier(val rank: Int, val label: String) {
    FREE(0, "Free"),
    SPARK(1, "Spark"),
    GLOW(2, "Glow"),
    RADIANCE(3, "Radiance");

    fun hasAccess(required: SubscriptionTier): Boolean = this.rank >= required.rank
}

@Serializable
enum class MindWellThemeId(val label: String, val subtitle: String) {
    MIDNIGHT_INDIGO("Midnight Indigo", "Calm slate & deep indigo"),
    SAGE_FOREST("Sage Forest", "Organic greens & soft cream"),
    WARM_SAND("Warm Sand", "Earthy linen & warm amber"),
    HIGH_CONTRAST_DARK("High-Contrast Dark", "Deep obsidian accessibility")
}

@Serializable
data class DashboardConfig(
    val showQuote: Boolean = true,
    val showMoodChart: Boolean = true,
    val showHabitTracker: Boolean = true,
    val showWellnessPulse: Boolean = true,
    val showQuickLinks: Boolean = true,
    val showEnergyBattery: Boolean = true,
    val showMicroJournal: Boolean = true,
    val showWeeklyDigest: Boolean = true
)

@Serializable
data class UserPreferences(
    val blurJournalPreviews: Boolean = false,
    val enableDailyReminder: Boolean = false,
    val dailyReminderTime: String = "09:00",
    val enableMorningReminder: Boolean = false,
    val morningReminderTime: String = "08:30",
    val enableHabitReminder: Boolean = false,
    val habitReminderTime: String = "13:00",
    val enableEveningReminder: Boolean = false,
    val eveningReminderTime: String = "20:30",
    val privacyMode: Boolean = false,
    val reduceMotion: Boolean = false,
    val biometricLockEnabled: Boolean = false,
    val pinCode: String = "",
    val themeId: MindWellThemeId = MindWellThemeId.MIDNIGHT_INDIGO,
    val fontScale: Float = 1.0f,
    val showSupplementLog: Boolean = false,
    val supplementItems: List<String> = listOf(
        "Morning Hydration",
        "Vitamin D / Omega-3",
        "Daily Wellness Supplement"
    )
)

@Serializable
data class UserProfile(
    val name: String = "",
    val avatar: String = "🌿",
    val goals: List<String> = emptyList(),
    val challenges: List<String> = emptyList(),
    val zodiacSign: String? = null,
    val birthDate: String? = null,
    val birthTime: String? = null,
    val birthLocation: String? = null,
    val onboardingCompleted: Boolean = false,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    val enableCycleTracking: Boolean = false,
    val enableAstrology: Boolean = false,
    val enableBreathing: Boolean = true,
    val enableMeditation: Boolean = true,
    val dashboardConfig: DashboardConfig = DashboardConfig(),
    val lastSynced: String? = null,
    val enableCloudSync: Boolean = true,
    val preferences: UserPreferences = UserPreferences()
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val values: List<Int>
)

data class QuizResult(
    val score: Int,
    val level: String,
    val interpretation: String
)

@Serializable
data class QuizLog(
    val id: Long = 0L,
    val date: String = "",
    val quizId: String = "",
    val quizLabel: String = "",
    val score: Int = 0,
    val maxScore: Int = 0,
    val level: String = ""
)

@Serializable
data class ChatMessage(
    val id: Long = 0L,
    val sender: String = "user",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class JournalEntry(
    val id: Long = 0L,
    val date: String = "",
    val content: String = "",
    val prompt: String? = null,
    val tags: List<String> = emptyList(),
    val moodRating: Int? = null,
    val analysis: String? = null,
    val sentiment: String? = null,
    val themes: List<String> = emptyList()
)

@Serializable
data class MoodLog(
    val date: String = "",
    val mood: Int = 3,
    val tags: List<String> = emptyList()
)

@Serializable
data class DailyHabit(
    val id: String = "",
    val title: String = "",
    val category: String = "Wellness",
    val active: Boolean = true
)

@Serializable
data class HabitCompletion(
    val id: String = "",
    val habitId: String = "",
    val date: String = "",
    val completed: Boolean = true
)

@Serializable
data class EnergyCheckIn(
    val date: String = "",
    val mentalBattery: Int = 75,
    val physicalBattery: Int = 75
)

@Serializable
data class ThoughtReframeLog(
    val id: Long = 0L,
    val date: String = "",
    val thought: String = "",
    val distortion: String = "",
    val explanation: String = "",
    val reframes: List<String> = emptyList()
)

@Serializable
data class EmergencyPlanItem(
    val id: String = "",
    val type: String = "contact", // "contact", "activity", "mantra", "warning_sign"
    val label: String = "",
    val value: String = ""
)

@Serializable
data class GratitudeNote(
    val id: Long = 0L,
    val text: String = "",
    val date: String = "",
    val colorIndex: Int = 0,
    val rotation: Float = 0f
)

@Serializable
data class AffirmationItem(
    val id: String = "",
    val text: String = "",
    val favorite: Boolean = false,
    val pinned: Boolean = false,
    val custom: Boolean = false
)

@Serializable
data class CustomBreathingPattern(
    val id: String = "",
    val name: String = "",
    val inhaleSeconds: Int = 4,
    val holdInSeconds: Int = 4,
    val exhaleSeconds: Int = 4,
    val holdOutSeconds: Int = 4
)

@Serializable
data class BreathingSession(
    val id: Long = 0L,
    val date: String = "",
    val timestamp: Long = 0L,
    val durationSeconds: Int = 0,
    val technique: String = ""
)

@Serializable
data class SleepLog(
    val date: String = "",
    val checklistDone: List<String> = emptyList(),
    val qualityRating: Int = 0,
    val notes: String = ""
)

@Serializable
data class CopingRating(
    val id: Long = 0L,
    val strategyTitle: String = "",
    val problemSummary: String = "",
    val stars: Int = 5
)

@Serializable
data class IfThenPlan(
    val id: String = "",
    val triggerText: String = "",
    val actionText: String = "",
    val safeSpace: String = ""
)

@Serializable
data class SupplementLog(
    val date: String = "",
    val itemsDone: List<String> = emptyList()
)

@Serializable
data class CycleData(
    val lastPeriodStart: String = "",
    val cycleLength: Int = 28,
    val periodLength: Int = 5
)

@Serializable
data class HabitData(
    val lastCompletedDate: String = "",
    val streak: Int = 0
)

@Serializable
data class HoroscopeData(
    val mood: String = "",
    val luckyColor: String = "",
    val luckyNumber: String = "",
    val love: String = "",
    val career: String = "",
    val wellness: String = "",
    val quote: String = "",
    val transits: String = ""
)

@Serializable
data class NatalChart(
    val sunSign: String = "",
    val moonSign: String = "",
    val risingSign: String = "",
    val sunAnalysis: String = "",
    val moonAnalysis: String = "",
    val risingAnalysis: String = "",
    val elementBalance: String = ""
)

@Serializable
data class CompatibilityData(
    val score: Int = 0,
    val summary: String = "",
    val relationshipDynamics: String = "",
    val challenges: String = ""
)

@Serializable
data class ReframeResult(
    val distortion: String,
    val explanation: String,
    val reframes: List<String>
)

data class GroundingSource(
    val title: String,
    val uri: String
)

data class ResourceSearchResult(
    val text: String,
    val sources: List<GroundingSource>,
    val isSamplePreview: Boolean = false
)

data class ResourceItem(
    val name: String,
    val description: String,
    val category: String = "Support",
    val phone: String? = null,
    val phoneText: String? = null,
    val email: String? = null,
    val url: String? = null
)

@Serializable
data class BackupPayload(
    val profile: UserProfile? = null,
    val journal: List<JournalEntry> = emptyList(),
    val moods: List<MoodLog> = emptyList(),
    val habits: List<DailyHabit> = emptyList(),
    val habitCompletions: List<HabitCompletion> = emptyList(),
    val energyLogs: List<EnergyCheckIn> = emptyList(),
    val thoughtReframes: List<ThoughtReframeLog> = emptyList(),
    val history: List<ChatMessage> = emptyList(),
    val emergency: List<EmergencyPlanItem> = emptyList(),
    val gratitude: List<GratitudeNote> = emptyList(),
    val affirmations: List<AffirmationItem> = emptyList(),
    val breathingPatterns: List<CustomBreathingPattern> = emptyList(),
    val breathing: List<BreathingSession> = emptyList(),
    val sleepLogs: List<SleepLog> = emptyList(),
    val copingRatings: List<CopingRating> = emptyList(),
    val ifThenPlans: List<IfThenPlan> = emptyList(),
    val quizzes: List<QuizLog> = emptyList(),
    val supplementLogs: List<SupplementLog> = emptyList(),
    val cycle: CycleData? = null
)
