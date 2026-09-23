package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ProgressTrackingEntity
import com.example.data.model.QuizQuestionEntity
import com.example.data.model.SubscriptionPlanEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReforcaDao {

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: Long): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM quiz_questions ORDER BY questionNumber ASC")
    fun getAllQuestions(): Flow<List<QuizQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuizQuestionEntity>)

    @Query("SELECT * FROM subscription_plans")
    fun getAllPlans(): Flow<List<SubscriptionPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<SubscriptionPlanEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ProgressTrackingEntity)

    @Query("SELECT * FROM progress_tracking WHERE userId = :userId ORDER BY timestamp DESC")
    fun getProgressHistory(userId: Long): Flow<List<ProgressTrackingEntity>>

    @Query("UPDATE users SET progressPercentage = :percentage, currentQuestionIndex = :questionIndex WHERE id = :userId")
    suspend fun updateProgress(userId: Long, percentage: Int, questionIndex: Int)

    @Query("UPDATE users SET selectedPlanId = :planId WHERE id = :userId")
    suspend fun updateUserPlan(userId: Long, planId: String)

    @Query("UPDATE users SET name = :name, personaType = :personaType WHERE id = :userId")
    suspend fun switchPersona(userId: Long, name: String, personaType: String)
}
