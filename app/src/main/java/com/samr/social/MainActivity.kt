package com.samr.social

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import com.samr.social.core.designsystem.components.SamrBottomBar
import com.samr.social.core.designsystem.components.SamrNavigationTab
import com.samr.social.core.designsystem.components.SamrTopBar
import com.samr.social.core.repository.SamrRepository
import com.samr.social.core.util.LocaleManager
import com.samr.social.core.util.SessionManager
import com.samr.social.core.util.ThemeManager
import com.samr.social.features.activity.ActivityCenterScreen
import com.samr.social.features.auth.AuthScreen
import com.samr.social.features.chat.ChatScreen
import com.samr.social.features.clips.ClipsScreen
import com.samr.social.features.create.CreatePostScreen
import com.samr.social.features.discover.DiscoverScreen
import com.samr.social.features.home.HomeScreen
import com.samr.social.features.home.SmartCatchUpContent
import com.samr.social.features.language.LanguageSelectScreen
import com.samr.social.features.majlis.MajlisScreen
import com.samr.social.features.profile.ProfileScreen
import com.samr.social.features.settings.SettingsScreen
import com.samr.social.features.studio.CreatorStudioScreen
import com.samr.social.ui.theme.SamrTheme

class MainActivity : AppCompatActivity() {
    private val localeManager by lazy { LocaleManager(applicationContext) }
    private val themeManager by lazy { ThemeManager(applicationContext) }
    private val sessionManager by lazy { SessionManager(applicationContext) }
    private val repository by lazy { SamrRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by themeManager.themeMode.collectAsState()
            SamrTheme(themeMode = themeMode) {
                SamrApp(
                    repository = repository,
                    localeManager = localeManager,
                    themeManager = themeManager,
                    sessionManager = sessionManager
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamrApp(
    repository: SamrRepository,
    localeManager: LocaleManager,
    themeManager: ThemeManager,
    sessionManager: SessionManager
) {
    val currentLanguage by localeManager.currentLanguage.collectAsState()
    val hasSelectedLanguage by localeManager.hasSelectedLanguageOnboarding.collectAsState()
    val isDemoSession by sessionManager.isDemoSession.collectAsState()
    val themeMode by themeManager.themeMode.collectAsState()

    var isSelectingLanguage by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(SamrNavigationTab.HOME) }
    var isCreatingPost by remember { mutableStateOf(false) }
    var isViewingSettings by remember { mutableStateOf(false) }
    var isViewingActivity by remember { mutableStateOf(false) }
    var isViewingStudio by remember { mutableStateOf(false) }
    var isViewingMajlis by remember { mutableStateOf(false) }
    var showCatchUpDialog by remember { mutableStateOf(false) }

    val activeLayer by repository.activeLayer.collectAsState()
    val isQuietMode by repository.isQuietMode.collectAsState()
    val currentUser by repository.currentUser.collectAsState()
    val notifications by repository.notifications.collectAsState()

    when {
        !hasSelectedLanguage || isSelectingLanguage -> {
            BackHandler(enabled = isSelectingLanguage) {
                isSelectingLanguage = false
            }
            LanguageSelectScreen(
                currentLanguage = currentLanguage,
                onLanguageConfirmed = { language ->
                    localeManager.setLanguage(language)
                    isSelectingLanguage = false
                }
            )
        }

        !isDemoSession -> {
            AuthScreen(
                onAuthSuccess = { sessionManager.startDemoSession() }
            )
        }

        isViewingActivity -> {
            BackHandler { isViewingActivity = false }
            ActivityCenterScreen(
                repository = repository,
                onBack = { isViewingActivity = false }
            )
        }

        isViewingStudio -> {
            BackHandler { isViewingStudio = false }
            CreatorStudioScreen(
                repository = repository,
                onBack = { isViewingStudio = false }
            )
        }

        isViewingMajlis -> {
            BackHandler { isViewingMajlis = false }
            MajlisScreen(
                repository = repository,
                onBack = { isViewingMajlis = false }
            )
        }

        isViewingSettings -> {
            BackHandler { isViewingSettings = false }
            SettingsScreen(
                repository = repository,
                themeMode = themeMode,
                onThemeChange = themeManager::setThemeMode,
                onBack = { isViewingSettings = false },
                onLogout = {
                    sessionManager.clearSession()
                    isViewingSettings = false
                },
                onResetDemo = {
                    sessionManager.clearSession()
                    isViewingSettings = false
                },
                onOpenLanguageSelect = {
                    isSelectingLanguage = true
                }
            )
        }

        else -> {
            BackHandler(enabled = currentTab != SamrNavigationTab.HOME || isCreatingPost) {
                if (isCreatingPost) {
                    isCreatingPost = false
                } else {
                    currentTab = SamrNavigationTab.HOME
                }
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    if (currentTab == SamrNavigationTab.HOME && !isCreatingPost) {
                        SamrTopBar(
                            currentLayer = activeLayer,
                            onLayerSelected = { repository.setSocialLayer(it) },
                            isQuietMode = isQuietMode,
                            onToggleQuietMode = { repository.toggleQuietMode() },
                            onCatchUpClick = { showCatchUpDialog = true },
                            onSearchClick = { currentTab = SamrNavigationTab.DISCOVER },
                            onNotificationsClick = { isViewingActivity = true },
                            unreadNotificationsCount = notifications.count { !it.isRead }
                        )
                    }
                },
                bottomBar = {
                    if (!isCreatingPost) {
                        SamrBottomBar(
                            currentTab = currentTab,
                            onTabSelected = { tab ->
                                if (tab == SamrNavigationTab.CREATE) {
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
                            top = if (currentTab == SamrNavigationTab.HOME) {
                                innerPadding.calculateTopPadding()
                            } else {
                                0.dp
                            },
                            bottom = if (isCreatingPost) {
                                0.dp
                            } else {
                                innerPadding.calculateBottomPadding()
                            }
                        )
                ) {
                    if (isCreatingPost) {
                        CreatePostScreen(
                            repository = repository,
                            onPostCreated = { isCreatingPost = false },
                            onCancel = { isCreatingPost = false }
                        )
                    } else {
                        Crossfade(
                            targetState = currentTab,
                            label = "tab_transition"
                        ) { tab ->
                            when (tab) {
                                SamrNavigationTab.HOME -> HomeScreen(
                                    repository = repository,
                                    onNavigateToProfile = {
                                        currentTab = SamrNavigationTab.PROFILE
                                    },
                                    onNavigateToDiscover = {
                                        currentTab = SamrNavigationTab.DISCOVER
                                    },
                                    onCreatePost = { isCreatingPost = true },
                                    onOpenStudio = { isViewingStudio = true },
                                    onOpenMajlis = { isViewingMajlis = true }
                                )

                                SamrNavigationTab.DISCOVER -> DiscoverScreen(
                                    repository = repository,
                                    onNavigateToProfile = {
                                        currentTab = SamrNavigationTab.PROFILE
                                    }
                                )

                                SamrNavigationTab.CREATE -> Unit

                                SamrNavigationTab.CLIPS -> ClipsScreen(
                                    repository = repository,
                                    onNavigateToProfile = {
                                        currentTab = SamrNavigationTab.PROFILE
                                    }
                                )

                                SamrNavigationTab.INBOX -> ChatScreen(
                                    repository = repository,
                                    onNavigateToProfile = {
                                        currentTab = SamrNavigationTab.PROFILE
                                    }
                                )

                                SamrNavigationTab.PROFILE -> ProfileScreen(
                                    repository = repository,
                                    onNavigateToSettings = {
                                        isViewingSettings = true
                                    },
                                    onOpenStudio = { isViewingStudio = true }
                                )
                            }
                        }
                    }
                }
            }

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
}
