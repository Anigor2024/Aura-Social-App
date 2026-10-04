package com.samr.social.features.settings

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DataUsage
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VolumeMute
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samr.social.R
import com.samr.social.core.repository.SamrRepository
import com.samr.social.core.util.AppThemeMode
import com.samr.social.ui.theme.SamrChampagne
import com.samr.social.ui.theme.SamrRose

private enum class InfoSection {
    ACCOUNT,
    PRIVACY,
    SECURITY,
    NOTIFICATIONS
}

@Composable
fun SettingsScreen(
    repository: SamrRepository,
    themeMode: AppThemeMode,
    onThemeChange: (AppThemeMode) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onResetDemo: () -> Unit,
    onOpenLanguageSelect: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val isQuietMode by repository.isQuietMode.collectAsState()

    var isDataSaverEnabled by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var infoSection by remember { mutableStateOf<InfoSection?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        SettingsSectionHeader(
            title = stringResource(R.string.mindful_experience)
        )

        SettingsSwitchRow(
            icon = Icons.Outlined.VolumeMute,
            title = stringResource(R.string.quiet_feed_mode),
            description = stringResource(R.string.quiet_feed_active_hint),
            checked = isQuietMode,
            onCheckedChange = {
                view.performHapticFeedback(
                    HapticFeedbackConstants.KEYBOARD_TAP
                )
                repository.toggleQuietMode()
            }
        )

        SettingsSwitchRow(
            icon = Icons.Outlined.DataUsage,
            title = stringResource(R.string.data_saver),
            description = stringResource(R.string.data_saver_desc),
            checked = isDataSaverEnabled,
            onCheckedChange = { isDataSaverEnabled = it }
        )

        SettingsSectionHeader(
            title = stringResource(R.string.privacy_safety)
        )

        SettingsNavigationRow(
            icon = Icons.Outlined.AccountCircle,
            title = stringResource(R.string.account_settings),
            onClick = { infoSection = InfoSection.ACCOUNT }
        )

        SettingsNavigationRow(
            icon = Icons.Outlined.Shield,
            title = stringResource(R.string.privacy_safety),
            onClick = { infoSection = InfoSection.PRIVACY }
        )

        SettingsNavigationRow(
            icon = Icons.Outlined.Lock,
            title = stringResource(R.string.security_center),
            onClick = { infoSection = InfoSection.SECURITY }
        )

        SettingsNavigationRow(
            icon = Icons.Outlined.Notifications,
            title = stringResource(R.string.notifications_settings),
            onClick = { infoSection = InfoSection.NOTIFICATIONS }
        )

        SettingsSectionHeader(
            title = stringResource(R.string.system_localization)
        )

        SettingsNavigationRow(
            icon = Icons.Outlined.Palette,
            title = stringResource(R.string.appearance_settings),
            subtitle = when (themeMode) {
                AppThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                AppThemeMode.LIGHT -> stringResource(R.string.theme_light)
                AppThemeMode.DARK -> stringResource(R.string.theme_dark)
                AppThemeMode.OLED -> stringResource(R.string.theme_oled)
            },
            onClick = { showThemePicker = true }
        )

        SettingsNavigationRow(
            icon = Icons.Outlined.Language,
            title = stringResource(R.string.language_settings),
            subtitle = "العربية / English",
            onClick = onOpenLanguageSelect
        )

        Spacer(modifier = Modifier.height(24.dp))

        SettingsActionRow(
            icon = Icons.Outlined.Logout,
            title = stringResource(R.string.logout),
            color = MaterialTheme.colorScheme.onSurface,
            onClick = onLogout
        )

        SettingsActionRow(
            icon = Icons.Outlined.DeleteForever,
            title = stringResource(R.string.delete_account),
            color = SamrRose,
            onClick = { showResetDialog = true }
        )

        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showThemePicker) {
        AlertDialog(
            onDismissRequest = { showThemePicker = false },
            title = {
                Text(stringResource(R.string.theme_picker_title))
            },
            text = {
                Column {
                    ThemeChoice(
                        mode = AppThemeMode.SYSTEM,
                        label = stringResource(R.string.theme_system),
                        current = themeMode,
                        onChange = onThemeChange
                    )
                    ThemeChoice(
                        mode = AppThemeMode.LIGHT,
                        label = stringResource(R.string.theme_light),
                        current = themeMode,
                        onChange = onThemeChange
                    )
                    ThemeChoice(
                        mode = AppThemeMode.DARK,
                        label = stringResource(R.string.theme_dark),
                        current = themeMode,
                        onChange = onThemeChange
                    )
                    ThemeChoice(
                        mode = AppThemeMode.OLED,
                        label = stringResource(R.string.theme_oled),
                        current = themeMode,
                        onChange = onThemeChange
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showThemePicker = false }
                ) {
                    Text(stringResource(R.string.done))
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(stringResource(R.string.reset_demo_title))
            },
            text = {
                Text(stringResource(R.string.reset_demo_desc))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        onResetDemo()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.clear_local_session),
                        color = SamrRose
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    infoSection?.let { section ->
        val title = when (section) {
            InfoSection.ACCOUNT -> stringResource(
                R.string.account_details_title
            )
            InfoSection.PRIVACY -> stringResource(
                R.string.privacy_title
            )
            InfoSection.SECURITY -> stringResource(
                R.string.security_title
            )
            InfoSection.NOTIFICATIONS -> stringResource(
                R.string.notifications_title
            )
        }

        val body = when (section) {
            InfoSection.ACCOUNT -> stringResource(
                R.string.account_status_demo
            )
            InfoSection.PRIVACY -> stringResource(
                R.string.privacy_circles_hint
            )
            InfoSection.SECURITY -> stringResource(
                R.string.security_desc
            )
            InfoSection.NOTIFICATIONS -> stringResource(
                R.string.settings_info_demo
            )
        }

        AlertDialog(
            onDismissRequest = { infoSection = null },
            title = { Text(title) },
            text = { Text(body) },
            confirmButton = {
                TextButton(
                    onClick = { infoSection = null }
                ) {
                    Text(stringResource(R.string.done))
                }
            }
        )
    }
}

@Composable
private fun ThemeChoice(
    mode: AppThemeMode,
    label: String,
    current: AppThemeMode,
    onChange: (AppThemeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChange(mode) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = current == mode,
            onClick = { onChange(mode) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            color = SamrChampagne
        ),
        modifier = Modifier.padding(
            horizontal = 20.dp,
            vertical = 12.dp
        )
    )
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = SamrChampagne
            )
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = color
        )
    }
}
