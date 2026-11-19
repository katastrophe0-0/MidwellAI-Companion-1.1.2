package com.example.data.local

import androidx.room.withTransaction
import com.example.data.constants.MindWellConstants
import com.example.data.model.AffirmationItem
import com.example.data.model.BackupPayload
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
import com.example.data.model.HabitData
import com.example.data.model.IfThenPlan
import com.example.data.model.JournalEntry
import com.example.data.model.MoodLog
import com.example.data.model.QuizLog
import com.example.data.model.SleepLog
import com.example.data.model.SupplementLog
import com.example.data.model.ThoughtReframeLog
import com.example.data.model.UserProfile
import com.example.data.remote.FirestoreSyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.util.UUID

class MindWellRepository(
    private val db: AppDatabase,
    private val firestoreSync: FirestoreSyncManager? = null
) {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    companion object {
        const val KEY_DISCLAIMER_SEEN = "disclaimer_seen"
        const val KEY_HABIT_TRACKER = "habit_tracker"
        const val KEY_JOURNAL_DRAFT = "journal_draft"
    }

    val disclaimerSeen: Flow<Boolean> = db.appPrefDao()
        .observePref(KEY_DISCLAIMER_SEEN)
        .map { it?.value == "true" }

    val userProfile: Flow<UserProfile?> = db.userProfileDao()
        .observeProfile()
        .map { it?.toModel() }

    val journalEntries: Flow<List<JournalEntry>> = db.journalDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val totalJournalCount: Flow<Int> = db.journalDao().observeCount()

    val moodLogs: Flow<List<MoodLog>> = db.moodDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val dailyHabits: Flow<List<DailyHabit>> = db.dailyHabitDao()
        .observeHabits()
        .map { list -> list.map { it.toModel() } }

    val habitCompletions: Flow<List<HabitCompletion>> = db.habitCompletionDao()
        .observeCompletions()
        .map { list -> list.map { it.toModel() } }

    fun observeWeeklyHabitCompletions(startDate: String, endDate: String): Flow<List<HabitCompletion>> =
        db.habitCompletionDao().observeCompletionsBetween(startDate, endDate)
            .map { list -> list.map { it.toModel() } }

    val totalCompletedHabits: Flow<Int> = db.habitCompletionDao().observeTotalCompleted()

    val energyCheckIns: Flow<List<EnergyCheckIn>> = db.energyCheckInDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val thoughtReframes: Flow<List<ThoughtReframeLog>> = db.thoughtReframeDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val quizLogs: Flow<List<QuizLog>> = db.quizLogDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val chatMessages: Flow<List<ChatMessage>> = db.chatDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val emergencyItems: Flow<List<EmergencyPlanItem>> = db.emergencyDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val gratitudeNotes: Flow<List<GratitudeNote>> = db.gratitudeDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val affirmations: Flow<List<AffirmationItem>> = db.affirmationDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val customBreathingPatterns: Flow<List<CustomBreathingPattern>> = db.customBreathingPatternDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val breathingSessions: Flow<List<BreathingSession>> = db.breathingDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val totalBreathingCount: Flow<Int> = db.breathingDao().observeCount()

    val sleepLogs: Flow<List<SleepLog>> = db.sleepLogDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val copingRatings: Flow<List<CopingRating>> = db.copingRatingDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val ifThenPlans: Flow<List<IfThenPlan>> = db.ifThenPlanDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val supplementLogs: Flow<List<SupplementLog>> = db.supplementLogDao()
        .observeAll()
        .map { list -> list.map { it.toModel() } }

    val cycleData: Flow<CycleData?> = db.cycleDao()
        .observeCycle()
        .map { it?.toModel() }

    val habitData: Flow<HabitData> = db.appPrefDao()
        .observePref(KEY_HABIT_TRACKER)
        .map { entity ->
            if (entity == null) HabitData()
            else runCatching { json.decodeFromString<HabitData>(entity.value) }.getOrDefault(HabitData())
        }

    val journalDraft: Flow<String> = db.appPrefDao()
        .observePref(KEY_JOURNAL_DRAFT)
        .map { it?.value.orEmpty() }

    fun searchJournalEntries(query: String): Flow<List<JournalEntry>> =
        db.journalDao().searchEntries(query).map { list -> list.map { it.toModel() } }

    suspend fun acceptDisclaimer() {
        db.appPrefDao().setPref(AppPrefEntity(KEY_DISCLAIMER_SEEN, "true"))
    }

    suspend fun syncFromCloud(): Result<Unit> {
        return firestoreSync?.pullCloudStateIntoRoom(db) ?: Result.success(Unit)
    }

    suspend fun seedInitialDefaults() {
        if (db.emergencyDao().getAll().isEmpty()) {
            db.emergencyDao().insertAll(
                MindWellConstants.DEFAULT_EMERGENCY_ITEMS.map { EmergencyItemEntity.fromModel(it) }
            )
        }
        if (db.dailyHabitDao().getAll().isEmpty()) {
            val defaults = listOf(
                DailyHabit("habit_hydration", "Hydration (8 glasses)", "Hydration", true),
                DailyHabit("habit_mindfulness", "Mindfulness (5 min)", "Mindfulness", true),
                DailyHabit("habit_movement", "Gentle Movement", "Movement", true)
            )
            db.dailyHabitDao().insertAll(defaults.map { DailyHabitEntity.fromModel(it) })
        }
        if (db.affirmationDao().getAll().isEmpty()) {
            val defaultAffirmations = listOf(
                AffirmationItem("aff_1", "I allow myself to take one steady breath at a time.", favorite = false, pinned = true, custom = false),
                AffirmationItem("aff_2", "My worth is not measured by productivity or perfection.", favorite = false, pinned = false, custom = false),
                AffirmationItem("aff_3", "I can feel discomfort and still remain grounded and safe.", favorite = false, pinned = false, custom = false),
                AffirmationItem("aff_4", "Each gentle step forward honors my well-being.", favorite = false, pinned = false, custom = false),
                AffirmationItem("aff_5", "I meet today with patience, curiosity, and self-kindness.", favorite = false, pinned = false, custom = false)
            )
            db.affirmationDao().insertAll(defaultAffirmations.map { AffirmationEntity.fromModel(it) })
        }
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        db.userProfileDao().upsert(UserProfileEntity.fromModel(profile))
        return if (profile.enableCloudSync) {
            firestoreSync?.syncUserProfile(profile) ?: Result.success(Unit)
        } else Result.success(Unit)
    }

    suspend fun updateUserProfile(transform: (UserProfile) -> UserProfile): Result<Unit> {
        val current = db.userProfileDao().getProfile()?.toModel() ?: return Result.success(Unit)
        return saveUserProfile(transform(current))
    }

    suspend fun addJournalEntry(entry: JournalEntry): Long {
        val newId = db.journalDao().insert(JournalEntryEntity.fromModel(entry))
        val saved = entry.copy(id = newId)
        firestoreSync?.syncJournalEntry(saved)
        return newId
    }

    suspend fun updateJournalEntry(entry: JournalEntry) {
        db.journalDao().update(JournalEntryEntity.fromModel(entry))
        firestoreSync?.syncJournalEntry(entry)
    }

    suspend fun deleteJournalEntry(id: Long) {
        db.journalDao().deleteById(id)
        firestoreSync?.deleteJournalEntry(id)
    }

    suspend fun saveJournalDraft(draft: String) {
        if (draft.isBlank()) db.appPrefDao().deletePref(KEY_JOURNAL_DRAFT)
        else db.appPrefDao().setPref(AppPrefEntity(KEY_JOURNAL_DRAFT, draft))
    }

    suspend fun upsertMoodLog(log: MoodLog) {
        db.moodDao().upsert(MoodLogEntity.fromModel(log))
        firestoreSync?.syncMoodLog(log)
    }

    suspend fun addCustomHabit(title: String, category: String) {
        val habit = DailyHabit(
            id = "habit_${UUID.randomUUID().toString().take(8)}",
            title = title.trim(),
            category = category.trim().ifBlank { "Wellness" },
            active = true
        )
        db.dailyHabitDao().upsert(DailyHabitEntity.fromModel(habit))
        firestoreSync?.syncDailyHabit(habit)
    }

    suspend fun deleteHabit(habitId: String) {
        db.dailyHabitDao().deleteById(habitId)
        firestoreSync?.deleteDailyHabit(habitId)
    }

    suspend fun toggleHabitCompletion(habitId: String, date: String) {
        val id = "${habitId}_$date"
        val existing = db.habitCompletionDao().getAll().firstOrNull { it.id == id }
        val nextCompleted = !(existing?.completed ?: false)
        val completion = HabitCompletion(id = id, habitId = habitId, date = date, completed = nextCompleted)
        db.habitCompletionDao().upsert(HabitCompletionEntity.fromModel(completion))
        firestoreSync?.syncHabitCompletion(completion)
    }

    suspend fun upsertEnergyCheckIn(checkIn: EnergyCheckIn) {
        db.energyCheckInDao().upsert(EnergyCheckInEntity.fromModel(checkIn))
        firestoreSync?.syncEnergyCheckIn(checkIn)
    }

    suspend fun addThoughtReframe(log: ThoughtReframeLog): Long {
        val newId = db.thoughtReframeDao().insert(ThoughtReframeEntity.fromModel(log))
        firestoreSync?.syncThoughtReframe(log.copy(id = newId))
        return newId
    }

    suspend fun deleteThoughtReframe(id: Long) {
        db.thoughtReframeDao().deleteById(id)
    }

    suspend fun addQuizLog(log: QuizLog): Long {
        val newId = db.quizLogDao().insert(QuizLogEntity.fromModel(log))
        firestoreSync?.syncQuizLog(log.copy(id = newId))
        return newId
    }

    suspend fun deleteQuizLog(id: Long) {
        db.quizLogDao().deleteById(id)
        firestoreSync?.deleteQuizLog(id)
    }

    suspend fun clearQuizLogs() {
        db.quizLogDao().clear()
    }

    suspend fun addChatMessage(message: ChatMessage): Long {
        return db.chatDao().insert(ChatMessageEntity.fromModel(message))
    }

    suspend fun clearChatHistory() {
        db.chatDao().clear()
    }

    suspend fun addEmergencyItem(type: String, label: String, value: String) {
        val item = EmergencyPlanItem(
            id = "em_${UUID.randomUUID().toString().take(8)}",
            type = type,
            label = label.trim(),
            value = value.trim()
        )
        db.emergencyDao().insert(EmergencyItemEntity.fromModel(item))
        firestoreSync?.syncEmergencyItem(item)
    }

    suspend fun deleteEmergencyItem(id: String) {
        db.emergencyDao().deleteById(id)
        firestoreSync?.deleteEmergencyItem(id)
    }

    suspend fun addGratitudeNote(text: String) {
        val note = GratitudeNote(
            text = text.trim(),
            date = java.text.DateFormat.getDateInstance().format(java.util.Date()),
            colorIndex = (0..3).random(),
            rotation = (-3..3).random().toFloat()
        )
        val newId = db.gratitudeDao().insert(GratitudeNoteEntity.fromModel(note))
        firestoreSync?.syncGratitudeNote(note.copy(id = newId))
    }

    suspend fun drawRandomGratitudeNote(): GratitudeNote? {
        return db.gratitudeDao().getRandomNote()?.toModel()
    }

    suspend fun deleteGratitudeNote(id: Long) {
        db.gratitudeDao().deleteById(id)
        firestoreSync?.deleteGratitudeNote(id)
    }

    suspend fun addCustomAffirmation(text: String) {
        val item = AffirmationItem(
            id = "aff_${UUID.randomUUID().toString().take(8)}",
            text = text.trim(),
            favorite = true,
            pinned = false,
            custom = true
        )
        db.affirmationDao().upsert(AffirmationEntity.fromModel(item))
        firestoreSync?.syncAffirmation(item)
    }

    suspend fun toggleAffirmationFavorite(item: AffirmationItem) {
        val updated = item.copy(favorite = !item.favorite)
        db.affirmationDao().upsert(AffirmationEntity.fromModel(updated))
        firestoreSync?.syncAffirmation(updated)
    }

    suspend fun toggleAffirmationPinned(item: AffirmationItem) {
        val currentlyPinned = item.pinned
        db.affirmationDao().clearAllPinned()
        val updated = item.copy(pinned = !currentlyPinned)
        db.affirmationDao().upsert(AffirmationEntity.fromModel(updated))
        firestoreSync?.syncAffirmation(updated)
    }

    suspend fun saveCustomBreathingPattern(
        name: String,
        inhale: Int,
        holdIn: Int,
        exhale: Int,
        holdOut: Int
    ) {
        val pattern = CustomBreathingPattern(
            id = "bp_${UUID.randomUUID().toString().take(8)}",
            name = name.trim(),
            inhaleSeconds = inhale.coerceIn(1, 30),
            holdInSeconds = holdIn.coerceIn(0, 30),
            exhaleSeconds = exhale.coerceIn(1, 30),
            holdOutSeconds = holdOut.coerceIn(0, 30)
        )
        db.customBreathingPatternDao().upsert(CustomBreathingPatternEntity.fromModel(pattern))
        firestoreSync?.syncBreathingPattern(pattern)
    }

    suspend fun deleteCustomBreathingPattern(id: String) {
        db.customBreathingPatternDao().deleteById(id)
        firestoreSync?.deleteBreathingPattern(id)
    }

    suspend fun logBreathingSession(durationSeconds: Int, technique: String) {
        val session = BreathingSession(
            date = LocalDate.now().toString(),
            timestamp = System.currentTimeMillis(),
            durationSeconds = durationSeconds,
            technique = technique
        )
        val newId = db.breathingDao().insert(BreathingSessionEntity.fromModel(session))
        firestoreSync?.syncBreathingSession(session.copy(id = newId))
    }

    suspend fun upsertSleepLog(log: SleepLog) {
        db.sleepLogDao().upsert(SleepLogEntity.fromModel(log))
        firestoreSync?.syncSleepLog(log)
    }

    suspend fun addCopingRating(strategyTitle: String, problemSummary: String, stars: Int) {
        val rating = CopingRating(
            strategyTitle = strategyTitle.trim(),
            problemSummary = problemSummary.trim(),
            stars = stars.coerceIn(1, 5)
        )
        val newId = db.copingRatingDao().insert(CopingRatingEntity.fromModel(rating))
        firestoreSync?.syncCopingRating(rating.copy(id = newId))
    }

    suspend fun addIfThenPlan(triggerText: String, actionText: String, safeSpace: String) {
        val plan = IfThenPlan(
            id = "if_${UUID.randomUUID().toString().take(8)}",
            triggerText = triggerText.trim(),
            actionText = actionText.trim(),
            safeSpace = safeSpace.trim()
        )
        db.ifThenPlanDao().upsert(IfThenPlanEntity.fromModel(plan))
        firestoreSync?.syncIfThenPlan(plan)
    }

    suspend fun deleteIfThenPlan(id: String) {
        db.ifThenPlanDao().deleteById(id)
        firestoreSync?.deleteIfThenPlan(id)
    }

    suspend fun upsertSupplementLog(log: SupplementLog) {
        db.supplementLogDao().upsert(SupplementLogEntity.fromModel(log))
        firestoreSync?.syncSupplementLog(log)
    }

    suspend fun saveCycleData(data: CycleData) {
        db.cycleDao().upsert(CycleDataEntity.fromModel(data))
        firestoreSync?.syncCycleData(data)
    }

    suspend fun toggleDailyHabitCompleted() {
        val today = LocalDate.now().toString()
        val yesterday = LocalDate.now().minusDays(1).toString()
        val currentEntity = db.appPrefDao().getPref(KEY_HABIT_TRACKER)
        val current = if (currentEntity == null) HabitData()
        else runCatching { json.decodeFromString<HabitData>(currentEntity.value) }.getOrDefault(HabitData())
        if (current.lastCompletedDate == today) return
        val nextStreak = if (current.lastCompletedDate == yesterday) current.streak + 1 else 1
        val updated = HabitData(lastCompletedDate = today, streak = nextStreak)
        db.appPrefDao().setPref(AppPrefEntity(KEY_HABIT_TRACKER, json.encodeToString(updated)))
    }

    suspend fun exportBackupJson(): String {
        val payload = BackupPayload(
            profile = db.userProfileDao().getProfile()?.toModel(),
            journal = db.journalDao().getAll().map { it.toModel() },
            moods = db.moodDao().getAll().map { it.toModel() },
            habits = db.dailyHabitDao().getAll().map { it.toModel() },
            habitCompletions = db.habitCompletionDao().getAll().map { it.toModel() },
            energyLogs = db.energyCheckInDao().getAll().map { it.toModel() },
            thoughtReframes = db.thoughtReframeDao().getAll().map { it.toModel() },
            history = db.chatDao().getAll().map { it.toModel() },
            emergency = db.emergencyDao().getAll().map { it.toModel() },
            gratitude = db.gratitudeDao().getAll().map { it.toModel() },
            affirmations = db.affirmationDao().getAll().map { it.toModel() },
            breathingPatterns = db.customBreathingPatternDao().getAll().map { it.toModel() },
            breathing = db.breathingDao().getAll().map { it.toModel() },
            sleepLogs = db.sleepLogDao().getAll().map { it.toModel() },
            copingRatings = db.copingRatingDao().getAll().map { it.toModel() },
            ifThenPlans = db.ifThenPlanDao().getAll().map { it.toModel() },
            quizzes = db.quizLogDao().getAll().map { it.toModel() },
            supplementLogs = db.supplementLogDao().getAll().map { it.toModel() },
            cycle = db.cycleDao().getCycle()?.toModel()
        )
        return json.encodeToString(payload)
    }

    suspend fun importBackupJson(rawJson: String): Result<Unit> = runCatching {
        val payload = json.decodeFromString<BackupPayload>(rawJson)
        db.withTransaction {
            payload.profile?.let { db.userProfileDao().upsert(UserProfileEntity.fromModel(it)) }
            if (payload.journal.isNotEmpty()) {
                db.journalDao().clear()
                db.journalDao().insertAll(payload.journal.map { JournalEntryEntity.fromModel(it) })
            }
            if (payload.moods.isNotEmpty()) {
                db.moodDao().clear()
                db.moodDao().insertAll(payload.moods.map { MoodLogEntity.fromModel(it) })
            }
            if (payload.habits.isNotEmpty()) {
                db.dailyHabitDao().clear()
                db.dailyHabitDao().insertAll(payload.habits.map { DailyHabitEntity.fromModel(it) })
            }
            if (payload.habitCompletions.isNotEmpty()) {
                db.habitCompletionDao().clear()
                db.habitCompletionDao().insertAll(payload.habitCompletions.map { HabitCompletionEntity.fromModel(it) })
            }
            if (payload.energyLogs.isNotEmpty()) {
                db.energyCheckInDao().clear()
                db.energyCheckInDao().insertAll(payload.energyLogs.map { EnergyCheckInEntity.fromModel(it) })
            }
            if (payload.thoughtReframes.isNotEmpty()) {
                db.thoughtReframeDao().clear()
                db.thoughtReframeDao().insertAll(payload.thoughtReframes.map { ThoughtReframeEntity.fromModel(it) })
            }
            if (payload.history.isNotEmpty()) {
                db.chatDao().clear()
                db.chatDao().insertAll(payload.history.map { ChatMessageEntity.fromModel(it) })
            }
            if (payload.emergency.isNotEmpty()) {
                db.emergencyDao().clear()
                db.emergencyDao().insertAll(payload.emergency.map { EmergencyItemEntity.fromModel(it) })
            }
            if (payload.gratitude.isNotEmpty()) {
                db.gratitudeDao().clear()
                db.gratitudeDao().insertAll(payload.gratitude.map { GratitudeNoteEntity.fromModel(it) })
            }
            if (payload.affirmations.isNotEmpty()) {
                db.affirmationDao().clear()
                db.affirmationDao().insertAll(payload.affirmations.map { AffirmationEntity.fromModel(it) })
            }
            if (payload.breathingPatterns.isNotEmpty()) {
                db.customBreathingPatternDao().clear()
                db.customBreathingPatternDao().insertAll(payload.breathingPatterns.map { CustomBreathingPatternEntity.fromModel(it) })
            }
            if (payload.breathing.isNotEmpty()) {
                db.breathingDao().clear()
                db.breathingDao().insertAll(payload.breathing.map { BreathingSessionEntity.fromModel(it) })
            }
            if (payload.sleepLogs.isNotEmpty()) {
                db.sleepLogDao().clear()
                db.sleepLogDao().insertAll(payload.sleepLogs.map { SleepLogEntity.fromModel(it) })
            }
            if (payload.copingRatings.isNotEmpty()) {
                db.copingRatingDao().clear()
                db.copingRatingDao().insertAll(payload.copingRatings.map { CopingRatingEntity.fromModel(it) })
            }
            if (payload.ifThenPlans.isNotEmpty()) {
                db.ifThenPlanDao().clear()
                db.ifThenPlanDao().insertAll(payload.ifThenPlans.map { IfThenPlanEntity.fromModel(it) })
            }
            if (payload.quizzes.isNotEmpty()) {
                db.quizLogDao().clear()
                db.quizLogDao().insertAll(payload.quizzes.map { QuizLogEntity.fromModel(it) })
            }
            if (payload.supplementLogs.isNotEmpty()) {
                db.supplementLogDao().clear()
                db.supplementLogDao().insertAll(payload.supplementLogs.map { SupplementLogEntity.fromModel(it) })
            }
            payload.cycle?.let { db.cycleDao().upsert(CycleDataEntity.fromModel(it)) }
        }
    }

    suspend fun clearAllUserData() {
        db.withTransaction {
            db.userProfileDao().clear()
            db.journalDao().clear()
            db.moodDao().clear()
            db.dailyHabitDao().clear()
            db.habitCompletionDao().clear()
            db.energyCheckInDao().clear()
            db.thoughtReframeDao().clear()
            db.quizLogDao().clear()
            db.chatDao().clear()
            db.emergencyDao().clear()
            db.gratitudeDao().clear()
            db.affirmationDao().clear()
            db.customBreathingPatternDao().clear()
            db.breathingDao().clear()
            db.sleepLogDao().clear()
            db.copingRatingDao().clear()
            db.ifThenPlanDao().clear()
            db.supplementLogDao().clear()
            db.cycleDao().clear()
            db.appPrefDao().clear()
        }
    }
}
