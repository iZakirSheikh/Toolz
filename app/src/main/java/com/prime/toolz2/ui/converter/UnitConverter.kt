@file:OptIn(ExperimentalMaterialApi::class)

package com.prime.toolz2.ui.converter

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardBackspace
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prime.toolz2.Padding
import com.prime.toolz2.R
import com.prime.toolz2.common.compose.*
import com.prime.toolz2.core.math.NumUtil
import com.prime.toolz2.settings.GlobalKeys
import com.prime.toolz2.settings.SettingsRoute
import com.primex.core.VerticalGrid
import com.primex.preferences.LocalPreferenceStore
import cz.levinzonr.saferoute.core.annotations.Route
import cz.levinzonr.saferoute.core.annotations.RouteNavGraph
import cz.levinzonr.saferoute.core.navigateTo
import kotlinx.coroutines.flow.map
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.*
import com.prime.toolz2.core.converter.Unet
import com.prime.toolz2.primaryContainer
import com.primex.core.rememberState
import com.primex.core.rotate
import com.primex.ui.Label
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*

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

private val LightShadowColor =
    Color(red = 1.0f, green = 1.0f, blue = 1.0f)
private val DarkShadowColor =
    Color(red = 0.820f, green = 0.851f, blue = 0.902f)

private val DarkThemeLightShadowColor =
    Color.Black.copy(0.5f)

private val DarkThemeDarkShadowColor =
    Color.White.copy(0.01f)

private val buttons =
    arrayOf(
        R.string.digit_7,
        R.string.digit_8,
        R.string.digit_9,
        null,
        R.string.digit_4,
        R.string.digit_5,
        R.string.digit_6,
        R.string.all_cleared,
        R.string.digit_1,
        R.string.digit_2,
        R.string.digit_3,
        R.string.backspace,
        null,
        R.string.dec_point,
        R.string.digit_0,
        R.string.swap,
    )

@Composable
fun UnitConverterViewModel.NumPad(modifier: Modifier = Modifier) {
    VerticalGrid(
        columns = 4,
        modifier = modifier
    ) {
        val buttonModifier =
            Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .aspectRatio(1.0f)

        val lightShadowColor =
            if (MaterialTheme.colors.isLight) LightShadowColor else DarkThemeLightShadowColor
        val darkShadowColor =
            if (MaterialTheme.colors.isLight) DarkShadowColor else DarkThemeDarkShadowColor

        @Composable
        fun Text(text: String, size: TextUnit = 36.sp) {
            Text(
                text = text,
                fontSize = size,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.wrapContentSize(),
                color = LocalContentColor.current
            )
        }

        val primary = MaterialTheme.colors.primary

        @Composable
        fun NeoButton(onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
            NeumorphicButton(
                onClick = onClick,
                modifier = buttonModifier,
                content = content,
                elevation = NeumorphicButtonDefaults.elevation(defaultElevation = 8.dp),
                colors = NeumorphicButtonDefaults.neumorphicButtonColors(
                    lightShadowColor = lightShadowColor,
                    darkShadowColor = darkShadowColor
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(8.dp)
            )
        }

        @Composable
        fun ColorNeoButton(onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
            NeumorphicButton(
                onClick = onClick,
                modifier = buttonModifier,
                content = content,
                elevation = NeumorphicButtonDefaults.elevation(defaultElevation = 8.dp),
                colors = NeumorphicButtonDefaults.neumorphicButtonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    lightShadowColor = lightShadowColor,
                    darkShadowColor = darkShadowColor
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(8.dp)
            )
        }

        val buttons = buttons
        buttons.forEach { resId ->
            when (resId) {
                null -> Spacer(modifier = buttonModifier)
                R.string.backspace -> NeoButton(onClick = { backspace() }) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardBackspace,
                        contentDescription = null,
                        modifier = Modifier.wrapContentSize(),
                        tint = primary
                    )
                }
                R.string.swap -> NeoButton(onClick = { swap() }) {
                    Icon(
                        imageVector = Icons.Outlined.SwapVert,
                        contentDescription = null,
                        modifier = Modifier.wrapContentSize(),
                        tint = primary
                    )
                }
                R.string.all_cleared -> ColorNeoButton(onClick = { clear() }) {
                    Text(text = stringResource(id = resId), size = 20.sp)
                }
                else -> {
                    val char = stringResource(id = resId)
                    NeoButton(onClick = { append(char.first()) }) {
                        Text(text = stringResource(id = resId))
                    }
                }
            }
        }
    }
}


@Composable
private fun AppBar(modifier: Modifier = Modifier) {
    // nav actions
    val actions = @Composable { _: RowScope ->
        val controller = LocalNavController.current
        val onRequestNavigate = {
            val direction = SettingsRoute()
            controller.navigateTo(direction)
        }

        IconButton(
            onClick = onRequestNavigate,
            imageVector = Icons.Outlined.Settings,
            contentDescription = null
        )
    }
    // nav icons
    val navIcon = @Composable {
        IconButton(
            onClick = { /*TODO*/ },
            painter = painterResource(id = R.drawable.ic_handyman),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = Padding.NORMAL)
                .requiredSize(24.dp)
        )
    }

    // title column
    val title =
        @Composable {
            Text(
                text = stringHtmlResource(id = R.string.unit_converter_html),
                fontWeight = FontWeight.Light,
            )
        }

    TopAppBar(
        modifier = modifier, // sdp.
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 0.dp,
        navigationIcon = navIcon,
        title = title,
        actions = actions
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun UnitConverterViewModel.Converters(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colors.onPrimary
    val colors =
        ChipDefaults.filterChipColors(
            backgroundColor = color.copy(alpha = 0.2f),
            selectedBackgroundColor = color.copy(alpha = 0.35f),
            contentColor = color,
            selectedContentColor = color
        )
    val border = BorderStroke(1.dp, color)

    // state to scroll to the selected one.
    val state = rememberLazyListState()
    val padding =
        PaddingValues(
            horizontal = Padding.NORMAL,
            vertical = Padding.NORMAL
        )

    val converters = converters
    val current by converter

    val content: LazyListScope.() -> Unit = {
        items(converters) { converter ->

            val selected = current == converter
            // The chip content
            val content: @Composable RowScope.() -> Unit = {
                Icon(
                    painter = painterResource(id = converter.drawableRes),
                    contentDescription = null,
                    modifier = Modifier.requiredSize(18.dp)
                )

                val text = stringResource(id = converter.title)
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    }

                Label(
                    text = text,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            // actual chip.
            FilterChip(
                onClick = { converter(converter) },
                selected = selected,
                modifier = Modifier.padding(horizontal = 4.dp),
                colors = colors,
                border = if (selected) border else null,
                content = content
            )
        }
    }

    Surface(
        color = MaterialTheme.colors.primary,
        shape = RoundedCornerShape(bottomStartPercent = 100, bottomEndPercent = 0),
        modifier = modifier
    ) {
        LazyRow(
            contentPadding = padding,
            state = state,
            content = content,
            modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)
        )
    }
}


@Composable
private fun DropDown(
    modifier: Modifier = Modifier,
    values: Map<Int, List<Unet>>,
    selected: Unet,
    expanded: Boolean = false,
    OnUnitSelected: (new: Unet) -> Unit,
    field: @Composable () -> Unit
) {

    val dividerAlpha = 0.1f

    val listContent: @Composable ColumnScope.() -> Unit =
        @Composable {

            // the colors
            val primary = MaterialTheme.colors.primary
            val container = MaterialTheme.colors.primaryContainer
            val secondary = MaterialTheme.colors.secondary

            values.forEach { (id, list) ->
                // emit the title of the group
                Label(
                    text = stringResource(id = id),
                    modifier = Modifier.padding(
                        start = Padding.NORMAL,
                        top = Padding.SMALL,
                        bottom = Padding.SMALL
                    ),
                    fontWeight = FontWeight.SemiBold,
                    color = secondary,
                    maxLines = 2
                )
                Divider(
                    color = secondary.copy(dividerAlpha),
                )

                list.forEach { value ->
                    val isChecked = selected == value
                    val color = if (isChecked) primary else LocalContentColor.current
                    val itemContent: @Composable RowScope.() -> Unit =
                        @Composable {
                            CompositionLocalProvider(LocalContentColor provides color) {
                                Text(
                                    text = stringResource(id = value.code),
                                    style = MaterialTheme.typography.body1,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(id = value.title),
                                    modifier = Modifier.padding(start = Padding.NORMAL),
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }
                    //TODO find a way to support selected.
                    DropdownMenuItem(
                        onClick = { OnUnitSelected(value) },
                        modifier = if (isChecked) Modifier.background(color = container) else Modifier,
                        content = itemContent
                    )

                    // show divider only when checked.
                    if (isChecked)
                        Divider(color = color)
                }
            }
        }

    val content: @Composable ExposedDropdownMenuBoxScope.() -> Unit =
        @Composable {
            // The field of that this menu exposes
            field()
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { OnUnitSelected(selected) },
                content = listContent,
                modifier = Modifier.exposedDropdownSize(true)
            )
        }


    //The actual menu.
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { /*expanded = it*/ },
        content = content
    )
}


@Composable
private fun UnitConverterViewModel.UnitFrom(modifier: Modifier = Modifier) {
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

        val converter by converter
        val unit by fromUnit
        var expanded by rememberState(initial = false)
        val visualTransformation by NumberFormatTransformation

        val field =
            @Composable {
                val text by value
                OutlinedTextField(
                    value = text,
                    onValueChange = { },
                    label = { Text(stringResource(id = unit.title)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) {
                            expanded = true
                        }
                    },
                    textStyle = MaterialTheme.typography.h4.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                    readOnly = true,
                    singleLine = true,
                    enabled = true,
                    visualTransformation = visualTransformation,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(percent = 10),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                )
            }

        val units = remember(converter.uuid) {
            converter.units.groupBy { it.group }
        }

        DropDown(
            modifier = Modifier.padding(start = Padding.MEDIUM),
            values = units,
            selected = unit,
            expanded = expanded,
            OnUnitSelected = { from(it); expanded = false },
            field = field
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun UnitConverterViewModel.UnitTo(modifier: Modifier = Modifier) {
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

        val converter by converter
        val unit by toUnit

        val result by result
        val text by remember {
            derivedStateOf {
                val value = result.doubleValue()
                NumUtil.doubleToString(value, 12, 2)!!
            }
        }

        var expanded by rememberState(initial = false)
        val units = remember(converter.uuid) {
            converter.units.groupBy { it.group }
        }
        val visualTransformation by NumberFormatTransformation

        val field =
            @Composable {
                TextField(
                    readOnly = true,
                    value = text,
                    onValueChange = { },
                    label = { Text(stringResource(id = unit.title)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) {
                            expanded = true
                        }
                    },
                    textStyle = MaterialTheme.typography.h4,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = visualTransformation,
                    shape = RoundedCornerShape(topStartPercent = 10, topEndPercent = 10),
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                )
            }

        DropDown(
            modifier = Modifier.padding(start = Padding.MEDIUM),
            values = units,
            selected = unit,
            expanded = expanded,
            OnUnitSelected = { toUnit(it); expanded = false },
            field = field
        )
    }
}


@Composable
private fun UnitConverterViewModel.AboutEquals(modifier: Modifier = Modifier) {
    val map by more
    val context = LocalContext.current

    val color = LocalContentColor.current.copy(ContentAlpha.disabled)
    val text by remember {
        derivedStateOf {
            buildAnnotatedString {
                val formatter = DecimalFormat("###,###.##")
                map.forEach { (u, v) ->
                    val string = formatter.format(v.doubleValue())
                    val code = context.getString(u.code)

                    append(string)
                    val style = SpanStyle(color = color, fontStyle = FontStyle.Italic)
                    withStyle(style) {
                        append(" $code   ")
                    }
                }
            }
        }
    }

    Text(
        text = text,
        modifier = Modifier
            .horizontalScroll(state = rememberScrollState())
            .then(modifier),
        style = MaterialTheme.typography.body1,
        fontWeight = FontWeight.W400,
        maxLines = 1,
    )
}


@Route(navGraph = RouteNavGraph(start = true))
@Composable
fun UnitConverter(viewModel: UnitConverterViewModel) {
    with(viewModel) {
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

        val background = MaterialTheme.colors.primary
        val isLight = false

        val modifier = Modifier.statusBarsPadding2(color = background, isLight)
        LayoutVertical(modifier)
    }
}


@Composable
private fun UnitConverterViewModel.ModelVerticalLayout(modifier: Modifier = Modifier) {
    val state = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val sheet: @Composable ColumnScope.() -> Unit =
        @Composable {
            NumPad(
                modifier = Modifier
                    .padding(horizontal = 36.dp, vertical = Padding.MEDIUM)
                    .wrapContentSize(Alignment.BottomCenter)
            )
        }

    ModalBottomSheetLayout(
        sheetContent = sheet,
        sheetState = state,
        modifier = modifier,
        sheetBackgroundColor = MaterialTheme.colors.background,
        scrimColor = Color.Transparent
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerticalTop()

            // capture unclaimed space
            Spacer(modifier = Modifier.weight(1f))

            val scope = rememberCoroutineScope()
            val onButtonClick = {
                scope.launch {
                    if (state.isVisible) state.hide() else state.show()
                }
                Unit
            }

            Button(
                onClick = onButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Padding.LARGE)
            ) {
                Icon(imageVector = Icons.Outlined.Keyboard, contentDescription = null)
            }
        }
    }
}

@Composable
private fun UnitConverterViewModel.FullVerticalLayout(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalTop()
        // capture unclaimed space
        Spacer(modifier = Modifier.weight(1f))

        NumPad(
            modifier = Modifier
                .padding(horizontal = 36.dp)
                .wrapContentSize(Alignment.BottomCenter)
        )
    }
}


@Composable
private fun UnitConverterViewModel.LayoutVertical(modifier: Modifier = Modifier) {
    BoxWithConstraints {
        if (this.maxHeight < 600.dp)
            ModelVerticalLayout(modifier)
        else
            FullVerticalLayout(modifier = modifier)
    }
}

context (UnitConverterViewModel, ColumnScope)
        @Composable
        private fun VerticalTop() {
    AppBar()
    Converters()

    val customTextSelectionColors = remember {
        TextSelectionColors(
            handleColor = Color.Transparent,
            backgroundColor = Color.Transparent
        )
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides customTextSelectionColors
    ) {
        UnitFrom(
            modifier = Modifier.padding(
                horizontal = 24.dp,
                vertical = Padding.NORMAL
            )
        )
        UnitTo(
            modifier = Modifier.padding(
                //  top = 12.dp,
                start = 24.dp,
                end = 24.dp
            )
        )
    }

    Text(
        text = "About Equals",
        style = MaterialTheme.typography.h4,
        modifier = Modifier
            .padding(start = Padding.LARGE, top = Padding.NORMAL)
            .align(Alignment.Start),
        fontWeight = FontWeight.Light,
    )

    AboutEquals(
        Modifier
            .padding(start = Padding.LARGE, top = Padding.SMALL)
            .align(Alignment.Start),
    )
}


