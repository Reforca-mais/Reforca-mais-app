package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PhaseActivity
import com.example.data.model.PhaseNode
import com.example.ui.QuizFeedback
import com.example.ui.components.ReforcaNarratorSpeakerButton
import com.example.ui.theme.ReforcaGold
import com.example.ui.theme.ReforcaGoldDark
import com.example.ui.theme.ReforcaPurple
import com.example.ui.theme.ReforcaPurpleContainer
import com.example.ui.theme.ReforcaPurpleDark
import com.example.ui.theme.ReforcaSuccess

@Composable
fun PhaseSessionScreen(
    phase: PhaseNode?,
    activity: PhaseActivity?,
    activityIndex: Int,
    totalActivities: Int,
    selectedOption: Int?,
    feedback: QuizFeedback?,
    feedbackVisible: Boolean,
    onSelectOption: (Int) -> Unit,
    onConfirmAnswer: () -> Unit,
    onAdvanceOrRetry: () -> Unit,
    onBackClick: () -> Unit,
    onSpeakerClick: () -> Unit,
    isSpeaking: Boolean,
    isMuted: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val currentNum = activityIndex + 1
    val progress = (currentNum.toFloat() / totalActivities.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp)
        ) {
            // Header with Back, Phase Title, and Logo
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Voltar ao mapa",
                                    tint = ReforcaPurple,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${phase?.title ?: "Fase"} • Atividade $currentNum de $totalActivities",
                                color = ReforcaPurple,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Icon(
                            painter = painterResource(id = R.drawable.ic_reforca_logo),
                            contentDescription = "Logo Reforça+",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(width = 46.dp, height = 42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Horizontal Progress Bar on Top (Requisito estrito)
                    LinearProgressIndicator(
                        progress = { progress },
                        color = ReforcaGold,
                        trackColor = ReforcaPurpleDark.copy(alpha = 0.2f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .testTag("phase_progress_bar")
                    )
                }
            }

            if (activity != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Atividade $currentNum",
                        color = ReforcaGoldDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Prompt
                    Text(
                        text = activity.promptText,
                        color = ReforcaPurple,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 30.sp,
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .testTag("activity_prompt")
                    )

                    // 4 Large Options
                    activity.options.forEachIndexed { index, optionText ->
                        val isSelected = (selectedOption == index)
                        Surface(
                            onClick = { onSelectOption(index) },
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected) ReforcaPurpleContainer else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.5.dp else 1.5.dp,
                                color = if (isSelected) ReforcaPurple else Color(0xFFE5DCE9)
                            ),
                            shadowElevation = if (isSelected) 4.dp else 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .testTag("phase_activity_opt_$index")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Large purple bullet dot
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) ReforcaPurple else ReforcaPurple.copy(alpha = 0.8f))
                                        .border(
                                            width = if (isSelected) 2.5.dp else 0.dp,
                                            color = ReforcaGold,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(ReforcaGold)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = optionText,
                                    color = ReforcaPurpleDark,
                                    fontSize = 18.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Confirmation Button (Pill shape gold)
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            onClick = onConfirmAnswer,
                            enabled = selectedOption != null,
                            shape = RoundedCornerShape(26.dp),
                            color = if (selectedOption != null) Color.White else Color(0xFFF9F7FA),
                            shadowElevation = if (selectedOption != null) 4.dp else 0.dp,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(54.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (selectedOption != null) ReforcaGold else Color.LightGray,
                                    shape = RoundedCornerShape(26.dp)
                                )
                                .testTag("btn_confirm_activity")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Confirmar Resposta",
                                    color = if (selectedOption != null) ReforcaGoldDark else Color.Gray,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = if (selectedOption != null) ReforcaGoldDark else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Omnipresent Narrator Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            ReforcaNarratorSpeakerButton(
                isSpeaking = isSpeaking,
                isMuted = isMuted,
                onClick = onSpeakerClick
            )
        }

        // Acolhedor Feedback Dialog (Se errar, acolhe e tenta novamente)
        if (feedbackVisible && feedback != null) {
            AlertDialog(
                onDismissRequest = onAdvanceOrRetry,
                confirmButton = {
                    Button(
                        onClick = onAdvanceOrRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (feedback.isCorrect) ReforcaSuccess else ReforcaPurple
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_feedback_advance_or_retry")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (feedback.isCorrect) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (feedback.isCorrect) {
                                    if (currentNum == totalActivities) "Finalizar Fase com Estrela!" else "Próxima Atividade"
                                } else "Tentar Novamente",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (feedback.isCorrect) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (feedback.isCorrect) ReforcaSuccess else ReforcaGoldDark,
                        modifier = Modifier.size(54.dp)
                    )
                },
                title = {
                    Text(
                        text = feedback.title,
                        color = if (feedback.isCorrect) ReforcaSuccess else ReforcaPurpleDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                text = {
                    Text(
                        text = feedback.message,
                        color = ReforcaPurpleDark,
                        fontSize = 17.sp,
                        lineHeight = 24.sp
                    )
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White
            )
        }
    }
}
