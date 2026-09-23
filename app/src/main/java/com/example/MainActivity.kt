package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.PhaseStatus
import com.example.ui.AppScreen
import com.example.ui.ReforcaViewModel
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AccessibilityScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MinigameConnectDotsScreen
import com.example.ui.screens.MinigameDragBuildScreen
import com.example.ui.screens.MinigameSoundSyllableScreen
import com.example.ui.screens.PhaseSessionScreen
import com.example.ui.screens.PhasesMapScreen
import com.example.ui.screens.PlansScreen
import com.example.ui.screens.ProgressDetailScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.ReforcaPlusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReforcaPlusTheme {
                ReforcaApp()
            }
        }
    }
}

@Composable
fun ReforcaApp(viewModel: ReforcaViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val user by viewModel.user.collectAsState()
    val questions by viewModel.questions.collectAsState()
    val plans by viewModel.plans.collectAsState()
    val currentQuestionIdx by viewModel.currentQuestionIndex.collectAsState()
    val selectedOption by viewModel.selectedOption.collectAsState()
    val feedback by viewModel.feedback.collectAsState()
    val feedbackDialogVisible by viewModel.feedbackDialogVisible.collectAsState()

    // Fases & Minigames State
    val phasesList by viewModel.phasesList.collectAsState()
    val selectedPhase by viewModel.selectedPhase.collectAsState()
    val phaseActivityIndex by viewModel.phaseActivityIndex.collectAsState()
    val phaseActivities by viewModel.phaseActivities.collectAsState()
    val phaseSessionFeedback by viewModel.phaseSessionFeedback.collectAsState()
    val phaseSessionFeedbackVisible by viewModel.phaseSessionFeedbackVisible.collectAsState()

    // Minigames State
    val mg1TargetSyllable by viewModel.mg1TargetSyllable.collectAsState()
    val mg1Options by viewModel.mg1Options.collectAsState()
    val mg1Success by viewModel.mg1Success.collectAsState()

    val mg2AvailablePieces by viewModel.mg2AvailablePieces.collectAsState()
    val mg2Slots by viewModel.mg2Slots.collectAsState()
    val mg2Success by viewModel.mg2Success.collectAsState()

    val mg3Pairs by viewModel.mg3Pairs.collectAsState()
    val mg3SelectedImageId by viewModel.mg3SelectedImageId.collectAsState()
    val mg3MatchedIds by viewModel.mg3MatchedIds.collectAsState()
    val mg3Success by viewModel.mg3Success.collectAsState()

    val isSpeaking by viewModel.narrator.isSpeaking.collectAsState()
    val isMuted by viewModel.narrator.isMuted.collectAsState()
    val speechRate by viewModel.narrator.speechRate.collectAsState()
    val autoNarrate by viewModel.narrator.autoNarrateScreens.collectAsState()

    // Android System Back press navigation
    BackHandler(enabled = currentScreen != AppScreen.WELCOME) {
        when (currentScreen) {
            AppScreen.PHASE_SESSION,
            AppScreen.MINIGAME_1_SOUND,
            AppScreen.MINIGAME_2_DRAG,
            AppScreen.MINIGAME_3_CONNECT -> viewModel.navigateTo(AppScreen.PHASES_MAP)
            AppScreen.PHASES_MAP,
            AppScreen.QUIZ,
            AppScreen.ACCESSIBILITY,
            AppScreen.PLANS,
            AppScreen.ABOUT,
            AppScreen.PROGRESS_DETAIL -> viewModel.navigateTo(AppScreen.DASHBOARD)
            AppScreen.DASHBOARD -> viewModel.navigateTo(AppScreen.WELCOME)
            AppScreen.WELCOME -> {}
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val screenModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (currentScreen) {
            AppScreen.WELCOME -> {
                WelcomeScreen(
                    onEnterClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    onCreateAccountClick = {
                        viewModel.narrator.speak("Criar conta no Reforça Mais. Preparamos o perfil ideal para você começar imediatamente!")
                        viewModel.navigateTo(AppScreen.DASHBOARD)
                    },
                    onSpeakerClick = {
                        if (isSpeaking) viewModel.narrator.stop()
                        else viewModel.narrator.speak("Bem-vindo ao Reforça Mais! Plataforma inclusiva de alfabetização. Toque em Entrar para ver suas atividades.")
                    },
                    isSpeaking = isSpeaking,
                    isMuted = isMuted,
                    modifier = screenModifier
                )
            }

            AppScreen.DASHBOARD -> {
                DashboardScreen(
                    user = user,
                    onContinueLesson = { viewModel.navigateTo(AppScreen.QUIZ) },
                    onOpenPhases = { viewModel.navigateTo(AppScreen.PHASES_MAP) },
                    onViewProgress = { viewModel.navigateTo(AppScreen.PROGRESS_DETAIL) },
                    onAccessibility = { viewModel.navigateTo(AppScreen.ACCESSIBILITY) },
                    onAbout = { viewModel.navigateTo(AppScreen.ABOUT) },
                    onPlans = { viewModel.navigateTo(AppScreen.PLANS) },
                    onSpeakerClick = {
                        if (isSpeaking) viewModel.narrator.stop()
                        else {
                            val name = user?.name ?: "Roberto"
                            viewModel.narrator.speak("Painel de $name. Toque em Fases para ver sua trilha passo a passo, ou Continue de onde parou.")
                        }
                    },
                    onSwitchPersona = { personaType ->
                        viewModel.switchPersona(personaType)
                    },
                    isSpeaking = isSpeaking,
                    isMuted = isMuted,
                    modifier = screenModifier
                )
            }

            // Tela "Fases" (Mecânica de Mapa Interativo em Ziguezague)
            AppScreen.PHASES_MAP -> {
                PhasesMapScreen(
                    user = user,
                    phases = phasesList,
                    onPhaseClick = { phase -> viewModel.onPhaseNodeClick(phase) },
                    onBackClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    onSpeakerClick = {
                        if (isSpeaking) viewModel.narrator.stop()
                        else {
                            val cur = phasesList.firstOrNull { it.status == PhaseStatus.CURRENT }
                            val pNum = cur?.id ?: 2
                            viewModel.narrator.speak("Aqui é o seu mapa de estudos. Você está na fase $pNum. Toque no botão roxo brilhante para começar sua atividade.")
                        }
                    },
                    isSpeaking = isSpeaking,
                    isMuted = isMuted,
                    modifier = screenModifier
                )
            }

            // Sessão de 10 Atividades da Fase Padrão
            AppScreen.PHASE_SESSION -> {
                val act = phaseActivities.getOrNull(phaseActivityIndex)
                PhaseSessionScreen(
                    phase = selectedPhase,
                    activity = act,
                    activityIndex = phaseActivityIndex,
                    totalActivities = phaseActivities.size,
                    selectedOption = selectedOption,
                    feedback = phaseSessionFeedback,
                    feedbackVisible = phaseSessionFeedbackVisible,
                    onSelectOption = { idx -> viewModel.selectPhaseActivityOption(idx) },
                    onConfirmAnswer = { viewModel.confirmPhaseActivityAnswer() },
                    onAdvanceOrRetry = { viewModel.advanceOrRetryPhaseActivity() },
                    onBackClick = { viewModel.navigateTo(AppScreen.PHASES_MAP) },
                    onSpeakerClick = {
                        if (isSpeaking) viewModel.narrator.stop()
                        else if (act != null) {
                            viewModel.narrator.speak("Atividade ${act.activityNumber} de 10. ${act.promptText}")
                        }
                    },
                    isSpeaking = isSpeaking,
                    isMuted = isMuted,
                    modifier = screenModifier
                )
            }

            // Minigame 1: Som e Sílaba (Foco Auditivo - 4 Botões Gigantes)
            AppScreen.MINIGAME_1_SOUND -> {
                MinigameSoundSyllableScreen(
                    targetSyllable = mg1TargetSyllable,
                    options = mg1Options,
                    isSuccess = mg1Success,
                    onSelectOption = { syllable -> viewModel.selectMinigame1Option(syllable) },
                    onPlayAudioPrompt = { viewModel.playMinigame1AudioPrompt() },
                    onFinishMinigame = { viewModel.navigateTo(AppScreen.PHASES_MAP) },
                    onBackClick = { viewModel.navigateTo(AppScreen.PHASES_MAP) },
                    onSpeakerClick = {
                        if (isSpeaking) viewModel.narrator.stop()
                        else viewModel.narrator.speak("Minigame Som e Sílaba! Toque no alto-falante central para ouvir e escolha a sílaba certa.")
                    },
                    isSpeaking = isSpeaking,
                    isMuted = isMuted,
                    modifier = screenModifier
                )
            }

            // Minigame 2: Arraste e Monte (Memória Cotidiana - Figura do Pão)
            AppScreen.MINIGAME_2_DRAG -> {
                MinigameDragBuildScreen(
                    availablePieces = mg2AvailablePieces,
                    slots = mg2Slots,
                    isSuccess = mg2Success,
                    onTapPiece = { piece -> viewModel.tapMinigame2Piece(piece) },
                    onReset = { viewModel.resetMinigame2() },
                    onFinishMinigame = { viewModel.navigateTo(AppScreen.PHASES_MAP) },
                    onBackClick = { viewModel.navigateTo(AppScreen.PHASES_MAP) },
                    onSpeakerClick = {
                        if (isSpeaking) viewModel.narrator.stop()
                        else viewModel.narrator.speak("Arraste e Monte! Veja a figura do pão caseiro. Monte a palavra PÃO.")
                    },
                    isSpeaking = isSpeaking,
                    isMuted = isMuted,
                    modifier = screenModifier
                )
            }

            // Minigame 3: Ligando os Pontos (Associação - Confetes e Moedas)
            AppScreen.MINIGAME_3_CONNECT -> {
                MinigameConnectDotsScreen(
                    pairs = mg3Pairs,
                    selectedImageId = mg3SelectedImageId,
                    matchedIds = mg3MatchedIds,
                    isSuccess = mg3Success,
                    onSelectImage = { id -> viewModel.selectMinigame3Image(id) },
                    onSelectWord = { word -> viewModel.selectMinigame3Word(word) },
                    onFinishMinigame = { viewModel.navigateTo(AppScreen.PHASES_MAP) },
                    onBackClick = { viewModel.navigateTo(AppScreen.PHASES_MAP) },
                    onSpeakerClick = {
                        if (isSpeaking) viewModel.narrator.stop()
                        else viewModel.narrator.speak("Ligando os Pontos! Toque na imagem da esquerda e na palavra correta da direita.")
                    },
                    isSpeaking = isSpeaking,
                    isMuted = isMuted,
                    modifier = screenModifier
                )
            }

            AppScreen.QUIZ -> {
                val currentQ = if (questions.isNotEmpty() && currentQuestionIdx < questions.size) {
                    questions[currentQuestionIdx]
                } else null

                QuizScreen(
                    question = currentQ,
                    selectedOption = selectedOption,
                    feedback = feedback,
                    feedbackDialogVisible = feedbackDialogVisible,
                    onSelectOption = { idx -> viewModel.selectOption(idx) },
                    onConfirmAnswer = { viewModel.confirmAnswer() },
                    onNextQuestion = { viewModel.nextQuestion() },
                    onBackClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    onSpeakerClick = {
                        if (isSpeaking) viewModel.narrator.stop()
                        else if (currentQ != null) {
                            viewModel.narrator.speak("${currentQ.title}. ${currentQ.prompt}")
                        }
                    },
                    isSpeaking = isSpeaking,
                    isMuted = isMuted,
                    modifier = screenModifier
                )
            }

            AppScreen.ACCESSIBILITY -> {
                AccessibilityScreen(
                    speechRate = speechRate,
                    isMuted = isMuted,
                    isSpeaking = isSpeaking,
                    autoNarrate = autoNarrate,
                    onSpeechRateChange = { rate -> viewModel.narrator.setSpeechRate(rate) },
                    onToggleMute = { viewModel.narrator.toggleMute() },
                    onToggleAutoNarrate = { viewModel.narrator.toggleAutoNarrate() },
                    onTestVoice = {
                        viewModel.narrator.speak("Oi! Eu sou a Rê, sua companheira no Reforça Mais. Estou aqui para ler cada palavra e te ajudar a aprender no seu próprio ritmo!")
                    },
                    onBackClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    modifier = screenModifier
                )
            }

            AppScreen.PLANS -> {
                PlansScreen(
                    plans = plans,
                    selectedPlanId = user?.selectedPlanId ?: "FREE",
                    onSelectPlan = { planId, name -> viewModel.selectPlan(planId, name) },
                    onBackClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    modifier = screenModifier
                )
            }

            AppScreen.ABOUT -> {
                AboutScreen(
                    onBackClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    modifier = screenModifier
                )
            }

            AppScreen.PROGRESS_DETAIL -> {
                ProgressDetailScreen(
                    user = user,
                    onBackClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    modifier = screenModifier
                )
            }
        }
    }
}
