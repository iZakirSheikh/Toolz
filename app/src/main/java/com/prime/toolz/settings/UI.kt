package com.prime.toolz.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import com.prime.toolz.BuildConfig
import com.prime.toolz.core.compose.LocalNavController
import com.prime.toolz.core.compose.LocalWindowSizeClass
import com.prime.toolz.core.compose.LocalSystemFacade
import com.prime.toolz.Material
import com.prime.toolz.R
import com.prime.toolz.Toolz
import com.prime.toolz.core.ContentPadding
import com.prime.toolz.core.NightMode
import com.prime.toolz.core.billing.Banner
import com.prime.toolz.core.billing.Placement
import com.prime.toolz.core.billing.Product
import com.prime.toolz.core.billing.purchased
import com.prime.toolz.core.compose.purchase
import com.primex.core.get
import com.primex.core.rotate
import com.primex.core.stringHtmlResource
import com.primex.core.stringResource
import com.primex.core.withStyle
import com.primex.material3.Button
import com.primex.material3.DropDownPreference
import com.primex.material3.IconButton
import com.primex.material3.Preference
import com.primex.material3.SwitchPreference

private const val TAG = "Settings"

// Url to pages
// FixMe: In future replace donate me with in app purchases.
private const val WEB_PAGE_URL = "https://github.com/prime-zs/toolz2"
private const val DONATE_ME_URL = "https://www.buymeacoffee.com/sheikhzaki3"

private val SourceLauncherIntent
    get() = Intent(Intent.ACTION_VIEW, Uri.parse(WEB_PAGE_URL)).apply {
        this.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    };
private val DonateIntent
    get() = Intent(Intent.ACTION_VIEW, Uri.parse(DONATE_ME_URL)).apply {
        this.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    };

private val TopCurvedShape = RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp)
private val Rectangular = RectangleShape
private val BottomCurved = RoundedCornerShape(0.dp, 0.dp, 24.dp, 24.dp)
private val CurvedShape = RoundedCornerShape(24.dp)

@Deprecated("Requires a new solution!!")
private fun Context.shareApp() {
    ShareCompat.IntentBuilder(this)
        .setType("text/plain")
        .setChooserTitle(getString(R.string.app_name))
        .setText("Let me recommend you this application ${Toolz.GOOGLE_STORE}").startChooser()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@NonRestartableComposable
private fun Toolbar(
    modifier: Modifier = Modifier,
    behavior: TopAppBarScrollBehavior? = null
) {
    val controller = LocalNavController.current
    LargeTopAppBar(
        title = { Text(text = "Settings") },
        scrollBehavior = behavior,
        modifier = modifier,
        navigationIcon = {
            IconButton(icon = Icons.Default.ArrowBack,
                contentDescription = null,
                onClick = { controller.navigateUp() })
        },
        actions = {
            // Show if not purchased.
            val provider = LocalSystemFacade.current
            val purchased by purchase(id = Product.DISABLE_ADS)
            if (purchased.purchased)
                IconButton(
                    icon = Icons.Outlined.ShoppingCart,
                    contentDescription = "buy full version",
                    onClick = { provider.launchBillingFlow(Product.DISABLE_ADS) },
                )
        },
    )
}

@Composable
fun SideBar(
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        containerColor = Material.colorScheme.surfaceColorAtElevation(2.dp)
    ) {
        val controller = LocalNavController.current
        IconButton(
            icon = Icons.Default.ArrowBack,
            contentDescription = null,
            onClick = { controller.navigateUp() },
        )

        Text(
            text = stringResource(id = R.string.settings),
            modifier = Modifier
                .rotate(false)
                .weight(1f),
            style = Material.typography.labelLarge
        )

        // Show if not purchased.
        val provider = LocalSystemFacade.current
        val purchased by purchase(id = Product.DISABLE_ADS)
        if (purchased.purchased)
            IconButton(
                icon = Icons.Outlined.ShoppingCart,
                contentDescription = "buy full version",
                onClick = { provider.launchBillingFlow(Product.DISABLE_ADS) },
            )
    }
}

@Composable
private fun Banner(
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        // Title + Developer + App version
        // App name etc.
        Text(
            maxLines = 2,
            modifier = Modifier.fillMaxWidth(),
            text = buildAnnotatedString {
                val appName = stringResource(id = R.string.app_name)
                withStyle(Material.typography.headlineSmall) {
                    append(appName)
                }
                // The app version and check for updates.
                val version = BuildConfig.VERSION_NAME
                withStyle(Material.typography.labelSmall) {
                    append("v$version")
                }
                withStyle(Material.typography.labelMedium) {
                    append("\nby Zakir Sheikh")
                }
            },
        )

        // Donate + Source Code Row
        Row(
            modifier = Modifier.padding(top = ContentPadding.xLarge)
        ) {
            // Donate
            val context = LocalContext.current
            Button(
                label = stringResource(R.string.donate),
                icon = rememberVectorPainter(image = Icons.Default.Euro),
                onClick = { context.startActivity(DonateIntent) },
                shape = Material.shapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = Material.colorScheme.primary),
                modifier = Modifier
                    .padding(end = ContentPadding.medium)
                    .weight(1f)
                    .heightIn(52.dp),
            )

            // Source code
            Button(
                label = stringResource(R.string.github),
                icon = rememberVectorPainter(image = Icons.Default.DataObject),
                onClick = { context.startActivity(SourceLauncherIntent) },
                shape = Material.shapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = Material.colorScheme.secondary),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(52.dp),
            )
        }
    }
}

@Composable
private inline fun Header(
    text: CharSequence,
    modifier: Modifier = Modifier
) {
    com.primex.material3.Text(
        text = text,
        modifier = Modifier
            .padding(ContentPadding.normal)
            .padding(ContentPadding.normal)
            .then(modifier),
        color = Material.colorScheme.primary,
        style = Material.typography.titleSmall
    )
}

@Composable
private inline fun ColumnScope.Feedback() {
    // Feedback Prefs
    Header(text = stringResource(id = R.string.feedback))

    val context = LocalContext.current
    val provider = LocalSystemFacade.current
    Preference(
        title = stringResource(id = R.string.feedback),
        summery = stringResource(id = R.string.feedback_desc),
        icon = Icons.Outlined.Feedback,
        shape = TopCurvedShape,
        modifier = Modifier
            .padding(horizontal = ContentPadding.normal)
            .clickable(onClick = { provider.launchAppStore() })
    )

    Preference(
        title = stringResource(R.string.rate_us),
        summery = stringResource(id = R.string.rate_us_msg),
        icon = Icons.Outlined.Star,
        shape = Rectangular,
        modifier = Modifier
            .padding(horizontal = ContentPadding.normal)
            .clickable(onClick = { provider.launchAppStore() })
    )

    Preference(
        title = stringResource(R.string.spread_the_word),
        summery = stringResource(R.string.spread_the_word_msg),
        icon = Icons.Outlined.Share,
        shape = BottomCurved,
        modifier = Modifier
            .padding(horizontal = ContentPadding.normal)
            .clickable(onClick = { context.shareApp() })
    )
}

private const val ZERO_WIDTH_CHAR = '\u200B'
private val GroupSeparatorList = listOf(
    "Space  ( ) " to ' ',
    "Hyphen (_) " to '_',
    "None" to ZERO_WIDTH_CHAR,
    "Comma ( , )" to ','
)

@Composable
private inline fun ColumnScope.Content(
    state: Settings
) {
    // Appearance Prefs
    Header(text = stringResource(id = R.string.appearance))
    //Dark Mode
    val darkTheme = state.nightMode
    val provider = LocalSystemFacade.current
    DropDownPreference(
        title = stringResource(value = darkTheme.title),
        defaultValue = darkTheme.value,
        icon = darkTheme.vector,
        entries = listOf(
            "Dark" to NightMode.YES,
            "Light" to NightMode.NO,
            "Sync with System" to NightMode.FOLLOW_SYSTEM
        ),
        onRequestChange = {
            state.set(Settings.KEY_NIGHT_MODE, it)
            provider.showAd(force = true)
        },
        shape = TopCurvedShape,
        modifier = Modifier.padding(horizontal = ContentPadding.normal)
    )

    //Color status bar
    val colorStatusBar = state.colorSystemBars
    SwitchPreference(
        checked = colorStatusBar.value,
        title = stringResource(value = colorStatusBar.title),
        summery = colorStatusBar.summery?.get,
        onCheckedChange = { should: Boolean ->
            state.set(Settings.KEY_COLOR_STATUS_BAR, should)
            provider.showAd(force = true)
        },
        shape = Rectangular,
        modifier = Modifier.padding(horizontal = ContentPadding.normal)
    )

    // Enable/Disable dynamic colors
    val dynamicColors = state.dynamicColors
    SwitchPreference(
        checked = dynamicColors.value,
        title = stringResource(value = dynamicColors.title),
        summery = dynamicColors.summery?.get,
        onCheckedChange = { should: Boolean ->
            state.set(Settings.KEY_DYNAMIC_COLORS, should)
        },
        shape = RectangleShape,
        modifier = Modifier.padding(horizontal = ContentPadding.normal)
    )

    //Hide StatusBar
    val hideStatusBar = state.hideStatusBar
    SwitchPreference(
        checked = hideStatusBar.value,
        title = stringResource(value = hideStatusBar.title),
        summery = hideStatusBar.summery?.get,
        onCheckedChange = { should: Boolean ->
            state.set(Settings.KEY_HIDE_STATUS_BAR, should)
        },
        shape = BottomCurved,
        modifier = Modifier.padding(horizontal = ContentPadding.normal)
    )

    //General Prefs
    Header(text = stringResource(R.string.general))
    // group separator
    val separator = state.numberGroupSeparator
    DropDownPreference(
        title = stringResource(separator.title),
        entries = GroupSeparatorList,
        defaultValue = separator.value,
        icon = separator.vector,
        onRequestChange = {
            state.set(Settings.KEY_GROUP_SEPARATOR, it)
            provider.showAd(force = true)
        },
        modifier = Modifier.padding(horizontal = ContentPadding.normal),
        shape = CurvedShape,
    )

    // Feedback Prefs
    Header(text = stringResource(id = R.string.about_us))
    Preference(
        title = stringResource(R.string.about_us),
        summery = stringHtmlResource(R.string.about_us_desc),
        shape = CurvedShape,
        modifier = Modifier.padding(horizontal = ContentPadding.normal),
        icon = Icons.Outlined.Info
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Compact(
    state: Settings,
    modifier: Modifier = Modifier
) {
    val behavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = { Toolbar(behavior = behavior) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier.nestedScroll(behavior.nestedScrollConnection)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .then(
                    modifier
                        .padding(it)
                        .navigationBarsPadding()
                )
        ) {
            // Banner on Card
            Surface(
                modifier = Modifier.padding(ContentPadding.normal),
                color = Material.colorScheme.surfaceColorAtElevation(2.dp),
                shape = CurvedShape
            ) {
                Banner(Modifier.padding(ContentPadding.normal))
            }

            // Ad Banner
            val purchase by purchase(id = Product.DISABLE_ADS)
            if (!purchase.purchased) Banner(
                placementID = Placement.BANNER_SETTINGS,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            // Main Content
            Content(state = state)

            // Feedback section.
            Feedback()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Medium(
    state: Settings,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        //SideBar
        SideBar()
        // Main
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .widthIn(max = 600.dp)
                .align(Alignment.CenterVertically)
                .fillMaxHeight()
                .systemBarsPadding()
        ) {
            // Banner on Card
            Surface(
                modifier = Modifier.padding(ContentPadding.normal),
                color = Material.colorScheme.surfaceColorAtElevation(2.dp),
                shape = CurvedShape
            ) {
                Banner(Modifier.padding(ContentPadding.normal))
            }

            // Ad Banner
            val purchase by purchase(id = Product.DISABLE_ADS)
            if (!purchase.purchased) Banner(
                placementID = Placement.BANNER_SETTINGS,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            // Main Content
            Content(state = state)
            // Feedback section.
            Feedback()
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}


@Composable
private inline fun Expanded(
    state: Settings,
    modifier: Modifier = Modifier
) {
    Medium(state = state, modifier)
}


@Composable
@NonRestartableComposable
fun Settings(state: Settings) {
    when (LocalWindowSizeClass.current.widthSizeClass) {
        WindowWidthSizeClass.Compact -> Compact(state = state)
        WindowWidthSizeClass.Medium -> Medium(state = state)
        WindowWidthSizeClass.Expanded -> Expanded(state = state)
    }
}
