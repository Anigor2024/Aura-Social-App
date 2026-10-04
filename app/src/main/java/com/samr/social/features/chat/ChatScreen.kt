package com.samr.social.features.chat

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.samr.social.R
import com.samr.social.core.designsystem.components.AuraAvatar
import com.samr.social.core.model.Conversation
import com.samr.social.core.model.DirectMessage
import com.samr.social.core.model.MessageStatus
import com.samr.social.core.repository.SamrRepository
import com.samr.social.ui.theme.AuraChampagne
import com.samr.social.ui.theme.AuraViolet
import com.samr.social.ui.theme.ObsidianVoid

@Composable
fun ChatScreen(
    repository: SamrRepository,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val conversations by repository.conversations.collectAsState()
    var selectedConversation by remember { mutableStateOf<Conversation?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }

    if (selectedConversation != null) {
        BackHandler { selectedConversation = null }
        ConversationThreadScreen(
            conversation = selectedConversation!!,
            repository = repository,
            onBack = { selectedConversation = null },
            modifier = modifier
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.messages_title),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Tabs: Direct vs Circles
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = AuraChampagne,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AuraChampagne,
                        height = 2.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(stringResource(R.string.direct_chats)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(stringResource(R.string.circles_chats)) }
                )
            }

            // Conversation items
            val displayed = remember(conversations, selectedTab) {
                conversations.filter {
                    if (selectedTab == 0) !it.isCircleChat else it.isCircleChat
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
            ) {
                items(displayed, key = { it.id }) { conv ->
                    ConversationItemRow(
                        conversation = conv,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            selectedConversation = conv
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationItemRow(
    conversation: Conversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AuraAvatar(
            imageUrl = conversation.participant.avatarUrl,
            name = conversation.participant.displayName,
            size = 52.dp,
            isOnline = conversation.isOnline
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (conversation.isCircleChat) conversation.circleName ?: conversation.participant.displayName else conversation.participant.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = conversation.lastTimestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                if (conversation.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(AuraChampagne),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conversation.unreadCount.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ObsidianVoid
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationThreadScreen(
    conversation: Conversation,
    repository: SamrRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val messages by repository.threadMessages.collectAsState()
    var inputMessage by remember { mutableStateOf("") }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var editingMessage by remember { mutableStateOf<DirectMessage?>(null) }
    var deletingMessage by remember { mutableStateOf<DirectMessage?>(null) }
    var showDeleteConversation by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()
    ) {
        // Thread Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            AuraAvatar(
                imageUrl = conversation.participant.avatarUrl,
                name = conversation.participant.displayName,
                size = 38.dp,
                isOnline = conversation.isOnline
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conversation.participant.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (conversation.isOnline) stringResource(R.string.online_now) else stringResource(R.string.last_seen, "15m"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { showDeleteConversation = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_action),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubble(
                    msg = msg,
                    onEdit = { editingMessage = msg },
                    onDelete = { deletingMessage = msg }
                )
            }
        }

        // Voice Recording State Banner
        AnimatedVisibility(visible = isRecordingVoice) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.recording, "0:04"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        isRecordingVoice = false
                        repository.sendMessage(conversation.id, "Voice note (0:12)", voiceDuration = 12)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Send Voice",
                        tint = AuraChampagne
                    )
                }
            }
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.type_message_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AuraChampagne,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (inputMessage.isNotBlank()) {
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        repository.sendMessage(conversation.id, inputMessage)
                        inputMessage = ""
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AuraChampagne)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = ObsidianVoid,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        isRecordingVoice = !isRecordingVoice
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isRecordingVoice) Color.Red.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = stringResource(R.string.voice_message),
                        tint = if (isRecordingVoice) Color.Red else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }

    editingMessage?.let { message ->
        var editedText by remember(message.id) { mutableStateOf(message.text) }
        AlertDialog(
            onDismissRequest = { editingMessage = null },
            title = { Text(stringResource(R.string.edit_post)) },
            text = {
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.editMessage(message.id, editedText)
                        editingMessage = null
                    },
                    enabled = editedText.isNotBlank()
                ) {
                    Text(stringResource(R.string.save_changes))
                }
            },
            dismissButton = {
                TextButton(onClick = { editingMessage = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    deletingMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { deletingMessage = null },
            title = { Text(stringResource(R.string.delete_action)) },
            text = { Text(message.text.ifBlank { stringResource(R.string.voice_message) }) },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.deleteMessage(message.id)
                        deletingMessage = null
                    }
                ) {
                    Text(stringResource(R.string.delete_action), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingMessage = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showDeleteConversation) {
        AlertDialog(
            onDismissRequest = { showDeleteConversation = false },
            title = { Text(stringResource(R.string.delete_action)) },
            text = { Text(conversation.participant.displayName) },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.deleteConversation(conversation.id)
                        showDeleteConversation = false
                        onBack()
                    }
                ) {
                    Text(stringResource(R.string.delete_action), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConversation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun MessageBubble(
    msg: DirectMessage,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val bubbleColor = if (msg.isMine) AuraChampagne else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (msg.isMine) ObsidianVoid else MaterialTheme.colorScheme.onSurface

    var isPlayingVoice by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isMine) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (msg.isMine) 18.dp else 4.dp,
                        bottomEnd = if (msg.isMine) 4.dp else 18.dp
                    )
                )
                .background(bubbleColor)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (msg.voiceDurationSeconds != null) {
                // Interactive Voice Note Bubble with Waveform
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { isPlayingVoice = !isPlayingVoice },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (msg.isMine) ObsidianVoid else AuraChampagne)
                    ) {
                        Icon(
                            imageVector = if (isPlayingVoice) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (msg.isMine) AuraChampagne else ObsidianVoid,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Audio Waveform bars
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        val bars = msg.voiceWaveform ?: listOf(0.3f, 0.6f, 0.9f, 0.4f, 0.7f, 0.3f, 0.8f, 0.5f)
                        bars.forEach { fraction ->
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height((18 * fraction + 6).dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(textColor.copy(alpha = 0.8f))
                            )
                        }
                    }

                    Text(
                        text = "0:${msg.voiceDurationSeconds}",
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor
                    )
                }
            } else {
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = msg.timestampFormatted,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (msg.isMine) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = if (msg.status == MessageStatus.READ) AuraChampagne else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(12.dp)
                )

                var showMessageMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = { showMessageMenu = true },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.more_options),
                            modifier = Modifier.size(15.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = showMessageMenu,
                        onDismissRequest = { showMessageMenu = false }
                    ) {
                        if (msg.voiceDurationSeconds == null) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.edit_post)) },
                                onClick = {
                                    showMessageMenu = false
                                    onEdit()
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(R.string.delete_action),
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                showMessageMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}
