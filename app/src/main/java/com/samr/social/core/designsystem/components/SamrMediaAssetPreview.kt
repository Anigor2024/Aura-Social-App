package com.samr.social.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.samr.social.core.model.MediaAsset
import com.samr.social.core.model.MediaKind
import kotlinx.coroutines.delay

@Composable
fun SamrMediaAssetPreview(
    asset: MediaAsset,
    height: Dp = 260.dp,
    autoplay: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    when (asset.kind) {
        MediaKind.IMAGE -> {
            AsyncImage(
                model = asset.uri,
                contentDescription = asset.altText.ifBlank { asset.title },
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Black.copy(alpha = 0.08f))
            )
        }

        MediaKind.VIDEO, MediaKind.AUDIO -> {
            val player = remember(asset.uri) {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(asset.uri))
                    prepare()
                }
            }

            LaunchedEffect(
                asset.playbackSpeed,
                asset.isMuted,
                asset.trimStartMs,
                autoplay,
                asset.kind
            ) {
                player.playbackParameters = PlaybackParameters(asset.playbackSpeed.coerceIn(0.5f, 2f))
                player.volume = if (asset.isMuted) 0f else 1f
                if (asset.trimStartMs > 0L) player.seekTo(asset.trimStartMs)
                player.playWhenReady = autoplay && asset.kind == MediaKind.VIDEO
            }

            LaunchedEffect(player, asset.trimEndMs, asset.trimStartMs) {
                val end = asset.trimEndMs
                if (end != null && end > asset.trimStartMs) {
                    while (true) {
                        delay(200)
                        if (player.currentPosition >= end) {
                            player.pause()
                            player.seekTo(asset.trimStartMs)
                        }
                    }
                }
            }

            DisposableEffect(player) {
                onDispose { player.release() }
            }

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(if (asset.kind == MediaKind.AUDIO) 92.dp else height)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = {
                        PlayerView(it).apply {
                            this.player = player
                            useController = true
                            setShowNextButton(false)
                            setShowPreviousButton(false)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
