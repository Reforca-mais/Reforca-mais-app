package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long = 1L,
    val name: String = "Roberto",
    val personaType: String = "ROBERTO", // "ROBERTO" or "MARIA"
    val ageRange: String = "41 a 59 anos",
    val occupation: String = "Agricultor em agricultura familiar",
    val city: String = "Lagarto, Sergipe",
    val familyIncome: String = "Até R$ 1.500,00",
    val educationalLevel: String = "Sem escolaridade formal",
    val selectedPlanId: String = "FREE",
    val progressPercentage: Int = 79,
    val currentQuestionIndex: Int = 0
)

@Entity(tableName = "quiz_questions")
data class QuizQuestionEntity(
    @PrimaryKey val id: Int,
    val questionNumber: Int,
    val title: String,
    val prompt: String,
    val audioNarration: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int,
    val explanation: String
)

@Entity(tableName = "subscription_plans")
data class SubscriptionPlanEntity(
    @PrimaryKey val id: String,
    val name: String,
    val priceMonthly: String,
    val priceAnnual: String,
    val features: String,
    val isRecommended: Boolean = false,
    val badge: String? = null
)

@Entity(tableName = "progress_tracking")
data class ProgressTrackingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val questionNumber: Int,
    val selectedOptionIndex: Int,
    val wasCorrect: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
