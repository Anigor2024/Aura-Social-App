package com.samr.social.core.designsystem.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.samr.social.R
import com.samr.social.core.model.Post
import com.samr.social.core.model.PostLifetime
import com.samr.social.core.util.FormatUtils
import com.samr.social.ui.theme.SamrChampagne
import com.samr.social.ui.theme.SamrRose

@Composable
fun SamrPostCard(
    post: Post,
    isQuietMode: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onRepostClick: () -> Unit,
    onShareClick: () -> Unit,
    onAuthorClick: () -> Unit,
    onJoinRoomClick: ((String) -> Unit)? = null,
    isOwner: Boolean = false,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onPinClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var showMenu by remember { mutableStateOf(false) }
    val isArabic = LocalLayoutDirection.current == LayoutDirection.Rtl

    // Like heart bounce animation
    val likeScale by animateFloatAsState(
        targetValue = if (post.isLiked) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "like_bounce"
    )
    val heartColor by animateColorAsState(
        targetValue = if (post.isLiked) SamrRose else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "heart_color"
    )

    // Relative Time string
    val relativeTime = remember(post.timestampMinutesAgo, isArabic) {
        FormatUtils.formatRelativeTime(post.timestampMinutesAgo, isArabic)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("post_card_${post.id}")
    ) {
        // Author Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onAuthorClick)
            ) {
                SamrAvatar(
                    imageUrl = post.author.avatarUrl,
                    name = post.author.displayName,
                    size = 42.dp,
                    isVerified = post.author.isVerified
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.author.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Collaborator indicator
                        if (post.collaborator != null) {
                            Text(
                                text = " ${stringResource(R.string.collaborator_with)} ${post.collaborator.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SamrChampagne
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "@${post.author.username}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = " • $relativeTime",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Circle indicator
                        if (post.circle != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(post.circle.colorHex).copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = post.circle.localizedName(isArabic),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color(post.circle.colorHex)
                                )
                            }
                        }
                    }
                }
            }

            // More Options Menu & Expiration badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (post.isPinned) {
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SamrChampagne.copy(alpha = 0.14f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "PIN",
                            style = MaterialTheme.typography.labelSmall,
                            color = SamrChampagne
                        )
                    }
                }

                if (post.lifetime != PostLifetime.PERMANENT) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = stringResource(R.string.time_limited_post),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (post.lifetime) {
                                PostLifetime.HOURS_24 -> stringResource(R.string.expires_24h)
                                PostLifetime.DAYS_3 -> stringResource(R.string.expires_3d)
                                PostLifetime.DAYS_7 -> stringResource(R.string.expires_7d)
                                PostLifetime.PERMANENT -> ""
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = stringResource(R.string.more_options),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        if (isOwner) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.edit_post)) },
                                onClick = {
                                    showMenu = false
                                    onEditClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.pin_post)) },
                                onClick = {
                                    showMenu = false
                                    onPinClick()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(R.string.delete_post),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick()
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_copy_link)) },
                                onClick = {
                                    showMenu = false
                                    onShareClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_mute_creator)) },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(R.string.menu_report),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = { showMenu = false }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Post Text Body
        Text(
            text = post.text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp
        )

        // Post Media Attachments
        if (post.mediaUrls.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            val firstMedia = post.mediaUrls.first()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(firstMedia)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.post_media_desc),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )

                if (post.mediaUrls.size > 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "1/${post.mediaUrls.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Attached Active Discussion Room Indicator
        if (post.discussionRoomTopic != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SamrChampagne.copy(alpha = 0.12f))
                    .border(1.dp, SamrChampagne.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onJoinRoomClick?.invoke(post.id)
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = stringResource(R.string.live_discussion_room),
                        tint = SamrChampagne,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.live_discussion_room),
                            style = MaterialTheme.typography.labelSmall,
                            color = SamrChampagne
                        )
                        Text(
                            text = post.discussionRoomTopic,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SamrChampagne)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.join_room),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interaction Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Like Action
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onLikeClick()
                    }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(R.string.like),
                    tint = heartColor,
                    modifier = Modifier
                        .scale(likeScale)
                        .size(20.dp)
                )
                if (!isQuietMode && !post.hideLikeCount && post.likesCount > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = FormatUtils.formatCount(post.likesCount, isArabic),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (post.isLiked) SamrRose else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Comment Action
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onCommentClick()
                    }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = stringResource(R.string.comment),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                if (!isQuietMode && post.commentsCount > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = FormatUtils.formatCount(post.commentsCount, isArabic),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Repost Action
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onRepostClick()
                    }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Repeat,
                    contentDescription = stringResource(R.string.repost),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                if (!isQuietMode && post.repostsCount > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = FormatUtils.formatCount(post.repostsCount, isArabic),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Bookmark Action
            IconButton(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onBookmarkClick()
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (post.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = stringResource(R.string.bookmark),
                    tint = if (post.isBookmarked) SamrChampagne else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Share Action
            IconButton(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onShareClick()
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = stringResource(R.string.share),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Deprecated compatibility alias
@Composable
fun AuraPostCard(
    post: Post,
    isQuietMode: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onRepostClick: () -> Unit,
    onShareClick: () -> Unit,
    onAuthorClick: () -> Unit,
    onJoinRoomClick: ((String) -> Unit)? = null,
    isOwner: Boolean = false,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onPinClick: () -> Unit = {},
    modifier: Modifier = Modifier
) = SamrPostCard(
    post = post,
    isQuietMode = isQuietMode,
    onLikeClick = onLikeClick,
    onCommentClick = onCommentClick,
    onBookmarkClick = onBookmarkClick,
    onRepostClick = onRepostClick,
    onShareClick = onShareClick,
    onAuthorClick = onAuthorClick,
    onJoinRoomClick = onJoinRoomClick,
    isOwner = isOwner,
    onEditClick = onEditClick,
    onDeleteClick = onDeleteClick,
    onPinClick = onPinClick,
    modifier = modifier
)
