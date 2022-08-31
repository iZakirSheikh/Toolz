package com.prime.toolz2.settings

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prime.toolz2.*
import com.prime.toolz2.R
import com.prime.toolz2.common.compose.*
import com.primex.core.activity
import com.primex.core.drawHorizontalDivider
import com.primex.core.stringHtmlResource
import com.primex.ui.*
import cz.levinzonr.saferoute.accompanist.navigation.transitions.AnimatedRouteTransition
import cz.levinzonr.saferoute.core.annotations.Route
import cz.levinzonr.saferoute.core.annotations.RouteNavGraph


private val RESERVE_PADDING = 56.dp

private const val FONT_SCALE_LOWER_BOUND = 0.5f
private const val FONT_SCALE_UPPER_BOUND = 2.0f

private const val SLIDER_STEPS = 15

private const val ZERO_WIDTH_CHAR = '\u200B'

//TODO: Instead of string use Text
private val familyList =
    listOf(
        "Lato" to FontFamily.PROVIDED,
        "Cursive" to FontFamily.CURSIVE,
        "San serif" to FontFamily.SAN_SERIF,
        "serif" to FontFamily.SARIF,
        "System default" to FontFamily.SYSTEM_DEFAULT
    )

val GroupSeparatorList =
    listOf(
        "Space  ( ) " to ' ',
        "Hyphen (_) " to '_',
        "None" to ZERO_WIDTH_CHAR,
        "Comma ( , )" to ','
    )

@Composable
private inline fun PrefHeader(text: String) {
    val primary = MaterialTheme.colors.secondary
    val modifier =
        Modifier
            .padding(
                start = RESERVE_PADDING,
                top = ContentPadding.normal,
                end = ContentPadding.large,
                bottom = ContentPadding.medium
            )
            .fillMaxWidth()
            .drawHorizontalDivider(color = primary)
            .padding(bottom = ContentPadding.medium)
    Label(
        text = text,
        modifier = modifier,
        fontWeight = FontWeight.SemiBold,
        maxLines = 2,
        color = primary
    )
}

context(ColumnScope) @Composable
private inline fun AboutUs() {
    PrefHeader(text = "Feedback")

    // val feedbackCollector = LocalFeedbackCollector.current
    val onRequestFeedback = {
        // TODO: Handle feedback
    }

    Preference(
        title = stringResource(R.string.feedback),
        summery = (stringResource(id = R.string.feedback_dialog_placeholder) + "\nTap to open feedback dialog."),
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
            .padding(start = RESERVE_PADDING, end = ContentPadding.large)
            .padding(vertical = ContentPadding.small),
        color = LocalContentColor.current.copy(ContentAlpha.medium)
    )

    val context = LocalContext.current
    val version = remember {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }
    //val updateNotifier = LocalUpdateNotifier.current
    val activity = LocalContext.current.activity!!
    val channel = LocalSnackDataChannel.current
    val onCheckUpdate: () -> Unit = {
        activity.launchUpdateFlow(channel, true)
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

@Composable
private fun TopAppBar(modifier: Modifier = Modifier) {
    val navigator = LocalNavController.current

    NeumorphicTopAppBar(
        title = { Label(text = stringResource(R.string.settings)) },
        modifier = modifier.padding(top = ContentPadding.medium),
        navigationIcon = {
            IconButton(
                onClick = { navigator.navigateUp() },
                imageVector = Icons.Outlined.ReplyAll,
                contentDescription = null
            )
        },
        shape = CircleShape,
        elevation = ContentElevation.low,
        lightShadowColor = Material.colors.lightShadowColor,
        darkShadowColor = Material.colors.darkShadowColor
    )
}


@OptIn(ExperimentalAnimationApi::class)
@Route(
    transition = AnimatedRouteTransition.Default::class,
    navGraph = RouteNavGraph(start = false)
)
@Composable
fun Settings(viewModel: SettingsViewModel) {
    with(viewModel) {
        Scaffold(
            topBar = {
                val (colorStatusBar, _, _, _) = colorStatusBar.value
                val primaryOrTransparent =
                    Material.colors.primary(colorStatusBar, Color.Transparent)
                TopAppBar(
                    modifier = Modifier
                        .statusBarsPadding2(
                            color = primaryOrTransparent,
                            darkIcons = !colorStatusBar && Material.colors.isLight
                        )
                        .drawHorizontalDivider(color = Material.colors.onSurface)
                        .padding(bottom = ContentPadding.medium)
                )
            },


            content = {
                val state = rememberScrollState()
                //val color = if (MaterialTheme.colors.isLight) Color.White else Color.Black
                Column(
                    modifier = Modifier
                        .padding(it)
                        //FixMe: Creates a issue between Theme changes
                        // needs to be study properly
                        // disabling for now
                        //.fadeEdge(state = state, length = 16.dp, horizontal = false, color = color)
                        .verticalScroll(state),
                ) {
                    PrefHeader(text = stringResource(R.string.appearence))

                    //dark mode
                    val darkTheme by darkUiMode
                    SwitchPreference(
                        checked = darkTheme.value,
                        title = stringResource(res = darkTheme.title),
                        summery = darkTheme.summery?.let { stringResource(res = it) },
                        icon = darkTheme.vector,
                        onCheckedChange = { new: Boolean ->
                            set(GlobalKeys.NIGHT_MODE, if (new) NightMode.YES else NightMode.NO)
                        }
                    )

                    //font
                    val font by font
                    DropDownPreference(
                        title = stringResource(res = font.title),
                        entries = familyList,
                        defaultValue = font.value,
                        icon = font.vector,
                        onRequestChange = { family: FontFamily ->
                            viewModel.set(GlobalKeys.FONT_FAMILY, family)
                        }
                    )

                    val scale by fontScale
                    SliderPreference(
                        defaultValue = scale.value,
                        title = stringResource(res = scale.title),
                        summery = scale.summery?.let { stringResource(res = it) },
                        valueRange = FONT_SCALE_LOWER_BOUND..FONT_SCALE_UPPER_BOUND,
                        steps = SLIDER_STEPS,
                        icon = scale.vector,
                        iconChange = Icons.Outlined.TextFormat,
                        onValueChange = { value: Float ->
                            set(GlobalKeys.FONT_SCALE, value)
                        }
                    )

                    //force accent
                    val forceAccent by forceAccent
                    SwitchPreference(
                        checked = forceAccent.value,
                        title = stringResource(res = forceAccent.title),
                        summery = forceAccent.summery?.let { stringResource(res = it) },
                        onCheckedChange = { should: Boolean ->
                            set(GlobalKeys.FORCE_COLORIZE, should)
                            if (should)
                                set(GlobalKeys.COLOR_STATUS_BAR, true)
                        }
                    )

                    //color status bar
                    val colorStatusBar by colorStatusBar
                    SwitchPreference(
                        checked = colorStatusBar.value,
                        title = stringResource(res = colorStatusBar.title),
                        summery = colorStatusBar.summery?.let { stringResource(res = it) },
                        enabled = !forceAccent.value,
                        onCheckedChange = { should: Boolean ->
                            set(GlobalKeys.COLOR_STATUS_BAR, should)
                        }
                    )

                    //hide status bar
                    val hideStatusBar by hideStatusBar
                    SwitchPreference(
                        checked = hideStatusBar.value,
                        title = stringResource(res = hideStatusBar.title),
                        summery = hideStatusBar.summery?.let { stringResource(res = it) },
                        onCheckedChange = { should: Boolean ->
                            set(GlobalKeys.HIDE_STATUS_BAR, should)
                        }
                    )

                    // group separator
                    val separator by groupSeparator
                    DropDownPreference(
                        title = stringResource(separator.title),
                        entries = GroupSeparatorList,
                        defaultValue = separator.value,
                        icon = separator.vector,
                        onRequestChange = {
                            viewModel.set(GlobalKeys.GROUP_SEPARATOR, it)
                        }
                    )

                    //About us section.
                    AboutUs()
                }
            }
        )
    }
}