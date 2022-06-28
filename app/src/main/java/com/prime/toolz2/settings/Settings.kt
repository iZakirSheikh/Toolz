package com.prime.toolz2.settings

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
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
import com.prime.toolz2.Padding
import com.prime.toolz2.R
import com.prime.toolz2.common.compose.*
import com.prime.toolz2.primary
import com.primex.core.fadeEdge
import com.primex.preferences.LocalPreferenceStore
import com.primex.ui.*
import cz.levinzonr.saferoute.accompanist.navigation.transitions.AnimatedRouteTransition
import cz.levinzonr.saferoute.core.annotations.Route
import cz.levinzonr.saferoute.core.annotations.RouteNavGraph
import kotlinx.coroutines.launch

private val RESERVE_PADDING = 48.dp

private const val FONT_SCALE_LOWER_BOUND = 0.5f
private const val FONT_SCALE_UPPER_BOUND = 2.0f

private const val SLIDER_STEPS = 15

private const val ZERO_WIDTH_CHAR = '\u8203'

@Composable
private fun PrefHeader(text: String) {
    val primary = MaterialTheme.colors.primary
    Label(
        text = text,
        modifier = Modifier.padding(
            start = RESERVE_PADDING,
            top = Padding.LARGE,
            bottom = Padding.MEDIUM
        ),
        fontWeight = FontWeight.SemiBold,
        maxLines = 2,
        color = primary
    )

    Divider(
        modifier = Modifier.padding(
            start = RESERVE_PADDING,
            end = Padding.LARGE,
            bottom = Padding.MEDIUM
        ),
        color = primary.copy(0.12f)
    )
}

@Composable
private inline fun ColumnScope.AboutUs() {
    PrefHeader(text = "Feedback")

    // val feedbackCollector = LocalFeedbackCollector.current
    val onRequestFeedback = {
        // TODO: Handle feedback
    }

    Preference(
        title = stringResource(R.string.feedback),
        summery = stringResource(id = R.string.feedback_dialog_placeholder) + "\nTap to open feedback dialog.",
        icon = Icons.Outlined.Feedback,
        modifier = Modifier.clickable(onClick = onRequestFeedback)
    )


    val onRequestRateApp = {
        // TODO: Handle rate app.
    }
    Preference(
        title = stringResource(R.string.rate_us),
        summery = stringResource(id = R.string.review_msg),
        icon = Icons.Outlined.Star,
        modifier = Modifier.clickable(onClick = onRequestRateApp)
    )

    val onRequestShareApp = {
        // TODO: Share app.
    }
    Preference(
        title = stringResource(R.string.spread_the_word),
        summery = stringResource(R.string.spread_the_word_summery),
        icon = Icons.Outlined.Share,
        modifier = Modifier.clickable(onClick = onRequestShareApp)
    )

    PrefHeader(text = stringResource(R.string.about_us))
    Text(
        text = stringHtmlResource(R.string.about_us_desc),
        style = MaterialTheme.typography.body2,
        modifier = Modifier
            .padding(start = RESERVE_PADDING, end = Padding.LARGE)
            .padding(vertical = Padding.SMALL),
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
        title = stringResource(R.string.app_version),
        summery = "$version \nClick to check for updates.",
        icon = Icons.Outlined.TouchApp,
        modifier = Modifier.clickable(onClick = onCheckUpdate)
    )
}

private fun Activity.restart() {
    finish()
    startActivity(Intent(this, this.javaClass))
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

@OptIn(ExperimentalAnimationApi::class)
@Route(
    transition = AnimatedRouteTransition.Default::class,
    navGraph = RouteNavGraph(start = false)
)
@Composable
fun Settings(viewModel: SettingsViewModel) {
    with(viewModel) {
        val topBar =
            @Composable {
                val iNavActions = LocalNavController.current
                val colorStatusBar by with(LocalPreferenceStore.current) {
                    this[GlobalKeys.COLOR_STATUS_BAR].observeAsState()
                }
                val darkIcons = !colorStatusBar && MaterialTheme.colors.isLight
                val primaryOrElse = MaterialTheme.colors.primary(colorStatusBar)

                TopAppBar(
                    title = { Label(text = stringResource(R.string.settings)) },
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
                            color = primaryOrElse,
                            darkIcons = darkIcons
                        ),
                    backgroundColor = MaterialTheme.colors.primary(),
                    elevation = 12.dp
                )
            }

        val content: @Composable ColumnScope.() -> Unit =
            @Composable {
                PrefHeader(text = stringResource(R.string.appearence))

                //dark mode
                val darkTheme by darkUiMode

                val onCheckedChange = { new: Boolean ->
                    set(GlobalKeys.NIGHT_MODE, if (new) NightMode.YES else NightMode.NO)
                }

                SwitchPreference(
                    checked = darkTheme.value,
                    title = darkTheme.title,
                    summery = darkTheme.summery,
                    icon = darkTheme.vector,
                    onCheckedChange = onCheckedChange
                )

                //font
                val font by font
                val familyList = listOf(
                    "Lato" to FontFamily.PROVIDED,
                    "Cursive" to FontFamily.CURSIVE,
                    "San serif" to FontFamily.SAN_SERIF,
                    "serif" to FontFamily.SARIF,
                    "System default" to FontFamily.SYSTEM_DEFAULT
                )
                val onRequestChange = { family: FontFamily ->
                    viewModel.set(GlobalKeys.FONT_FAMILY, family)
                }
                DropDownPreference(
                    title = font.title,
                    entries = familyList,
                    defaultValue = font.value,
                    icon = font.vector,
                    onRequestChange = onRequestChange
                )

                val scale by fontScale
                val onValueChange = { value: Float ->
                    set(GlobalKeys.FONT_SCALE, value)
                }
                SliderPreference(
                    defaultValue = scale.value,
                    title = scale.title,
                    summery = scale.summery,
                    valueRange = FONT_SCALE_LOWER_BOUND..FONT_SCALE_UPPER_BOUND,
                    steps = SLIDER_STEPS,
                    icon = scale.vector,
                    onValueChange = onValueChange,
                    iconChange = Icons.Outlined.TextFormat
                )

                //force accent
                val forceAccent by forceAccent
                val onRequestForceAccent = { should: Boolean ->
                    set(GlobalKeys.FORCE_COLORIZE, should)
                    if (should)
                        set(GlobalKeys.COLOR_STATUS_BAR, true)
                }
                SwitchPreference(
                    checked = forceAccent.value,
                    title = forceAccent.title,
                    summery = forceAccent.summery,
                    onCheckedChange = onRequestForceAccent
                )

                //color status bar
                val colorStatusBar by colorStatusBar
                val onRequestColorChange = { should: Boolean ->
                    set(GlobalKeys.COLOR_STATUS_BAR, should)
                }
                SwitchPreference(
                    checked = colorStatusBar.value,
                    title = colorStatusBar.title,
                    summery = colorStatusBar.summery,
                    onCheckedChange = onRequestColorChange,
                    enabled = !forceAccent.value
                )

                //hide status bar
                val hideStatusBar by hideStatusBar
                val onRequestHideStatusBar = { should: Boolean ->
                    set(GlobalKeys.HIDE_STATUS_BAR, should)
                }
                SwitchPreference(
                    checked = hideStatusBar.value,
                    title = hideStatusBar.title,
                    summery = hideStatusBar.summery,
                    onCheckedChange = onRequestHideStatusBar
                )




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
                    icon = separator.vector,
                    onRequestChange = {
                        viewModel.set(GlobalKeys.GROUP_SEPARATOR, it)
                    }
                )

                //About us section.
                AboutUs()
            }
        // content
        Scaffold(topBar = topBar) {
            val state = rememberScrollState()
            val color = if (MaterialTheme.colors.isLight) Color.White else Color.Black
            Column(
                modifier = Modifier
                    .padding(it)
                    .fadeEdge(state = state, length = 16.dp, horizontal = false, color = color)
                    .verticalScroll(state),
                content = content
            )
        }
    }
}