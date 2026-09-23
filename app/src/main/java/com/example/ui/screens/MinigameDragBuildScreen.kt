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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.components.ReforcaNarratorSpeakerButton
import com.example.ui.theme.ReforcaGold
import com.example.ui.theme.ReforcaGoldDark
import com.example.ui.theme.ReforcaPurple
import com.example.ui.theme.ReforcaPurpleContainer
import com.example.ui.theme.ReforcaPurpleDark
import com.example.ui.theme.ReforcaSuccess

@Composable
fun MinigameDragBuildScreen(
    availablePieces: List<String>,
    slots: List<String>,
    isSuccess: Boolean,
    onTapPiece: (String) -> Unit,
    onReset: () -> Unit,
    onFinishMinigame: () -> Unit,
    onBackClick: () -> Unit,
    onSpeakerClick: () -> Unit,
    isSpeaking: Boolean,
    isMuted: Boolean,
    modifier: Modifier = Modifier
) {
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
                            text = "Desafio do Baú • Arraste e Monte",
                            color = ReforcaPurple,
                            fontSize = 16.sp,
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
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Instruction
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Memória Cotidiana",
                        color = ReforcaGoldDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Monte a palavra da figura abaixo:",
                        color = ReforcaPurple,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Item Image Card (Pão Quentinho do Cotidiano)
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = ReforcaPurpleContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🥖",
                            fontSize = 72.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pão caseiro",
                            color = ReforcaPurpleDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Dotted Target Slots Area
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Área da Palavra:",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Slot 1
                        val slot1 = slots.getOrNull(0)
                        DottedSlotBox(content = slot1)

                        // Slot 2
                        val slot2 = slots.getOrNull(1)
                        DottedSlotBox(content = slot2)
                    }
                }

                // Available Pieces to Tap / Assemble
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Toque nos blocos abaixo para posicionar:",
                        color = ReforcaPurple,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        availablePieces.forEach { piece ->
                            Surface(
                                onClick = { onTapPiece(piece) },
                                shape = RoundedCornerShape(20.dp),
                                color = ReforcaGold,
                                shadowElevation = 6.dp,
                                modifier = Modifier
                                    .size(width = 110.dp, height = 75.dp)
                                    .testTag("piece_$piece")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = piece,
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    if (slots.isNotEmpty()) {
                        OutlinedButton(
                            onClick = onReset,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ReforcaPurple)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Recomeçar Palavra")
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

        // Success Dialog
        if (isSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    modifier = Modifier.fillMaxWidth(0.92f)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(CircleShape)
                                .background(ReforcaSuccess),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Palavra Formada!",
                            color = ReforcaPurpleDark,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "P com ÃO formou 'PÃO'! Um alimento essencial que está todos os dias na sua mesa com a sua família.",
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
                                .height(52.dp)
                                .testTag("btn_mg2_finish")
                        ) {
                            Text(
                                text = "Concluir e Voltar à Trilha",
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

@Composable
private fun DottedSlotBox(content: String?) {
    Box(
        modifier = Modifier
            .size(width = 110.dp, height = 80.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (content != null) ReforcaPurpleContainer else Color(0xFFF6F3F9))
            .border(
                width = 2.5.dp,
                color = if (content != null) ReforcaPurple else ReforcaGoldDark,
                shape = RoundedCornerShape(18.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (content != null) {
            Text(
                text = content,
                color = ReforcaPurpleDark,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black
            )
        } else {
            Text(
                text = "...",
                color = Color.Gray,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
