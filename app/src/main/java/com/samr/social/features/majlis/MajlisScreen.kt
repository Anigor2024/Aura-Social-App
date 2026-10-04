package com.samr.social.features.majlis

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samr.social.R
import com.samr.social.core.designsystem.components.SamrAvatar
import com.samr.social.core.model.MajlisRoom
import com.samr.social.core.model.MajlisStatus
import com.samr.social.core.repository.SamrRepository
import com.samr.social.ui.theme.SamrChampagne
import com.samr.social.ui.theme.SamrCyan
import com.samr.social.ui.theme.SamrEmerald
import com.samr.social.ui.theme.SamrViolet

private enum class MajlisFilter {
    ALL, DESIGN, TECH, ARTS, CAREER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MajlisScreen(
    repository: SamrRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rooms by repository.majlisRooms.collectAsState()
    var filter by remember { mutableStateOf(MajlisFilter.ALL) }
    var activeRoomId by remember { mutableStateOf<String?>(null) }

    val filteredRooms = remember(rooms, filter) {
        rooms.filter { room ->
            when (filter) {
                MajlisFilter.ALL -> true
                MajlisFilter.DESIGN -> room.category.contains("التصميم") || room.category.contains("Design", true)
                MajlisFilter.TECH -> room.category.contains("التقنية") || room.category.contains("Tech", true)
                MajlisFilter.ARTS -> room.category.contains("الفنون") || room.category.contains("Art", true)
                MajlisFilter.CAREER -> room.category.contains("المسار") || room.category.contains("Career", true)
            }
        }
    }

    val liveRooms = filteredRooms.filter { it.status == MajlisStatus.LIVE }
    val upcomingRooms = filteredRooms.filter { it.status == MajlisStatus.UPCOMING }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 34.dp)
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
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.majlis_title),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = stringResource(R.string.majlis_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                SamrChampagne.copy(alpha = 0.20f),
                                SamrViolet.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .border(1.dp, SamrChampagne.copy(alpha = 0.28f), RoundedCornerShape(28.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(SamrChampagne.copy(alpha = 0.17f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = SamrChampagne)
                        }
                        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                            Text(
                                stringResource(R.string.majlis_featured),
                                style = MaterialTheme.typography.labelMedium,
                                color = SamrChampagne
                            )
                            Text(
                                liveRooms.firstOrNull()?.title ?: stringResource(R.string.majlis_title),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                maxLines = 2
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        liveRooms.firstOrNull()?.description ?: stringResource(R.string.majlis_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter == MajlisFilter.ALL,
                    label = stringResource(R.string.majlis_all),
                    onClick = { filter = MajlisFilter.ALL }
                )
                FilterChip(
                    selected = filter == MajlisFilter.DESIGN,
                    label = stringResource(R.string.majlis_design),
                    onClick = { filter = MajlisFilter.DESIGN }
                )
                FilterChip(
                    selected = filter == MajlisFilter.TECH,
                    label = stringResource(R.string.majlis_tech),
                    onClick = { filter = MajlisFilter.TECH }
                )
                FilterChip(
                    selected = filter == MajlisFilter.ARTS,
                    label = stringResource(R.string.majlis_arts),
                    onClick = { filter = MajlisFilter.ARTS }
                )
                FilterChip(
                    selected = filter == MajlisFilter.CAREER,
                    label = stringResource(R.string.majlis_career),
                    onClick = { filter = MajlisFilter.CAREER }
                )
            }
        }

        if (liveRooms.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.majlis_live),
                    dotColor = Color(0xFFFF4D67)
                )
            }

            items(liveRooms, key = { it.id }) { room ->
                MajlisCard(
                    room = room,
                    onPrimaryAction = {
                        if (!room.isJoined) repository.toggleMajlisJoin(room.id)
                        activeRoomId = room.id
                    },
                    onSecondaryAction = {
                        repository.toggleMajlisJoin(room.id)
                    }
                )
            }
        }

        if (upcomingRooms.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.majlis_upcoming),
                    dotColor = SamrCyan
                )
            }

            items(upcomingRooms, key = { it.id }) { room ->
                MajlisCard(
                    room = room,
                    onPrimaryAction = { repository.toggleMajlisReminder(room.id) },
                    onSecondaryAction = { repository.toggleMajlisJoin(room.id) }
                )
            }
        }

        if (filteredRooms.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.majlis_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }

    activeRoomId?.let { roomId ->
        val room = rooms.firstOrNull { it.id == roomId }
        if (room != null) {
            ModalBottomSheet(
                onDismissRequest = { activeRoomId = null },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                LiveMajlisSheet(
                    room = room,
                    onToggleHand = { repository.toggleMajlisHand(room.id) },
                    onApplause = { repository.reactMajlis(room.id, "applause") },
                    onHeart = { repository.reactMajlis(room.id, "heart") },
                    onLeave = {
                        if (room.isJoined) repository.toggleMajlisJoin(room.id)
                        activeRoomId = null
                    },
                    onClose = { activeRoomId = null }
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) SamrChampagne.copy(alpha = 0.16f)
                else MaterialTheme.colorScheme.surface
            )
            .border(
                1.dp,
                if (selected) SamrChampagne.copy(alpha = 0.55f)
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.28f),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (selected) SamrChampagne else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    dotColor: Color
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun MajlisCard(
    room: MajlisRoom,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit
) {
    val accent = Color(room.accentHex)
    val isLive = room.status == MajlisStatus.LIVE

    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.14f))
                    .padding(horizontal = 9.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isLive) stringResource(R.string.majlis_live_badge) else room.scheduledLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = accent
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                stringResource(R.string.majlis_participants, room.participantCount),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            room.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            room.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            SamrAvatar(
                imageUrl = room.host.avatarUrl,
                name = room.host.displayName,
                size = 38.dp,
                isVerified = room.host.isVerified
            )
            room.coHosts.take(2).forEach { host ->
                Box(modifier = Modifier.padding(start = 4.dp)) {
                    SamrAvatar(
                        imageUrl = host.avatarUrl,
                        name = host.displayName,
                        size = 32.dp,
                        isVerified = host.isVerified
                    )
                }
            }
            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                Text(
                    stringResource(R.string.majlis_hosted_by, room.host.displayName),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    room.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onPrimaryAction,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLive) accent else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isLive) Color.Black else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = if (isLive) Icons.Default.Mic else Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (isLive) {
                        if (room.isJoined) stringResource(R.string.majlis_join) else stringResource(R.string.majlis_join)
                    } else {
                        if (room.isReminderSet) stringResource(R.string.majlis_reminder_on) else stringResource(R.string.majlis_remind)
                    }
                )
            }

            if (room.isJoined) {
                TextButton(
                    onClick = onSecondaryAction,
                    modifier = Modifier.weight(0.55f)
                ) {
                    Text(stringResource(R.string.majlis_leave))
                }
            } else if (!isLive) {
                TextButton(
                    onClick = onSecondaryAction,
                    modifier = Modifier.weight(0.55f)
                ) {
                    Text(stringResource(R.string.majlis_join))
                }
            }
        }
    }
}

@Composable
private fun LiveMajlisSheet(
    room: MajlisRoom,
    onToggleHand: () -> Unit,
    onApplause: () -> Unit,
    onHeart: () -> Unit,
    onLeave: () -> Unit,
    onClose: () -> Unit
) {
    val accent = Color(room.accentHex)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = accent)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    room.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    stringResource(R.string.majlis_participants, room.participantCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onClose) {
                Text(stringResource(R.string.close_dialog))
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            accent.copy(alpha = 0.16f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Text(
                    room.description,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SamrAvatar(
                        imageUrl = room.host.avatarUrl,
                        name = room.host.displayName,
                        size = 54.dp,
                        isVerified = room.host.isVerified
                    )
                    room.coHosts.forEach { host ->
                        SamrAvatar(
                            imageUrl = host.avatarUrl,
                            name = host.displayName,
                            size = 46.dp,
                            isVerified = host.isVerified
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            stringResource(R.string.majlis_reactions),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(
                onClick = onApplause,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SamrChampagne.copy(alpha = 0.10f))
            ) {
                Text("👏  " + room.applauseCount + "  " + stringResource(R.string.majlis_applause))
            }
            TextButton(
                onClick = onHeart,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
            ) {
                Text("❤  " + room.heartCount + "  " + stringResource(R.string.majlis_heart))
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onToggleHand,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (room.isHandRaised) SamrEmerald.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (room.isHandRaised) SamrEmerald else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    if (room.isHandRaised) Icons.Default.CheckCircle else Icons.Default.Mic,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (room.isHandRaised) stringResource(R.string.majlis_lower_hand)
                    else stringResource(R.string.majlis_raise_hand)
                )
            }

            Button(
                onClick = onLeave,
                modifier = Modifier.weight(0.72f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.13f),
                    contentColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.majlis_leave))
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
