package com.samr.social.features.home

import android.content.Intent
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samr.social.R
import com.samr.social.core.designsystem.components.AuraAvatar
import com.samr.social.core.designsystem.components.AuraEmptyState
import com.samr.social.core.designsystem.components.AuraPostCard
import com.samr.social.core.designsystem.components.AuraPrimaryButton
import com.samr.social.core.designsystem.components.AuraSecondaryButton
import com.samr.social.core.model.CatchUpSummary
import com.samr.social.core.model.EchoNote
import com.samr.social.core.model.MoodType
import com.samr.social.core.model.Post
import com.samr.social.core.model.Story
import com.samr.social.core.repository.SamrRepository
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.samr.social.ui.theme.AuraChampagne
import com.samr.social.ui.theme.AuraViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: SamrRepository,
    onNavigateToProfile: () -> Unit,
    onNavigateToDiscover: () -> Unit,
    onCreatePost: () -> Unit = {},
    onOpenStudio: () -> Unit = {},
    onOpenMajlis: () -> Unit = {},
    onOpenMediaStudio: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current

    val posts by repository.posts.collectAsState()
    val stories by repository.stories.collectAsState()
    val echoNotes by repository.echoNotes.collectAsState()
    val activeLayer by repository.activeLayer.collectAsState()
    val activeMood by repository.activeMood.collectAsState()
    val isQuietMode by repository.isQuietMode.collectAsState()
    val currentUser by repository.currentUser.collectAsState()
    val comments by repository.comments.collectAsState()
    val experiencePreferences by repository.experiencePreferences.collectAsState()
    val mutedUserIds by repository.mutedUserIds.collectAsState()
    val majlisRooms by repository.majlisRooms.collectAsState()
    val notifications by repository.notifications.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        stringResource(R.string.feed_for_you),
        stringResource(R.string.feed_following),
        stringResource(R.string.feed_trending),
        stringResource(R.string.feed_circles)
    )

    var activeRoomId by remember { mutableStateOf<String?>(null) }
    var showCatchUpSheet by remember { mutableStateOf(false) }
    var selectedPostId by remember { mutableStateOf<String?>(null) }
    var selectedStory by remember { mutableStateOf<Story?>(null) }
    var showAddStoryDialog by remember { mutableStateOf(false) }
    var showEchoDialog by remember { mutableStateOf(false) }
    var selectedEcho by remember { mutableStateOf<EchoNote?>(null) }
    var editingPost by remember { mutableStateOf<Post?>(null) }
    var deletingPost by remember { mutableStateOf<Post?>(null) }

    val filteredPosts = remember(posts, activeLayer, activeMood, selectedTabIndex, mutedUserIds) {
        posts.sortedByDescending { it.isPinned }.filter { post ->
            if (post.isHidden || post.author.id in mutedUserIds) return@filter false
            val matchesLayer = post.socialLayer == activeLayer || selectedTabIndex == 0
            val matchesMood = activeMood == MoodType.ALL || post.mood == activeMood
            val matchesTab = when (selectedTabIndex) {
                0 -> true // For You
                1 -> post.author.isFollowing // Following
                2 -> post.likesCount > 2000 // Trending
                3 -> post.circle != null // Circles
                else -> true
            }
            matchesLayer && matchesMood && matchesTab
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            EchoTray(
                notes = echoNotes,
                onAddClick = { showEchoDialog = true },
                onNoteClick = {
                    selectedEcho = it
                    if (it.isMine) showEchoDialog = true
                }
            )
        }

        // Stories Tray
        item {
            StoriesTray(
                stories = stories,
                onStoryClick = {
                    repository.markStoryViewed(it.id)
                    selectedStory = it
                },
                onAddStoryClick = { showAddStoryDialog = true }
            )
        }

        item {
            SamrTodayBrief(
                postsCount = posts.size,
                unreadMessages = repository.catchUpSummary.unreadMessagesCount,
                isQuietMode = isQuietMode,
                onCreate = onCreatePost,
                onCatchUp = { showCatchUpSheet = true },
                onQuiet = repository::toggleQuietMode,
                onStudio = onOpenStudio,
                onMajlis = onOpenMajlis,
                onMediaStudio = onOpenMediaStudio
            )
        }

        item {
            SocialPulseBar(
                liveCount = majlisRooms.count { it.status == com.samr.social.core.model.MajlisStatus.LIVE },
                unreadSignals = notifications.count { !it.isRead },
                onClick = onOpenMajlis
            )
        }

        // Mood Filter Selector Bar
        item {
            MoodSelectorBar(
                activeMood = activeMood,
                onMoodSelected = { mood ->
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    repository.setMood(mood)
                }
            )
        }

        // Feed Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = AuraChampagne,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = AuraChampagne,
                            height = 2.dp
                        )
                    }
                },
                divider = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            selectedTabIndex = index
                        },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (selectedTabIndex == index) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                color = if (selectedTabIndex == index) AuraChampagne else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Posts List or Empty State
        if (filteredPosts.isEmpty()) {
            item {
                AuraEmptyState(
                    title = stringResource(R.string.empty_feed_title),
                    description = stringResource(R.string.empty_feed_desc),
                    actionText = stringResource(R.string.trending_topics),
                    onActionClick = onNavigateToDiscover,
                    modifier = Modifier.padding(top = 40.dp)
                )
            }
        } else {
            items(filteredPosts, key = { it.id }) { post ->
                AuraPostCard(
                    post = post,
                    isQuietMode = isQuietMode,
                    onLikeClick = { repository.toggleLike(post.id) },
                    onCommentClick = { selectedPostId = post.id },
                    onBookmarkClick = { repository.toggleBookmark(post.id) },
                    onRepostClick = { repository.toggleRepost(post.id) },
                    onShareClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, post.text)
                        }
                        context.startActivity(Intent.createChooser(intent, null))
                    },
                    onAuthorClick = onNavigateToProfile,
                    onJoinRoomClick = { roomId ->
                        activeRoomId = roomId
                    },
                    isOwner = post.author.id == currentUser.id,
                    onEditClick = { editingPost = post },
                    onDeleteClick = { deletingPost = post },
                    onPinClick = { repository.togglePinPost(post.id) },
                    onResonanceClick = { repository.toggleResonance(post.id) },
                    onPollVote = { optionId -> repository.votePoll(post.id, optionId) },
                    onMuteCreatorClick = { repository.toggleMuteUser(post.author.id) },
                    onReportClick = { repository.reportPost(post.id) },
                    onHideClick = { repository.hidePost(post.id) },
                    compactMode = experiencePreferences.compactFeed
                )
            }
        }
    }

    // Discussion Room Bottom Sheet
    if (activeRoomId != null) {
        ModalBottomSheet(
            onDismissRequest = { activeRoomId = null },
            containerColor = MaterialTheme.colorScheme.surface,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            DiscussionRoomSheetContent(
                roomId = activeRoomId!!,
                onClose = { activeRoomId = null }
            )
        }
    }

    // Smart Catch-up Sheet
    if (showCatchUpSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCatchUpSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            SmartCatchUpContent(
                summary = repository.catchUpSummary,
                onClose = { showCatchUpSheet = false }
            )
        }
    }

    selectedPostId?.let { postId ->
        val post = posts.firstOrNull { it.id == postId }
        if (post != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedPostId = null },
                containerColor = MaterialTheme.colorScheme.surface,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                CommentsSheetContent(
                    post = post,
                    comments = comments.filter { it.postId == postId },
                    currentUserId = currentUser.id,
                    onAddComment = { repository.addComment(postId, it) },
                    onDeleteComment = repository::deleteComment,
                    onLikeComment = repository::toggleCommentLike,
                    onClose = { selectedPostId = null }
                )
            }
        }
    }

    selectedStory?.let { story ->
        ModalBottomSheet(
            onDismissRequest = { selectedStory = null },
            containerColor = MaterialTheme.colorScheme.surface,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            StoryViewerContent(
                story = story,
                isOwner = story.author.id == currentUser.id,
                onDelete = {
                    repository.deleteStory(story.id)
                    selectedStory = null
                },
                onClose = { selectedStory = null }
            )
        }
    }

    if (showEchoDialog) {
        val mine = echoNotes.firstOrNull { it.isMine }
        var echoText by remember(showEchoDialog) { mutableStateOf(mine?.text ?: "") }
        var echoEmoji by remember(showEchoDialog) { mutableStateOf(mine?.emoji ?: "✦") }
        AlertDialog(
            onDismissRequest = {
                showEchoDialog = false
                selectedEcho = null
            },
            title = { Text(stringResource(R.string.echo_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("✦", "☕", "☀", "◌", "♡", "⚡").forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (echoEmoji == emoji) AuraChampagne.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { echoEmoji = emoji }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(emoji)
                            }
                        }
                    }
                    OutlinedTextField(
                        value = echoText,
                        onValueChange = { if (it.length <= 72) echoText = it },
                        label = { Text(stringResource(R.string.echo_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.setEcho(echoText, echoEmoji)
                        showEchoDialog = false
                        selectedEcho = null
                    },
                    enabled = echoText.isNotBlank()
                ) {
                    Text(stringResource(R.string.echo_edit))
                }
            },
            dismissButton = {
                if (mine != null) {
                    TextButton(
                        onClick = {
                            repository.deleteMyEcho()
                            showEchoDialog = false
                            selectedEcho = null
                        }
                    ) {
                        Text(
                            stringResource(R.string.echo_delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        )
    }

    if (showAddStoryDialog) {
        var mediaUrl by remember { mutableStateOf("") }
        var caption by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddStoryDialog = false },
            title = { Text(stringResource(R.string.add_story)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = mediaUrl,
                        onValueChange = { mediaUrl = it },
                        label = { Text(stringResource(R.string.story_media_url)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = caption,
                        onValueChange = { caption = it },
                        label = { Text(stringResource(R.string.story_caption)) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.addStory(mediaUrl, caption)
                        showAddStoryDialog = false
                    }
                ) {
                    Text(stringResource(R.string.create_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStoryDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    editingPost?.let { post ->
        var editedText by remember(post.id) { mutableStateOf(post.text) }
        AlertDialog(
            onDismissRequest = { editingPost = null },
            title = { Text(stringResource(R.string.edit_post)) },
            text = {
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.editPost(post.id, editedText)
                        editingPost = null
                    },
                    enabled = editedText.isNotBlank()
                ) {
                    Text(stringResource(R.string.save_changes))
                }
            },
            dismissButton = {
                TextButton(onClick = { editingPost = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    deletingPost?.let { post ->
        AlertDialog(
            onDismissRequest = { deletingPost = null },
            title = { Text(stringResource(R.string.delete_post)) },
            text = { Text(post.text.take(120)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.deletePost(post.id)
                        deletingPost = null
                    }
                ) {
                    Text(
                        stringResource(R.string.delete_action),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingPost = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}


@Composable
fun CommentsSheetContent(
    post: Post,
    comments: List<com.samr.social.core.model.PostComment>,
    currentUserId: String,
    onAddComment: (String) -> Unit,
    onDeleteComment: (String) -> Unit,
    onLikeComment: (String) -> Unit = {},
    onClose: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.comments_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close_dialog))
            }
        }

        if (comments.isEmpty()) {
            Text(
                text = stringResource(R.string.no_comments_yet),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        } else {
            comments.forEach { comment ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 9.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    AuraAvatar(
                        imageUrl = comment.author.avatarUrl,
                        name = comment.author.displayName,
                        size = 38.dp,
                        isVerified = comment.author.isVerified
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp)
                    ) {
                        Text(
                            comment.author.displayName,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(comment.text, style = MaterialTheme.typography.bodyMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                comment.timestampLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (comment.isLiked) stringResource(R.string.comment_liked)
                                else stringResource(R.string.comment_like),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (comment.isLiked) AuraChampagne else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.clickable { onLikeComment(comment.id) }
                            )
                            if (comment.likesCount > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    comment.likesCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    if (comment.author.id == currentUserId) {
                        TextButton(onClick = { onDeleteComment(comment.id) }) {
                            Text(
                                stringResource(R.string.delete_action),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        if (post.allowComments) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text(stringResource(R.string.add_comment_hint)) },
                    modifier = Modifier.weight(1f),
                    maxLines = 3
                )
                TextButton(
                    onClick = {
                        onAddComment(commentText)
                        commentText = ""
                    },
                    enabled = commentText.isNotBlank()
                ) {
                    Text(stringResource(R.string.send_action), color = AuraChampagne)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StoryViewerContent(
    story: Story,
    isOwner: Boolean,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AuraAvatar(
                imageUrl = story.author.avatarUrl,
                name = story.author.displayName,
                size = 42.dp,
                isVerified = story.author.isVerified
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    story.author.displayName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    stringResource(R.string.story_viewer),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isOwner) {
                TextButton(onClick = onDelete) {
                    Text(
                        stringResource(R.string.delete_story),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close_dialog))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(story.mediaUrl)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.story_viewer),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp)
                .clip(RoundedCornerShape(24.dp))
        )
        if (story.caption.isNotBlank()) {
            Text(
                text = story.caption,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 14.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun EchoTray(
    notes: List<EchoNote>,
    onAddClick: () -> Unit,
    onNoteClick: (EchoNote) -> Unit
) {
    Column(modifier = Modifier.padding(top = 6.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.echo_title),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = AuraChampagne
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "24h",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .width(118.dp)
                        .height(82.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    AuraChampagne.copy(alpha = 0.18f),
                                    AuraViolet.copy(alpha = 0.10f)
                                )
                            )
                        )
                        .border(1.dp, AuraChampagne.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .clickable(onClick = onAddClick)
                        .padding(12.dp)
                ) {
                    Column {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.echo_title),
                            tint = AuraChampagne,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.echo_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            items(notes, key = { it.id }) { note ->
                Box(
                    modifier = Modifier
                        .width(168.dp)
                        .height(82.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            1.dp,
                            if (note.isMine) AuraChampagne.copy(alpha = 0.45f)
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onNoteClick(note) }
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        AuraAvatar(
                            imageUrl = note.author.avatarUrl,
                            name = note.author.displayName,
                            size = 34.dp,
                            isVerified = note.author.isVerified
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = note.emoji + "  " + note.text,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = note.author.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialPulseBar(
    liveCount: Int,
    unreadSignals: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AuraChampagne.copy(alpha = 0.08f))
            .border(1.dp, AuraChampagne.copy(alpha = 0.22f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF4D67))
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            Text(
                stringResource(R.string.social_pulse),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = AuraChampagne
            )
            Text(
                stringResource(R.string.social_pulse_desc, liveCount, unreadSignals),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            stringResource(R.string.quick_majlis),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = AuraChampagne
        )
    }
}

@Composable
private fun SamrTodayBrief(
    postsCount: Int,
    unreadMessages: Int,
    isQuietMode: Boolean,
    onCreate: () -> Unit,
    onCatchUp: () -> Unit,
    onQuiet: () -> Unit,
    onStudio: () -> Unit,
    onMajlis: () -> Unit,
    onMediaStudio: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        AuraViolet.copy(alpha = 0.17f),
                        AuraChampagne.copy(alpha = 0.11f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .border(1.dp, AuraChampagne.copy(alpha = 0.22f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(AuraChampagne.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AuraChampagne)
            }
            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                Text(
                    stringResource(R.string.today_brief),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    stringResource(R.string.today_brief_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BriefMetric(postsCount.toString(), stringResource(R.string.posts_stat), Modifier.weight(1f))
            BriefMetric(unreadMessages.toString(), stringResource(R.string.nav_inbox), Modifier.weight(1f))
            BriefMetric(if (isQuietMode) "ON" else "OFF", stringResource(R.string.quick_quiet), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            QuickActionChip(stringResource(R.string.quick_create), onCreate, Modifier.weight(1f))
            QuickActionChip(stringResource(R.string.quick_catchup), onCatchUp, Modifier.weight(1f))
            QuickActionChip(stringResource(R.string.quick_quiet), onQuiet, Modifier.weight(1f))
            QuickActionChip(stringResource(R.string.quick_studio), onStudio, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            QuickActionChip(
                label = stringResource(R.string.quick_majlis),
                onClick = onMajlis,
                modifier = Modifier.weight(1f)
            )
            QuickActionChip(
                label = stringResource(R.string.open_media_studio),
                onClick = onMediaStudio,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BriefMetric(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.72f))
            .padding(horizontal = 10.dp, vertical = 9.dp)
    ) {
        Text(
            value,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = AuraChampagne
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
private fun QuickActionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

@Composable
fun StoriesTray(
    stories: List<Story>,
    onStoryClick: (Story) -> Unit,
    onAddStoryClick: () -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onAddStoryClick)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.your_story),
                            tint = AuraChampagne,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.your_story),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        items(stories, key = { it.id }) { story ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onStoryClick(story) }
            ) {
                AuraAvatar(
                    imageUrl = story.author.avatarUrl,
                    name = story.author.displayName,
                    size = 56.dp,
                    hasStory = true,
                    isStoryViewed = story.isViewed
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = story.author.displayName.split(" ").firstOrNull() ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun MoodSelectorBar(
    activeMood: MoodType,
    onMoodSelected: (MoodType) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MoodType.values().forEach { mood ->
            val isSelected = mood == activeMood
            val label = when (mood) {
                MoodType.ALL -> stringResource(R.string.feed_for_you)
                MoodType.RELAX -> stringResource(R.string.mood_relax)
                MoodType.DISCOVER -> stringResource(R.string.mood_discover)
                MoodType.LEARN -> stringResource(R.string.mood_learn)
                MoodType.LAUGH -> stringResource(R.string.mood_laugh)
                MoodType.CONNECT -> stringResource(R.string.mood_connect)
                MoodType.FOCUS -> stringResource(R.string.mood_focus)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) AuraChampagne.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        1.dp,
                        if (isSelected) AuraChampagne else Color.Transparent,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onMoodSelected(mood) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) AuraChampagne else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun DiscussionRoomSheetContent(
    roomId: String,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Forum,
                    contentDescription = null,
                    tint = AuraChampagne,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(R.string.view_discussion_room),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "52 مشاركاً • حوار مباشر في سَمَر",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_dialog),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(14.dp)
        ) {
            Text(
                text = "موضوع الجلسة: أثر تبسيط مسارات المستخدم في رفع رضا وولاء العملاء لتطبيقات الهواتف الحديثة.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        AuraPrimaryButton(
            text = "الانضمام للمتحدثين",
            onClick = onClose,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        AuraSecondaryButton(
            text = "الاستماع كضيف",
            onClick = onClose,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SmartCatchUpContent(
    summary: CatchUpSummary,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AuraChampagne,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(R.string.catch_up_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.catch_up_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_dialog),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "${summary.missedPostsCount}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AuraChampagne,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.catch_up_key_posts),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "${summary.circleUpdatesCount}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AuraViolet,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.catch_up_missed_circles),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "${summary.unreadMessagesCount}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AuraChampagne,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.catch_up_direct_messages),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AuraPrimaryButton(
            text = stringResource(R.string.review_catch_up),
            onClick = onClose,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
