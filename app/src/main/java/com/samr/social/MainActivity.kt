package com.samr.social

import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.samr.social.core.database.SamrDatabase
import com.samr.social.core.designsystem.components.AuraBottomBar
import com.samr.social.core.designsystem.components.AuraNavigationTab
import com.samr.social.core.designsystem.components.AuraTopBar
import com.samr.social.core.repository.SamrRepository
import com.samr.social.core.util.LocaleManager
import com.samr.social.features.auth.AuthScreen
import com.samr.social.features.chat.ChatScreen
import com.samr.social.features.clips.ClipsScreen
import com.samr.social.features.create.CreatePostScreen
import com.samr.social.features.discover.DiscoverScreen
import com.samr.social.features.home.HomeScreen
import com.samr.social.features.home.SmartCatchUpContent
import com.samr.social.features.language.LanguageSelectScreen
import com.samr.social.features.profile.ProfileScreen
import com.samr.social.features.settings.SettingsScreen
import com.samr.social.ui.theme.SamrTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val localeManager by lazy { LocaleManager(applicationContext) }

    private val repository by lazy {
        // Initialize Room DB in background
        SamrDatabase.getInstance(applicationContext)
        SamrRepository()
    }

    override fun attachBaseContext(newBase: Context) {
        val manager = LocaleManager(newBase)
        super.attachBaseContext(manager.applyLocaleToContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Apply locale to resources configuration
        val currentLang = localeManager.currentLanguage.value
        applyLanguageToResources(currentLang)

        setContent {
            val currentLanguage by localeManager.currentLanguage.collectAsState()
            val hasSelectedLang by localeManager.hasSelectedLanguageOnboarding.collectAsState()
            val layoutDirection = if (currentLanguage == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                SamrTheme(darkTheme = true) {
                    SamrApp(
                        repository = repository,
                        localeManager = localeManager,
                        currentLanguage = currentLanguage,
                        hasSelectedLanguageOnboarding = hasSelectedLang,
                        onLanguageChange = { newLang ->
                            localeManager.setLanguage(newLang)
                            applyLanguageToResources(newLang)
                            recreate()
                        }
                    )
                }
            }
        }
    }

    private fun applyLanguageToResources(languageCode: String) {
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamrApp(
    repository: SamrRepository,
    localeManager: LocaleManager,
    currentLanguage: String,
    hasSelectedLanguageOnboarding: Boolean,
    onLanguageChange: (String) -> Unit
) {
    var isSelectingLanguage by remember { mutableStateOf(false) }
    var isAuthenticated by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(AuraNavigationTab.HOME) }
    var isCreatingPost by remember { mutableStateOf(false) }
    var isViewingSettings by remember { mutableStateOf(false) }
    var showCatchUpDialog by remember { mutableStateOf(false) }

    val activeLayer by repository.activeLayer.collectAsState()
    val isQuietMode by repository.isQuietMode.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    // 1. Language Selection First-Run Experience (Arabic-first by default)
    if (!hasSelectedLanguageOnboarding || isSelectingLanguage) {
        BackHandler(enabled = isSelectingLanguage) {
            isSelectingLanguage = false
        }
        LanguageSelectScreen(
            currentLanguage = currentLanguage,
            onLanguageConfirmed = { chosenLang ->
                onLanguageChange(chosenLang)
                isSelectingLanguage = false
            }
        )
    } else if (!isAuthenticated) {
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
            },
            onOpenLanguageSelect = {
                isSelectingLanguage = true
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
