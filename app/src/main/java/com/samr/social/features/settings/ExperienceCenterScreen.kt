package com.samr.social.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samr.social.R
import com.samr.social.core.repository.SamrRepository
import com.samr.social.ui.theme.SamrChampagne
import com.samr.social.ui.theme.SamrCyan
import com.samr.social.ui.theme.SamrViolet

@Composable
fun ExperienceCenterScreen(
    repository: SamrRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val preferences by repository.experiencePreferences.collectAsState()
    var autoplay by remember(preferences) { mutableStateOf(preferences.autoplayVideos) }
    var reducedMotion by remember(preferences) { mutableStateOf(preferences.reducedMotion) }
    var compactFeed by remember(preferences) { mutableStateOf(preferences.compactFeed) }
    var haptics by remember(preferences) { mutableStateOf(preferences.hapticFeedback) }
    var highQuality by remember(preferences) { mutableStateOf(preferences.highQualityMedia) }
    var readReceipts by remember(preferences) { mutableStateOf(preferences.showReadReceipts) }
    var saved by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.experience_center),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    stringResource(R.string.experience_center_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            SamrViolet.copy(alpha = 0.17f),
                            SamrCyan.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .border(1.dp, SamrViolet.copy(alpha = 0.26f), RoundedCornerShape(24.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(SamrChampagne.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = SamrChampagne)
            }
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(
                    stringResource(R.string.feed_density),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    if (compactFeed) stringResource(R.string.feed_density_compact)
                    else stringResource(R.string.feed_density_comfortable),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        ExperienceToggle(stringResource(R.string.autoplay_videos), autoplay) {
            autoplay = it; saved = false
        }
        ExperienceToggle(stringResource(R.string.reduced_motion), reducedMotion) {
            reducedMotion = it; saved = false
        }
        ExperienceToggle(stringResource(R.string.compact_feed), compactFeed) {
            compactFeed = it; saved = false
        }
        ExperienceToggle(stringResource(R.string.haptic_feedback), haptics) {
            haptics = it; saved = false
        }
        ExperienceToggle(stringResource(R.string.high_quality_media), highQuality) {
            highQuality = it; saved = false
        }
        ExperienceToggle(stringResource(R.string.read_receipts), readReceipts) {
            readReceipts = it; saved = false
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                repository.updateExperiencePreferences(
                    autoplayVideos = autoplay,
                    reducedMotion = reducedMotion,
                    compactFeed = compactFeed,
                    hapticFeedback = haptics,
                    highQualityMedia = highQuality,
                    showReadReceipts = readReceipts
                )
                saved = true
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SamrChampagne,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Icon(
                if (saved) Icons.Default.CheckCircle else Icons.Default.Tune,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                if (saved) stringResource(R.string.experience_saved)
                else stringResource(R.string.save_experience),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ExperienceToggle(
    title: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
