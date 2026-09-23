package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ConnectPair
import com.example.ui.components.ReforcaNarratorSpeakerButton
import com.example.ui.theme.ReforcaGold
import com.example.ui.theme.ReforcaGoldContainer
import com.example.ui.theme.ReforcaGoldDark
import com.example.ui.theme.ReforcaPurple
import com.example.ui.theme.ReforcaPurpleContainer
import com.example.ui.theme.ReforcaPurpleDark
import com.example.ui.theme.ReforcaSuccess
import kotlin.random.Random

@Composable
fun MinigameConnectDotsScreen(
    pairs: List<ConnectPair>,
    selectedImageId: Int?,
    matchedIds: Set<Int>,
    isSuccess: Boolean,
    onSelectImage: (Int) -> Unit,
    onSelectWord: (String) -> Unit,
    onFinishMinigame: () -> Unit,
    onBackClick: () -> Unit,
    onSpeakerClick: () -> Unit,
    isSpeaking: Boolean,
    isMuted: Boolean,
    modifier: Modifier = Modifier
) {
    // Confetti animation for victory celebration
    val infiniteTransition = rememberInfiniteTransition(label = "confettiAnim")
    val confettiProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti"
    )

    // Shuffled words for right column (fixed display order: CASA, VASO, ÔNIBUS)
    val wordsList = listOf("CASA", "VASO", "ÔNIBUS")

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
            // Header
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = ReforcaPurple,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Grande Desafio • Ligando os Pontos",
                            color = ReforcaPurple,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        painter = painterResource(id = R.drawable.ic_reforca_logo),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(width = 46.dp, height = 42.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header instruction
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Associação Palavra-Imagem",
                        color = ReforcaGoldDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Toque na imagem à esquerda e depois na palavra certa à direita:",
                        color = ReforcaPurple,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // 2 Columns (Left: Images, Right: Words)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Images
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        pairs.forEach { item ->
                            val isMatched = matchedIds.contains(item.id)
                            val isSelected = (selectedImageId == item.id)

                            Surface(
                                onClick = { if (!isMatched) onSelectImage(item.id) },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isMatched) Color(0xFFEBF7ED) else if (isSelected) ReforcaGoldContainer else Color(0xFFFAF7FC),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 3.dp else 1.5.dp,
                                    color = if (isMatched) ReforcaSuccess else if (isSelected) ReforcaGold else Color(0xFFE2D6E9)
                                ),
                                shadowElevation = if (isSelected) 5.dp else 2.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(95.dp)
                                    .testTag("connect_img_${item.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.iconEmoji,
                                            fontSize = 38.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.labelImage,
                                            color = ReforcaPurpleDark,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (isMatched) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Ligado!",
                                            tint = ReforcaSuccess,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Right Column: Words
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        wordsList.forEach { word ->
                            val correspondingPair = pairs.firstOrNull { it.word == word }
                            val isMatched = correspondingPair != null && matchedIds.contains(correspondingPair.id)

                            Surface(
                                onClick = { onSelectWord(word) },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isMatched) Color(0xFFEBF7ED) else ReforcaPurpleContainer,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isMatched) 3.dp else 1.5.dp,
                                    color = if (isMatched) ReforcaSuccess else ReforcaPurple
                                ),
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(95.dp)
                                    .testTag("connect_word_$word")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = word,
                                        color = if (isMatched) ReforcaSuccess else ReforcaPurpleDark,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black
                                    )

                                    if (isMatched) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Conectado",
                                            tint = ReforcaSuccess,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Progress Indicator (e.g. 2/3 ligados)
                Text(
                    text = "${matchedIds.size} de ${pairs.size} pares ligados",
                    color = ReforcaPurple,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
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

        // Confetti & Golden Coins Celebration Overlay when Completed!
        if (isSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
            ) {
                // Animated Confetti Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val colors = listOf(Color(0xFFFFD700), Color(0xFF8A2BE2), Color(0xFFFF69B4), Color(0xFF00FA9A), Color(0xFF00BFFF))
                    for (i in 0..40) {
                        val randomX = (i * 27) % size.width
                        val initialY = (i * 43) % size.height
                        val y = (initialY + (confettiProgress * size.height)) % size.height
                        drawCircle(
                            color = colors[i % colors.size],
                            radius = 9f,
                            center = Offset(randomX, y)
                        )
                    }
                }

                // Victory Modal Card
                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.Center)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Golden Trophy & Coins Icon
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = ReforcaGold,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = ReforcaGoldDark,
                                modifier = Modifier.size(46.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Chuva de Recompensas!",
                            color = ReforcaPurpleDark,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Roberto, você associou todas as figuras às palavras correspondentes com sucesso! Você ganhou 50 moedas douradas do Reforça+!",
                            color = Color.DarkGray,
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onFinishMinigame,
                            colors = ButtonDefaults.buttonColors(containerColor = ReforcaPurple),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("btn_mg3_finish")
                        ) {
                            Text(
                                text = "Coletar Recompensa & Voltar",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
