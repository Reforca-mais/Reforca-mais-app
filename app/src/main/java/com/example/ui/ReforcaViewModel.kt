package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.accessibility.NarratorManager
import com.example.data.local.ReforcaDatabase
import com.example.data.model.ConnectPair
import com.example.data.model.PhaseActivity
import com.example.data.model.PhaseNode
import com.example.data.model.PhaseStatus
import com.example.data.model.PhaseType
import com.example.data.model.QuizQuestionEntity
import com.example.data.model.SubscriptionPlanEntity
import com.example.data.model.UserEntity
import com.example.data.repository.ReforcaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    WELCOME,
    DASHBOARD,
    QUIZ,
    ACCESSIBILITY,
    PLANS,
    ABOUT,
    PROGRESS_DETAIL,
    PHASES_MAP,
    PHASE_SESSION,
    MINIGAME_1_SOUND,
    MINIGAME_2_DRAG,
    MINIGAME_3_CONNECT
}

data class QuizFeedback(
    val isCorrect: Boolean,
    val title: String,
    val message: String
)

class ReforcaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReforcaRepository
    val narrator: NarratorManager

    private val _currentScreen = MutableStateFlow(AppScreen.WELCOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOption = MutableStateFlow<Int?>(null)
    val selectedOption: StateFlow<Int?> = _selectedOption.asStateFlow()

    private val _feedback = MutableStateFlow<QuizFeedback?>(null)
    val feedback: StateFlow<QuizFeedback?> = _feedback.asStateFlow()

    private val _feedbackDialogVisible = MutableStateFlow(false)
    val feedbackDialogVisible: StateFlow<Boolean> = _feedbackDialogVisible.asStateFlow()

    // --- Fases & Mapa State ---
    private val _phasesList = MutableStateFlow<List<PhaseNode>>(emptyList())
    val phasesList: StateFlow<List<PhaseNode>> = _phasesList.asStateFlow()

    private val _selectedPhase = MutableStateFlow<PhaseNode?>(null)
    val selectedPhase: StateFlow<PhaseNode?> = _selectedPhase.asStateFlow()

    // Phase Session (10 Activities) State
    private val _phaseActivityIndex = MutableStateFlow(0)
    val phaseActivityIndex: StateFlow<Int> = _phaseActivityIndex.asStateFlow()

    private val _phaseActivities = MutableStateFlow<List<PhaseActivity>>(emptyList())
    val phaseActivities: StateFlow<List<PhaseActivity>> = _phaseActivities.asStateFlow()

    private val _phaseSessionFeedback = MutableStateFlow<QuizFeedback?>(null)
    val phaseSessionFeedback: StateFlow<QuizFeedback?> = _phaseSessionFeedback.asStateFlow()

    private val _phaseSessionFeedbackVisible = MutableStateFlow(false)
    val phaseSessionFeedbackVisible: StateFlow<Boolean> = _phaseSessionFeedbackVisible.asStateFlow()

    // --- Minigame 1 State (Som e Sílaba) ---
    private val _mg1TargetSyllable = MutableStateFlow("BA")
    val mg1TargetSyllable: StateFlow<String> = _mg1TargetSyllable.asStateFlow()

    private val _mg1Options = MutableStateFlow(listOf("BA", "MA", "DA", "TA"))
    val mg1Options: StateFlow<List<String>> = _mg1Options.asStateFlow()

    private val _mg1Success = MutableStateFlow(false)
    val mg1Success: StateFlow<Boolean> = _mg1Success.asStateFlow()

    // --- Minigame 2 State (Arraste e Monte) ---
    private val _mg2AvailablePieces = MutableStateFlow(listOf("ÃO", "P"))
    val mg2AvailablePieces: StateFlow<List<String>> = _mg2AvailablePieces.asStateFlow()

    private val _mg2Slots = MutableStateFlow<List<String>>(emptyList())
    val mg2Slots: StateFlow<List<String>> = _mg2Slots.asStateFlow()

    private val _mg2Success = MutableStateFlow(false)
    val mg2Success: StateFlow<Boolean> = _mg2Success.asStateFlow()

    // --- Minigame 3 State (Ligando os Pontos) ---
    private val _mg3Pairs = MutableStateFlow(
        listOf(
            ConnectPair(1, "🚌", "Ônibus", "ÔNIBUS"),
            ConnectPair(2, "🏠", "Casa", "CASA"),
            ConnectPair(3, "🏺", "Vaso", "VASO")
        )
    )
    val mg3Pairs: StateFlow<List<ConnectPair>> = _mg3Pairs.asStateFlow()

    private val _mg3SelectedImageId = MutableStateFlow<Int?>(null)
    val mg3SelectedImageId: StateFlow<Int?> = _mg3SelectedImageId.asStateFlow()

    private val _mg3MatchedIds = MutableStateFlow<Set<Int>>(emptySet())
    val mg3MatchedIds: StateFlow<Set<Int>> = _mg3MatchedIds.asStateFlow()

    private val _mg3Success = MutableStateFlow(false)
    val mg3Success: StateFlow<Boolean> = _mg3Success.asStateFlow()

    val user: StateFlow<UserEntity?>
    val questions: StateFlow<List<QuizQuestionEntity>>
    val plans: StateFlow<List<SubscriptionPlanEntity>>

    init {
        val database = ReforcaDatabase.getInstance(application)
        repository = ReforcaRepository(database.reforcaDao())
        narrator = NarratorManager(application)

        user = repository.currentUser.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        questions = repository.questions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        plans = repository.plans.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        initDefaultPhases()
        initDefaultPhaseActivities()

        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
            narrator.speak(
                "Bem-vindo ao Reforça Mais! Plataforma de alfabetização acessível. Toque no botão roxo grande para Entrar!",
                isScreenChange = true
            )
        }
    }

    private fun initDefaultPhases() {
        // Trilha vertical em ziguezague com nós padrão e minigames a cada 3 fases
        _phasesList.value = listOf(
            PhaseNode(
                id = 1,
                title = "Fase 1",
                subtitle = "Vogais e Sons Básicos",
                type = PhaseType.STANDARD,
                status = PhaseStatus.COMPLETED,
                totalActivities = 10,
                xOffsetFraction = 0f
            ),
            PhaseNode(
                id = 2,
                title = "Fase 2",
                subtitle = "Sílabas do Cotidiano",
                type = PhaseType.STANDARD,
                status = PhaseStatus.CURRENT, // Fase Atual do Roberto
                totalActivities = 10,
                xOffsetFraction = -0.4f
            ),
            PhaseNode(
                id = 3,
                title = "Fase 3",
                subtitle = "Palavras do Campo",
                type = PhaseType.STANDARD,
                status = PhaseStatus.LOCKED,
                totalActivities = 10,
                xOffsetFraction = 0f
            ),
            PhaseNode(
                id = 4,
                title = "Desafio Baú",
                subtitle = "Minigame 1: Som e Sílaba",
                type = PhaseType.MINIGAME_SOUND_SYLLABLE, // Baú Especial Minigame 1
                status = PhaseStatus.LOCKED,
                totalActivities = 1,
                xOffsetFraction = 0.42f
            ),
            PhaseNode(
                id = 5,
                title = "Fase 5",
                subtitle = "Palavras da Casa",
                type = PhaseType.STANDARD,
                status = PhaseStatus.LOCKED,
                totalActivities = 10,
                xOffsetFraction = 0f
            ),
            PhaseNode(
                id = 6,
                title = "Fase 6",
                subtitle = "Frases e Família",
                type = PhaseType.STANDARD,
                status = PhaseStatus.LOCKED,
                totalActivities = 10,
                xOffsetFraction = -0.42f
            ),
            PhaseNode(
                id = 7,
                title = "Fase 7",
                subtitle = "Ortografia do Dia a Dia",
                type = PhaseType.STANDARD,
                status = PhaseStatus.LOCKED,
                totalActivities = 10,
                xOffsetFraction = 0f
            ),
            PhaseNode(
                id = 8,
                title = "Desafio Baú",
                subtitle = "Minigame 2: Arraste e Monte",
                type = PhaseType.MINIGAME_DRAG_BUILD, // Baú Especial Minigame 2
                status = PhaseStatus.LOCKED,
                totalActivities = 1,
                xOffsetFraction = 0.4f
            ),
            PhaseNode(
                id = 9,
                title = "Fase 9",
                subtitle = "Lendo Placas da Cidade",
                type = PhaseType.STANDARD,
                status = PhaseStatus.LOCKED,
                totalActivities = 10,
                xOffsetFraction = 0f
            ),
            PhaseNode(
                id = 10,
                title = "Grande Desafio",
                subtitle = "Minigame 3: Ligando os Pontos",
                type = PhaseType.MINIGAME_CONNECT_DOTS, // Baú Especial Minigame 3
                status = PhaseStatus.LOCKED,
                totalActivities = 1,
                xOffsetFraction = -0.35f
            )
        )
    }

    private fun initDefaultPhaseActivities() {
        // 10 atividades sequenciais para a Fase Padrão
        _phaseActivities.value = listOf(
            PhaseActivity(
                activityNumber = 1,
                promptText = "Qual letra tem o som inicial da palavra 'Água'?",
                audioScript = "Atividade 1. Qual letra tem o som inicial da palavra Água? Toque na opção correta.",
                options = listOf("A", "E", "O", "U"),
                correctIndex = 0,
                hint = "Ouça o começo da palavra: Á-gua começa com a letra A."
            ),
            PhaseActivity(
                activityNumber = 2,
                promptText = "Juntando a letra B com a letra A, que som nós temos?",
                audioScript = "Atividade 2. Juntando a letra B com a letra A, que som nós formamos?",
                options = listOf("BA", "BE", "BO", "BU"),
                correctIndex = 0,
                hint = "B com A forma a sílaba BA, como em Bala e Bahia."
            ),
            PhaseActivity(
                activityNumber = 3,
                promptText = "Qual sílaba completa a palavra 'PA-...' para formar 'PATO'?",
                audioScript = "Atividade 3. Qual sílaba completa PA para formar PATO?",
                options = listOf("TO", "CO", "LO", "NO"),
                correctIndex = 0,
                hint = "PA mais TO forma a palavra Pato!"
            ),
            PhaseActivity(
                activityNumber = 4,
                promptText = "Qual palavra representa a bebida matinal mais querida do campo?",
                audioScript = "Atividade 4. Qual dessas palavras é Café?",
                options = listOf("CAFÉ", "LEITE", "ÁGUA", "SUCO"),
                correctIndex = 0,
                hint = "Começa com CA e termina com FÉ: Café."
            ),
            PhaseActivity(
                activityNumber = 5,
                promptText = "Qual dessas palavras rima com 'FEIJÃO'?",
                audioScript = "Atividade 5. Qual dessas palavras rima com Feijão?",
                options = listOf("PÃO", "BOLO", "FACA", "CASA"),
                correctIndex = 0,
                hint = "Feijão termina com o som ÃO, assim como PÃO."
            ),
            PhaseActivity(
                activityNumber = 6,
                promptText = "Quantas sílabas (pedaços de som) tem a palavra 'CA-SA'?",
                audioScript = "Atividade 6. Bata palmas para cada pedaço: CA-SA. Quantos sons são?",
                options = listOf("2 pedaços", "1 pedaço", "3 pedaços", "4 pedaços"),
                correctIndex = 0,
                hint = "CA é um, SA é dois. A palavra Casa tem 2 pedaços!"
            ),
            PhaseActivity(
                activityNumber = 7,
                promptText = "Quais dos conjuntos de palavras abaixo, estão TODAS escritas de maneira incorreta?",
                audioScript = "Atividade 7. Quais dos conjuntos de palavras abaixo estão todas escritas de maneira incorreta?",
                options = listOf(
                    "vrido, pão, xinelo e faca",
                    "carro, onibos, vassoura e pé",
                    "casa, vinho, cabelo e ovo",
                    "celebro, irriba, peda e oio"
                ),
                correctIndex = 3,
                hint = "celebro, irriba, peda e oio são todas escritas com erros da fala popular."
            ),
            PhaseActivity(
                activityNumber = 8,
                promptText = "Qual placa do mercado avisa onde comprar farinha e arroz?",
                audioScript = "Atividade 8. Qual placa identifica o Mercado?",
                options = listOf("MERCADO", "FARMÁCIA", "OFICINA", "POSTO"),
                correctIndex = 0,
                hint = "Começa com M: MERCADO."
            ),
            PhaseActivity(
                activityNumber = 9,
                promptText = "Qual palavra completa a frase: 'Roberto cuida da sua ... com amor'?",
                audioScript = "Atividade 9. Qual palavra completa: Roberto cuida da sua o quê com amor?",
                options = listOf("FAMÍLIA", "PEDRA", "NU VEM", "ESTRADA"),
                correctIndex = 0,
                hint = "Roberto ama sua esposa e suas duas filhas: cuida da sua Família."
            ),
            PhaseActivity(
                activityNumber = 10,
                promptText = "Parabéns, Roberto! O que você conquistou ao concluir estas 10 atividades?",
                audioScript = "Atividade 10 de 10. O que você conquistou com essa dedicação?",
                options = listOf("CONHECIMENTO", "DUVIDA", "DESISTENCIA", "PRESSA"),
                correctIndex = 0,
                hint = "Cada passo traz Conhecimento e orgulho para suas filhas!"
            )
        )
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
        _feedback.value = null
        _feedbackDialogVisible.value = false
        _phaseSessionFeedbackVisible.value = false

        when (screen) {
            AppScreen.WELCOME -> {
                narrator.speak(
                    "Tela de Boas-Vindas. Toque em Entrar para acessar suas aulas.",
                    isScreenChange = true
                )
            }
            AppScreen.DASHBOARD -> {
                val userName = user.value?.name ?: "Roberto"
                narrator.speak(
                    "Olá, $userName! Você está no seu Painel Principal. Toque em Fases para ver sua trilha de aprendizado, ou Continue de onde parou.",
                    isScreenChange = true
                )
            }
            AppScreen.PHASES_MAP -> {
                val currentPhase = _phasesList.value.firstOrNull { it.status == PhaseStatus.CURRENT }
                val phaseNum = currentPhase?.id ?: 2
                narrator.speak(
                    "Aqui é o seu mapa de estudos. Você está na fase $phaseNum. Toque no botão roxo brilhante para começar sua atividade.",
                    isScreenChange = true
                )
            }
            AppScreen.PHASE_SESSION -> {
                val act = _phaseActivities.value.getOrNull(_phaseActivityIndex.value)
                if (act != null) {
                    narrator.speak(
                        "Atividade ${act.activityNumber} de 10. ${act.promptText}. Toque nas opções para ouvir.",
                        isScreenChange = true
                    )
                }
            }
            AppScreen.MINIGAME_1_SOUND -> {
                resetMinigame1()
                narrator.speak(
                    "Minigame Som e Sílaba! Ouça com atenção. Toque na sílaba: ${_mg1TargetSyllable.value}.",
                    isScreenChange = true
                )
            }
            AppScreen.MINIGAME_2_DRAG -> {
                resetMinigame2()
                narrator.speak(
                    "Minigame Arraste e Monte! Veja a figura do pão quentinho. Toque nas peças na ordem certa para formar a palavra PÃO.",
                    isScreenChange = true
                )
            }
            AppScreen.MINIGAME_3_CONNECT -> {
                resetMinigame3()
                narrator.speak(
                    "Grande Desafio: Ligando os Pontos! Toque em uma figura à esquerda e depois na palavra correspondente à direita para ligar.",
                    isScreenChange = true
                )
            }
            AppScreen.QUIZ -> {
                val qList = questions.value
                val idx = _currentQuestionIndex.value
                if (qList.isNotEmpty() && idx < qList.size) {
                    val q = qList[idx]
                    narrator.speak(
                        "${q.title}. ${q.prompt}. Toque em cada opção para ouvir antes de confirmar.",
                        isScreenChange = true
                    )
                }
            }
            AppScreen.ACCESSIBILITY -> {
                narrator.speak(
                    "Painel de Acessibilidade da Rê. Aqui você escolhe a velocidade da minha voz, ativa a leitura das telas e ajusta o volume.",
                    isScreenChange = true
                )
            }
            AppScreen.PLANS -> {
                narrator.speak(
                    "Conheça os Planos do Reforça Mais. Temos Acesso Gratuito de Inclusão, Assinatura Mensal por 9 e 90, Assinatura Anual por 99 e 90, e Assinatura Seção Mais com Inteligência Artificial.",
                    isScreenChange = true
                )
            }
            AppScreen.ABOUT -> {
                narrator.speak(
                    "Quem Somos! O Reforça Mais nasceu para superar o déficit de alfabetização em jovens e adultos no Brasil através de tecnologia inclusiva e voz humanizada.",
                    isScreenChange = true
                )
            }
            AppScreen.PROGRESS_DETAIL -> {
                val prog = user.value?.progressPercentage ?: 79
                narrator.speak(
                    "Seu Progresso atual é de $prog porcento! Você já completou a maior parte do seu módulo de leitura do cotidiano. Parabéns pela dedicação!",
                    isScreenChange = true
                )
            }
        }
    }

    // --- Phase Node Click Handling ---
    fun onPhaseNodeClick(phase: PhaseNode) {
        _selectedPhase.value = phase
        when (phase.status) {
            PhaseStatus.LOCKED -> {
                narrator.speak("Esta fase ainda está trancada. Conclua as fases anteriores para desbloquear.")
            }
            PhaseStatus.COMPLETED -> {
                narrator.speak("${phase.title} concluída com estrela dourada! Toque para refazer ou praticar.")
                openPhaseContent(phase)
            }
            PhaseStatus.CURRENT -> {
                narrator.speak("${phase.title}: ${phase.subtitle}. Iniciando suas atividades agora!")
                openPhaseContent(phase)
            }
        }
    }

    private fun openPhaseContent(phase: PhaseNode) {
        when (phase.type) {
            PhaseType.STANDARD -> {
                _phaseActivityIndex.value = 0
                _selectedOption.value = null
                _phaseSessionFeedbackVisible.value = false
                navigateTo(AppScreen.PHASE_SESSION)
            }
            PhaseType.MINIGAME_SOUND_SYLLABLE -> {
                navigateTo(AppScreen.MINIGAME_1_SOUND)
            }
            PhaseType.MINIGAME_DRAG_BUILD -> {
                navigateTo(AppScreen.MINIGAME_2_DRAG)
            }
            PhaseType.MINIGAME_CONNECT_DOTS -> {
                navigateTo(AppScreen.MINIGAME_3_CONNECT)
            }
        }
    }

    // --- Phase 10-Activities Session Logic ---
    fun selectPhaseActivityOption(optionIndex: Int) {
        _selectedOption.value = optionIndex
        val activities = _phaseActivities.value
        val idx = _phaseActivityIndex.value
        if (idx < activities.size) {
            val opt = activities[idx].options[optionIndex]
            narrator.speak("Opção ${optionIndex + 1}: $opt")
        }
    }

    fun confirmPhaseActivityAnswer() {
        val selected = _selectedOption.value ?: return
        val activities = _phaseActivities.value
        val idx = _phaseActivityIndex.value
        if (idx >= activities.size) return

        val currentAct = activities[idx]
        val isCorrect = (selected == currentAct.correctIndex)

        val isLast = (idx == activities.size - 1)
        if (isCorrect) {
            val msg = if (isLast) {
                "Parabéns, Roberto! Você completou com sucesso as 10 atividades da Fase! Ela agora está dourada no seu mapa!"
            } else {
                "Muito bem! Resposta correta! ${currentAct.hint}"
            }
            _phaseSessionFeedback.value = QuizFeedback(true, if (isLast) "Fase Concluída!" else "Muito bem!", msg)
            _phaseSessionFeedbackVisible.value = true
            narrator.speak(msg)

            if (isLast) {
                markCurrentPhaseAsCompleted()
            }
        } else {
            val msg = "Não se preocupe, Roberto! Aprender exige calma. Vamos tentar de novo: ${currentAct.hint}"
            _phaseSessionFeedback.value = QuizFeedback(false, "Vamos tentar de novo!", msg)
            _phaseSessionFeedbackVisible.value = true
            narrator.speak(msg)
        }
    }

    fun advanceOrRetryPhaseActivity() {
        val fb = _phaseSessionFeedback.value
        _phaseSessionFeedbackVisible.value = false
        if (fb?.isCorrect == true) {
            val activities = _phaseActivities.value
            val idx = _phaseActivityIndex.value
            if (idx + 1 < activities.size) {
                _phaseActivityIndex.value = idx + 1
                _selectedOption.value = null
                val nextAct = activities[idx + 1]
                narrator.speak("Atividade ${nextAct.activityNumber} de 10. ${nextAct.promptText}")
            } else {
                // Return to map with updated stars!
                navigateTo(AppScreen.PHASES_MAP)
            }
        } else {
            // Retry same question
            _selectedOption.value = null
            val act = _phaseActivities.value.getOrNull(_phaseActivityIndex.value)
            if (act != null) {
                narrator.speak("Ouça com atenção: ${act.promptText}")
            }
        }
    }

    private fun markCurrentPhaseAsCompleted() {
        val current = _selectedPhase.value ?: return
        val updated = _phasesList.value.map { node ->
            when (node.id) {
                current.id -> node.copy(status = PhaseStatus.COMPLETED)
                current.id + 1 -> node.copy(status = PhaseStatus.CURRENT)
                else -> node
            }
        }
        _phasesList.value = updated
    }

    // --- Minigame 1: Som e Sílaba ---
    fun selectMinigame1Option(syllable: String) {
        if (syllable == _mg1TargetSyllable.value) {
            _mg1Success.value = true
            narrator.speak("Perfeito, Roberto! Você ouviu BA e encontrou a sílaba BA certinho! Desafio concluído!")
            markCurrentPhaseAsCompleted()
        } else {
            narrator.speak("Essa é a sílaba $syllable. Vamos ouvir de novo: toque em BA.")
        }
    }

    fun playMinigame1AudioPrompt() {
        narrator.speak("Atenção ao som: ${_mg1TargetSyllable.value}!")
    }

    private fun resetMinigame1() {
        _mg1TargetSyllable.value = "BA"
        _mg1Options.value = listOf("BA", "MA", "DA", "TA").shuffled()
        _mg1Success.value = false
    }

    // --- Minigame 2: Arraste e Monte ---
    fun tapMinigame2Piece(piece: String) {
        val currentSlots = _mg2Slots.value.toMutableList()
        val currentPieces = _mg2AvailablePieces.value.toMutableList()

        currentSlots.add(piece)
        currentPieces.remove(piece)

        _mg2Slots.value = currentSlots
        _mg2AvailablePieces.value = currentPieces

        val assembledWord = currentSlots.joinToString("")
        narrator.speak("Você colocou: $piece. Palavra até agora: $assembledWord")

        if (assembledWord == "PÃO") {
            _mg2Success.value = true
            narrator.speak("Parabéns! P mais ÃO formou a palavra PÃO! Pão quentinho da mesa familiar!")
            markCurrentPhaseAsCompleted()
        } else if (currentPieces.isEmpty()) {
            narrator.speak("Quase lá! Essa ordem formou $assembledWord. Toque em Limpar para tentar montar PÃO.")
        }
    }

    fun resetMinigame2() {
        _mg2AvailablePieces.value = listOf("ÃO", "P")
        _mg2Slots.value = emptyList()
        _mg2Success.value = false
    }

    // --- Minigame 3: Ligando os Pontos ---
    fun selectMinigame3Image(id: Int) {
        _mg3SelectedImageId.value = id
        val pair = _mg3Pairs.value.firstOrNull { it.id == id }
        if (pair != null) {
            narrator.speak("Figura selecionada: ${pair.labelImage}. Agora toque na palavra correspondente à direita.")
        }
    }

    fun selectMinigame3Word(word: String) {
        val selectedId = _mg3SelectedImageId.value
        if (selectedId == null) {
            narrator.speak("Primeiro toque em uma das figuras da esquerda.")
            return
        }

        val pair = _mg3Pairs.value.firstOrNull { it.id == selectedId }
        if (pair != null && pair.word == word) {
            val newMatched = _mg3MatchedIds.value + selectedId
            _mg3MatchedIds.value = newMatched
            _mg3SelectedImageId.value = null

            narrator.speak("Muito bem! Você ligou ${pair.labelImage} com a palavra ${pair.word}!")

            if (newMatched.size == _mg3Pairs.value.size) {
                _mg3Success.value = true
                narrator.speak("Sensacional, Roberto! Você ligou todos os pontos com perfeição! Chuva de estrelas e moedas de recompensa!")
                markCurrentPhaseAsCompleted()
            }
        } else {
            narrator.speak("Ainda não é essa palavra. Tente ligar com outra opção.")
        }
    }

    private fun resetMinigame3() {
        _mg3SelectedImageId.value = null
        _mg3MatchedIds.value = emptySet()
        _mg3Success.value = false
    }

    // Standard Quiz Methods
    fun selectOption(optionIndex: Int) {
        _selectedOption.value = optionIndex
        val qList = questions.value
        val idx = _currentQuestionIndex.value
        if (qList.isNotEmpty() && idx < qList.size) {
            val q = qList[idx]
            val text = when (optionIndex) {
                0 -> "Opção 1: ${q.optionA}"
                1 -> "Opção 2: ${q.optionB}"
                2 -> "Opção 3: ${q.optionC}"
                3 -> "Opção 4: ${q.optionD}"
                else -> ""
            }
            narrator.speak(text)
        }
    }

    fun confirmAnswer() {
        val selected = _selectedOption.value ?: return
        val qList = questions.value
        val idx = _currentQuestionIndex.value
        if (qList.isEmpty() || idx >= qList.size) return

        val currentQ = qList[idx]
        val isCorrect = (selected == currentQ.correctOptionIndex)
        val currentUser = user.value

        val feedbackObj = if (isCorrect) {
            QuizFeedback(
                isCorrect = true,
                title = "Parabéns! Você acertou!",
                message = currentQ.explanation
            )
        } else {
            QuizFeedback(
                isCorrect = false,
                title = "Quase lá!",
                message = "Não se preocupe! Aprender leva tempo e paciência. Vamos rever: ${currentQ.explanation}"
            )
        }

        _feedback.value = feedbackObj
        _feedbackDialogVisible.value = true

        narrator.speak("${feedbackObj.title}. ${feedbackObj.message}")

        viewModelScope.launch {
            if (currentUser != null) {
                val newProgress = if (isCorrect) minOf(100, currentUser.progressPercentage + 5) else currentUser.progressPercentage
                val nextIdx = (idx + 1) % qList.size
                repository.saveAnswer(
                    userId = currentUser.id,
                    questionNumber = currentQ.questionNumber,
                    selectedOption = selected,
                    isCorrect = isCorrect,
                    newProgress = newProgress,
                    nextIdx = nextIdx
                )
            }
        }
    }

    fun nextQuestion() {
        val qList = questions.value
        if (qList.isNotEmpty()) {
            _currentQuestionIndex.value = (_currentQuestionIndex.value + 1) % qList.size
            _selectedOption.value = null
            _feedback.value = null
            _feedbackDialogVisible.value = false
            val nextQ = qList[_currentQuestionIndex.value]
            narrator.speak("${nextQ.title}. ${nextQ.prompt}")
        }
    }

    fun switchPersona(type: String) {
        viewModelScope.launch {
            val u = user.value ?: return@launch
            if (type == "ROBERTO") {
                repository.switchPersona(u.id, "Roberto", "ROBERTO")
                narrator.speak("Perfil alternado para Roberto, agricultor de 52 anos em Lagarto, Sergipe.")
            } else {
                repository.switchPersona(u.id, "Maria", "MARIA")
                narrator.speak("Perfil alternado para Maria, mãe de família buscando aprender a ler.")
            }
        }
    }

    fun selectPlan(planId: String, planName: String) {
        viewModelScope.launch {
            val u = user.value ?: return@launch
            repository.selectPlan(u.id, planId)
            narrator.speak("Plano $planName selecionado com sucesso! Suas aulas foram atualizadas.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        narrator.shutdown()
    }
}
