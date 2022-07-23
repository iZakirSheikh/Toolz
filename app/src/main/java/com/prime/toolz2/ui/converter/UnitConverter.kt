package com.prime.toolz2.ui.converter

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SwapVerticalCircle
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prime.toolz2.*
import com.prime.toolz2.R
import com.prime.toolz2.common.compose.*
import com.prime.toolz2.core.converter.Unet
import com.prime.toolz2.core.math.NumUtil
import com.prime.toolz2.settings.GlobalKeys
import com.prime.toolz2.settings.SettingsRoute
import com.primex.core.*
import com.primex.preferences.LocalPreferenceStore
import com.primex.ui.ColoredOutlineButton
import com.primex.ui.IconButton
import com.primex.ui.Label
import cz.levinzonr.saferoute.core.annotations.Route
import cz.levinzonr.saferoute.core.annotations.RouteNavGraph
import cz.levinzonr.saferoute.core.navigateTo
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private const val TAG = "UnitConverter"

@Suppress("FunctionName")
private fun NumberFormatterTransformation(separator: Char = ',') =
    VisualTransformation {
        val text = it.text
        val transformed = run {
            // split into respective components.
            // maybe remove this and replace with already formatted text.
            val (w, f, e) = NumUtil.split(text)
            val whole = if (!w.isNullOrBlank()) NumUtil.addThousandSeparators(w, separator) else ""
            val fraction = if (f != null) ".$f" else ""
            val exponent = if (e != null) "E$e" else ""
            whole + fraction + exponent
        }

        // FIXME: The offsets are not mapped accurately
        // use separator in the transformed text.
        TransformedText(
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return transformed.length
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return text.length
                }
            },
            text = AnnotatedString(transformed)
        )
    }

private inline val NumberFormatTransformation: State<VisualTransformation>
    @Composable
    get() {
        //TODO: maybe consider using LocalComposition for preferences.
        val preferences = LocalPreferenceStore.current
        return with(preferences) {
            val initial = remember {
                val value = preferences[GlobalKeys.GROUP_SEPARATOR].obtain()
                NumberFormatterTransformation(value)
            }
            produceState(initialValue = initial) {
                preferences[GlobalKeys.GROUP_SEPARATOR].map {
                    NumberFormatterTransformation(it)
                }.collect {
                    value = it
                }
            }
        }
    }

@Composable
private fun AppBarTop(
    modifier: Modifier = Modifier
) {
    val prefs = LocalPreferenceStore.current
    val forceColorize by with(prefs) {
        prefs[GlobalKeys.FORCE_COLORIZE].observeAsState()
    }
    Surface(
        color = if (forceColorize) Material.colors.primary else Material.colors.overlay,
        contentColor = if (forceColorize) Material.colors.onPrimary else Material.colors.onBackground,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(bottomStartPercent = 70),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = ContentPadding.large, end = ContentPadding.normal)
        ) {
            // title
            Text(
                text = stringHtmlResource(id = R.string.unit_converter_html),
                fontWeight = FontWeight.Light,
                modifier = Modifier.weight(1f),
                style = Material.typography.h5
            )

            // actions
            val controller = LocalNavController.current
            IconButton(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null,
                onClick = {
                    val direction = SettingsRoute()
                    controller.navigateTo(direction)
                }
            )

            // app icon
            // TODO: Replace it with buy option.
            IconButton(
                onClick = { /*TODO*/ },
                painter = painterResource(id = R.drawable.ic_handyman),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = ContentPadding.normal)
                    .requiredSize(24.dp)
            )
        }
    }
}

@Composable
private fun Tab(
    title: Text,
    @DrawableRes imageRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color =
        if (selected) Material.colors.secondary.copy(ContentAlpha.Indication) else Material.colors.overlay
    val contentColor = if (selected) Material.colors.secondary else Material.colors.onBackground
    // The color of the Ripple should always the selected color, as we want to show the color
    // before the item is considered selected, and hence before the new contentColor is
    // provided by TabTransition.
    val ripple = rememberRipple(bounded = false, color = Material.colors.secondary)
    Column(
        modifier = modifier
            .clip(Material.shapes.small)
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = true,
                role = Role.Tab,
                interactionSource = remember(::MutableInteractionSource),
                indication = ripple
            )
            .padding(ContentPadding.small)
            .width(62.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            contentColor = contentColor,
            color = color,
            border = BorderStroke(1.dp, if (selected) contentColor else color),
            shape = RoundedCornerShape(30),
            content = {
                Icon(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.requiredSize(24.dp)
                )
            }
        )

        Label(
            text = stringResource(res = title),
            maxLines = 2,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = ContentPadding.medium),
            color = contentColor,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun UnitConverterViewModel.Converters(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val current = converter
    LazyRow(
        contentPadding = contentPadding,
        modifier = modifier.height(80.dp),
        content = {
            items(converters) { item ->
                val selected = item == current
                Tab(
                    title = item.title,
                    imageRes = item.drawableRes,
                    selected = selected,
                    onClick = { converter = item }
                )
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun UnitConverterViewModel.ExposedDropdownMenuBox(
    isFrom: Boolean,
    values: Map<AnnotatedString, List<Unet>>,
    modifier: Modifier = Modifier,
    field: @Composable () -> Unit,
    expanded: Boolean = false,
    onDismissRequest: () -> Unit,
) {

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { /*expanded = it*/ },
        content = {
            // The field of that this menu exposes
            field()

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissRequest,
                modifier = Modifier.exposedDropdownSize(true),
                content = {
                    val container = MaterialTheme.colors.secondaryContainer
                    val secondary = MaterialTheme.colors.secondary

                    values.forEach { (title, list) ->
                        // list header
                        Label(
                            text = title,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            color = secondary,
                            modifier = Modifier
                                .padding(
                                    start = ContentPadding.normal,
                                    top = ContentPadding.normal,
                                    end = ContentPadding.normal,
                                    bottom = ContentPadding.medium
                                )
                                .fillMaxWidth()
                                .drawHorizontalDivider(color = secondary)
                                .padding(bottom = ContentPadding.medium),
                        )

                        // emit the list of this title
                        list.forEach { value ->
                            val selected = (if (isFrom) fromUnit else toUnit) == value
                            val color = if (selected) secondary else LocalContentColor.current

                            //TODO find a way to support selected.
                            DropdownMenuItem(
                                modifier = if (selected) Modifier.background(color = container) else Modifier,
                                //handle click based on the type
                                //weather it is from or to.
                                onClick = {
                                    if (isFrom)
                                        fromUnit = value
                                    else
                                        toUnit = value

                                    onDismissRequest()
                                },

                                content = {
                                    // code
                                    Text(
                                        text = stringResource(res = value.code),
                                        style = MaterialTheme.typography.body1,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.Bold,
                                        color = color
                                    )

                                    // title
                                    Text(
                                        text = stringResource(res = value.title),
                                        modifier = Modifier.padding(start = ContentPadding.normal),
                                        style = MaterialTheme.typography.caption,
                                        color = color,
                                    )
                                },
                            )
                            // show divider only when checked.
                            if (selected)
                                Divider(color = color, thickness = 2.dp)
                        }
                    }
                }
            )
        }
    )
}

private val FIELD_MIN_HEIGHT = 80.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun UnitConverterViewModel.ValueField(
    visualTransformation: VisualTransformation,
    values: Map<AnnotatedString, List<Unet>>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Header
        Text(
            text = "FROM",
            style = MaterialTheme.typography.overline,
            modifier = Modifier
                .rotate(false),
        )

        var expanded by rememberState(initial = false)
        ExposedDropdownMenuBox(
            isFrom = true,
            values = values,
            modifier = Modifier.padding(start = ContentPadding.medium),
            expanded = expanded,
            onDismissRequest = { expanded = false },
            field = {
                OutlinedTextField(
                    value = TextFieldValue(value, TextRange(value.length)),
                    onValueChange = { value = it.text },
                    readOnly = false,
                    singleLine = true,
                    enabled = true,
                    visualTransformation = visualTransformation,
                    shape = RoundedCornerShape(percent = 10),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),

                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        textColor = Material.colors.onBackground,
                        cursorColor = Color.Transparent,
                    ),

                    label = {
                        Label(text = stringResource(res = fromUnit.title))
                    },

                    trailingIcon = {
                        val rotate by animateFloatAsState(targetValue = if (expanded) 0f else 180f)
                        IconButton(
                            onClick = { expanded = !expanded },
                            imageVector = Icons.Outlined.KeyboardArrowUp,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotate)
                        )
                    },

                    textStyle = MaterialTheme.typography.h4.copy(
                        fontWeight = FontWeight.SemiBold
                    ),

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = FIELD_MIN_HEIGHT),
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun UnitConverterViewModel.ResultField(
    visualTransformation: VisualTransformation,
    values: Map<AnnotatedString, List<Unet>>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Header
        Text(
            text = "EQUALS TO",
            style = MaterialTheme.typography.overline,
            modifier = Modifier
                .rotate(false),
        )

        var expanded by rememberState(initial = false)
        ExposedDropdownMenuBox(
            isFrom = false,
            expanded = expanded,
            values = values,
            modifier = Modifier.padding(start = ContentPadding.medium),
            onDismissRequest = { expanded = false },
            field = {
                TextField(
                    readOnly = true,
                    value = result,
                    onValueChange = { },
                    singleLine = true,
                    visualTransformation = visualTransformation,
                    shape = RoundedCornerShape(topStartPercent = 10, topEndPercent = 10),
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),


                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = FIELD_MIN_HEIGHT),

                    label = { Label(text = stringResource(res = toUnit.title)) },

                    trailingIcon = {
                        val rotate by animateFloatAsState(targetValue = if (expanded) 0f else 180f)
                        IconButton(
                            onClick = { expanded = !expanded },
                            imageVector = Icons.Outlined.KeyboardArrowUp,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotate)
                        )
                    },
                    textStyle = MaterialTheme.typography.h5.copy(
                        letterSpacing = 0.35.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                )
            }
        )
    }
}


@Composable
private fun OutlineChip(
    text: AnnotatedString,
    onRequestCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = ContentPadding.medium)
                    .scale(0.85f)
            ) {
                Text(
                    text = text,
                    style = Material.typography.body2,
                    color = LocalContentColor.current,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(
                    onClick = onRequestCopy,
                    imageVector = Icons.Outlined.FileCopy,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = ContentPadding.medium),
                    tint = LocalContentColor.current
                )
            }
        },
        border = BorderStroke(1.dp, Material.colors.outline),
        contentColor = Material.colors.onBackground.copy(ContentAlpha.medium),
        color = Color.Transparent,
        modifier = modifier
            .height(34.dp)
            .wrapContentWidth(),
        shape = CircleShape
    )
}

@Composable
private fun UnitConverterViewModel.AboutEquals(
    modifier: Modifier = Modifier,
) {
    StaggeredGrid(rows = 2, modifier = modifier) {
        more.forEach { (unit, value) ->
            val code = stringResource(res = unit)
            OutlineChip(
                text = buildAnnotatedString {
                    append(value)
                    append(" ")
                    withStyle(
                        style = SpanStyle(fontStyle = FontStyle.Italic),
                        block = {
                            append(code)
                        }
                    )
                },
                onRequestCopy = { /*TODO*/ },
                modifier = Modifier.padding(
                    end = ContentPadding.medium,
                    bottom = ContentPadding.normal
                )
            )
        }
    }
}

@Route(navGraph = RouteNavGraph(start = true))
@Composable
fun UnitConverter(viewModel: UnitConverterViewModel) {
    // Dispose off messenger when out of scope.
    // this ensures the viewModel has a instance of channel only when
    // device is active and working.
    val channel = LocalSnackDataChannel.current
    DisposableEffect(key1 = Unit) {
        viewModel.channel = channel
        onDispose {
            viewModel.channel = null
        }
    }

    with(viewModel) {

        Scaffold(
            topBar = {
                val prefs = LocalPreferenceStore.current
                val colorize by with(prefs) {
                    prefs[GlobalKeys.COLOR_STATUS_BAR].observeAsState()
                }
                AppBarTop(
                    modifier = Modifier.statusBarsPadding2(
                        color = if (colorize) Material.colors.primaryVariant else Material.colors.overlay,
                        darkIcons = Material.colors.isLight && !colorize
                    )
                )
            },

            content = {
                Column(Modifier.padding(it)) {
                    Converters(
                        modifier = Modifier.padding(top = ContentPadding.normal),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    )
                    CompositionLocalProvider(
                        LocalTextSelectionColors provides TextSelectionColors(
                            Color.Transparent,
                            Color.Transparent
                        )
                    ) {
                        val visualTransformation by NumberFormatTransformation
                        val resources = LocalContext.current.resources
                        val values = remember(converter.uuid) {
                            converter.units.groupBy { resources.stringResource(it.group) }
                        }
                        ValueField(
                            modifier = Modifier.padding(
                                top = ContentPadding.normal,
                                start = 24.dp,
                                end = 24.dp
                            ),
                            visualTransformation = visualTransformation,
                            values = values
                        )

                        ResultField(
                            modifier = Modifier.padding(
                                top = ContentPadding.normal,
                                start = 24.dp,
                                end = 24.dp
                            ),
                            visualTransformation = visualTransformation,
                            values = values
                        )
                    }

                    // copy
                    TextButton(
                        onClick = { runBlocking { channel.send("Coming soon!") } },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 24.dp, top = ContentPadding.medium),
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.FileCopy,
                                contentDescription = null
                            )

                            Label(
                                text = "COPY",
                                modifier = Modifier.padding(
                                    start = ContentPadding.medium
                                )
                            )
                        }
                    )

                    Text(
                        text = "About Equals",
                        style = MaterialTheme.typography.h4,
                        modifier = Modifier
                            .padding(start = ContentPadding.large, top = ContentPadding.small)
                            .align(Alignment.Start),
                        fontWeight = FontWeight.Light,
                    )

                    AboutEquals(
                        modifier = Modifier
                            .horizontalScroll(state = rememberScrollState())
                            .wrapContentHeight(Alignment.Top)
                            .padding(horizontal = 28.dp, vertical = ContentPadding.medium),
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    ColoredOutlineButton(
                        onClick = { swap() },
                        shape = CircleShape,

                        modifier = Modifier
                            // add padding so that
                            .padding(vertical = ContentPadding.normal)
                            // at least the height of the TopAppBar
                            .height(48.dp)
                            // width around 70% of screen
                            .fillMaxWidth(0.7f)
                            // align centre of column
                            .align(Alignment.CenterHorizontally),

                        content = {
                            Icon(
                                imageVector = Icons.Outlined.SwapVerticalCircle,
                                contentDescription = null,
                                modifier = Modifier.padding(end = ContentPadding.medium)
                            )
                            Label(text = "SWAP")
                        },
                        border = BorderStroke(1.dp, Material.colors.primary)
                    )
                }
            }
        )
    }
}