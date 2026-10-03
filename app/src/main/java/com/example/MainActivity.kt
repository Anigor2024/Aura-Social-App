package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.database.AuraDatabase
import com.example.core.designsystem.components.AuraBottomBar
import com.example.core.designsystem.components.AuraNavigationTab
import com.example.core.designsystem.components.AuraTopBar
import com.example.core.repository.AuraRepository
import com.example.features.auth.AuthScreen
import com.example.features.chat.ChatScreen
import com.example.features.clips.ClipsScreen
import com.example.features.create.CreatePostScreen
import com.example.features.discover.DiscoverScreen
import com.example.features.home.HomeScreen
import com.example.features.home.SmartCatchUpContent
import com.example.features.profile.ProfileScreen
import com.example.features.settings.SettingsScreen
import com.example.ui.theme.AuraTheme

class MainActivity : ComponentActivity() {

    private val repository by lazy {
        // Initialize Room DB in background if needed
        AuraDatabase.getInstance(applicationContext)
        AuraRepository()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraTheme(darkTheme = true) {
                AuraApp(repository = repository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraApp(repository: AuraRepository) {
    var isAuthenticated by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(AuraNavigationTab.HOME) }
    var isCreatingPost by remember { mutableStateOf(false) }
    var isViewingSettings by remember { mutableStateOf(false) }
    var showCatchUpDialog by remember { mutableStateOf(false) }

    val activeLayer by repository.activeLayer.collectAsState()
    val isQuietMode by repository.isQuietMode.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    if (!isAuthenticated) {
        AuthScreen(
            onAuthSuccess = { isAuthenticated = true }
        )
    } else if (isViewingSettings) {
        BackHandler { isViewingSettings = false }
        SettingsScreen(
            repository = repository,
            onBack = { isViewingSettings = false },
            onLogout = {
                isViewingSettings = false
                isAuthenticated = false
            }
        )
    } else {
        // Handle Back button to return to HOME tab before exiting
        BackHandler(enabled = currentTab != AuraNavigationTab.HOME || isCreatingPost) {
            if (isCreatingPost) {
                isCreatingPost = false
            } else {
                currentTab = AuraNavigationTab.HOME
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (currentTab == AuraNavigationTab.HOME && !isCreatingPost) {
                    AuraTopBar(
                        currentLayer = activeLayer,
                        onLayerSelected = { repository.setSocialLayer(it) },
                        isQuietMode = isQuietMode,
                        onToggleQuietMode = { repository.toggleQuietMode() },
                        onCatchUpClick = { showCatchUpDialog = true },
                        onSearchClick = { currentTab = AuraNavigationTab.DISCOVER }
                    )
                }
            },
            bottomBar = {
                if (!isCreatingPost) {
                    AuraBottomBar(
                        currentTab = currentTab,
                        onTabSelected = { tab ->
                            if (tab == AuraNavigationTab.CREATE) {
                                isCreatingPost = true
                            } else {
                                currentTab = tab
                            }
                        },
                        unreadMessagesCount = 2,
                        userAvatarUrl = currentUser.avatarUrl
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(
                        top = if (currentTab == AuraNavigationTab.HOME) innerPadding.calculateTopPadding() else 0.dp,
                        bottom = if (isCreatingPost) 0.dp else innerPadding.calculateBottomPadding()
                    )
            ) {
                if (isCreatingPost) {
                    CreatePostScreen(
                        repository = repository,
                        onPostCreated = { isCreatingPost = false },
                        onCancel = { isCreatingPost = false }
                    )
                } else {
                    Crossfade(targetState = currentTab, label = "tab_transition") { tab ->
                        when (tab) {
                            AuraNavigationTab.HOME -> {
                                HomeScreen(
                                    repository = repository,
                                    onNavigateToProfile = { currentTab = AuraNavigationTab.PROFILE },
                                    onNavigateToDiscover = { currentTab = AuraNavigationTab.DISCOVER }
                                )
                            }
                            AuraNavigationTab.DISCOVER -> {
                                DiscoverScreen(
                                    repository = repository,
                                    onNavigateToProfile = { currentTab = AuraNavigationTab.PROFILE }
                                )
                            }
                            AuraNavigationTab.CREATE -> {
                                // Handled via isCreatingPost state
                            }
                            AuraNavigationTab.CLIPS -> {
                                ClipsScreen(
                                    repository = repository,
                                    onNavigateToProfile = { currentTab = AuraNavigationTab.PROFILE }
                                )
                            }
                            AuraNavigationTab.INBOX -> {
                                ChatScreen(
                                    repository = repository,
                                    onNavigateToProfile = { currentTab = AuraNavigationTab.PROFILE }
                                )
                            }
                            AuraNavigationTab.PROFILE -> {
                                ProfileScreen(
                                    repository = repository,
                                    onNavigateToSettings = { isViewingSettings = true }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Catch-up dialog
        if (showCatchUpDialog) {
            ModalBottomSheet(
                onDismissRequest = { showCatchUpDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                SmartCatchUpContent(
                    summary = repository.catchUpSummary,
                    onClose = { showCatchUpDialog = false }
                )
            }
        }
    }
}
