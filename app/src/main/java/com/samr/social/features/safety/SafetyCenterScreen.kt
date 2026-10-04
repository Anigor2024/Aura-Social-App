package com.samr.social.features.safety

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.samr.social.ui.theme.SamrEmerald
import com.samr.social.ui.theme.SamrViolet

@Composable
fun SafetyCenterScreen(
    repository: SamrRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val preferences by repository.privacyPreferences.collectAsState()

    var allowMessages by remember(preferences) { mutableStateOf(preferences.allowMessages) }
    var allowMentions by remember(preferences) { mutableStateOf(preferences.allowMentions) }
    var showActivityStatus by remember(preferences) { mutableStateOf(preferences.showActivityStatus) }
    var sensitiveFilter by remember(preferences) { mutableStateOf(preferences.sensitiveContentFilter) }
    var allowMediaDownloads by remember(preferences) { mutableStateOf(preferences.allowMediaDownloads) }
    var allowRemixes by remember(preferences) { mutableStateOf(preferences.allowRemixes) }
    var allowClipReuse by remember(preferences) { mutableStateOf(preferences.allowClipReuse) }
    var hiddenWords by remember(preferences) { mutableStateOf(preferences.hiddenWords.joinToString(", ")) }
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
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.safety_center_full),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    stringResource(R.string.safety_center_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            SamrEmerald.copy(alpha = 0.17f),
                            SamrViolet.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .border(
                    1.dp,
                    SamrEmerald.copy(alpha = 0.27f),
                    RoundedCornerShape(26.dp)
                )
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SamrEmerald.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = SamrEmerald
                    )
                }
                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text(
                        stringResource(R.string.privacy_safety),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        stringResource(R.string.safety_center_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        PrivacyToggle(
            title = stringResource(R.string.allow_messages),
            checked = allowMessages,
            onChange = {
                allowMessages = it
                saved = false
            }
        )
        PrivacyToggle(
            title = stringResource(R.string.allow_mentions),
            checked = allowMentions,
            onChange = {
                allowMentions = it
                saved = false
            }
        )
        PrivacyToggle(
            title = stringResource(R.string.show_activity_status),
            checked = showActivityStatus,
            onChange = {
                showActivityStatus = it
                saved = false
            }
        )
        PrivacyToggle(
            title = stringResource(R.string.sensitive_filter),
            checked = sensitiveFilter,
            onChange = {
                sensitiveFilter = it
                saved = false
            }
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SamrChampagne.copy(alpha = 0.06f))
                .border(
                    1.dp,
                    SamrChampagne.copy(alpha = 0.22f),
                    RoundedCornerShape(20.dp)
                )
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.media_privacy_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
            PrivacyToggle(
                title = stringResource(R.string.allow_media_downloads),
                checked = allowMediaDownloads,
                onChange = {
                    allowMediaDownloads = it
                    saved = false
                },
                nested = true
            )
            PrivacyToggle(
                title = stringResource(R.string.allow_remixes),
                checked = allowRemixes,
                onChange = {
                    allowRemixes = it
                    saved = false
                },
                nested = true
            )
            PrivacyToggle(
                title = stringResource(R.string.allow_clip_reuse),
                checked = allowClipReuse,
                onChange = {
                    allowClipReuse = it
                    saved = false
                },
                nested = true
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.28f),
                    RoundedCornerShape(20.dp)
                )
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = SamrChampagne
                )
                Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                    Text(
                        stringResource(R.string.hidden_words),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        stringResource(R.string.hidden_words_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = hiddenWords,
                onValueChange = {
                    hiddenWords = it
                    saved = false
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp)
            )
        }

        Button(
            onClick = {
                repository.updatePrivacyPreferences(
                    allowMessages = allowMessages,
                    allowMentions = allowMentions,
                    showActivityStatus = showActivityStatus,
                    sensitiveContentFilter = sensitiveFilter,
                    hiddenWords = hiddenWords.split(","),
                    allowMediaDownloads = allowMediaDownloads,
                    allowRemixes = allowRemixes,
                    allowClipReuse = allowClipReuse
                )
                saved = true
            },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SamrChampagne,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Icon(
                if (saved) Icons.Default.CheckCircle else Icons.Default.Shield,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                if (saved) stringResource(R.string.privacy_saved)
                else stringResource(R.string.save_privacy),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun PrivacyToggle(
    title: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    nested: Boolean = false
) {
    Row(
        modifier = Modifier
            .padding(horizontal = if (nested) 8.dp else 16.dp, vertical = 5.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (nested) MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
                else MaterialTheme.colorScheme.surface
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onChange
        )
    }
}
