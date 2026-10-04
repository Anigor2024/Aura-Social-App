package com.samr.social.features.profile

import android.content.Intent
import android.view.HapticFeedbackConstants
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Link
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.samr.social.R
import com.samr.social.core.designsystem.components.AuraAvatar
import com.samr.social.core.designsystem.components.AuraEmptyState
import com.samr.social.core.designsystem.components.AuraPostCard
import com.samr.social.core.designsystem.components.AuraPrimaryButton
import com.samr.social.core.designsystem.components.AuraSecondaryButton
import com.samr.social.core.designsystem.components.getLayerColor
import com.samr.social.core.designsystem.components.getLayerLabel
import com.samr.social.core.repository.SamrRepository
import com.samr.social.features.home.CommentsSheetContent
import com.samr.social.ui.theme.AuraChampagne

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    repository: SamrRepository,
    onNavigateToSettings: () -> Unit,
    onOpenStudio: () -> Unit = {},
    onOpenContentHub: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current
    val currentUser by repository.currentUser.collectAsState()
    val posts by repository.posts.collectAsState()
    val clips by repository.clips.collectAsState()
    val comments by repository.comments.collectAsState()
    val isQuietMode by repository.isQuietMode.collectAsState()
    val experiencePreferences by repository.experiencePreferences.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showEditSheet by remember { mutableStateOf(false) }
    var editingPostId by remember { mutableStateOf<String?>(null) }
    var deletingPostId by remember { mutableStateOf<String?>(null) }
    var commentingPostId by remember { mutableStateOf<String?>(null) }

    // User's own posts and bookmarked posts
    val userPosts = remember(posts, currentUser.id) {
        posts.filter { it.author.id == currentUser.id }
    }
    val bookmarkedPosts = remember(posts) {
        posts.filter { it.isBookmarked }
    }
    val userClips = remember(clips, currentUser.id) {
        clips.filter { it.author.id == currentUser.id }
    }

    val highlights = listOf(
        Pair("Kyoto", "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=300&q=80"),
        Pair("Pavilion", "https://images.unsplash.com/photo-1506157786151-b8491531f063?auto=format&fit=crop&w=300&q=80"),
        Pair("Study", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=300&q=80"),
        Pair("Woodcraft", "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=300&q=80")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Header Cover & Top Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Editorial Cover Art
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=1200&q=80")
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Settings Shortcut Icon
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .statusBarsPadding()
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }
        }

        // Avatar & Info Overlay
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Avatar positioned over the cover boundary
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-36).dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    AuraAvatar(
                        imageUrl = currentUser.avatarUrl,
                        name = currentUser.displayName,
                        size = 80.dp,
                        isVerified = currentUser.isVerified,
                        hasStory = true
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        AuraSecondaryButton(
                            text = stringResource(R.string.edit_profile),
                            onClick = { showEditSheet = true },
                            height = 38.dp
                        )
                        AuraSecondaryButton(
                            text = stringResource(R.string.share_profile),
                            onClick = {
                                val shareText = "@${currentUser.username} — ${currentUser.displayName}\n${currentUser.bio}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(intent, null))
                            },
                            height = 38.dp
                        )
                    }
                }

                // Name & Layer Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentUser.displayName,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(getLayerColor(currentUser.activeSocialLayer).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = getLayerLabel(currentUser.activeSocialLayer),
                            style = MaterialTheme.typography.labelSmall,
                            color = getLayerColor(currentUser.activeSocialLayer)
                        )
                    }
                }

                Text(
                    text = "@${currentUser.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Bio
                Text(
                    text = currentUser.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (currentUser.profileLinks.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.profile_links),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(currentUser.profileLinks) { link ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        val value = if (link.value.startsWith("http")) link.value else "https://" + link.value
                                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(value))
                                        context.startActivity(intent)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Link,
                                    contentDescription = null,
                                    tint = AuraChampagne,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = link.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AuraChampagne
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ProfileStatItem(count = "${currentUser.postsCount}", label = stringResource(R.string.posts_stat))
                    ProfileStatItem(count = "${currentUser.followersCount / 1000}k", label = stringResource(R.string.followers_stat))
                    ProfileStatItem(count = "${currentUser.followingCount}", label = stringResource(R.string.following_stat))
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (currentUser.achievements.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.achievements),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(currentUser.achievements) { achievement ->
                            Column(
                                modifier = Modifier
                                    .width(150.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AuraChampagne.copy(alpha = 0.08f))
                                    .border(1.dp, AuraChampagne.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
                                    .padding(12.dp)
                            ) {
                                Text(achievement.icon, style = MaterialTheme.typography.titleLarge)
                                Text(
                                    achievement.title,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    achievement.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(AuraChampagne.copy(alpha = 0.09f))
                        .border(1.dp, AuraChampagne.copy(alpha = 0.28f), RoundedCornerShape(18.dp))
                        .clickable(onClick = onOpenStudio)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AuraChampagne.copy(alpha = 0.17f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AuraChampagne
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp)
                    ) {
                        Text(
                            stringResource(R.string.creator_studio),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            stringResource(R.string.creator_studio_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        stringResource(R.string.view_studio),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = AuraChampagne
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.28f), RoundedCornerShape(18.dp))
                        .clickable(onClick = onOpenContentHub)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = AuraChampagne)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp)
                    ) {
                        Text(
                            stringResource(R.string.content_hub),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            stringResource(R.string.content_hub_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        stringResource(R.string.open_content_hub),
                        style = MaterialTheme.typography.labelSmall,
                        color = AuraChampagne
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Story Highlights
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(highlights) { (title, imageUrl) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = AuraChampagne,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = AuraChampagne,
                        height = 2.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    icon = { Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    text = { Text(stringResource(R.string.posts_tab)) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = { Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    text = { Text(stringResource(R.string.clips_tab)) }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    text = { Text(stringResource(R.string.saved_tab)) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Tab Content
        if (selectedTabIndex == 1) {
            if (userClips.isEmpty()) {
                item {
                    AuraEmptyState(
                        title = stringResource(R.string.profile_clips_empty),
                        description = stringResource(R.string.empty_feed_desc),
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            } else {
                items(userClips, key = { it.id }) { clip ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(clip.thumbnailUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = clip.caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(92.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        ) {
                            Text(
                                clip.caption,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                clip.audioTrackTitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { repository.toggleClipLike(clip.id) }) {
                            Icon(
                                imageVector = if (clip.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = if (clip.isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            val displayedPosts = if (selectedTabIndex == 2) bookmarkedPosts else userPosts

            if (displayedPosts.isEmpty()) {
                item {
                    AuraEmptyState(
                        title = if (selectedTabIndex == 2) stringResource(R.string.empty_bookmarks_title) else stringResource(R.string.empty_feed_title),
                        description = if (selectedTabIndex == 2) stringResource(R.string.empty_bookmarks_desc) else stringResource(R.string.empty_feed_desc),
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            } else {
                items(displayedPosts, key = { it.id }) { post ->
                    AuraPostCard(
                        post = post,
                        isQuietMode = isQuietMode,
                        onLikeClick = { repository.toggleLike(post.id) },
                        onCommentClick = { commentingPostId = post.id },
                        onBookmarkClick = { repository.toggleBookmark(post.id) },
                        onRepostClick = { repository.toggleRepost(post.id) },
                        onShareClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, post.text)
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        },
                        onAuthorClick = { },
                        isOwner = post.author.id == currentUser.id,
                        onEditClick = { editingPostId = post.id },
                        onDeleteClick = { deletingPostId = post.id },
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
    }

    commentingPostId?.let { postId ->
        val post = posts.firstOrNull { it.id == postId }
        if (post != null) {
            ModalBottomSheet(
                onDismissRequest = { commentingPostId = null },
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
                    onClose = { commentingPostId = null }
                )
            }
        }
    }

    // Edit Profile Modal Bottom Sheet
    if (showEditSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            var updatedName by remember { mutableStateOf(currentUser.displayName) }
            var updatedUsername by remember { mutableStateOf(currentUser.username) }
            var updatedBio by remember { mutableStateOf(currentUser.bio) }
            var updatedLocation by remember { mutableStateOf(currentUser.location) }
            var updatedAvatar by remember { mutableStateOf(currentUser.avatarUrl) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.edit_profile),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = updatedName,
                    onValueChange = { updatedName = it },
                    label = { Text(stringResource(R.string.full_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = updatedUsername,
                    onValueChange = { updatedUsername = it },
                    label = { Text(stringResource(R.string.username_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = updatedBio,
                    onValueChange = { updatedBio = it },
                    label = { Text(stringResource(R.string.bio)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = updatedLocation,
                    onValueChange = { updatedLocation = it },
                    label = { Text(stringResource(R.string.location_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = updatedAvatar,
                    onValueChange = { updatedAvatar = it },
                    label = { Text(stringResource(R.string.avatar_url_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                AuraPrimaryButton(
                    text = stringResource(R.string.save_changes),
                    onClick = {
                        repository.updateProfile(
                            displayName = updatedName,
                            username = updatedUsername,
                            bio = updatedBio,
                            location = updatedLocation,
                            avatarUrl = updatedAvatar
                        )
                        showEditSheet = false
                    },
                    enabled = updatedName.isNotBlank() && updatedUsername.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    editingPostId?.let { postId ->
        val post = posts.firstOrNull { it.id == postId }
        if (post != null) {
            var editedText by remember(postId) { mutableStateOf(post.text) }
            AlertDialog(
                onDismissRequest = { editingPostId = null },
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
                            repository.editPost(postId, editedText)
                            editingPostId = null
                        },
                        enabled = editedText.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save_changes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingPostId = null }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }

    deletingPostId?.let { postId ->
        AlertDialog(
            onDismissRequest = { deletingPostId = null },
            title = { Text(stringResource(R.string.delete_post)) },
            text = { Text(stringResource(R.string.delete_account_dialog_desc)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.deletePost(postId)
                        deletingPostId = null
                    }
                ) {
                    Text(stringResource(R.string.delete_action), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingPostId = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun ProfileStatItem(count: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
