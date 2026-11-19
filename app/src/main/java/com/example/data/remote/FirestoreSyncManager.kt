package com.example.data.remote

import android.content.Context
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.AffirmationEntity
import com.example.data.local.BreathingSessionEntity
import com.example.data.local.CopingRatingEntity
import com.example.data.local.CustomBreathingPatternEntity
import com.example.data.local.CycleDataEntity
import com.example.data.local.DailyHabitEntity
import com.example.data.local.EmergencyItemEntity
import com.example.data.local.EnergyCheckInEntity
import com.example.data.local.GratitudeNoteEntity
import com.example.data.local.HabitCompletionEntity
import com.example.data.local.IfThenPlanEntity
import com.example.data.local.JournalEntryEntity
import com.example.data.local.MoodLogEntity
import com.example.data.local.QuizLogEntity
import com.example.data.local.SleepLogEntity
import com.example.data.local.SupplementLogEntity
import com.example.data.local.ThoughtReframeEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.AffirmationItem
import com.example.data.model.BreathingSession
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
import com.example.data.model.MindWellThemeId
import com.example.data.model.MoodLog
import com.example.data.model.QuizLog
import com.example.data.model.SleepLog
import com.example.data.model.SubscriptionTier
import com.example.data.model.SupplementLog
import com.example.data.model.ThoughtReframeLog
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class FirestoreSyncManager(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        val uid = auth.currentUser?.uid
        check(!uid.isNullOrBlank()) {
            "User must be signed in with Google before accessing Firestore."
        }
        return uid
    }

    private fun sanitizeDocId(raw: String): String =
        raw.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
            .take(120)
            .ifBlank { "doc_${System.currentTimeMillis()}" }

    private suspend fun upsertDocument(
        docRef: DocumentReference,
        uid: String,
        mutableFields: Map<String, Any?>
    ): Result<Unit> {
        val cleanFields = mutableFields.filterValues { it != null } as Map<String, Any>
        return try {
            val snap = docRef.get().await()
            if (snap.exists()) {
                docRef.update(
                    cleanFields + ("updatedAt" to FieldValue.serverTimestamp())
                ).await()
            } else {
                docRef.set(
                    cleanFields + mapOf(
                        "userId" to uid,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun syncUserProfile(profile: UserProfile): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid)
        val fields = mapOf<String, Any?>(
            "name" to profile.name.take(80),
            "avatar" to profile.avatar.ifBlank { "🌿" }.take(16),
            "goals" to profile.goals.take(12),
            "challenges" to profile.challenges.take(12),
            "zodiacSign" to profile.zodiacSign?.take(32),
            "birthDate" to profile.birthDate?.take(32),
            "birthTime" to profile.birthTime?.take(32),
            "birthLocation" to profile.birthLocation?.take(120),
            "onboardingCompleted" to profile.onboardingCompleted,
            "subscriptionTier" to profile.subscriptionTier.name,
            "enableCycleTracking" to profile.enableCycleTracking,
            "enableAstrology" to profile.enableAstrology,
            "enableBreathing" to profile.enableBreathing,
            "enableMeditation" to profile.enableMeditation,
            "showSupplementLog" to profile.preferences.showSupplementLog,
            "themeId" to profile.preferences.themeId.name,
            "fontScale" to profile.preferences.fontScale.toDouble().coerceIn(0.85, 1.4),
            "reduceMotion" to profile.preferences.reduceMotion
        )
        return upsertDocument(docRef, uid, fields)
    }

    suspend fun syncJournalEntry(entry: JournalEntry): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docId = sanitizeDocId("journal_${entry.id}")
        val docRef = db.collection("users").document(uid).collection("journals").document(docId)
        val fields = mapOf<String, Any?>(
            "date" to entry.date.take(40),
            "content" to entry.content.take(10000),
            "prompt" to entry.prompt?.take(500),
            "tags" to entry.tags.take(15),
            "moodRating" to entry.moodRating?.coerceIn(1, 5),
            "analysis" to entry.analysis?.take(4000),
            "sentiment" to entry.sentiment?.take(80),
            "themes" to entry.themes.take(10)
        )
        return upsertDocument(docRef, uid, fields)
    }

    suspend fun deleteJournalEntry(id: Long): Result<Unit> =
        deleteSubDoc("journals", sanitizeDocId("journal_$id"))

    suspend fun syncMoodLog(log: MoodLog): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docId = sanitizeDocId(log.date)
        val docRef = db.collection("users").document(uid).collection("moods").document(docId)
        return upsertDocument(
            docRef, uid, mapOf(
                "date" to log.date.take(20),
                "mood" to log.mood.coerceIn(1, 5),
                "tags" to log.tags.take(15)
            )
        )
    }

    suspend fun syncDailyHabit(habit: DailyHabit): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("habits").document(sanitizeDocId(habit.id))
        return upsertDocument(
            docRef, uid, mapOf(
                "title" to habit.title.take(80),
                "category" to habit.category.take(40),
                "active" to habit.active
            )
        )
    }

    suspend fun deleteDailyHabit(habitId: String): Result<Unit> =
        deleteSubDoc("habits", sanitizeDocId(habitId))

    suspend fun syncHabitCompletion(c: HabitCompletion): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("habitCompletions").document(sanitizeDocId(c.id))
        return upsertDocument(
            docRef, uid, mapOf(
                "habitId" to c.habitId.take(128),
                "date" to c.date.take(20),
                "completed" to c.completed
            )
        )
    }

    suspend fun syncEnergyCheckIn(checkIn: EnergyCheckIn): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("energyLogs").document(sanitizeDocId(checkIn.date))
        return upsertDocument(
            docRef, uid, mapOf(
                "date" to checkIn.date.take(20),
                "mentalBattery" to checkIn.mentalBattery.coerceIn(1, 100),
                "physicalBattery" to checkIn.physicalBattery.coerceIn(1, 100)
            )
        )
    }

    suspend fun syncThoughtReframe(log: ThoughtReframeLog): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("thoughtReframes").document(sanitizeDocId("reframe_${log.id}"))
        return upsertDocument(
            docRef, uid, mapOf(
                "date" to log.date.take(40),
                "thought" to log.thought.take(2000),
                "distortion" to log.distortion.take(120),
                "explanation" to log.explanation.take(2000),
                "reframes" to log.reframes.take(6)
            )
        )
    }

    suspend fun syncGratitudeNote(note: GratitudeNote): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("gratitudeNotes").document(sanitizeDocId("grat_${note.id}"))
        return upsertDocument(
            docRef, uid, mapOf(
                "text" to note.text.take(1000),
                "date" to note.date.take(40),
                "colorIndex" to note.colorIndex.coerceIn(0, 10),
                "rotation" to note.rotation.toDouble()
            )
        )
    }

    suspend fun deleteGratitudeNote(id: Long): Result<Unit> =
        deleteSubDoc("gratitudeNotes", sanitizeDocId("grat_$id"))

    suspend fun syncAffirmation(item: AffirmationItem): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("affirmations").document(sanitizeDocId(item.id))
        return upsertDocument(
            docRef, uid, mapOf(
                "text" to item.text.take(500),
                "favorite" to item.favorite,
                "pinned" to item.pinned,
                "custom" to item.custom
            )
        )
    }

    suspend fun syncBreathingPattern(pattern: CustomBreathingPattern): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("breathingPatterns").document(sanitizeDocId(pattern.id))
        return upsertDocument(
            docRef, uid, mapOf(
                "name" to pattern.name.take(80),
                "inhaleSeconds" to pattern.inhaleSeconds.coerceIn(1, 30),
                "holdInSeconds" to pattern.holdInSeconds.coerceIn(0, 30),
                "exhaleSeconds" to pattern.exhaleSeconds.coerceIn(1, 30),
                "holdOutSeconds" to pattern.holdOutSeconds.coerceIn(0, 30)
            )
        )
    }

    suspend fun deleteBreathingPattern(id: String): Result<Unit> =
        deleteSubDoc("breathingPatterns", sanitizeDocId(id))

    suspend fun syncBreathingSession(session: BreathingSession): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("breathingSessions").document(sanitizeDocId("breath_${session.id}"))
        return upsertDocument(
            docRef, uid, mapOf(
                "date" to session.date.take(20),
                "durationSeconds" to session.durationSeconds.coerceIn(1, 7200),
                "technique" to session.technique.take(120)
            )
        )
    }

    suspend fun syncSleepLog(log: SleepLog): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("sleepLogs").document(sanitizeDocId(log.date))
        return upsertDocument(
            docRef, uid, mapOf(
                "date" to log.date.take(20),
                "checklistDone" to log.checklistDone.take(15),
                "qualityRating" to log.qualityRating.coerceIn(0, 5),
                "notes" to log.notes.take(1000)
            )
        )
    }

    suspend fun syncCopingRating(rating: CopingRating): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("copingRatings").document(sanitizeDocId("coping_${rating.id}"))
        return upsertDocument(
            docRef, uid, mapOf(
                "strategyTitle" to rating.strategyTitle.take(120),
                "problemSummary" to rating.problemSummary.take(400),
                "stars" to rating.stars.coerceIn(1, 5)
            )
        )
    }

    suspend fun syncIfThenPlan(plan: IfThenPlan): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("ifThenTriggers").document(sanitizeDocId(plan.id))
        return upsertDocument(
            docRef, uid, mapOf(
                "triggerText" to plan.triggerText.take(300),
                "actionText" to plan.actionText.take(500),
                "safeSpace" to plan.safeSpace.take(200)
            )
        )
    }

    suspend fun deleteIfThenPlan(id: String): Result<Unit> =
        deleteSubDoc("ifThenTriggers", sanitizeDocId(id))

    suspend fun syncQuizLog(log: QuizLog): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("quizzes").document(sanitizeDocId("quiz_${log.id}"))
        return upsertDocument(
            docRef, uid, mapOf(
                "date" to log.date.take(40),
                "quizId" to log.quizId.take(40),
                "quizLabel" to log.quizLabel.take(80),
                "score" to log.score.coerceIn(0, 200),
                "maxScore" to log.maxScore.coerceIn(1, 200),
                "level" to log.level.take(120)
            )
        )
    }

    suspend fun deleteQuizLog(id: Long): Result<Unit> =
        deleteSubDoc("quizzes", sanitizeDocId("quiz_$id"))

    suspend fun syncSupplementLog(log: SupplementLog): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("supplements").document(sanitizeDocId(log.date))
        return upsertDocument(
            docRef, uid, mapOf(
                "date" to log.date.take(20),
                "itemsDone" to log.itemsDone.take(20)
            )
        )
    }

    suspend fun syncEmergencyItem(item: EmergencyPlanItem): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("emergencyItems").document(sanitizeDocId(item.id))
        return upsertDocument(
            docRef, uid, mapOf(
                "type" to item.type,
                "label" to item.label.take(80),
                "value" to item.value.take(300)
            )
        )
    }

    suspend fun deleteEmergencyItem(id: String): Result<Unit> =
        deleteSubDoc("emergencyItems", sanitizeDocId(id))

    suspend fun syncCycleData(data: CycleData): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection("cycleData").document("primary")
        return upsertDocument(
            docRef, uid, mapOf(
                "lastPeriodStart" to data.lastPeriodStart.take(20),
                "cycleLength" to data.cycleLength.coerceIn(15, 60),
                "periodLength" to data.periodLength.coerceIn(1, 15)
            )
        )
    }

    private suspend fun deleteSubDoc(collection: String, docId: String): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val docRef = db.collection("users").document(uid).collection(collection).document(docId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    /**
     * Pulls remote Firestore records for the authenticated user into local Room cache on sign-in
     * so switching devices restores user progress seamlessly.
     */
    suspend fun pullCloudStateIntoRoom(localDb: AppDatabase): Result<Unit> {
        val uid = runCatching { requireUserId() }.getOrElse { return Result.failure(it) }
        val userRef = db.collection("users").document(uid)
        return try {
            val profileSnap = userRef.get().await()
            if (profileSnap.exists() && localDb.userProfileDao().getProfile() == null) {
                val data = profileSnap.data.orEmpty()
                val tierName = data["subscriptionTier"] as? String ?: "FREE"
                val themeName = data["themeId"] as? String ?: "MIDNIGHT_INDIGO"
                val existingPrefs = UserProfile().preferences
                val profile = UserProfile(
                    name = data["name"] as? String ?: "",
                    avatar = data["avatar"] as? String ?: "🌿",
                    goals = (data["goals"] as? List<*>)?.filterIsInstance<String>().orEmpty(),
                    challenges = (data["challenges"] as? List<*>)?.filterIsInstance<String>().orEmpty(),
                    zodiacSign = data["zodiacSign"] as? String,
                    birthDate = data["birthDate"] as? String,
                    birthTime = data["birthTime"] as? String,
                    birthLocation = data["birthLocation"] as? String,
                    onboardingCompleted = data["onboardingCompleted"] as? Boolean ?: false,
                    subscriptionTier = enumValues<SubscriptionTier>().firstOrNull { it.name == tierName } ?: SubscriptionTier.FREE,
                    enableCycleTracking = data["enableCycleTracking"] as? Boolean ?: false,
                    enableAstrology = data["enableAstrology"] as? Boolean ?: false,
                    enableBreathing = data["enableBreathing"] as? Boolean ?: true,
                    enableMeditation = data["enableMeditation"] as? Boolean ?: true,
                    preferences = existingPrefs.copy(
                        showSupplementLog = data["showSupplementLog"] as? Boolean ?: false,
                        themeId = enumValues<MindWellThemeId>().firstOrNull { it.name == themeName } ?: MindWellThemeId.MIDNIGHT_INDIGO,
                        fontScale = ((data["fontScale"] as? Number)?.toFloat() ?: 1.0f),
                        reduceMotion = data["reduceMotion"] as? Boolean ?: false
                    )
                )
                localDb.userProfileDao().upsert(UserProfileEntity.fromModel(profile))
            }

            if (localDb.moodDao().getAll().isEmpty()) {
                val moods = userRef.collection("moods").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val date = data["date"] as? String ?: return@mapNotNull null
                    val mood = (data["mood"] as? Number)?.toInt() ?: 3
                    val tags = (data["tags"] as? List<*>)?.filterIsInstance<String>().orEmpty()
                    MoodLogEntity(date = date, mood = mood, tags = tags)
                }
                if (moods.isNotEmpty()) localDb.moodDao().insertAll(moods)
            }

            if (localDb.journalDao().getAll().isEmpty()) {
                val journals = userRef.collection("journals").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val content = data["content"] as? String ?: return@mapNotNull null
                    JournalEntryEntity(
                        date = data["date"] as? String ?: "",
                        content = content,
                        prompt = data["prompt"] as? String,
                        tags = (data["tags"] as? List<*>)?.filterIsInstance<String>().orEmpty(),
                        moodRating = (data["moodRating"] as? Number)?.toInt(),
                        analysis = data["analysis"] as? String,
                        sentiment = data["sentiment"] as? String,
                        themes = (data["themes"] as? List<*>)?.filterIsInstance<String>().orEmpty()
                    )
                }
                if (journals.isNotEmpty()) localDb.journalDao().insertAll(journals)
            }

            if (localDb.dailyHabitDao().getAll().isEmpty()) {
                val habits = userRef.collection("habits").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    DailyHabitEntity(
                        id = doc.id,
                        title = data["title"] as? String ?: return@mapNotNull null,
                        category = data["category"] as? String ?: "Wellness",
                        active = data["active"] as? Boolean ?: true
                    )
                }
                if (habits.isNotEmpty()) localDb.dailyHabitDao().insertAll(habits)
            }

            if (localDb.habitCompletionDao().getAll().isEmpty()) {
                val completions = userRef.collection("habitCompletions").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    HabitCompletionEntity(
                        id = doc.id,
                        habitId = data["habitId"] as? String ?: return@mapNotNull null,
                        date = data["date"] as? String ?: return@mapNotNull null,
                        completed = data["completed"] as? Boolean ?: true
                    )
                }
                if (completions.isNotEmpty()) localDb.habitCompletionDao().insertAll(completions)
            }

            if (localDb.energyCheckInDao().getAll().isEmpty()) {
                val energy = userRef.collection("energyLogs").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    EnergyCheckInEntity(
                        date = data["date"] as? String ?: return@mapNotNull null,
                        mentalBattery = (data["mentalBattery"] as? Number)?.toInt() ?: 75,
                        physicalBattery = (data["physicalBattery"] as? Number)?.toInt() ?: 75
                    )
                }
                if (energy.isNotEmpty()) localDb.energyCheckInDao().insertAll(energy)
            }

            if (localDb.thoughtReframeDao().getAll().isEmpty()) {
                val reframes = userRef.collection("thoughtReframes").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    ThoughtReframeEntity(
                        date = data["date"] as? String ?: "",
                        thought = data["thought"] as? String ?: return@mapNotNull null,
                        distortion = data["distortion"] as? String ?: "Cognitive Distortion",
                        explanation = data["explanation"] as? String ?: "",
                        reframes = (data["reframes"] as? List<*>)?.filterIsInstance<String>().orEmpty()
                    )
                }
                if (reframes.isNotEmpty()) localDb.thoughtReframeDao().insertAll(reframes)
            }

            if (localDb.gratitudeDao().getAll().isEmpty()) {
                val notes = userRef.collection("gratitudeNotes").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    GratitudeNoteEntity(
                        text = data["text"] as? String ?: return@mapNotNull null,
                        date = data["date"] as? String ?: "",
                        colorIndex = (data["colorIndex"] as? Number)?.toInt() ?: 0,
                        rotation = (data["rotation"] as? Number)?.toFloat() ?: 0f
                    )
                }
                if (notes.isNotEmpty()) localDb.gratitudeDao().insertAll(notes)
            }

            if (localDb.affirmationDao().getAll().isEmpty()) {
                val affirmations = userRef.collection("affirmations").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    AffirmationEntity(
                        id = doc.id,
                        text = data["text"] as? String ?: return@mapNotNull null,
                        favorite = data["favorite"] as? Boolean ?: false,
                        pinned = data["pinned"] as? Boolean ?: false,
                        custom = data["custom"] as? Boolean ?: false
                    )
                }
                if (affirmations.isNotEmpty()) localDb.affirmationDao().insertAll(affirmations)
            }

            if (localDb.customBreathingPatternDao().getAll().isEmpty()) {
                val patterns = userRef.collection("breathingPatterns").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    CustomBreathingPatternEntity(
                        id = doc.id,
                        name = data["name"] as? String ?: return@mapNotNull null,
                        inhaleSeconds = (data["inhaleSeconds"] as? Number)?.toInt() ?: 4,
                        holdInSeconds = (data["holdInSeconds"] as? Number)?.toInt() ?: 4,
                        exhaleSeconds = (data["exhaleSeconds"] as? Number)?.toInt() ?: 4,
                        holdOutSeconds = (data["holdOutSeconds"] as? Number)?.toInt() ?: 4
                    )
                }
                if (patterns.isNotEmpty()) localDb.customBreathingPatternDao().insertAll(patterns)
            }

            if (localDb.breathingDao().getAll().isEmpty()) {
                val sessions = userRef.collection("breathingSessions").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    BreathingSessionEntity(
                        date = data["date"] as? String ?: return@mapNotNull null,
                        timestamp = System.currentTimeMillis(),
                        durationSeconds = (data["durationSeconds"] as? Number)?.toInt() ?: 60,
                        technique = data["technique"] as? String ?: "Breathing"
                    )
                }
                if (sessions.isNotEmpty()) localDb.breathingDao().insertAll(sessions)
            }

            if (localDb.sleepLogDao().getAll().isEmpty()) {
                val sleeps = userRef.collection("sleepLogs").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    SleepLogEntity(
                        date = data["date"] as? String ?: return@mapNotNull null,
                        checklistDone = (data["checklistDone"] as? List<*>)?.filterIsInstance<String>().orEmpty(),
                        qualityRating = (data["qualityRating"] as? Number)?.toInt() ?: 0,
                        notes = data["notes"] as? String ?: ""
                    )
                }
                if (sleeps.isNotEmpty()) localDb.sleepLogDao().insertAll(sleeps)
            }

            if (localDb.copingRatingDao().getAll().isEmpty()) {
                val ratings = userRef.collection("copingRatings").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    CopingRatingEntity(
                        strategyTitle = data["strategyTitle"] as? String ?: return@mapNotNull null,
                        problemSummary = data["problemSummary"] as? String ?: "",
                        stars = (data["stars"] as? Number)?.toInt() ?: 5
                    )
                }
                if (ratings.isNotEmpty()) localDb.copingRatingDao().insertAll(ratings)
            }

            if (localDb.ifThenPlanDao().getAll().isEmpty()) {
                val plans = userRef.collection("ifThenTriggers").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    IfThenPlanEntity(
                        id = doc.id,
                        triggerText = data["triggerText"] as? String ?: return@mapNotNull null,
                        actionText = data["actionText"] as? String ?: return@mapNotNull null,
                        safeSpace = data["safeSpace"] as? String ?: ""
                    )
                }
                if (plans.isNotEmpty()) localDb.ifThenPlanDao().insertAll(plans)
            }

            if (localDb.quizLogDao().getAll().isEmpty()) {
                val quizzes = userRef.collection("quizzes").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    QuizLogEntity(
                        date = data["date"] as? String ?: "",
                        quizId = data["quizId"] as? String ?: return@mapNotNull null,
                        quizLabel = data["quizLabel"] as? String ?: "",
                        score = (data["score"] as? Number)?.toInt() ?: 0,
                        maxScore = (data["maxScore"] as? Number)?.toInt() ?: 1,
                        level = data["level"] as? String ?: ""
                    )
                }
                if (quizzes.isNotEmpty()) localDb.quizLogDao().insertAll(quizzes)
            }

            if (localDb.supplementLogDao().getAll().isEmpty()) {
                val supps = userRef.collection("supplements").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    SupplementLogEntity(
                        date = data["date"] as? String ?: return@mapNotNull null,
                        itemsDone = (data["itemsDone"] as? List<*>)?.filterIsInstance<String>().orEmpty()
                    )
                }
                if (supps.isNotEmpty()) localDb.supplementLogDao().insertAll(supps)
            }

            if (localDb.emergencyDao().getAll().isEmpty()) {
                val items = userRef.collection("emergencyItems").get().await().documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    EmergencyItemEntity(
                        id = doc.id,
                        type = data["type"] as? String ?: "activity",
                        label = data["label"] as? String ?: return@mapNotNull null,
                        value = data["value"] as? String ?: return@mapNotNull null
                    )
                }
                if (items.isNotEmpty()) localDb.emergencyDao().insertAll(items)
            }

            if (localDb.cycleDao().getCycle() == null) {
                val cycleSnap = userRef.collection("cycleData").document("primary").get().await()
                if (cycleSnap.exists()) {
                    val data = cycleSnap.data.orEmpty()
                    val start = data["lastPeriodStart"] as? String
                    if (!start.isNullOrBlank()) {
                        localDb.cycleDao().upsert(
                            CycleDataEntity(
                                lastPeriodStart = start,
                                cycleLength = (data["cycleLength"] as? Number)?.toInt() ?: 28,
                                periodLength = (data["periodLength"] as? Number)?.toInt() ?: 5
                            )
                        )
                    }
                }
            }

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, "users/$uid")
            Result.failure(e)
        }
    }
}
