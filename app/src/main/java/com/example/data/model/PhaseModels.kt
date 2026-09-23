package com.example.data.model

enum class PhaseType {
    STANDARD,
    MINIGAME_SOUND_SYLLABLE,  // Minigame 1: Som e Sílaba (auditivo)
    MINIGAME_DRAG_BUILD,       // Minigame 2: Arraste e Monte (memória cotidiana)
    MINIGAME_CONNECT_DOTS      // Minigame 3: Ligando os Pontos (associação)
}

enum class PhaseStatus {
    COMPLETED,
    CURRENT,
    LOCKED
}

data class PhaseNode(
    val id: Int,
    val title: String,
    val subtitle: String,
    val type: PhaseType,
    val status: PhaseStatus,
    val totalActivities: Int = 10,
    val currentActivityIndex: Int = 0,
    val xOffsetFraction: Float = 0f // For zigzag trail: -0.45f (left), 0f (center), 0.45f (right)
)

data class PhaseActivity(
    val activityNumber: Int,
    val promptText: String,
    val audioScript: String,
    val options: List<String>,
    val correctIndex: Int,
    val hint: String
)

data class ConnectPair(
    val id: Int,
    val iconEmoji: String,
    val labelImage: String,
    val word: String
)
