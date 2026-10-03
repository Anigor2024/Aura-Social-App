package com.example.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.AuraChampagne
import com.example.ui.theme.AuraViolet
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.VerifiedBadgeColor

@Composable
fun AuraAvatar(
    imageUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    hasStory: Boolean = false,
    isStoryViewed: Boolean = false,
    isOnline: Boolean = false,
    isVerified: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val ringBrush = when {
        hasStory && !isStoryViewed -> Brush.sweepGradient(listOf(AuraChampagne, AuraViolet, AuraChampagne))
        hasStory && isStoryViewed -> Brush.linearGradient(listOf(Color.Gray.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.5f)))
        else -> null
    }

    Box(
        modifier = modifier
            .size(if (hasStory) size + 6.dp else size)
            .then(
                if (ringBrush != null) {
                    Modifier
                        .border(2.dp, ringBrush, CircleShape)
                        .clip(CircleShape)
                } else Modifier
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        } else {
            // Initials Fallback
            val initials = name.split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .ifEmpty { "A" }

            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = (size.value * 0.4).sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        // Online Indicator
        if (isOnline) {
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-1).dp, y = (-1).dp)
                    .clip(CircleShape)
                    .background(StatusOnline)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }

        // Verified Badge overlay if needed for compact rows
        if (isVerified && !isOnline) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verified",
                tint = VerifiedBadgeColor,
                modifier = Modifier
                    .size(size * 0.35f)
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}
