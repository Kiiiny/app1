package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.HabitCompletion
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND dateString = :dateString LIMIT 1")
    suspend fun getCompletion(habitId: Long, dateString: String): HabitCompletion?

    @Query("SELECT * FROM habit_completions WHERE dateString = :dateString")
    fun getCompletionsForDate(dateString: String): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE dateString = :dateString")
    suspend fun getCompletionsListForDate(dateString: String): List<HabitCompletion>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY dateString DESC")
    fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY dateString DESC")
    suspend fun getCompletionsListForHabit(habitId: Long): List<HabitCompletion>

    @Query("SELECT * FROM habit_completions ORDER BY dateString DESC")
    fun getAllCompletions(): Flow<List<HabitCompletion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletion): Long

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND dateString = :dateString")
    suspend fun deleteCompletion(habitId: Long, dateString: String)

    @Query("DELETE FROM habit_completions WHERE dateString = :dateString")
    suspend fun deleteCompletionsForDate(dateString: String)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteAllCompletionsForHabit(habitId: Long)

    @Query("DELETE FROM habit_completions")
    suspend fun deleteAllCompletions()
}
