package com.samr.social.features.create

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.samr.social.R
import com.samr.social.core.designsystem.components.AuraAvatar
import com.samr.social.core.designsystem.components.AuraPrimaryButton
import com.samr.social.core.designsystem.components.getLayerColor
import com.samr.social.core.designsystem.components.getLayerLabel
import com.samr.social.core.model.PostLifetime
import com.samr.social.core.model.SamrCircle
import com.samr.social.core.repository.SamrRepository
import com.samr.social.ui.theme.AuraChampagne

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    repository: SamrRepository,
    onPostCreated: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val currentUser by repository.currentUser.collectAsState()
    val activeLayer by repository.activeLayer.collectAsState()
    val circles by repository.circles.collectAsState()
    val isArabic = LocalLayoutDirection.current == LayoutDirection.Rtl

    var postText by remember { mutableStateOf("") }
    var selectedLifetime by remember { mutableStateOf(PostLifetime.PERMANENT) }
    var selectedCircle by remember { mutableStateOf<SamrCircle?>(null) }
    var attachedImageUrl by remember { mutableStateOf<String?>(null) }
    var mediaUrlInput by remember { mutableStateOf("") }
    var allowComments by remember { mutableStateOf(true) }
    var hideLikeCount by remember { mutableStateOf(false) }
    var pollEnabled by remember { mutableStateOf(false) }
    var pollQuestion by remember { mutableStateOf("") }
    var pollOption1 by remember { mutableStateOf("") }
    var pollOption2 by remember { mutableStateOf("") }
    var pollOption3 by remember { mutableStateOf("") }
    var pollOption4 by remember { mutableStateOf("") }
    var selectedFilterIndex by remember { mutableStateOf(0) }

    val filterNames = listOf(
        stringResource(R.string.filter_original),
        stringResource(R.string.filter_warm),
        stringResource(R.string.filter_cool),
        stringResource(R.string.filter_noir),
        stringResource(R.string.filter_vibrant)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Bar Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_dialog),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = stringResource(R.string.new_post),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            val hasValidPoll = pollEnabled &&
                pollQuestion.isNotBlank() &&
                listOf(pollOption1, pollOption2, pollOption3, pollOption4).count { it.isNotBlank() } >= 2

            AuraPrimaryButton(
                text = stringResource(R.string.publish),
                onClick = {
                    if (postText.isNotBlank() || attachedImageUrl != null || hasValidPoll) {
                        repository.publishPost(
                            text = postText,
                            mediaUrls = if (attachedImageUrl != null) listOf(attachedImageUrl!!) else emptyList(),
                            circle = selectedCircle,
                            lifetime = selectedLifetime,
                            collaborator = null,
                            allowComments = allowComments,
                            hideLikeCount = hideLikeCount,
                            pollQuestion = if (hasValidPoll) pollQuestion else null,
                            pollOptions = if (hasValidPoll) {
                                listOf(pollOption1, pollOption2, pollOption3, pollOption4)
                            } else {
                                emptyList()
                            }
                        )
                        onPostCreated()
                    }
                },
                enabled = postText.isNotBlank() || attachedImageUrl != null || hasValidPoll,
                height = 40.dp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Author and Context Layer Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            AuraAvatar(
                imageUrl = currentUser.avatarUrl,
                name = currentUser.displayName,
                size = 46.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = currentUser.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(getLayerColor(activeLayer).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = getLayerLabel(activeLayer),
                            style = MaterialTheme.typography.labelSmall,
                            color = getLayerColor(activeLayer)
                        )
                    }

                    if (selectedCircle != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(selectedCircle!!.colorHex).copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = selectedCircle!!.localizedName(isArabic),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(selectedCircle!!.colorHex)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Text Input
        OutlinedTextField(
            value = postText,
            onValueChange = { postText = it },
            placeholder = {
                Text(
                    text = stringResource(R.string.post_placeholder),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.advanced_publish),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = mediaUrlInput,
            onValueChange = {
                mediaUrlInput = it
                attachedImageUrl = it.trim().takeIf(String::isNotBlank)
            },
            label = { Text(stringResource(R.string.media_url_label)) },
            placeholder = { Text("https://...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = {
                    mediaUrlInput = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=1000&q=80"
                    attachedImageUrl = mediaUrlInput
                }
            ) {
                Text("Demo media")
            }
            if (attachedImageUrl != null) {
                TextButton(
                    onClick = {
                        mediaUrlInput = ""
                        attachedImageUrl = null
                    }
                ) {
                    Text(stringResource(R.string.remove_media))
                }
            }
        }

        // Media Preview & Filter Selector
        if (attachedImageUrl != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(attachedImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = {
                        attachedImageUrl = null
                        mediaUrlInput = ""
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.remove_attachment),
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filters horizontal row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterNames.forEachIndexed { index, filterName ->
                    val isSelected = selectedFilterIndex == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) AuraChampagne else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                selectedFilterIndex = index
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filterName,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.add_poll),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = stringResource(R.string.add_poll_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = pollEnabled,
                onCheckedChange = { pollEnabled = it }
            )
        }

        if (pollEnabled) {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(AuraChampagne.copy(alpha = 0.06f))
                    .border(1.dp, AuraChampagne.copy(alpha = 0.24f), RoundedCornerShape(18.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = pollQuestion,
                    onValueChange = { if (it.length <= 120) pollQuestion = it },
                    label = { Text(stringResource(R.string.poll_question)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                OutlinedTextField(
                    value = pollOption1,
                    onValueChange = { if (it.length <= 60) pollOption1 = it },
                    label = { Text(stringResource(R.string.poll_option, 1)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = pollOption2,
                    onValueChange = { if (it.length <= 60) pollOption2 = it },
                    label = { Text(stringResource(R.string.poll_option, 2)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = pollOption3,
                    onValueChange = { if (it.length <= 60) pollOption3 = it },
                    label = { Text(stringResource(R.string.poll_option_optional, 3)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = pollOption4,
                    onValueChange = { if (it.length <= 60) pollOption4 = it },
                    label = { Text(stringResource(R.string.poll_option_optional, 4)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.content_controls),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.allow_comments))
            Switch(checked = allowComments, onCheckedChange = { allowComments = it })
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.hide_like_count))
            Switch(checked = hideLikeCount, onCheckedChange = { hideLikeCount = it })
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Expiration (Time Limited Post) Options
        Text(
            text = stringResource(R.string.post_lifetime),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PostLifetime.values().forEach { lifetime ->
                val isSelected = selectedLifetime == lifetime
                val label = when (lifetime) {
                    PostLifetime.PERMANENT -> stringResource(R.string.lifetime_permanent)
                    PostLifetime.HOURS_24 -> stringResource(R.string.expires_24h)
                    PostLifetime.DAYS_3 -> stringResource(R.string.expires_3d)
                    PostLifetime.DAYS_7 -> stringResource(R.string.expires_7d)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) AuraChampagne.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            1.dp,
                            if (isSelected) AuraChampagne else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            selectedLifetime = lifetime
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) AuraChampagne else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Circle Audience Selector (Exclusive Feature)
        Text(
            text = stringResource(R.string.audience_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Public chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selectedCircle == null) AuraChampagne.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        1.dp,
                        if (selectedCircle == null) AuraChampagne else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedCircle = null }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.audience_public),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selectedCircle == null) AuraChampagne else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Circles chips
            circles.forEach { circle ->
                val isSelected = selectedCircle?.id == circle.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) Color(circle.colorHex).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color(circle.colorHex) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedCircle = circle }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = circle.localizedName(isArabic),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color(circle.colorHex) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
