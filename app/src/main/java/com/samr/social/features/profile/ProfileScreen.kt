package com.samr.social.features.profile

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Link
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
import com.samr.social.ui.theme.AuraChampagne

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    repository: SamrRepository,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val currentUser by repository.currentUser.collectAsState()
    val posts by repository.posts.collectAsState()
    val isQuietMode by repository.isQuietMode.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showEditSheet by remember { mutableStateOf(false) }

    // User's own posts and bookmarked posts
    val userPosts = remember(posts, currentUser.id) {
        posts.filter { it.author.id == currentUser.id }
    }
    val bookmarkedPosts = remember(posts) {
        posts.filter { it.isBookmarked }
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
                            onClick = { /* Share */ },
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

                // External Link
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Link,
                        contentDescription = null,
                        tint = AuraChampagne,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "nourstudio.design",
                        style = MaterialTheme.typography.bodySmall,
                        color = AuraChampagne
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ProfileStatItem(count = "${currentUser.postsCount}", label = stringResource(R.string.posts_stat))
                    ProfileStatItem(count = "${currentUser.followersCount / 1000}k", label = stringResource(R.string.followers_stat))
                    ProfileStatItem(count = "${currentUser.followingCount}", label = stringResource(R.string.following_stat))
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
        val displayedPosts = when (selectedTabIndex) {
            0 -> userPosts
            1 -> emptyList() // Clips tab
            2 -> bookmarkedPosts
            else -> userPosts
        }

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
                    onCommentClick = { },
                    onBookmarkClick = { repository.toggleBookmark(post.id) },
                    onRepostClick = { repository.toggleRepost(post.id) },
                    onShareClick = { },
                    onAuthorClick = { }
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
            var updatedBio by remember { mutableStateOf(currentUser.bio) }

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
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = updatedBio,
                    onValueChange = { updatedBio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(20.dp))
                AuraPrimaryButton(
                    text = "Save Changes",
                    onClick = {
                        showEditSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
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
