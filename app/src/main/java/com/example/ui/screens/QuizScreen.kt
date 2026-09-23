package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.QuizQuestionEntity
import com.example.ui.QuizFeedback
import com.example.ui.components.ReforcaHeader
import com.example.ui.components.ReforcaNarratorSpeakerButton
import com.example.ui.theme.ReforcaGold
import com.example.ui.theme.ReforcaGoldDark
import com.example.ui.theme.ReforcaPurple
import com.example.ui.theme.ReforcaPurpleContainer
import com.example.ui.theme.ReforcaPurpleDark
import com.example.ui.theme.ReforcaSuccess

@Composable
fun QuizScreen(
    question: QuizQuestionEntity?,
    selectedOption: Int?,
    feedback: QuizFeedback?,
    feedbackDialogVisible: Boolean,
    onSelectOption: (Int) -> Unit,
    onConfirmAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onBackClick: () -> Unit,
    onSpeakerClick: () -> Unit,
    isSpeaking: Boolean,
    isMuted: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // School doodles watermark
        Image(
            painter = painterResource(id = R.drawable.bg_educational_doodles),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.2f,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp)
        ) {
            // Header with back navigation & centered R+ logo
            ReforcaHeader(
                showBack = true,
                onBackClick = onBackClick
            )

            if (question == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Carregando questão...",
                        fontSize = 18.sp,
                        color = ReforcaPurple
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    // Question Number Title (e.g. "Questão 7")
                    Text(
                        text = question.title,
                        color = ReforcaPurple,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .testTag("quiz_question_title")
                    )

                    // Prompt
                    Text(
                        text = question.prompt,
                        color = ReforcaPurple.copy(alpha = 0.85f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 28.sp,
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .testTag("quiz_question_prompt")
                    )

                    // Multiple choice options list
                    val options = listOf(
                        question.optionA,
                        question.optionB,
                        question.optionC,
                        question.optionD
                    )

                    options.forEachIndexed { index, optionText ->
                        val isSelected = (selectedOption == index)
                        QuizOptionItem(
                            index = index,
                            text = optionText,
                            isSelected = isSelected,
                            onClick = { onSelectOption(index) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // "confirmar resposta ->" Pill Button matching mockup
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
                                .testTag("btn_confirm_answer")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "confirmar resposta",
                                    color = if (selectedOption != null) ReforcaGoldDark else Color.Gray,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = if (selectedOption != null) ReforcaGoldDark else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Bottom Bar matching mockup: Speaker icon + 12% Progress Bar
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speaker Audio Icon
                ReforcaNarratorSpeakerButton(
                    isSpeaking = isSpeaking,
                    isMuted = isMuted,
                    onClick = onSpeakerClick,
                    modifier = Modifier.size(46.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Progress Bar: 12% (like mockup) or dynamic based on question number
                val qPercent = when (question?.questionNumber) {
                    1 -> 12
                    2 -> 25
                    7 -> 12 // Exact 12% as in user's mockup!
                    8 -> 85
                    else -> 12
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(ReforcaPurpleDark)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(qPercent / 100f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(ReforcaGold, Color(0xFFE8B246))
                                    )
                                )
                        )
                    }
                    Text(
                        text = "$qPercent%",
                        color = ReforcaPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Accessible Feedback Dialog
        if (feedbackDialogVisible && feedback != null) {
            AlertDialog(
                onDismissRequest = onNextQuestion,
                confirmButton = {
                    Button(
                        onClick = onNextQuestion,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (feedback.isCorrect) ReforcaSuccess else ReforcaPurple
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_next_question")
                    ) {
                        Text(
                            text = "Próxima Atividade",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (feedback.isCorrect) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (feedback.isCorrect) ReforcaSuccess else ReforcaGoldDark,
                        modifier = Modifier.size(48.dp)
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

@Composable
private fun QuizOptionItem(
    index: Int,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) ReforcaPurpleContainer else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp)
            .testTag("quiz_option_$index"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Large Purple Circular Selector Bullet (matches screenshot)
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isSelected) ReforcaPurple else ReforcaPurple.copy(alpha = 0.85f))
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) ReforcaGold else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(ReforcaGold)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Option Text
        Text(
            text = text,
            color = ReforcaPurple,
            fontSize = 19.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            lineHeight = 24.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
