package com.samr.social.features.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samr.social.R
import com.samr.social.core.model.MoodType
import com.samr.social.core.repository.SamrRepository
import com.samr.social.ui.theme.AuraChampagne
import com.samr.social.ui.theme.AuraViolet
import com.samr.social.ui.theme.SamrCyan
import com.samr.social.ui.theme.SamrEmerald

@Composable
fun CreatorStudioScreen(
    repository: SamrRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val posts by repository.posts.collectAsState()
    val clips by repository.clips.collectAsState()

    val ownPosts = posts.filter { it.author.id == currentUser.id }
    val ownClips = clips.filter { it.author.id == currentUser.id }

    val totalLikes = ownPosts.sumOf { it.likesCount } + ownClips.sumOf { it.likesCount }
    val totalComments = ownPosts.sumOf { it.commentsCount } + ownClips.sumOf { it.commentsCount }
    val totalSaves = ownPosts.count { it.isBookmarked } + ownClips.count { it.isSaved }
    val totalResonance = ownPosts.sumOf { it.resonanceCount }
    val estimatedReach = ownPosts.sumOf { it.likesCount + it.commentsCount * 2 + it.repostsCount * 3 } +
        ownClips.sumOf { it.likesCount + it.commentsCount * 2 }
    val engagement = if (currentUser.followersCount > 0) {
        (((totalLikes + totalComments + totalResonance).toFloat() / currentUser.followersCount.toFloat()) * 100f)
            .coerceIn(0f, 99f)
    } else 0f
    val topPost = ownPosts.maxByOrNull { it.likesCount + it.commentsCount + it.resonanceCount }

    val moodCounts = ownPosts.groupingBy { it.mood }.eachCount()
    val focusSignal = ((moodCounts[MoodType.FOCUS] ?: 0) + 1).toFloat() / (ownPosts.size + 3).toFloat()
    val discoverSignal = ((moodCounts[MoodType.DISCOVER] ?: 0) + 1).toFloat() / (ownPosts.size + 3).toFloat()
    val relaxSignal = ((moodCounts[MoodType.RELAX] ?: 0) + 1).toFloat() / (ownPosts.size + 3).toFloat()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.creator_studio),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = stringResource(R.string.creator_studio_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                AuraViolet.copy(alpha = 0.26f),
                                AuraChampagne.copy(alpha = 0.18f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .border(1.dp, AuraChampagne.copy(alpha = 0.28f), RoundedCornerShape(28.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AuraChampagne.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AuraChampagne
                        )
                    }
                    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                        Text(
                            text = currentUser.displayName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "@" + currentUser.username,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StudioMetric(
                        value = compactNumber(estimatedReach),
                        label = stringResource(R.string.studio_reach),
                        modifier = Modifier.weight(1f)
                    )
                    StudioMetric(
                        value = String.format("%.1f%%", engagement),
                        label = stringResource(R.string.studio_engagement),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StudioMetric(
                        value = totalSaves.toString(),
                        label = stringResource(R.string.studio_saves),
                        modifier = Modifier.weight(1f)
                    )
                    StudioMetric(
                        value = compactNumber(totalResonance),
                        label = stringResource(R.string.studio_resonance),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            SectionTitle(stringResource(R.string.studio_content_health))
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = SamrEmerald)
                    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                        Text(
                            stringResource(R.string.studio_health_excellent),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = SamrEmerald
                        )
                        Text(
                            stringResource(R.string.studio_health_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                SignalRow(stringResource(R.string.studio_signal_focus), focusSignal, AuraChampagne)
                SignalRow(stringResource(R.string.studio_signal_discover), discoverSignal, SamrCyan)
                SignalRow(stringResource(R.string.studio_signal_relax), relaxSignal, SamrEmerald)
            }
        }

        item {
            SectionTitle(stringResource(R.string.studio_audience_pulse))
            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InsightCard(
                    title = stringResource(R.string.studio_growth),
                    value = stringResource(R.string.studio_growth_value),
                    icon = { Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AuraChampagne) },
                    modifier = Modifier.weight(1f)
                )
                InsightCard(
                    title = stringResource(R.string.studio_retention),
                    value = stringResource(R.string.studio_retention_value),
                    icon = { Icon(Icons.Default.PlayCircle, contentDescription = null, tint = SamrCyan) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            InsightCard(
                title = stringResource(R.string.studio_best_time),
                value = stringResource(R.string.studio_best_time_value),
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AuraViolet) },
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .fillMaxWidth()
            )
        }

        item {
            SectionTitle(stringResource(R.string.studio_top_content))
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                if (topPost != null) {
                    Text(
                        text = topPost.text,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        TinyStat(Icons.Default.Favorite, compactNumber(topPost.likesCount))
                        TinyStat(Icons.Default.Bookmark, if (topPost.isBookmarked) "1" else "0")
                        TinyStat(Icons.Default.AutoAwesome, compactNumber(topPost.resonanceCount))
                    }
                } else {
                    Text(
                        text = stringResource(R.string.empty_feed_desc),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StudioMetric(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.78f))
            .padding(14.dp)
    ) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AuraChampagne
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
    )
}

@Composable
private fun SignalRow(label: String, value: Float, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(0.34f)
        )
        LinearProgressIndicator(
            progress = { value.coerceIn(0.08f, 1f) },
            modifier = Modifier
                .weight(0.66f)
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun InsightCard(
    title: String,
    value: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            Text(
                value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TinyStat(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        Text(
            value,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 5.dp)
        )
    }
}

private fun compactNumber(value: Int): String = when {
    value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000f)
    value >= 1_000 -> String.format("%.1fK", value / 1_000f)
    else -> value.toString()
}
