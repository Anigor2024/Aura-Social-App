package com.example.core.designsystem.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeMute
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.model.SocialLayer
import com.example.ui.theme.AuraChampagne
import com.example.ui.theme.LayerCreative
import com.example.ui.theme.LayerPersonal
import com.example.ui.theme.LayerProfessional
import com.example.ui.theme.LayerTech

@Composable
fun AuraTopBar(
    currentLayer: SocialLayer,
    onLayerSelected: (SocialLayer) -> Unit,
    isQuietMode: Boolean,
    onToggleQuietMode: () -> Unit,
    onCatchUpClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var showLayerMenu by remember { mutableStateOf(false) }

    val layerColor by animateColorAsState(
        when (currentLayer) {
            SocialLayer.PERSONAL -> LayerPersonal
            SocialLayer.PROFESSIONAL -> LayerProfessional
            SocialLayer.CREATIVE -> LayerCreative
            SocialLayer.TECH -> LayerTech
        },
        label = "layer_color"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Layer Selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "AURA",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = AuraChampagne
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Social Layer Switcher Pill
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, layerColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                showLayerMenu = true
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(layerColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = getLayerLabel(currentLayer),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showLayerMenu,
                        onDismissRequest = { showLayerMenu = false }
                    ) {
                        SocialLayer.values().forEach { layer ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(getLayerColor(layer))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = getLayerLabel(layer))
                                    }
                                },
                                onClick = {
                                    onLayerSelected(layer)
                                    showLayerMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Quick Actions: Quiet Feed Toggle + Catch Up + Search
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quiet Feed Mode toggle
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        onToggleQuietMode()
                    }
                ) {
                    Icon(
                        imageVector = if (isQuietMode) Icons.AutoMirrored.Outlined.VolumeMute else Icons.AutoMirrored.Outlined.VolumeUp,
                        contentDescription = stringResource(R.string.quiet_feed_mode),
                        tint = if (isQuietMode) AuraChampagne else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Smart Catch-up Shortcut
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        onCatchUpClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = stringResource(R.string.smart_catch_up),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Search Shortcut
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onSearchClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Sub-indicator if quiet mode active
        AnimatedVisibility(visible = isQuietMode) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AuraChampagne.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.quiet_feed_active_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraChampagne
                )
            }
        }
    }
}

@Composable
fun getLayerLabel(layer: SocialLayer): String {
    return when (layer) {
        SocialLayer.PERSONAL -> stringResource(R.string.social_layer_personal)
        SocialLayer.PROFESSIONAL -> stringResource(R.string.social_layer_professional)
        SocialLayer.CREATIVE -> stringResource(R.string.social_layer_creative)
        SocialLayer.TECH -> stringResource(R.string.social_layer_tech)
    }
}

fun getLayerColor(layer: SocialLayer): Color {
    return when (layer) {
        SocialLayer.PERSONAL -> LayerPersonal
        SocialLayer.PROFESSIONAL -> LayerProfessional
        SocialLayer.CREATIVE -> LayerCreative
        SocialLayer.TECH -> LayerTech
    }
}
