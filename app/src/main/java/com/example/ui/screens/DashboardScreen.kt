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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.UserEntity
import com.example.ui.components.ReforcaNarratorSpeakerButton
import com.example.ui.components.ReforcaProgressBar
import com.example.ui.theme.ReforcaGold
import com.example.ui.theme.ReforcaGoldContainer
import com.example.ui.theme.ReforcaGoldDark
import com.example.ui.theme.ReforcaPurple
import com.example.ui.theme.ReforcaPurpleContainer
import com.example.ui.theme.ReforcaPurpleDark

@Composable
fun DashboardScreen(
    user: UserEntity?,
    onContinueLesson: () -> Unit,
    onOpenPhases: () -> Unit,
    onViewProgress: () -> Unit,
    onAccessibility: () -> Unit,
    onAbout: () -> Unit,
    onPlans: () -> Unit,
    onSpeakerClick: () -> Unit,
    onSwitchPersona: (String) -> Unit,
    isSpeaking: Boolean,
    isMuted: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val userName = user?.name ?: "Roberto"
    val progress = user?.progressPercentage ?: 79

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
            alpha = 0.25f,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Top Bar with Avatar (Left) & Reforça+ Logo (Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar with Persona Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            val next = if (user?.personaType == "ROBERTO") "MARIA" else "ROBERTO"
                            onSwitchPersona(next)
                        }
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(3.dp, ReforcaGold, CircleShape)
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
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "Toque para alternar",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Perfil: $userName",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ReforcaPurple
                            )
                            Icon(
                                imageVector = Icons.Default.SwitchAccount,
                                contentDescription = "Alternar perfil",
                                tint = ReforcaGoldDark,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(16.dp)
                            )
                        }
                    }
                }

                // Reforça+ Brand Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_reforca_logo),
                    contentDescription = "Logo Reforça+",
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Greeting: "Olá, Maria..." or "Olá, Roberto..."
            Text(
                text = "Olá, $userName...",
                color = ReforcaPurple,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .testTag("dashboard_greeting")
            )

            // Item 1: Continue de onde parou!
            DashboardActionCard(
                iconRes = R.drawable.ic_play_circle,
                title = "Continue de\nonde parou!",
                onClick = onContinueLesson,
                testTag = "card_continue_lesson"
            )

            HorizontalDivider(
                color = ReforcaPurple.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // NOVO ITEM SOLICITADO: Fases (posicionado exatamente abaixo de Continue de onde parou!)
            DashboardActionCard(
                iconRes = R.drawable.ic_map_trail,
                title = "Fases",
                subtitle = "Trilha de aprendizado passo a passo",
                onClick = onOpenPhases,
                testTag = "card_phases"
            )

            HorizontalDivider(
                color = ReforcaPurple.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Item 2: Ver seu progresso! (with 79% bar)
            DashboardProgressCard(
                iconRes = R.drawable.ic_trophy,
                title = "Ver seu progresso!",
                progress = progress,
                onClick = onViewProgress,
                testTag = "card_view_progress"
            )

            HorizontalDivider(
                color = ReforcaPurple.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Item 3: Acessibilidade!
            DashboardActionCard(
                iconRes = R.drawable.ic_accessibility_person,
                title = "Acessibilidade!",
                subtitle = "Voz da Rê e ajustes",
                onClick = onAccessibility,
                testTag = "card_accessibility"
            )

            HorizontalDivider(
                color = ReforcaPurple.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Item 4: Quem somos!
            DashboardActionCard(
                iconRes = R.drawable.ic_people_group,
                title = "Quem somos!",
                subtitle = "Conheça nossa missão",
                onClick = onAbout,
                testTag = "card_about_us"
            )

            HorizontalDivider(
                color = ReforcaPurple.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Item 5: Planos e Assinaturas (Monetização)
            Card(
                onClick = onPlans,
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ReforcaGoldContainer.copy(alpha = 0.7f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("card_plans")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(ReforcaGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Planos & Assinaturas",
                            color = ReforcaPurpleDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Grátis, Mensal, Anual e Seção+",
                            color = ReforcaGoldDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Floating Narrator Button at Bottom-Start
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
private fun DashboardActionCard(
    iconRes: Int,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(58.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column {
            Text(
                text = title,
                color = ReforcaPurple,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun DashboardProgressCard(
    iconRes: Int,
    title: String,
    progress: Int,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(58.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = ReforcaPurple,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            ReforcaProgressBar(
                percentage = progress,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
