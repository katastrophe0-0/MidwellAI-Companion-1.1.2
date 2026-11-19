package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.serialization.json.Json

class StringListConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(list: List<String>): String = json.encodeToString(list)

    @TypeConverter
    fun toStringList(data: String): List<String> =
        if (data.isBlank()) emptyList() else runCatching { json.decodeFromString<List<String>>(data) }.getOrDefault(emptyList())
}

@Database(
    entities = [
        UserProfileEntity::class,
        JournalEntryEntity::class,
        MoodLogEntity::class,
        DailyHabitEntity::class,
        HabitCompletionEntity::class,
        EnergyCheckInEntity::class,
        ThoughtReframeEntity::class,
        QuizLogEntity::class,
        ChatMessageEntity::class,
        EmergencyItemEntity::class,
        GratitudeNoteEntity::class,
        AffirmationEntity::class,
        CustomBreathingPatternEntity::class,
        BreathingSessionEntity::class,
        SleepLogEntity::class,
        CopingRatingEntity::class,
        IfThenPlanEntity::class,
        SupplementLogEntity::class,
        CycleDataEntity::class,
        AppPrefEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun journalDao(): JournalDao
    abstract fun moodDao(): MoodDao
    abstract fun dailyHabitDao(): DailyHabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
    abstract fun energyCheckInDao(): EnergyCheckInDao
    abstract fun thoughtReframeDao(): ThoughtReframeDao
    abstract fun quizLogDao(): QuizLogDao
    abstract fun chatDao(): ChatDao
    abstract fun emergencyDao(): EmergencyDao
    abstract fun gratitudeDao(): GratitudeDao
    abstract fun affirmationDao(): AffirmationDao
    abstract fun customBreathingPatternDao(): CustomBreathingPatternDao
    abstract fun breathingDao(): BreathingDao
    abstract fun sleepLogDao(): SleepLogDao
    abstract fun copingRatingDao(): CopingRatingDao
    abstract fun ifThenPlanDao(): IfThenPlanDao
    abstract fun supplementLogDao(): SupplementLogDao
    abstract fun cycleDao(): CycleDao
    abstract fun appPrefDao(): AppPrefDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mindwell_local.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
