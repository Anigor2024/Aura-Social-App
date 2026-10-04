package com.samr.social.features.media

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.samr.social.R
import com.samr.social.core.model.MediaAsset
import com.samr.social.core.model.MediaKind
import com.samr.social.core.model.MediaOrigin
import com.samr.social.core.repository.SamrRepository
import com.samr.social.core.util.MediaFileUtils
import com.samr.social.ui.theme.SamrChampagne
import com.samr.social.ui.theme.SamrCyan
import com.samr.social.ui.theme.SamrEmerald
import com.samr.social.ui.theme.SamrViolet
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaStudioScreen(
    repository: SamrRepository,
    onBack: () -> Unit,
    onUseMedia: (List<MediaAsset>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val assets by repository.mediaLibrary.collectAsState()
    var section by remember { mutableIntStateOf(0) }
    var selectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var editingAsset by remember { mutableStateOf<MediaAsset?>(null) }

    val sectionLabels = listOf(
        stringResource(R.string.studio_library),
        stringResource(R.string.studio_camera),
        stringResource(R.string.studio_design),
        stringResource(R.string.studio_audio)
    )

    val importImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            MediaFileUtils.copyUriToCache(context, it, MediaKind.IMAGE)?.let { copied ->
                repository.addMediaAsset(
                    MediaAsset(
                        id = "media_${UUID.randomUUID().toString().take(8)}",
                        uri = copied.toString(),
                        kind = MediaKind.IMAGE,
                        origin = MediaOrigin.IMPORTED,
                        title = "Imported image",
                        mimeType = "image/jpeg"
                    )
                )
            }
        }
    }
    val importVideo = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            MediaFileUtils.copyUriToCache(context, it, MediaKind.VIDEO)?.let { copied ->
                repository.addMediaAsset(
                    MediaAsset(
                        id = "media_${UUID.randomUUID().toString().take(8)}",
                        uri = copied.toString(),
                        kind = MediaKind.VIDEO,
                        origin = MediaOrigin.IMPORTED,
                        title = "Imported video",
                        mimeType = "video/mp4"
                    )
                )
            }
        }
    }
    val importAudio = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            MediaFileUtils.copyUriToCache(context, it, MediaKind.AUDIO)?.let { copied ->
                repository.addMediaAsset(
                    MediaAsset(
                        id = "media_${UUID.randomUUID().toString().take(8)}",
                        uri = copied.toString(),
                        kind = MediaKind.AUDIO,
                        origin = MediaOrigin.IMPORTED,
                        title = "Imported audio",
                        mimeType = "audio/mp4"
                    )
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.media_studio),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    stringResource(R.string.media_studio_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (selectedIds.isNotEmpty()) {
                TextButton(
                    onClick = {
                        onUseMedia(assets.filter { it.id in selectedIds }.take(10))
                    }
                ) {
                    Text(stringResource(R.string.continue_to_post), color = SamrChampagne)
                }
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sectionLabels) { label ->
                val index = sectionLabels.indexOf(label)
                StudioTab(
                    label = label,
                    selected = section == index,
                    onClick = { section = index }
                )
            }
        }

        when (section) {
            0 -> MediaLibraryContent(
                assets = assets,
                selectedIds = selectedIds,
                onSelect = { asset ->
                    selectedIds = if (asset.id in selectedIds) {
                        selectedIds - asset.id
                    } else if (selectedIds.size < 10) {
                        selectedIds + asset.id
                    } else selectedIds
                },
                onEdit = { editingAsset = it },
                onFavorite = repository::toggleMediaFavorite,
                onDuplicate = repository::duplicateMediaAsset,
                onDelete = {
                    selectedIds = selectedIds - it
                    repository.deleteMediaAsset(it)
                },
                onImportImage = { importImage.launch("image/*") },
                onImportVideo = { importVideo.launch("video/*") },
                onImportAudio = { importAudio.launch("audio/*") }
            )
            1 -> CameraStudioContent(repository)
            2 -> DesignStudioContent(repository)
            else -> AudioStudioContent(repository)
        }
    }

    editingAsset?.let { asset ->
        ModalBottomSheet(
            onDismissRequest = { editingAsset = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            MediaEditorSheet(
                asset = asset,
                repository = repository,
                onClose = { editingAsset = null }
            )
        }
    }
}

@Composable
private fun StudioTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) SamrChampagne.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                if (selected) SamrChampagne.copy(alpha = 0.55f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal),
            color = if (selected) SamrChampagne else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MediaLibraryContent(
    assets: List<MediaAsset>,
    selectedIds: Set<String>,
    onSelect: (MediaAsset) -> Unit,
    onEdit: (MediaAsset) -> Unit,
    onFavorite: (String) -> Unit,
    onDuplicate: (String) -> Unit,
    onDelete: (String) -> Unit,
    onImportImage: () -> Unit,
    onImportVideo: () -> Unit,
    onImportAudio: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImportAction(
                    icon = Icons.Default.Image,
                    label = stringResource(R.string.import_image),
                    onClick = onImportImage,
                    modifier = Modifier.weight(1f)
                )
                ImportAction(
                    icon = Icons.Default.Movie,
                    label = stringResource(R.string.import_video),
                    onClick = onImportVideo,
                    modifier = Modifier.weight(1f)
                )
                ImportAction(
                    icon = Icons.Default.Mic,
                    label = stringResource(R.string.import_audio),
                    onClick = onImportAudio,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (selectedIds.isNotEmpty()) {
            item {
                Text(
                    stringResource(R.string.selected_media, selectedIds.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = SamrChampagne
                )
            }
        }

        if (assets.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.media_empty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(assets, key = { it.id }) { asset ->
                MediaLibraryCard(
                    asset = asset,
                    selected = asset.id in selectedIds,
                    onSelect = { onSelect(asset) },
                    onEdit = { onEdit(asset) },
                    onFavorite = { onFavorite(asset.id) },
                    onDuplicate = { onDuplicate(asset.id) },
                    onDelete = { onDelete(asset.id) }
                )
            }
        }
    }
}

@Composable
private fun ImportAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = SamrChampagne)
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 2)
    }
}

@Composable
private fun MediaLibraryCard(
    asset: MediaAsset,
    selected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onFavorite: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                if (selected) SamrChampagne else MaterialTheme.colorScheme.outline.copy(alpha = 0.28f),
                RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onSelect)
            .padding(12.dp)
    ) {
        MediaAssetPreview(asset = asset, height = if (asset.kind == MediaKind.AUDIO) 90.dp else 210.dp)

        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(asset.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(
                    mediaMeta(asset),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onFavorite) {
                Icon(
                    if (asset.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.media_favorite),
                    tint = if (asset.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(onClick = onEdit) {
                Text(stringResource(R.string.image_editor))
            }
            TextButton(onClick = onDuplicate) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                Text(stringResource(R.string.duplicate_media))
            }
            TextButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(15.dp))
                Text(stringResource(R.string.delete_media), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun CameraStudioContent(repository: SamrRepository) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember(context) { ContextCompat.getMainExecutor(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val recorder = remember { Recorder.Builder().build() }
    val videoCapture = remember { VideoCapture.withOutput(recorder) }
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    var recording by remember { mutableStateOf<Recording?>(null) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasPermission = result[Manifest.permission.CAMERA] == true &&
            result[Manifest.permission.RECORD_AUDIO] == true
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val future = ProcessCameraProvider.getInstance(context)
            future.addListener({
                runCatching {
                    val provider = future.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture,
                        videoCapture
                    )
                }
            }, executor)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            recording?.stop()
            runCatching { ProcessCameraProvider.getInstance(context).get().unbindAll() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        if (!hasPermission) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = SamrChampagne, modifier = Modifier.size(42.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(stringResource(R.string.camera_permission), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SamrChampagne, contentColor = Color.Black)
                ) {
                    Text(stringResource(R.string.grant_permission))
                }
            }
            return@Column
        }

        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.Black)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, "SAMR_${System.currentTimeMillis()}")
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    }
                    val output = ImageCapture.OutputFileOptions.Builder(
                        context.contentResolver,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    ).build()
                    imageCapture.takePicture(
                        output,
                        executor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                                result.savedUri?.let { uri ->
                                    repository.addMediaAsset(
                                        MediaAsset(
                                            id = "media_${UUID.randomUUID().toString().take(8)}",
                                            uri = uri.toString(),
                                            kind = MediaKind.IMAGE,
                                            origin = MediaOrigin.CAMERA,
                                            title = "SAMR photo",
                                            mimeType = "image/jpeg"
                                        )
                                    )
                                }
                            }

                            override fun onError(exception: ImageCaptureException) = Unit
                        }
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SamrChampagne, contentColor = Color.Black)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(stringResource(R.string.capture_photo))
            }

            Button(
                onClick = {
                    if (recording != null) {
                        recording?.stop()
                        recording = null
                    } else {
                        val values = ContentValues().apply {
                            put(MediaStore.Video.Media.DISPLAY_NAME, "SAMR_VIDEO_${System.currentTimeMillis()}")
                            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                        }
                        val output = MediaStoreOutputOptions.Builder(
                            context.contentResolver,
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        )
                            .setContentValues(values)
                            .build()

                        recording = videoCapture.output
                            .prepareRecording(context, output)
                            .withAudioEnabled()
                            .start(executor) { event ->
                                if (event is VideoRecordEvent.Finalize) {
                                    if (!event.hasError()) {
                                        val uri = event.outputResults.outputUri
                                        repository.addMediaAsset(
                                            MediaAsset(
                                                id = "media_${UUID.randomUUID().toString().take(8)}",
                                                uri = uri.toString(),
                                                kind = MediaKind.VIDEO,
                                                origin = MediaOrigin.CAMERA,
                                                title = "SAMR video",
                                                mimeType = "video/mp4"
                                            )
                                        )
                                    }
                                    recording = null
                                }
                            }
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (recording != null) MaterialTheme.colorScheme.error else SamrViolet,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    if (recording != null) Icons.Default.StopCircle else Icons.Default.Movie,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (recording != null) stringResource(R.string.stop_recording)
                    else stringResource(R.string.record_video)
                )
            }
        }
    }
}

@Composable
private fun DesignStudioContent(repository: SamrRepository) {
    val context = LocalContext.current
    var headline by remember { mutableStateOf("مساحة تستحق أن تُروى") }
    var subtitle by remember { mutableStateOf("صمّم فكرتك داخل سَمَر وانشرها مباشرة.") }
    var emoji by remember { mutableStateOf("✦") }
    var style by remember { mutableIntStateOf(0) }
    var ratio by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    ) {
        Text(
            stringResource(R.string.create_design),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = headline,
            onValueChange = { if (it.length <= 120) headline = it },
            label = { Text(stringResource(R.string.design_headline)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = subtitle,
            onValueChange = { if (it.length <= 180) subtitle = it },
            label = { Text(stringResource(R.string.design_subtitle)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = emoji,
            onValueChange = { if (it.length <= 4) emoji = it },
            label = { Text(stringResource(R.string.design_emoji)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))
        Text(stringResource(R.string.design_theme), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(6) { index ->
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            listOf(SamrChampagne, SamrEmerald, SamrViolet, MaterialTheme.colorScheme.error, SamrCyan, Color.Gray)[index]
                        )
                        .border(if (style == index) 3.dp else 0.dp, Color.White, CircleShape)
                        .clickable { style = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(stringResource(R.string.design_ratio), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                stringResource(R.string.ratio_square),
                stringResource(R.string.ratio_story),
                stringResource(R.string.ratio_landscape)
            ).forEachIndexed { index, label ->
                StudioTab(label, ratio == index) { ratio = index }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = {
                val (w, h) = when (ratio) {
                    1 -> 1080 to 1920
                    2 -> 1600 to 900
                    else -> 1200 to 1200
                }
                MediaFileUtils.createDesignCard(
                    context = context,
                    headline = headline,
                    subtitle = subtitle,
                    emoji = emoji,
                    styleIndex = style,
                    width = w,
                    height = h
                )?.let { uri ->
                    repository.addMediaAsset(
                        MediaAsset(
                            id = "media_${UUID.randomUUID().toString().take(8)}",
                            uri = uri.toString(),
                            kind = MediaKind.IMAGE,
                            origin = MediaOrigin.STUDIO,
                            title = headline.take(42).ifBlank { "SAMR design" },
                            mimeType = "image/jpeg",
                            overlayText = headline
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SamrChampagne, contentColor = Color.Black)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null)
            Spacer(modifier = Modifier.width(7.dp))
            Text(stringResource(R.string.generate_visual))
        }
    }
}

@Composable
private fun AudioStudioContent(repository: SamrRepository) {
    val context = LocalContext.current
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var recordingFile by remember { mutableStateOf<java.io.File?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasPermission = it
    }

    DisposableEffect(Unit) {
        onDispose {
            runCatching { recorder?.release() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(
                    if (isRecording) MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                    else SamrChampagne.copy(alpha = 0.12f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isRecording) Icons.Default.PauseCircle else Icons.Default.Mic,
                contentDescription = null,
                tint = if (isRecording) MaterialTheme.colorScheme.error else SamrChampagne,
                modifier = Modifier.size(52.dp)
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            if (isRecording) stringResource(R.string.recording_now) else stringResource(R.string.record_audio),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(14.dp))

        if (!hasPermission) {
            Button(onClick = { launcher.launch(Manifest.permission.RECORD_AUDIO) }) {
                Text(stringResource(R.string.grant_permission))
            }
        } else {
            Button(
                onClick = {
                    if (!isRecording) {
                        val file = MediaFileUtils.createAudioFile(context)
                        recordingFile = file
                        val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            MediaRecorder(context)
                        } else {
                            @Suppress("DEPRECATION")
                            MediaRecorder()
                        }
                        runCatching {
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                            mediaRecorder.setAudioEncodingBitRate(128000)
                            mediaRecorder.setAudioSamplingRate(44100)
                            mediaRecorder.setOutputFile(file.absolutePath)
                            mediaRecorder.prepare()
                            mediaRecorder.start()
                            recorder = mediaRecorder
                            isRecording = true
                        }.onFailure {
                            mediaRecorder.release()
                            recorder = null
                        }
                    } else {
                        runCatching { recorder?.stop() }
                        runCatching { recorder?.release() }
                        recorder = null
                        isRecording = false
                        recordingFile?.takeIf { it.exists() }?.let { file ->
                            val uri = MediaFileUtils.uriForFile(context, file)
                            repository.addMediaAsset(
                                MediaAsset(
                                    id = "media_${UUID.randomUUID().toString().take(8)}",
                                    uri = uri.toString(),
                                    kind = MediaKind.AUDIO,
                                    origin = MediaOrigin.RECORDER,
                                    title = "SAMR voice recording",
                                    mimeType = "audio/mp4"
                                )
                            )
                        }
                        recordingFile = null
                    }
                },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) MaterialTheme.colorScheme.error else SamrChampagne,
                    contentColor = if (isRecording) Color.White else Color.Black
                )
            ) {
                Icon(
                    if (isRecording) Icons.Default.StopCircle else Icons.Default.Mic,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    if (isRecording) stringResource(R.string.stop_audio)
                    else stringResource(R.string.record_audio)
                )
            }
        }
    }
}

@Composable
private fun MediaEditorSheet(
    asset: MediaAsset,
    repository: SamrRepository,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var title by remember(asset.id) { mutableStateOf(asset.title) }
    var overlay by remember(asset.id) { mutableStateOf(asset.overlayText) }
    var filter by remember(asset.id) { mutableStateOf(asset.filterName) }
    var speed by remember(asset.id) { mutableStateOf(asset.playbackSpeed) }
    var muted by remember(asset.id) { mutableStateOf(asset.isMuted) }
    var trimStart by remember(asset.id) { mutableStateOf(asset.trimStartMs.toString()) }
    var trimEnd by remember(asset.id) { mutableStateOf(asset.trimEndMs?.toString().orEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        MediaAssetPreview(asset = asset, height = if (asset.kind == MediaKind.AUDIO) 100.dp else 230.dp)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { if (it.length <= 80) title = it },
            label = { Text(stringResource(R.string.media_title)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (asset.kind == MediaKind.IMAGE) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = overlay,
                onValueChange = { if (it.length <= 120) overlay = it },
                label = { Text(stringResource(R.string.overlay_text)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                listOf("Original", "Warm", "Cool", "Noir", "Vibrant").forEach { item ->
                    StudioTab(item, filter == item) { filter = item }
                }
            }
        }

        if (asset.kind == MediaKind.VIDEO) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(stringResource(R.string.video_controls), fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = trimStart,
                    onValueChange = { trimStart = it.filter(Char::isDigit).take(8) },
                    label = { Text(stringResource(R.string.trim_start)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = trimEnd,
                    onValueChange = { trimEnd = it.filter(Char::isDigit).take(8) },
                    label = { Text(stringResource(R.string.trim_end)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf(0.5f, 1f, 1.5f, 2f).forEach { item ->
                    StudioTab("${item}x", speed == item) { speed = item }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { muted = !muted }) {
                Text(
                    if (muted) stringResource(R.string.mute_video) + " ✓"
                    else stringResource(R.string.mute_video)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = {
                if (asset.kind == MediaKind.IMAGE) {
                    MediaFileUtils.applyImageEdit(
                        context = context,
                        source = Uri.parse(asset.uri),
                        filterName = filter,
                        overlayText = overlay
                    )?.let { edited ->
                        val updated = asset.copy(
                            uri = edited.toString(),
                            title = title.trim().ifBlank { asset.title },
                            filterName = filter,
                            overlayText = overlay
                        )
                        repository.updateMediaAsset(updated)
                    }
                } else {
                    repository.updateMediaAsset(
                        asset.copy(
                            title = title.trim().ifBlank { asset.title },
                            trimStartMs = trimStart.toLongOrNull() ?: 0L,
                            trimEndMs = trimEnd.toLongOrNull(),
                            playbackSpeed = speed,
                            isMuted = muted
                        )
                    )
                }
                onClose()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SamrChampagne, contentColor = Color.Black)
        ) {
            Text(stringResource(R.string.apply_edit))
        }

        if (asset.kind == MediaKind.VIDEO) {
            TextButton(
                onClick = {
                    repository.publishVideoAssetAsClip(asset.copy(
                        title = title,
                        playbackSpeed = speed,
                        isMuted = muted,
                        trimStartMs = trimStart.toLongOrNull() ?: 0L,
                        trimEndMs = trimEnd.toLongOrNull()
                    ), title)
                    onClose()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.publish_as_clip), color = SamrViolet)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun MediaAssetPreview(
    asset: MediaAsset,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    when (asset.kind) {
        MediaKind.IMAGE -> {
            AsyncImage(
                model = asset.uri,
                contentDescription = asset.altText.ifBlank { stringResource(R.string.image_preview) },
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        MediaKind.VIDEO, MediaKind.AUDIO -> {
            val player = remember(asset.uri) {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(asset.uri))
                    prepare()
                    playWhenReady = false
                }
            }
            LaunchedEffect(asset.playbackSpeed, asset.isMuted, asset.trimStartMs) {
                player.playbackParameters = PlaybackParameters(asset.playbackSpeed.coerceIn(0.5f, 2f))
                player.volume = if (asset.isMuted) 0f else 1f
                if (asset.trimStartMs > 0L) player.seekTo(asset.trimStartMs)
            }
            DisposableEffect(player) {
                onDispose { player.release() }
            }

            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        this.player = player
                        useController = true
                        setShowNextButton(false)
                        setShowPreviousButton(false)
                    }
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
            )
        }
    }
}

@Composable
private fun mediaMeta(asset: MediaAsset): String {
    val kind = when (asset.kind) {
        MediaKind.IMAGE -> stringResource(R.string.media_kind_image)
        MediaKind.VIDEO -> stringResource(R.string.media_kind_video)
        MediaKind.AUDIO -> stringResource(R.string.media_kind_audio)
    }
    val origin = when (asset.origin) {
        MediaOrigin.CAMERA -> stringResource(R.string.media_origin_camera)
        MediaOrigin.IMPORTED -> stringResource(R.string.media_origin_imported)
        MediaOrigin.RECORDER -> stringResource(R.string.media_origin_recorder)
        MediaOrigin.STUDIO -> stringResource(R.string.media_origin_studio)
    }
    return "$kind • $origin"
}
