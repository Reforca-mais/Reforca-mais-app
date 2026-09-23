package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ReforcaHeader
import com.example.ui.theme.ReforcaGold
import com.example.ui.theme.ReforcaGoldContainer
import com.example.ui.theme.ReforcaGoldDark
import com.example.ui.theme.ReforcaPurple
import com.example.ui.theme.ReforcaPurpleContainer
import com.example.ui.theme.ReforcaPurpleDark

@Composable
fun AccessibilityScreen(
    speechRate: Float,
    isMuted: Boolean,
    isSpeaking: Boolean,
    autoNarrate: Boolean,
    onSpeechRateChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onToggleAutoNarrate: () -> Unit,
    onTestVoice: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Waveform animation
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(550, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w3"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        ReforcaHeader(
            showBack = true,
            onBackClick = onBackClick
        )

        // Rê Title (matching slide 11)
        Text(
            text = "Rê",
            color = ReforcaPurple,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .padding(top = 8.dp)
                .testTag("re_title")
        )

        Text(
            text = "Narradora Inteligente Reforça+",
            color = Color.Gray,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mascot Hexagon Illustration (matching slide 11)
        Box(
            modifier = Modifier
                .size(170.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(ReforcaGoldLightHex, ReforcaGold)
                    )
                )
                .border(4.dp, ReforcaPurple, RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "Rê com fones",
                        tint = ReforcaPurple,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Positivo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Voz Ativa",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sound Waveform & Speaker (matching slide 11 waveform)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                contentDescription = null,
                tint = ReforcaPurple,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Animated waveform bars
            val heights = listOf(14.dp, 28.dp, 44.dp, 20.dp, 36.dp, 50.dp, 22.dp, 40.dp, 16.dp)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(52.dp)
            ) {
                heights.forEachIndexed { i, baseH ->
                    val factor = when (i % 3) {
                        0 -> wave1
                        1 -> wave2
                        else -> wave3
                    }
                    val currentH = if (isSpeaking) (baseH * factor).coerceAtLeast(6.dp) else (baseH * 0.35f).coerceAtLeast(4.dp)
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(currentH)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isSpeaking) ReforcaPurple else ReforcaPurple.copy(alpha = 0.4f))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Controls Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ReforcaPurpleContainer.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Velocidade da Fala",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ReforcaPurpleDark
                )
                Text(
                    text = "Ajustada especialmente para facilitar o aprendizado e compreensão",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SpeedChip(
                        label = "Lento (0.7x)",
                        isSelected = speechRate <= 0.75f,
                        onClick = { onSpeechRateChange(0.7f) }
                    )
                    SpeedChip(
                        label = "Ideal (0.85x)",
                        isSelected = speechRate > 0.75f && speechRate < 0.95f,
                        onClick = { onSpeechRateChange(0.85f) }
                    )
                    SpeedChip(
                        label = "Rápido (1.0x)",
                        isSelected = speechRate >= 0.95f,
                        onClick = { onSpeechRateChange(1.0f) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Switch: Leitura Automática
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Narrar Telas Automaticamente",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ReforcaPurpleDark
                        )
                        Text(
                            text = "Lê em voz alta ao abrir cada exercício",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = autoNarrate,
                        onCheckedChange = { onToggleAutoNarrate() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ReforcaPurple,
                            checkedTrackColor = ReforcaGoldLightHex
                        ),
                        modifier = Modifier.testTag("switch_auto_narrate")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Switch: Silenciar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Som da Assistente",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ReforcaPurpleDark
                        )
                        Text(
                            text = if (isMuted) "Voz desativada" else "Voz da Rê ligada",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = !isMuted,
                        onCheckedChange = { onToggleMute() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ReforcaPurple,
                            checkedTrackColor = ReforcaGoldLightHex
                        ),
                        modifier = Modifier.testTag("switch_mute_toggle")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button: Test Voice
                Surface(
                    onClick = onTestVoice,
                    shape = RoundedCornerShape(18.dp),
                    color = ReforcaGold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_test_voice")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ouvir Exemplo da Rê",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // "Voltar" Text Button matching slide 11
        Text(
            text = "Voltar",
            color = ReforcaPurple,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .clickable(onClick = onBackClick)
                .padding(16.dp)
                .testTag("btn_back_slide11")
        )
    }
}

@Composable
private fun SpeedChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) ReforcaPurple else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSelected) ReforcaPurple else ReforcaPurple.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .height(38.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else ReforcaPurple,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

private val ReforcaGoldLightHex = Color(0xFFF3C052)
