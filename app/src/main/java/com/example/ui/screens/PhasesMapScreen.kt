package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PhaseNode
import com.example.data.model.PhaseStatus
import com.example.data.model.PhaseType
import com.example.data.model.UserEntity
import com.example.ui.components.ReforcaNarratorSpeakerButton
import com.example.ui.theme.ReforcaGold
import com.example.ui.theme.ReforcaGoldContainer
import com.example.ui.theme.ReforcaGoldDark
import com.example.ui.theme.ReforcaPurple
import com.example.ui.theme.ReforcaPurpleContainer
import com.example.ui.theme.ReforcaPurpleDark
import kotlin.math.roundToInt

@Composable
fun PhasesMapScreen(
    user: UserEntity?,
    phases: List<PhaseNode>,
    onPhaseClick: (PhaseNode) -> Unit,
    onBackClick: () -> Unit,
    onSpeakerClick: () -> Unit,
    isSpeaking: Boolean,
    isMuted: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val userName = user?.name ?: "Roberto"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Subtle background doodle watermark
        Image(
            painter = painterResource(id = R.drawable.bg_educational_doodles),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.18f,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header Obrigatório: Profile circle on Left, Reforça+ Logo on Right
            Surface(
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back + Profile Circle on left
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("btn_phases_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar ao painel",
                                tint = ReforcaPurple,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // User profile circle with "R"
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, ReforcaGold, CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(ReforcaPurpleContainer, ReforcaGoldContainer)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (userName.isNotEmpty()) userName.take(1) else "R",
                                color = ReforcaPurpleDark,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Trilha de Fases",
                                color = ReforcaPurple,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Passo a passo com a Rê",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Reforça+ Brand Logo on Right
                    Image(
                        painter = painterResource(id = R.drawable.ic_reforca_logo),
                        contentDescription = "Logo Reforça+",
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            // Scrollable Map Area with Zigzag Path & 3D Nodes
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                val screenWidth = maxWidth

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 100.dp, top = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display phases: Can be displayed in sequence with zigzag offset
                    phases.forEachIndexed { index, phase ->
                        PhaseNodeRow(
                            phase = phase,
                            index = index,
                            screenWidthDp = screenWidth.value,
                            onClick = { onPhaseClick(phase) }
                        )

                        // Path connector to next phase
                        if (index < phases.size - 1) {
                            val nextPhase = phases[index + 1]
                            PathConnector(
                                currentPhase = phase,
                                nextPhase = nextPhase
                            )
                        }
                    }
                }
            }
        }

        // Omnipresent FAB Narrator (Rê) at bottom-left
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
    }
}

@Composable
private fun PhaseNodeRow(
    phase: PhaseNode,
    index: Int,
    screenWidthDp: Float,
    onClick: () -> Unit
) {
    // Glow/pulse animation for current phase
    val infiniteTransition = rememberInfiniteTransition(label = "pulseGlow")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    // Calculate zigzag horizontal offset in dp
    val maxOffsetDp = (screenWidthDp * 0.28f).coerceAtMost(110f)
    val xOffset = (phase.xOffsetFraction * maxOffsetDp).dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Inclusive decoration item positioned on opposite side with low opacity
        if (index % 2 == 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentAlignment = if (phase.xOffsetFraction >= 0) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                DecorationElement(index = index)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset { IntOffset(xOffset.roundToPx(), 0) }
        ) {
            // Tooltip "Começar!" for CURRENT phase (as requested in spec)
            if (phase.status == PhaseStatus.CURRENT) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = ReforcaGold,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .scale(pulseGlow)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Começar!",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // The Phase Node Button (Circular 3D or Special Chest/Hexagon)
            when (phase.type) {
                PhaseType.STANDARD -> {
                    StandardPhaseButton(
                        phase = phase,
                        pulseScale = if (phase.status == PhaseStatus.CURRENT) pulseGlow else 1f,
                        onClick = onClick
                    )
                }
                PhaseType.MINIGAME_SOUND_SYLLABLE,
                PhaseType.MINIGAME_DRAG_BUILD,
                PhaseType.MINIGAME_CONNECT_DOTS -> {
                    MinigameChestButton(
                        phase = phase,
                        pulseScale = if (phase.status == PhaseStatus.CURRENT) pulseGlow else 1f,
                        onClick = onClick
                    )
                }
            }

            // Phase Title & Subtitle label
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = phase.title,
                color = when (phase.status) {
                    PhaseStatus.COMPLETED -> ReforcaGoldDark
                    PhaseStatus.CURRENT -> ReforcaPurpleDark
                    PhaseStatus.LOCKED -> Color.Gray
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = phase.subtitle,
                color = Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Standard Phase Button:
 * - Completed: Dourada com ícone de estrela.
 * - Current: Roxa, levemente maior, com pulso/glow.
 * - Locked: Cinza com cadeado.
 */
@Composable
private fun StandardPhaseButton(
    phase: PhaseNode,
    pulseScale: Float,
    onClick: () -> Unit
) {
    val size = if (phase.status == PhaseStatus.CURRENT) 78.dp else 68.dp

    val bgColor = when (phase.status) {
        PhaseStatus.COMPLETED -> ReforcaGold
        PhaseStatus.CURRENT -> ReforcaPurple
        PhaseStatus.LOCKED -> Color(0xFFCCCCCC)
    }

    val bottomShadowColor = when (phase.status) {
        PhaseStatus.COMPLETED -> ReforcaGoldDark
        PhaseStatus.CURRENT -> ReforcaPurpleDark
        PhaseStatus.LOCKED -> Color(0xFF9E9E9E)
    }

    // 3D Button container (with bottom shadow bevel)
    Box(
        modifier = Modifier
            .size(size)
            .scale(pulseScale)
            .clickable(onClick = onClick)
            .testTag("phase_node_${phase.id}"),
        contentAlignment = Alignment.Center
    ) {
        // 3D Bottom Bevel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(bottomShadowColor)
        )

        // Top Main Surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 6.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            bgColor,
                            bottomShadowColor
                        )
                    )
                )
                .border(
                    width = if (phase.status == PhaseStatus.CURRENT) 3.dp else 2.dp,
                    color = if (phase.status == PhaseStatus.CURRENT) ReforcaGold else Color.White.copy(alpha = 0.6f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            when (phase.status) {
                PhaseStatus.COMPLETED -> {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Fase Concluída com Estrela",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                PhaseStatus.CURRENT -> {
                    // Phase Number with Star Accent
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${phase.id}",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = ReforcaGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                PhaseStatus.LOCKED -> {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Fase Bloqueada",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

/**
 * Minigame Special Node (Hexágono ou Baú do Tesouro)
 */
@Composable
private fun MinigameChestButton(
    phase: PhaseNode,
    pulseScale: Float,
    onClick: () -> Unit
) {
    val size = if (phase.status == PhaseStatus.CURRENT) 82.dp else 74.dp

    val bgColor = when (phase.status) {
        PhaseStatus.COMPLETED -> ReforcaGold
        PhaseStatus.CURRENT -> ReforcaPurple
        PhaseStatus.LOCKED -> Color(0xFFDDDDDD)
    }

    Box(
        modifier = Modifier
            .size(size)
            .scale(pulseScale)
            .clickable(onClick = onClick)
            .testTag("phase_node_chest_${phase.id}"),
        contentAlignment = Alignment.Center
    ) {
        // Hexagonal / Rounded Square Baú Frame
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = bgColor,
            shadowElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = 3.dp,
                color = if (phase.status == PhaseStatus.CURRENT) ReforcaGold else Color.White
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_treasure_chest),
                    contentDescription = "Nó de Minigame Baú",
                    tint = if (phase.status == PhaseStatus.LOCKED) Color.Gray else Color.Unspecified,
                    modifier = Modifier.size(44.dp)
                )

                if (phase.status == PhaseStatus.LOCKED) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Connector dashed curved trail between nodes
 */
@Composable
private fun PathConnector(
    currentPhase: PhaseNode,
    nextPhase: PhaseNode
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        val width = size.width
        val height = size.height

        val maxOffsetX = width * 0.28f
        val startX = (width / 2f) + (currentPhase.xOffsetFraction * maxOffsetX)
        val endX = (width / 2f) + (nextPhase.xOffsetFraction * maxOffsetX)

        val pathColor = if (currentPhase.status == PhaseStatus.COMPLETED) {
            ReforcaGold.copy(alpha = 0.8f)
        } else {
            Color(0xFFDDCCE5)
        }

        drawLine(
            color = pathColor,
            start = Offset(startX, 0f),
            end = Offset(endX, height),
            strokeWidth = 14f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 14f), 0f)
        )
    }
}

/**
 * Low opacity inclusive decorations: Books, Sprout, Rê mascot
 */
@Composable
private fun DecorationElement(index: Int) {
    when (index % 3) {
        0 -> {
            // Rural sprout / plantation
            Icon(
                painter = painterResource(id = R.drawable.ic_sprout_rural),
                contentDescription = null,
                tint = ReforcaGoldDark.copy(alpha = 0.35f),
                modifier = Modifier.size(42.dp)
            )
        }
        1 -> {
            // Rê mascot head with headphones
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ReforcaPurpleContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Rê",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ReforcaPurple.copy(alpha = 0.4f)
                )
            }
        }
        else -> {
            // Book / Studies
            Text(
                text = "📖",
                fontSize = 28.sp,
                modifier = Modifier
                    .scale(0.85f)
                    .alpha(0.35f)
            )
        }
    }
}
