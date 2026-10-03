package com.example.features.auth

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.designsystem.components.AuraPrimaryButton
import com.example.core.designsystem.components.AuraSecondaryButton
import com.example.ui.theme.AuraChampagne
import com.example.ui.theme.AuraViolet
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianVoid

enum class AuthStage {
    ONBOARDING, SIGN_IN, SIGN_UP, INTERESTS
}

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stage by remember { mutableStateOf(AuthStage.ONBOARDING) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianVoid)
    ) {
        when (stage) {
            AuthStage.ONBOARDING -> {
                OnboardingPager(
                    onGetStarted = { stage = AuthStage.SIGN_UP },
                    onSignIn = { stage = AuthStage.SIGN_IN }
                )
            }
            AuthStage.SIGN_IN -> {
                SignInContent(
                    onSuccess = onAuthSuccess,
                    onSwitchToSignUp = { stage = AuthStage.SIGN_UP }
                )
            }
            AuthStage.SIGN_UP -> {
                SignUpContent(
                    onProceedToInterests = { stage = AuthStage.INTERESTS },
                    onSwitchToSignIn = { stage = AuthStage.SIGN_IN }
                )
            }
            AuthStage.INTERESTS -> {
                InterestsSelectionContent(
                    onFinish = onAuthSuccess
                )
            }
        }
    }
}

@Composable
fun OnboardingPager(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val pages = listOf(
        Triple(
            Icons.Default.Shield,
            stringResource(R.string.onboarding_title_1),
            stringResource(R.string.onboarding_desc_1)
        ),
        Triple(
            Icons.Default.Layers,
            stringResource(R.string.onboarding_title_2),
            stringResource(R.string.onboarding_desc_2)
        ),
        Triple(
            Icons.Default.AutoAwesome,
            stringResource(R.string.onboarding_title_3),
            stringResource(R.string.onboarding_desc_3)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Hero Brand Visual
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.aura_hero_art_1791055202114),
                contentDescription = "Aura Brand",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, ObsidianVoid.copy(alpha = 0.8f), ObsidianVoid)
                        )
                    )
            )

            // Brand Monogram
            Text(
                text = "AURA",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = AuraChampagne
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }

        // Pager Content
        Box(modifier = Modifier.weight(0.9f)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val (icon, title, desc) = pages[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AuraChampagne.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = AuraChampagne,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            // Pager Dots
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 24.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AuraChampagne else Color.DarkGray)
                    )
                }
            }
        }

        // Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuraPrimaryButton(
                text = stringResource(R.string.get_started),
                onClick = onGetStarted,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.already_have_account),
                style = MaterialTheme.typography.bodyMedium,
                color = AuraChampagne,
                modifier = Modifier
                    .clickable(onClick = onSignIn)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun SignInContent(
    onSuccess: () -> Unit,
    onSwitchToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("nour@aura.network") }
    var password by remember { mutableStateOf("••••••••") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.sign_in),
            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = "Welcome back to your curated space",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email_label)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AuraChampagne,
                unfocusedBorderColor = ObsidianBorder,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password_label)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AuraChampagne,
                unfocusedBorderColor = ObsidianBorder,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuraPrimaryButton(
            text = stringResource(R.string.sign_in),
            onClick = onSuccess,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuraSecondaryButton(
            text = stringResource(R.string.continue_with_google),
            onClick = onSuccess,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Don't have an account? Sign Up",
            style = MaterialTheme.typography.bodyMedium,
            color = AuraChampagne,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = onSwitchToSignUp)
                .padding(8.dp)
        )
    }
}

@Composable
fun SignUpContent(
    onProceedToInterests: () -> Unit,
    onSwitchToSignIn: () -> Unit
) {
    var fullName by remember { mutableStateOf("Nour Al-Mansour") }
    var username by remember { mutableStateOf("nour_arch") }
    var email by remember { mutableStateOf("nour@aura.network") }
    var password by remember { mutableStateOf("••••••••") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.create_account),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = "Begin your authentic connection experience",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text(stringResource(R.string.full_name)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.username)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email_label)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuraPrimaryButton(
            text = "Continue to Interests",
            onClick = onProceedToInterests,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.already_have_account),
            style = MaterialTheme.typography.bodyMedium,
            color = AuraChampagne,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = onSwitchToSignIn)
                .padding(8.dp)
        )
    }
}

@Composable
fun InterestsSelectionContent(
    onFinish: () -> Unit
) {
    val view = LocalView.current
    val interests = listOf(
        "Architecture & Space", "Analog Photography", "Ambient Sound",
        "Systems Engineering", "Minimalist Design", "Sustainable Materials",
        "Digital Philosophy", "Cinematography", "Nordic Living"
    )

    val selected = remember { mutableStateListOf("Architecture & Space", "Systems Engineering", "Minimalist Design") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.interests_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.interests_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Grid / Chips of interests
            interests.forEach { interest ->
                val isSelected = selected.contains(interest)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AuraChampagne.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, if (isSelected) AuraChampagne else Color.Transparent, RoundedCornerShape(14.dp))
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            if (isSelected) selected.remove(interest) else selected.add(interest)
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = interest,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSelected) AuraChampagne else Color.White
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AuraChampagne,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        AuraPrimaryButton(
            text = "Enter Aura",
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
