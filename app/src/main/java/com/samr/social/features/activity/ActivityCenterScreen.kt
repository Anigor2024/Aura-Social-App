package com.samr.social.features.activity

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.CircleNotifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samr.social.R
import com.samr.social.core.designsystem.components.SamrAvatar
import com.samr.social.core.model.NotificationType
import com.samr.social.core.repository.SamrRepository
import com.samr.social.ui.theme.SamrChampagne

@Composable
fun ActivityCenterScreen(
    repository: SamrRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications by repository.notifications.collectAsState()
    val collections by repository.savedCollections.collectAsState()
    val circles by repository.circles.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateCollection by remember { mutableStateOf(false) }
    var notificationFilter by remember { mutableIntStateOf(0) }
    var showCreateCircle by remember { mutableStateOf(false) }

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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button)
                )
            }
            Text(
                text = stringResource(R.string.activity_center),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            if (selectedTab == 0 && notifications.any { !it.isRead }) {
                TextButton(onClick = repository::markAllNotificationsRead) {
                    Text(stringResource(R.string.mark_all_read), color = SamrChampagne)
                }
            }
            if (selectedTab == 1) {
                IconButton(onClick = { showCreateCollection = true }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_collection))
                }
            }
            if (selectedTab == 2) {
                IconButton(onClick = { showCreateCircle = true }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_circle))
                }
            }
        }

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(stringResource(R.string.notifications_tab)) },
                icon = { Icon(Icons.Default.Notifications, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(stringResource(R.string.collections_tab)) },
                icon = { Icon(Icons.Default.Bookmark, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text(stringResource(R.string.circles_tab)) },
                icon = { Icon(Icons.Default.Groups, contentDescription = null) }
            )
        }

        val visibleNotifications = notifications.filter { item ->
            when (notificationFilter) {
                1 -> !item.isRead
                2 -> item.type != NotificationType.SYSTEM
                3 -> item.type == NotificationType.SYSTEM
                else -> true
            }
        }

        when (selectedTab) {
            0 -> LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            stringResource(R.string.activity_filter_all),
                            stringResource(R.string.activity_filter_unread),
                            stringResource(R.string.activity_filter_social),
                            stringResource(R.string.activity_filter_system)
                        ).forEachIndexed { index, label ->
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (notificationFilter == index) SamrChampagne else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (notificationFilter == index) SamrChampagne.copy(alpha = 0.12f)
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .clickable { notificationFilter = index }
                                    .padding(horizontal = 9.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                items(visibleNotifications, key = { it.id }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (item.isRead) MaterialTheme.colorScheme.surface
                                else SamrChampagne.copy(alpha = 0.10f)
                            )
                            .clickable { repository.markNotificationRead(item.id) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.actor != null) {
                            SamrAvatar(
                                imageUrl = item.actor.avatarUrl,
                                name = item.actor.displayName,
                                size = 46.dp,
                                isVerified = item.actor.isVerified
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(SamrChampagne.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.CircleNotifications,
                                    contentDescription = null,
                                    tint = SamrChampagne
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                if (!item.isRead) {
                                    Spacer(modifier = Modifier.size(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(SamrChampagne)
                                    )
                                }
                            }
                            Text(
                                text = item.body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.timestampLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { repository.deleteNotification(item.id) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_action),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            1 -> LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(collections, key = { it.id }) { collection ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SamrChampagne.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, tint = SamrChampagne)
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        ) {
                            Text(
                                collection.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                stringResource(R.string.saved_items_count, collection.postIds.size),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { repository.deleteSavedCollection(collection.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_action))
                        }
                    }
                }
            }

            else -> LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(circles, key = { it.id }) { circle ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(circle.colorHex).copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = Color(circle.colorHex))
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        ) {
                            Text(
                                circle.nameAr,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                circle.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (circle.id.startsWith("circle_") && circle.memberCount <= 1) {
                            IconButton(onClick = { repository.deleteCircle(circle.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_action))
                            }
                        } else {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SamrChampagne
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateCollection) {
        var title by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateCollection = false },
            title = { Text(stringResource(R.string.new_collection)) },
            text = {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.collection_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.createSavedCollection(title)
                        showCreateCollection = false
                    },
                    enabled = title.isNotBlank()
                ) {
                    Text(stringResource(R.string.create_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateCollection = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showCreateCircle) {
        var nameAr by remember { mutableStateOf("") }
        var nameEn by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateCircle = false },
            title = { Text(stringResource(R.string.new_circle)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameAr,
                        onValueChange = { nameAr = it },
                        label = { Text(stringResource(R.string.circle_name_ar)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nameEn,
                        onValueChange = { nameEn = it },
                        label = { Text(stringResource(R.string.circle_name_en)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(R.string.description_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.createCircle(nameAr, nameEn, description)
                        showCreateCircle = false
                    },
                    enabled = nameAr.isNotBlank() || nameEn.isNotBlank()
                ) {
                    Text(stringResource(R.string.create_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateCircle = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
