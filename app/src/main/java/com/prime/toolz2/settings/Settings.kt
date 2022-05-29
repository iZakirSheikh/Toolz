package com.prime.toolz2.settings

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prime.toolz2.R
import com.prime.toolz2.common.compose.*
import com.prime.toolz2.common.toggleStatusBarState
import com.prime.toolz2.padding
import com.prime.toolz2.primary
import com.primex.preferences.Preferences
import com.primex.widgets.*
import cz.levinzonr.saferoute.core.annotations.Route
import kotlinx.coroutines.launch

private val RESERVE_PADDING = 56.dp

private const val FONT_SCALE_LOWER_BOUND = 0.5f
private const val FONT_SCALE_UPPER_BOUND = 2.0f

private const val SLIDER_STEPS = 15

private const val ZERO_WIDTH_CHAR = '\u8203'

@Composable
private fun PrefHeader(text: String) {
    val primary = Material.colors.primary
    Label(
        text = text,
        modifier = Modifier.padding(
            start = RESERVE_PADDING,
            top = Material.padding.Large,
            bottom = Material.padding.Medium
        ),
        fontWeight = FontWeight.SemiBold,
        maxLines = 2,
        color = Material.colors.primary
    )

    Divider(
        modifier = Modifier.padding(
            start = RESERVE_PADDING,
            end = Material.padding.Large,
            bottom = Material.padding.Medium
        ),
        color = primary.copy(0.12f)
    )
}

@Composable
private inline fun ColumnScope.AboutUs() {

    val padding = Material.padding
    PrefHeader(text = "Feedback")

    // val feedbackCollector = LocalFeedbackCollector.current

    val onRequestFeedback = {
        // TODO: Handle feedback
    }
    Preference(
        title = "Feedback",
        summery = stringResource(id = R.string.feedback_dialog_placeholder) + "\nTap to open feedback dialog.",
        icon = Icons.Outlined.Feedback,
        modifier = Modifier.clickable(onClick = onRequestFeedback)
    )

    val onRequestRateApp = {
        // TODO: Handle rate app.
    }
    Preference(
        title = "Rate Us",
        summery = stringResource(id = R.string.review_msg) + "\nTap to rate.",
        icon = Icons.Outlined.Star,
        modifier = Modifier.clickable(onClick = onRequestRateApp)
    )

    val onRequestShareApp = {
        // TODO: Share app.
    }

    Preference(
        title = "Spread a word",
        summery = " As the saying goes 'Sharing is caring'. Please help the app grow by sharing it with friends, family and WhatsApp groups etc.",
        icon = Icons.Outlined.Share,
        modifier = Modifier.clickable(onClick = onRequestShareApp)
    )

    PrefHeader(text = "About Us")
    Text(
        text = stringHtmlResource(R.string.about_us_desc),
        style = Material.typography.body2,
        modifier = Modifier
            .padding(start = RESERVE_PADDING, end = padding.Large)
            .padding(vertical = padding.Small),
        color = LocalContentColor.current.copy(ContentAlpha.medium)
    )

    val context = LocalContext.current
    val version = remember {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }
    //val updateNotifier = LocalUpdateNotifier.current
    val scope = rememberCoroutineScope()
    val onCheckUpdate: () -> Unit = {
        scope.launch {
            //TODO: Check for update.
        }
    }

    Preference(
        title = "App Version",
        summery = "$version \nClick to check for updates.",
        icon = Icons.Outlined.TouchApp,
        modifier = Modifier.clickable(onClick = onCheckUpdate)
    )
}

@Route
@Composable
fun Settings(viewModel: SettingsViewModel) {
    with(viewModel) {
        val topBar =
            @Composable {
                val iNavActions = LocalNavController.current

                val colorStatusBar by with(Preferences.get(LocalContext.current)){
                    this[GlobalKeys.COLOR_STATUS_BAR].observeAsState()
                }
                val darkIcons = !colorStatusBar && Material.isLight
                val color = Material.colors.primary(colorStatusBar)
                TopAppBar(
                    title = { Label(text = "Settings") },
                    navigationIcon = {
                        IconButton(onClick = { iNavActions.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Outlined.Reply,
                                contentDescription = "navigate back"
                            )
                        }
                    },
                    modifier = Modifier
                        .statusBarsPadding2(
                            color = color,
                            darkIcons = darkIcons
                        ),
                    backgroundColor = color,
                    elevation = 12.dp
                )
            }

        val content: @Composable ColumnScope.() -> Unit =
            @Composable {

                PrefHeader(text = "Appearance")

                //dark mode
                val darkTheme by darkUiMode
                SwitchPreference(
                    checked = darkTheme.value,
                    title = darkTheme.title,
                    summery = darkTheme.summery,
                    icon = darkTheme.vector
                ) {
                    set(GlobalKeys.NIGHT_MODE, if (it) NightMode.YES else NightMode.NO)
                }

                //font
                val font by font
                val familyList = listOf(
                    "Lato" to FontFamily.PROVIDED,
                    "Cursive" to FontFamily.CURSIVE,
                    "San serif" to FontFamily.SAN_SERIF,
                    "serif" to FontFamily.SARIF,
                    "System default" to FontFamily.SYSTEM_DEFAULT
                )
                DropDownPreference(
                    title = font.title,
                    entries = familyList,
                    defaultValue = font.value,
                    icon = font.vector
                ) { new ->
                    viewModel.set(GlobalKeys.FONT_FAMILY, new)
                }

                val scale by fontScale
                val activity = LocalContext.current.activity
                FontScalePreference(
                    defaultValue = scale.value,
                    title = scale.title,
                    summery = scale.summery,
                    valueRange = FONT_SCALE_LOWER_BOUND..FONT_SCALE_UPPER_BOUND,
                    steps = SLIDER_STEPS,
                    icon = scale.vector
                ) {
                    set(GlobalKeys.FONT_SCALE, it)
                    activity?.restart()
                }

                //color status bar
                val colorStatusBar by colorStatusBar
                SwitchPreference(
                    checked = colorStatusBar.value,
                    title = colorStatusBar.title,
                    summery = colorStatusBar.summery
                ) {
                    set(GlobalKeys.COLOR_STATUS_BAR, it)
                }

                //hide status bar
                val hideStatusBar by hideStatusBar
                SwitchPreference(
                    checked = hideStatusBar.value,
                    title = hideStatusBar.title,
                    summery = hideStatusBar.summery
                ) {
                    set(GlobalKeys.HIDE_STATUS_BAR, it)
                }

                //force accent
                val forceAccent by forceAccent
                SwitchPreference(
                    checked = forceAccent.value,
                    title = forceAccent.title,
                    summery = forceAccent.summery
                ) {
                    set(GlobalKeys.FORCE_COLORIZE, it)
                }

                // group separator
                val separator by groupSeparator

                val entries = listOf(
                    "Space  ( ) " to ' ',
                    "Hyphen (_) " to '_',
                    "None" to ZERO_WIDTH_CHAR,
                    "Comma ( , )" to ','
                )
                DropDownPreference(
                    title = separator.title,
                    entries = entries,
                    defaultValue = separator.value,
                    icon = separator.vector
                ) { new ->
                    viewModel.set(GlobalKeys.GROUP_SEPARATOR, new)
                }

                AboutUs()
            }

        // content
        Scaffold(topBar = topBar) {
            val state = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(it)
                    .fadeEdge(state = state, length = 16.dp, horizontal = false)
                    .verticalScroll(state),
                content = content
            )
        }
    }
}


@Composable
fun resolveAppThemeState(): Boolean {
    val preferences = Preferences.get(LocalContext.current)
    val mode by with(preferences) {
        preferences[GlobalKeys.NIGHT_MODE].observeAsState()
    }
    return when (mode) {
        NightMode.YES -> true
        else -> false
    }
}


private fun Activity.restart(){
    finish();
    startActivity( Intent(this, this.javaClass));
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
}