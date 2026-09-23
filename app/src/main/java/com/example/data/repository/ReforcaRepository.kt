package com.example.data.repository

import com.example.data.local.ReforcaDao
import com.example.data.model.ProgressTrackingEntity
import com.example.data.model.QuizQuestionEntity
import com.example.data.model.SubscriptionPlanEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

class ReforcaRepository(private val dao: ReforcaDao) {

    val currentUser: Flow<UserEntity?> = dao.getUserById(1L)
    val questions: Flow<List<QuizQuestionEntity>> = dao.getAllQuestions()
    val plans: Flow<List<SubscriptionPlanEntity>> = dao.getAllPlans()
    val progressHistory: Flow<List<ProgressTrackingEntity>> = dao.getProgressHistory(1L)

    suspend fun initializeDefaultsIfNeeded() {
        // Default User: Roberto (matching the persona from the presentation)
        val defaultUser = UserEntity(
            id = 1L,
            name = "Roberto",
            personaType = "ROBERTO",
            ageRange = "41 a 59 anos",
            occupation = "Agricultor em agricultura familiar",
            city = "Lagarto, Sergipe",
            familyIncome = "Até R$ 1.500,00",
            educationalLevel = "Sem escolaridade formal",
            selectedPlanId = "FREE",
            progressPercentage = 79,
            currentQuestionIndex = 0
        )
        dao.insertUser(defaultUser)

        // Default Questions: Includes the exact question from user mockup ("Questão 7")
        val sampleQuestions = listOf(
            QuizQuestionEntity(
                id = 1,
                questionNumber = 7,
                title = "Questão 7",
                prompt = "Quais dos conjuntos de palavras abaixo, estão TODAS escritas de maneira incorreta?",
                audioNarration = "Questão 7. Quais dos conjuntos de palavras abaixo estão todas escritas de maneira incorreta? Ouça com atenção cada opção tocando nela.",
                optionA = "vrido, pão, xinelo e faca",
                optionB = "carro, onibos, vassoura e pé",
                optionC = "casa, vinho, cabelo e ovo",
                optionD = "celebro, irriba, peda e oio",
                correctOptionIndex = 3, // celebro (cérebro), irriba (arriba), peda (pedra), oio (olho) - ALL are incorrect!
                explanation = "Muito bem! No conjunto 'celebro, irriba, peda e oio', todas as quatro palavras contêm erros de grafia populares."
            ),
            QuizQuestionEntity(
                id = 2,
                questionNumber = 1,
                title = "Questão 1",
                prompt = "Qual dessas palavras começa com a letra 'C' de Casa?",
                audioNarration = "Questão 1. Qual dessas palavras começa com o som da letra C, como na palavra Casa?",
                optionA = "Casa",
                optionB = "Bola",
                optionC = "Pato",
                optionD = "Mão",
                correctOptionIndex = 0,
                explanation = "Excelente! A palavra Casa começa com a letra C."
            ),
            QuizQuestionEntity(
                id = 3,
                questionNumber = 2,
                title = "Questão 2",
                prompt = "Juntando o som de BA com o som de LA, formamos qual palavra?",
                audioNarration = "Questão 2. Juntando as sílabas BA e LA, qual palavra nós formamos?",
                optionA = "Bolo",
                optionB = "Bala",
                optionC = "Bule",
                optionD = "Beco",
                correctOptionIndex = 1,
                explanation = "Parabéns! BA mais LA forma a palavra Bala!"
            ),
            QuizQuestionEntity(
                id = 4,
                questionNumber = 8,
                title = "Questão 8",
                prompt = "Qual placa identifica a direção do transporte de passageiros?",
                audioNarration = "Questão 8. No terminal da sua cidade, qual palavra escrita na placa indica o ônibus?",
                optionA = "ÔNIBUS",
                optionB = "PADARIA",
                optionC = "FARMÁCIA",
                optionD = "MERCADO",
                correctOptionIndex = 0,
                explanation = "Muito bem, Roberto! A placa escrita ÔNIBUS indica o transporte coletivo."
            )
        )
        dao.insertQuestions(sampleQuestions)

        // Subscription Plans from Business Model Slides 6, 7 & 8
        val samplePlans = listOf(
            SubscriptionPlanEntity(
                id = "FREE",
                name = "Acesso Parcial Grátis",
                priceMonthly = "Grátis",
                priceAnnual = "R$ 0,00",
                features = "Matérias variadas • Acessibilidade básica com a Rê • Aulas introdutórias",
                isRecommended = false,
                badge = "Inclusão Social"
            ),
            SubscriptionPlanEntity(
                id = "MENSAL",
                name = "Assinatura Mensal",
                priceMonthly = "R$ 9,90 Mês",
                priceAnnual = "Custo anual = R$ 118,80",
                features = "Aulas ilimitadas • Mentor à disposição • Narradora inteligente Rê • Suporte humanizado",
                isRecommended = false,
                badge = null
            ),
            SubscriptionPlanEntity(
                id = "ANUAL",
                name = "Assinatura Anual",
                priceMonthly = "R$ 8,32 /mês",
                priceAnnual = "R$ 99,90 Anual",
                features = "Aulas ilimitadas • Sem anúncios • Mentor à disposição • Acesso prioritário • Economia de 16%",
                isRecommended = true,
                badge = "Mais Popular"
            ),
            SubscriptionPlanEntity(
                id = "SECAO_PLUS",
                name = "Assinatura Seção+",
                priceMonthly = "R$ 19,90 Mês",
                priceAnnual = "Custo anual = R$ 238,80",
                features = "IA auxiliadora sob medida • Aulas ilimitadas • Sem anúncios • Mentor à disposição em tempo real • Atendimento por voz",
                isRecommended = false,
                badge = "Tecnologia Completa"
            )
        )
        dao.insertPlans(samplePlans)
    }

    suspend fun saveAnswer(userId: Long, questionNumber: Int, selectedOption: Int, isCorrect: Boolean, newProgress: Int, nextIdx: Int) {
        dao.insertProgress(
            ProgressTrackingEntity(
                userId = userId,
                questionNumber = questionNumber,
                selectedOptionIndex = selectedOption,
                wasCorrect = isCorrect
            )
        )
        dao.updateProgress(userId, newProgress, nextIdx)
    }

    suspend fun selectPlan(userId: Long, planId: String) {
        dao.updateUserPlan(userId, planId)
    }

    suspend fun switchPersona(userId: Long, name: String, personaType: String) {
        dao.switchPersona(userId, name, personaType)
    }
}
