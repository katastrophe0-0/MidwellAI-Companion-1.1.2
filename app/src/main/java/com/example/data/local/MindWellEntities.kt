package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AffirmationItem
import com.example.data.model.BreathingSession
import com.example.data.model.ChatMessage
import com.example.data.model.CopingRating
import com.example.data.model.CustomBreathingPattern
import com.example.data.model.CycleData
import com.example.data.model.DailyHabit
import com.example.data.model.EmergencyPlanItem
import com.example.data.model.EnergyCheckIn
import com.example.data.model.GratitudeNote
import com.example.data.model.HabitCompletion
import com.example.data.model.IfThenPlan
import com.example.data.model.JournalEntry
import com.example.data.model.MoodLog
import com.example.data.model.QuizLog
import com.example.data.model.SleepLog
import com.example.data.model.SupplementLog
import com.example.data.model.ThoughtReframeLog
import com.example.data.model.UserProfile
import kotlinx.serialization.json.Json

private val entityJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val profileJson: String
) {
    fun toModel(): UserProfile = entityJson.decodeFromString(profileJson)

    companion object {
        fun fromModel(profile: UserProfile): UserProfileEntity =
            UserProfileEntity(id = 1, profileJson = entityJson.encodeToString(profile))
    }
}

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val date: String,
    val content: String,
    val prompt: String? = null,
    val tags: List<String> = emptyList(),
    val moodRating: Int? = null,
    val analysis: String? = null,
    val sentiment: String? = null,
    val themes: List<String> = emptyList()
) {
    fun toModel() = JournalEntry(id, date, content, prompt, tags, moodRating, analysis, sentiment, themes)

    companion object {
        fun fromModel(entry: JournalEntry) = JournalEntryEntity(
            id = entry.id,
            date = entry.date,
            content = entry.content,
            prompt = entry.prompt,
            tags = entry.tags,
            moodRating = entry.moodRating,
            analysis = entry.analysis,
            sentiment = entry.sentiment,
            themes = entry.themes
        )
    }
}

@Entity(tableName = "mood_logs")
data class MoodLogEntity(
    @PrimaryKey val date: String,
    val mood: Int,
    val tags: List<String> = emptyList()
) {
    fun toModel() = MoodLog(date, mood, tags)

    companion object {
        fun fromModel(log: MoodLog) = MoodLogEntity(log.date, log.mood, log.tags)
    }
}

@Entity(tableName = "daily_habits")
data class DailyHabitEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val active: Boolean = true
) {
    fun toModel() = DailyHabit(id, title, category, active)

    companion object {
        fun fromModel(habit: DailyHabit) = DailyHabitEntity(habit.id, habit.title, habit.category, habit.active)
    }
}

@Entity(tableName = "habit_completions")
data class HabitCompletionEntity(
    @PrimaryKey val id: String, // "${habitId}_${date}"
    val habitId: String,
    val date: String,
    val completed: Boolean = true
) {
    fun toModel() = HabitCompletion(id, habitId, date, completed)

    companion object {
        fun fromModel(c: HabitCompletion) = HabitCompletionEntity(c.id, c.habitId, c.date, c.completed)
    }
}

@Entity(tableName = "energy_checkins")
data class EnergyCheckInEntity(
    @PrimaryKey val date: String,
    val mentalBattery: Int,
    val physicalBattery: Int
) {
    fun toModel() = EnergyCheckIn(date, mentalBattery, physicalBattery)

    companion object {
        fun fromModel(e: EnergyCheckIn) = EnergyCheckInEntity(e.date, e.mentalBattery, e.physicalBattery)
    }
}

@Entity(tableName = "thought_reframes")
data class ThoughtReframeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val date: String,
    val thought: String,
    val distortion: String,
    val explanation: String,
    val reframes: List<String> = emptyList()
) {
    fun toModel() = ThoughtReframeLog(id, date, thought, distortion, explanation, reframes)

    companion object {
        fun fromModel(log: ThoughtReframeLog) = ThoughtReframeEntity(
            id = log.id,
            date = log.date,
            thought = log.thought,
            distortion = log.distortion,
            explanation = log.explanation,
            reframes = log.reframes
        )
    }
}

@Entity(tableName = "quiz_logs")
data class QuizLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val date: String,
    val quizId: String,
    val quizLabel: String,
    val score: Int,
    val maxScore: Int,
    val level: String
) {
    fun toModel() = QuizLog(id, date, quizId, quizLabel, score, maxScore, level)

    companion object {
        fun fromModel(log: QuizLog) = QuizLogEntity(
            id = log.id,
            date = log.date,
            quizId = log.quizId,
            quizLabel = log.quizLabel,
            score = log.score,
            maxScore = log.maxScore,
            level = log.level
        )
    }
}

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sender: String,
    val text: String,
    val timestamp: Long
) {
    fun toModel() = ChatMessage(id, sender, text, timestamp)

    companion object {
        fun fromModel(msg: ChatMessage) = ChatMessageEntity(msg.id, msg.sender, msg.text, msg.timestamp)
    }
}

@Entity(tableName = "emergency_items")
data class EmergencyItemEntity(
    @PrimaryKey val id: String,
    val type: String,
    val label: String,
    val value: String
) {
    fun toModel() = EmergencyPlanItem(id, type, label, value)

    companion object {
        fun fromModel(item: EmergencyPlanItem) = EmergencyItemEntity(item.id, item.type, item.label, item.value)
    }
}

@Entity(tableName = "gratitude_notes")
data class GratitudeNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val text: String,
    val date: String,
    val colorIndex: Int,
    val rotation: Float
) {
    fun toModel() = GratitudeNote(id, text, date, colorIndex, rotation)

    companion object {
        fun fromModel(note: GratitudeNote) = GratitudeNoteEntity(note.id, note.text, note.date, note.colorIndex, note.rotation)
    }
}

@Entity(tableName = "affirmation_items")
data class AffirmationEntity(
    @PrimaryKey val id: String,
    val text: String,
    val favorite: Boolean = false,
    val pinned: Boolean = false,
    val custom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toModel() = AffirmationItem(id, text, favorite, pinned, custom)

    companion object {
        fun fromModel(item: AffirmationItem) = AffirmationEntity(
            id = item.id,
            text = item.text,
            favorite = item.favorite,
            pinned = item.pinned,
            custom = item.custom
        )
    }
}

@Entity(tableName = "custom_breathing_patterns")
data class CustomBreathingPatternEntity(
    @PrimaryKey val id: String,
    val name: String,
    val inhaleSeconds: Int,
    val holdInSeconds: Int,
    val exhaleSeconds: Int,
    val holdOutSeconds: Int
) {
    fun toModel() = CustomBreathingPattern(id, name, inhaleSeconds, holdInSeconds, exhaleSeconds, holdOutSeconds)

    companion object {
        fun fromModel(p: CustomBreathingPattern) = CustomBreathingPatternEntity(
            p.id, p.name, p.inhaleSeconds, p.holdInSeconds, p.exhaleSeconds, p.holdOutSeconds
        )
    }
}

@Entity(tableName = "breathing_sessions")
data class BreathingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val date: String,
    val timestamp: Long,
    val durationSeconds: Int,
    val technique: String
) {
    fun toModel() = BreathingSession(id, date, timestamp, durationSeconds, technique)

    companion object {
        fun fromModel(session: BreathingSession) = BreathingSessionEntity(
            id = session.id,
            date = session.date,
            timestamp = session.timestamp,
            durationSeconds = session.durationSeconds,
            technique = session.technique
        )
    }
}

@Entity(tableName = "sleep_logs")
data class SleepLogEntity(
    @PrimaryKey val date: String,
    val checklistDone: List<String> = emptyList(),
    val qualityRating: Int = 0,
    val notes: String = ""
) {
    fun toModel() = SleepLog(date, checklistDone, qualityRating, notes)

    companion object {
        fun fromModel(log: SleepLog) = SleepLogEntity(log.date, log.checklistDone, log.qualityRating, log.notes)
    }
}

@Entity(tableName = "coping_ratings")
data class CopingRatingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val strategyTitle: String,
    val problemSummary: String,
    val stars: Int
) {
    fun toModel() = CopingRating(id, strategyTitle, problemSummary, stars)

    companion object {
        fun fromModel(r: CopingRating) = CopingRatingEntity(r.id, r.strategyTitle, r.problemSummary, r.stars)
    }
}

@Entity(tableName = "if_then_plans")
data class IfThenPlanEntity(
    @PrimaryKey val id: String,
    val triggerText: String,
    val actionText: String,
    val safeSpace: String
) {
    fun toModel() = IfThenPlan(id, triggerText, actionText, safeSpace)

    companion object {
        fun fromModel(p: IfThenPlan) = IfThenPlanEntity(p.id, p.triggerText, p.actionText, p.safeSpace)
    }
}

@Entity(tableName = "supplement_logs")
data class SupplementLogEntity(
    @PrimaryKey val date: String,
    val itemsDone: List<String> = emptyList()
) {
    fun toModel() = SupplementLog(date, itemsDone)

    companion object {
        fun fromModel(s: SupplementLog) = SupplementLogEntity(s.date, s.itemsDone)
    }
}

@Entity(tableName = "cycle_data")
data class CycleDataEntity(
    @PrimaryKey val id: Int = 1,
    val lastPeriodStart: String,
    val cycleLength: Int,
    val periodLength: Int
) {
    fun toModel() = CycleData(lastPeriodStart, cycleLength, periodLength)

    companion object {
        fun fromModel(data: CycleData) = CycleDataEntity(1, data.lastPeriodStart, data.cycleLength, data.periodLength)
    }
}

@Entity(tableName = "app_prefs")
data class AppPrefEntity(
    @PrimaryKey val key: String,
    val value: String
)
