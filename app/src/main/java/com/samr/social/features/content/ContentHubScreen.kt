package com.samr.social.features.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samr.social.R
import com.samr.social.core.model.PostDraft
import com.samr.social.core.repository.SamrRepository
import com.samr.social.ui.theme.SamrChampagne
import com.samr.social.ui.theme.SamrViolet

@Composable
fun ContentHubScreen(
    repository: SamrRepository,
    onBack: () -> Unit,
    onUseDraft: (PostDraft) -> Unit,
    modifier: Modifier = Modifier
) {
    val drafts by repository.drafts.collectAsState()
    val scheduled by repository.scheduledPosts.collectAsState()
    var tab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.content_hub),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    stringResource(R.string.content_hub_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        TabRow(selectedTabIndex = tab) {
            Tab(
                selected = tab == 0,
                onClick = { tab = 0 },
                text = { Text(stringResource(R.string.drafts_tab)) }
            )
            Tab(
                selected = tab == 1,
                onClick = { tab = 1 },
                text = { Text(stringResource(R.string.scheduled_tab)) }
            )
        }

        if (tab == 0) {
            if (drafts.isEmpty()) {
                EmptyHubState(stringResource(R.string.empty_drafts))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(drafts, key = { it.id }) { draft ->
                        Column(
                            modifier = Modifier
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
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(SamrChampagne.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = SamrChampagne)
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 10.dp)
                                ) {
                                    Text(
                                        draft.text.ifBlank { stringResource(R.string.media_url_label) },
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 3
                                    )
                                    Text(
                                        draft.updatedLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (draft.locationTag.isNotBlank()) {
                                Text(
                                    draft.locationTag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SamrViolet,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { repository.deleteDraft(draft.id) }) {
                                    Text(stringResource(R.string.delete_draft), color = MaterialTheme.colorScheme.error)
                                }
                                TextButton(onClick = { onUseDraft(draft) }) {
                                    Text(stringResource(R.string.use_draft), color = SamrChampagne)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (scheduled.isEmpty()) {
                EmptyHubState(stringResource(R.string.empty_scheduled))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(scheduled, key = { it.id }) { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(
                                    1.dp,
                                    SamrViolet.copy(alpha = 0.24f),
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(SamrViolet.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = SamrViolet)
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 10.dp)
                                ) {
                                    Text(
                                        item.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 3
                                    )
                                    Text(
                                        item.scheduledLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SamrViolet
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { repository.deleteScheduledPost(item.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text(stringResource(R.string.delete_action))
                                }
                                TextButton(onClick = { repository.publishScheduledPost(item.id) }) {
                                    Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text(stringResource(R.string.publish_now), color = SamrChampagne)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHubState(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
