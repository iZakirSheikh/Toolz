package com.prime.toolz.chatbot

import android.text.method.LinkMovementMethod
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.KeyOff
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.ReplyAll
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material.icons.twotone.CleaningServices
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.prime.toolz.LocalNavController
import com.prime.toolz.LocalWindowSizeClass
import com.prime.toolz.LocalsProvider
import com.prime.toolz.Material
import com.prime.toolz.R
import com.prime.toolz.core.ContentPadding
import com.prime.toolz.core.billing.Product
import com.prime.toolz.core.billing.purchased
import com.prime.toolz.core.compose.OutlinedButton2
import com.prime.toolz.core.gpt.Message
import com.prime.toolz.purchase
import com.primex.core.composableOrNull
import com.primex.core.padding
import com.primex.core.rememberState
import com.primex.core.rememberVectorPainter
import com.primex.core.rotate
import com.primex.core.stringHtmlResource
import com.primex.material3.IconButton
import com.primex.material3.OutlinedButton

private const val TAG = "ChatBot_UI"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@NonRestartableComposable
private fun ToolBar(
    state: ChatBot,
    modifier: Modifier = Modifier
) {
    SmallTopAppBar(
        scrollBehavior = null,
        modifier = modifier,
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Material.colorScheme.surfaceColorAtElevation(2.dp)
        ),
        title = {
            Text(
                text = stringHtmlResource(id = R.string.chatbot_name),
                style = Material.typography.titleLarge,
                fontWeight = FontWeight.Light
            )
        },
        navigationIcon = {
            val controller = LocalNavController.current
            IconButton(
                icon = Icons.Outlined.ReplyAll,
                contentDescription = "navigate back",
                onClick = { controller.navigateUp() }
            )
        },
        actions = {
            val provider = LocalsProvider.current
            val purchase by purchase(id = Product.DISABLE_ADS)
            if (!purchase.purchased) // only show when not purchased.
                OutlinedButton(
                    label = stringHtmlResource(id = R.string.remove_ads),
                    modifier = Modifier.scale(0.85f),
                    onClick = { provider.launchBillingFlow(Product.DISABLE_ADS) }
                )

            // Login.
            val isLoggedIn = state.isLoggedIn
            if (isLoggedIn)  // only show when not logged in.
                IconButton(
                    icon = Icons.Outlined.KeyOff,
                    contentDescription = "Remove key",
                    modifier = Modifier.rotate(false),
                    onClick = { state.onLoggedIn("") }
                )
        }
    )
}

@Composable
private fun SideBar(
    state: ChatBot,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        containerColor = Material.colorScheme.surfaceColorAtElevation(2.dp),
        modifier = modifier,
        header = {
            val controller = LocalNavController.current
            IconButton(
                icon = Icons.Outlined.ReplyAll,
                contentDescription = "navigate back",
                onClick = { controller.navigateUp() }
            )
        },
        content = {
            val provider = LocalsProvider.current
            val purchase by purchase(id = Product.DISABLE_ADS)
            if (!purchase.purchased) // only show when not purchased.
                OutlinedButton2(
                    label = stringHtmlResource(id = R.string.remove_ads),
                    modifier = Modifier.scale(0.85f),
                    onClick = { provider.launchBillingFlow(Product.DISABLE_ADS) },
                    icon = painterResource(id = R.drawable.ic_remove_ads),
                    shape = RoundedCornerShape(20)
                )

            // Login.
            val isLoggedIn = state.isLoggedIn
            if (isLoggedIn)  // only show when not logged in.
                OutlinedButton2(
                    label = "LOGOUT",
                    modifier = Modifier.scale(0.64f).size(100.dp),
                    onClick = { state.onLoggedIn("") },
                    icon = rememberVectorPainter(image = Icons.Outlined.KeyOff),
                    shape = RoundedCornerShape(20)
                )
            // Place Title at bottom.
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringHtmlResource(id = R.string.chatbot_name),
                style = Material.typography.titleLarge,
                fontWeight = FontWeight.Light,
                modifier = Modifier.rotate(false)
            )
        },
    )
}

private val SENDER_SHAPE = RoundedCornerShape(22.dp, 22.dp, 22.dp, 0.dp)
private val RECEIVER_SHAPE = RoundedCornerShape(22.dp, 22.dp, 0.dp, 22.dp)

@Composable
private inline fun Message(
    value: Message,
    modifier: Modifier = Modifier
) {
    val isFromMe = value.role == "user"
    val scheme = Material.colorScheme
    val bubbleColor =
        if (isFromMe) scheme.primaryContainer else scheme.surfaceColorAtElevation(1.dp)
    Surface(
        color = bubbleColor,
        shape = if (isFromMe) SENDER_SHAPE else RECEIVER_SHAPE,
        modifier = modifier
            .padding(8.dp)
            .padding(
                start = if (!isFromMe) ContentPadding.xLarge else 0.dp,
                end = if (isFromMe) ContentPadding.xLarge else 0.dp
            )
    ) {
        com.primex.material3.Text(
            text = value.content,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Messages(
    state: ChatBot,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp)
) {
    val list = state.conversation
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = padding,
        reverseLayout = true,
    ) {
        // add messages
        items(
            list,
            key = { it.id },
            contentType = { "message" }
        ) {
            Message(
                value = it,
                Modifier.animateItemPlacement()
            )
        }
        // Required so that messages are not hidden when updated.
        item(contentType = "Spacer") {
            Spacer(modifier = Modifier.padding(ContentPadding.medium))
        }
    }
}

@Composable
private fun Prompt(
    state: ChatBot,
    modifier: Modifier = Modifier
) {
    var value by rememberState(initial = "")
    OutlinedTextField(
        value = value,
        onValueChange = { value = it; },
        shape = RoundedCornerShape(16),
        modifier = modifier,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
        label = { Text(text = "Message") },
        placeholder = { Text(text = "Send a message") },
        maxLines = 4,
        trailingIcon = {
            Crossfade(
                targetState = state.processing,
                label = "isInProgress"
            ) {
                when (it) {
                    false -> IconButton(
                        icon = Icons.Outlined.Send,
                        contentDescription = "Message",
                        onClick = { state.send(value); value = "" },
                        enabled = value.isNotBlank()
                    )

                    else -> {
                        val composition by rememberLottieComposition(
                            spec = LottieCompositionSpec.RawRes(
                                R.raw.lt_loading_dots_blue
                            )
                        )

                        LottieAnimation(
                            composition = composition, iterations = Int.MAX_VALUE,
                            modifier = Modifier
                                .size(45.dp)
                                .scale(2.0f),
                            maintainOriginalImageBounds = true,
                            clipToCompositionBounds = true,
                        )
                    }
                }
            }
        },
        leadingIcon = {
            IconButton(
                icon = Icons.TwoTone.CleaningServices,
                contentDescription = "Clear all",
                onClick = { state.clear() },
                enabled = state.isLoggedIn
            )
        },
        enabled = state.isLoggedIn
    )
}

@Composable
private fun Login(
    state: ChatBot,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val color = LocalContentColor.current

        /*Force content to centre*/
        Spacer(modifier = Modifier.weight(1f))

        //Instructions
        val html = stringResource(id = R.string.access_token_instructions)
        AndroidView(
            modifier = Modifier,
            factory = { context ->
                TextView(context).apply {
                    clipToOutline = true
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    movementMethod = LinkMovementMethod.getInstance()
                    setTextColor(color.toArgb())
                }
            },
            update = {
                it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
                it.setTextColor(color.toArgb())
            }
        )

        var value by rememberState(initial = "")
        OutlinedTextField(
            value = value,
            onValueChange = { value = it; },
            shape = RoundedCornerShape(16),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
            label = { Text(text = "Access Token") },
            placeholder = { Text(text = "Paste the copied text here") },
            singleLine = true,
            modifier = Modifier
                .padding(ContentPadding.normal)
                .fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    icon = Icons.Outlined.ArrowForward,
                    contentDescription = "Validate",
                    onClick = { state.onLoggedIn(value) }
                )
            },
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.VpnKey, contentDescription = null)
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        /*Force content to centre*/
    }
}


@Composable
private fun Compact(
    state: ChatBot
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { ToolBar(state) },
        bottomBar = {
            if (state.isLoggedIn)
                Prompt(
                    state,
                    modifier = Modifier
                        .padding(horizontal = ContentPadding.xLarge)
                        .padding(bottom = ContentPadding.medium)
                        .navigationBarsPadding()
                        .fillMaxWidth(),
                )
        },
        content = {
            Crossfade(
                targetState = state.isLoggedIn,
                label = "$TAG _content",
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) { value ->
                when (value) {
                    true -> Messages(
                        state,
                        padding = PaddingValues(horizontal = ContentPadding.normal)
                    )

                    false -> Login(
                        state,
                        modifier = Modifier.padding(horizontal = ContentPadding.large)
                    )
                }
            }
        }
    )
}

@Composable
private fun Medium(state: ChatBot) {
    Row {
        SideBar(state = state)
        Column(modifier = Modifier.imePadding().weight(1f).fillMaxHeight()) {
            Crossfade(
                targetState = state.isLoggedIn,
                label = "$TAG _content",
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) { value ->
                when (value) {
                    true -> Messages(
                        state,
                        padding = PaddingValues(horizontal = ContentPadding.normal)
                    )

                    false -> Login(
                        state,
                        modifier = Modifier.padding(horizontal = ContentPadding.large)
                    )
                }
            }
            AnimatedVisibility(visible = state.isLoggedIn) {
                Prompt(
                    state,
                    modifier = Modifier
                        .padding(horizontal = ContentPadding.xLarge)
                        .padding(bottom = ContentPadding.medium)
                        .navigationBarsPadding()
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private inline fun Expanded(state: ChatBot) {
    Medium(state = state)
}

@Composable
@NonRestartableComposable
fun ChatBot(state: ChatBot) {
    when (LocalWindowSizeClass.current.widthSizeClass) {
        WindowWidthSizeClass.Compact -> Compact(state = state)
        WindowWidthSizeClass.Medium -> Medium(state = state)
        WindowWidthSizeClass.Expanded -> Expanded(state = state)
    }
}

