package com.samr.social.features.home

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import com.samr.social.core.model.MoodType
import com.samr.social.core.model.Story
import com.samr.social.core.repository.SamrRepository
import com.samr.social.ui.theme.AuraChampagne
import com.samr.social.ui.theme.AuraViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: SamrRepository,
    onNavigateToProfile: () -> Unit,
    onNavigateToDiscover: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    val posts by repository.posts.collectAsState()
    val stories by repository.stories.collectAsState()
    val activeLayer by repository.activeLayer.collectAsState()
    val activeMood by repository.activeMood.collectAsState()
    val isQuietMode by repository.isQuietMode.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        stringResource(R.string.feed_for_you),
        stringResource(R.string.feed_following),
        stringResource(R.string.feed_trending),
        stringResource(R.string.feed_circles)
    )

    var activeRoomId by remember { mutableStateOf<String?>(null) }
    var showCatchUpSheet by remember { mutableStateOf(false) }

    val filteredPosts = remember(posts, activeLayer, activeMood, selectedTabIndex) {
        posts.filter { post ->
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
        // Stories Tray
        item {
            StoriesTray(
                stories = stories,
                onStoryClick = { },
                onAddStoryClick = { }
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
                    onCommentClick = { },
                    onBookmarkClick = { repository.toggleBookmark(post.id) },
                    onRepostClick = { repository.toggleRepost(post.id) },
                    onShareClick = { },
                    onAuthorClick = onNavigateToProfile,
                    onJoinRoomClick = { roomId ->
                        activeRoomId = roomId
                    }
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
