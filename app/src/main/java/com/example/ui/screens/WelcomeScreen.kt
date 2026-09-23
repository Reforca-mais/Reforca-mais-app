package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.ReforcaNarratorSpeakerButton
import com.example.ui.components.ReforcaPrimaryButton
import com.example.ui.components.ReforcaSecondaryButton
import com.example.ui.theme.ReforcaPurple

@Composable
fun WelcomeScreen(
    onEnterClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
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
        // Subtle educational doodles background pattern
        Image(
            painter = painterResource(id = R.drawable.bg_educational_doodles),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.35f,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Increased top spacing to lower the logo on the screen
            Spacer(modifier = Modifier.weight(1.4f))

            // Official Reforça+ Brand Logo (Increased size to 320dp and positioned lower)
            Image(
                painter = painterResource(id = R.drawable.ic_reforca_logo),
                contentDescription = "Logo Oficial Reforça+",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(320.dp)
                    .testTag("welcome_logo")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // "Bem Vindo!" title
            Text(
                text = "Bem Vindo!",
                color = ReforcaPurple,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("welcome_title")
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Action Buttons
            ReforcaPrimaryButton(
                text = "Entrar!",
                onClick = onEnterClick,
                testTag = "btn_enter"
            )

            Spacer(modifier = Modifier.height(18.dp))

            ReforcaSecondaryButton(
                text = "Criar conta!",
                onClick = onCreateAccountClick,
                testTag = "btn_create_account"
            )

            Spacer(modifier = Modifier.weight(0.8f))
        }

        // Bottom Speaker button (matches bottom-left speaker in mockup)
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
