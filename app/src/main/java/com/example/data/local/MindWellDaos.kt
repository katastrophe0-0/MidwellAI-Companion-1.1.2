package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clear()
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY id DESC")
    fun observeAll(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE content LIKE '%' || :query || '%' OR sentiment LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchEntries(query: String): Flow<List<JournalEntryEntity>>

    @Query("SELECT COUNT(*) FROM journal_entries")
    fun observeCount(): Flow<Int>

    @Query("SELECT * FROM journal_entries ORDER BY id DESC")
    suspend fun getAll(): List<JournalEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<JournalEntryEntity>)

    @Update
    suspend fun update(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM journal_entries")
    suspend fun clear()
}

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_logs ORDER BY date ASC")
    fun observeAll(): Flow<List<MoodLogEntity>>

    @Query("SELECT * FROM mood_logs ORDER BY date ASC")
    suspend fun getAll(): List<MoodLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: MoodLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<MoodLogEntity>)

    @Query("DELETE FROM mood_logs")
    suspend fun clear()
}

@Dao
interface DailyHabitDao {
    @Query("SELECT * FROM daily_habits WHERE active = 1")
    fun observeHabits(): Flow<List<DailyHabitEntity>>

    @Query("SELECT * FROM daily_habits")
    suspend fun getAll(): List<DailyHabitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(habit: DailyHabitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(habits: List<DailyHabitEntity>)

    @Query("DELETE FROM daily_habits WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM daily_habits")
    suspend fun clear()
}

@Dao
interface HabitCompletionDao {
    @Query("SELECT * FROM habit_completions")
    fun observeCompletions(): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE date >= :startDate AND date <= :endDate")
    fun observeCompletionsBetween(startDate: String, endDate: String): Flow<List<HabitCompletionEntity>>

    @Query("SELECT COUNT(*) FROM habit_completions WHERE completed = 1")
    fun observeTotalCompleted(): Flow<Int>

    @Query("SELECT * FROM habit_completions")
    suspend fun getAll(): List<HabitCompletionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(completion: HabitCompletionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(completions: List<HabitCompletionEntity>)

    @Query("DELETE FROM habit_completions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM habit_completions")
    suspend fun clear()
}

@Dao
interface EnergyCheckInDao {
    @Query("SELECT * FROM energy_checkins ORDER BY date DESC")
    fun observeAll(): Flow<List<EnergyCheckInEntity>>

    @Query("SELECT * FROM energy_checkins WHERE date = :date")
    fun observeByDate(date: String): Flow<EnergyCheckInEntity?>

    @Query("SELECT * FROM energy_checkins ORDER BY date DESC")
    suspend fun getAll(): List<EnergyCheckInEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: EnergyCheckInEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<EnergyCheckInEntity>)

    @Query("DELETE FROM energy_checkins")
    suspend fun clear()
}

@Dao
interface ThoughtReframeDao {
    @Query("SELECT * FROM thought_reframes ORDER BY id DESC")
    fun observeAll(): Flow<List<ThoughtReframeEntity>>

    @Query("SELECT * FROM thought_reframes ORDER BY id DESC")
    suspend fun getAll(): List<ThoughtReframeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ThoughtReframeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ThoughtReframeEntity>)

    @Query("DELETE FROM thought_reframes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM thought_reframes")
    suspend fun clear()
}

@Dao
interface QuizLogDao {
    @Query("SELECT * FROM quiz_logs ORDER BY id DESC")
    fun observeAll(): Flow<List<QuizLogEntity>>

    @Query("SELECT * FROM quiz_logs ORDER BY id DESC")
    suspend fun getAll(): List<QuizLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: QuizLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<QuizLogEntity>)

    @Query("DELETE FROM quiz_logs WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM quiz_logs")
    suspend fun clear()
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC, id ASC")
    fun observeAll(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC, id ASC")
    suspend fun getAll(): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(msg: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(msgs: List<ChatMessageEntity>)

    @Query("DELETE FROM chat_messages")
    suspend fun clear()
}

@Dao
interface EmergencyDao {
    @Query("SELECT * FROM emergency_items")
    fun observeAll(): Flow<List<EmergencyItemEntity>>

    @Query("SELECT * FROM emergency_items")
    suspend fun getAll(): List<EmergencyItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: EmergencyItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<EmergencyItemEntity>)

    @Query("DELETE FROM emergency_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM emergency_items")
    suspend fun clear()
}

@Dao
interface GratitudeDao {
    @Query("SELECT * FROM gratitude_notes ORDER BY id DESC")
    fun observeAll(): Flow<List<GratitudeNoteEntity>>

    @Query("SELECT * FROM gratitude_notes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomNote(): GratitudeNoteEntity?

    @Query("SELECT * FROM gratitude_notes ORDER BY id DESC")
    suspend fun getAll(): List<GratitudeNoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: GratitudeNoteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<GratitudeNoteEntity>)

    @Query("DELETE FROM gratitude_notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM gratitude_notes")
    suspend fun clear()
}

@Dao
interface AffirmationDao {
    @Query("SELECT * FROM affirmation_items ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<AffirmationEntity>>

    @Query("SELECT * FROM affirmation_items ORDER BY createdAt DESC")
    suspend fun getAll(): List<AffirmationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: AffirmationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AffirmationEntity>)

    @Query("UPDATE affirmation_items SET pinned = 0")
    suspend fun clearAllPinned()

    @Query("DELETE FROM affirmation_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM affirmation_items")
    suspend fun clear()
}

@Dao
interface CustomBreathingPatternDao {
    @Query("SELECT * FROM custom_breathing_patterns ORDER BY name ASC")
    fun observeAll(): Flow<List<CustomBreathingPatternEntity>>

    @Query("SELECT * FROM custom_breathing_patterns ORDER BY name ASC")
    suspend fun getAll(): List<CustomBreathingPatternEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pattern: CustomBreathingPatternEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patterns: List<CustomBreathingPatternEntity>)

    @Query("DELETE FROM custom_breathing_patterns WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM custom_breathing_patterns")
    suspend fun clear()
}

@Dao
interface BreathingDao {
    @Query("SELECT * FROM breathing_sessions ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<BreathingSessionEntity>>

    @Query("SELECT COUNT(*) FROM breathing_sessions")
    fun observeCount(): Flow<Int>

    @Query("SELECT * FROM breathing_sessions ORDER BY timestamp DESC")
    suspend fun getAll(): List<BreathingSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: BreathingSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<BreathingSessionEntity>)

    @Query("DELETE FROM breathing_sessions")
    suspend fun clear()
}

@Dao
interface SleepLogDao {
    @Query("SELECT * FROM sleep_logs ORDER BY date DESC")
    fun observeAll(): Flow<List<SleepLogEntity>>

    @Query("SELECT * FROM sleep_logs ORDER BY date DESC")
    suspend fun getAll(): List<SleepLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SleepLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SleepLogEntity>)

    @Query("DELETE FROM sleep_logs")
    suspend fun clear()
}

@Dao
interface CopingRatingDao {
    @Query("SELECT * FROM coping_ratings ORDER BY id DESC")
    fun observeAll(): Flow<List<CopingRatingEntity>>

    @Query("SELECT * FROM coping_ratings ORDER BY id DESC")
    suspend fun getAll(): List<CopingRatingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CopingRatingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CopingRatingEntity>)

    @Query("DELETE FROM coping_ratings")
    suspend fun clear()
}

@Dao
interface IfThenPlanDao {
    @Query("SELECT * FROM if_then_plans ORDER BY triggerText ASC")
    fun observeAll(): Flow<List<IfThenPlanEntity>>

    @Query("SELECT * FROM if_then_plans ORDER BY triggerText ASC")
    suspend fun getAll(): List<IfThenPlanEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: IfThenPlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<IfThenPlanEntity>)

    @Query("DELETE FROM if_then_plans WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM if_then_plans")
    suspend fun clear()
}

@Dao
interface SupplementLogDao {
    @Query("SELECT * FROM supplement_logs ORDER BY date DESC")
    fun observeAll(): Flow<List<SupplementLogEntity>>

    @Query("SELECT * FROM supplement_logs ORDER BY date DESC")
    suspend fun getAll(): List<SupplementLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SupplementLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SupplementLogEntity>)

    @Query("DELETE FROM supplement_logs")
    suspend fun clear()
}

@Dao
interface CycleDao {
    @Query("SELECT * FROM cycle_data WHERE id = 1")
    fun observeCycle(): Flow<CycleDataEntity?>

    @Query("SELECT * FROM cycle_data WHERE id = 1")
    suspend fun getCycle(): CycleDataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CycleDataEntity)

    @Query("DELETE FROM cycle_data")
    suspend fun clear()
}

@Dao
interface AppPrefDao {
    @Query("SELECT * FROM app_prefs WHERE `key` = :key")
    fun observePref(key: String): Flow<AppPrefEntity?>

    @Query("SELECT * FROM app_prefs WHERE `key` = :key")
    suspend fun getPref(key: String): AppPrefEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPref(entity: AppPrefEntity)

    @Query("DELETE FROM app_prefs WHERE `key` = :key")
    suspend fun deletePref(key: String)

    @Query("DELETE FROM app_prefs")
    suspend fun clear()
}
