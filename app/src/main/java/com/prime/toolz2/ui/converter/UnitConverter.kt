package com.prime.toolz2.ui.converter

import LocalWindowSizeClass
import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ConfigurationHelper
import androidx.core.os.ConfigurationCompat
import com.prime.toolz2.R
import com.prime.toolz2.common.compose.*
import com.prime.toolz2.core.converter.Unet
import com.prime.toolz2.core.math.NumUtil
import com.prime.toolz2.theme.padding
import com.prime.toolz2.theme.primaryContainer
import com.primex.widgets.Divider
import com.primex.widgets.Material
import com.primex.widgets.VerticalGrid
import com.primex.widgets.isLight
import cz.levinzonr.saferoute.core.annotations.Route
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

@Composable
private fun UnitConverterViewModel.AppBar(modifier: Modifier = Modifier) {
    val converter by converter
    // nav actions
    val actions = @Composable { _: RowScope ->

        var showDialog by rememberState(initial = false)

        IconButton(
            onClick = { showDialog = !showDialog },
            imageVector = Icons.Outlined.Settings,
            contentDescription = null
        )

        SettingsDialog(expanded = showDialog) {
            showDialog = false
        }
    }

    // nav icons
    val navIcon = @Composable {
        IconButton(
            onClick = { /*TODO*/ },
            painter = painterResource(id = converter.drawableRes),
            contentDescription = null
        )
    }

    // title column
    val title = @Composable {
        Column {
            Text(
                text = stringHtmlResource(id = R.string.unit_converter_html),
                fontWeight = FontWeight.Light,
            )
            // converter title
            Crossfade(targetState = converter) { value ->
                Text(
                    text = stringResource(id = value.title),
                    style = Material.typography.caption,
                    color = LocalContentColor.current.copy(ContentAlpha.disabled),
                )
            }
        }
    }

    TopAppBar(
        modifier = modifier, // sdp.
        backgroundColor = Color.Transparent,
        contentColor = LocalContentColor.current,
        elevation = 0.dp,
        navigationIcon = navIcon,
        title = title,
        actions = actions
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun UnitConverterViewModel.Converters(modifier: Modifier = Modifier) {
    val converters = converters
    val current by converter

    val color = LocalContentColor.current
    val colors =
        ChipDefaults.filterChipColors(
            backgroundColor = color.copy(alpha = 0.1f),
            selectedBackgroundColor = color.copy(alpha = 0.15f),
            contentColor = color,
            selectedContentColor = color
        )
    val border = BorderStroke(1.dp, color)

    // state to scroll to the selected one.
    val state = rememberLazyListState()

    val padding =
        PaddingValues(
            horizontal = Material.padding.Normal,
            vertical = Material.padding.Small
        )

    LazyRow(
        modifier = modifier,
        contentPadding = padding,
        state = state
    ) {
        items(converters) { converter ->
            val selected = current == converter

            // The chip content
            val content: @Composable RowScope.() -> Unit = {
                Icon(
                    painter = painterResource(id = converter.drawableRes),
                    contentDescription = null
                )

                Text(
                    text = stringResource(id = converter.title).replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            // actual chip.
            FilterChip(
                onClick = { converter(converter) },
                selected = selected,
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                colors = colors,
                border = if (selected) border else null,
                content = content
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun UnitConverterViewModel.DropDown(
    modifier: Modifier = Modifier,
    values: Map<Int, List<Unet>>,
    selected: Unet,
    expanded: Boolean = false,
    OnUnitSelected: (new: Unet) -> Unit,
    field: @Composable () -> Unit
) {
    val content: @Composable ExposedDropdownMenuBoxScope.() -> Unit = {
        // The field of that this menu enxloses
        field()

        // the colors
        val primary = Material.colors.primary
        val container = Material.colors.primaryContainer
        val secondary = Material.colors.secondary
        val padding = Material.padding

        val content: @Composable ColumnScope.() -> Unit = {
            values.forEach { (id, list) ->
                // emit the title of the group
                Text(
                    text = stringResource(id = id),
                    modifier = Modifier.padding(
                        horizontal = padding.Normal,
                        vertical = padding.Small
                    ),
                    style = Material.typography.h5,
                    color = secondary,
                )

                Divider(color = secondary.copy(ContentAlpha.Divider))

                list.forEach { value ->

                    val isChecked = selected == value
                    val color = if (isChecked) primary else LocalContentColor.current

                    //TODO find a way to support selected.
                    DropdownMenuItem(
                        onClick = { OnUnitSelected(value) },
                        modifier = if (isChecked) Modifier.background(color = container) else Modifier
                    ) {
                        CompositionLocalProvider(LocalContentColor provides color) {
                            Text(
                                text = stringResource(id = value.code),
                                style = Material.typography.body1,
                                fontStyle = FontStyle.Italic,
                            )
                            Text(
                                text = stringResource(id = value.title),
                                modifier = Modifier.padding(start = Material.padding.Normal),
                            )
                        }
                    }
                    Divider(color = color.copy(if (isChecked) 1.0f else ContentAlpha.Divider))
                }
            }

        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { OnUnitSelected(selected) },
            content = content,
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun UnitConverterViewModel.UnitFrom(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        //Header
        val primary = Material.colors.primary
        Text(
            text = "FROM",
            style = Material.typography.overline,
            modifier = Modifier
                .rotate(false),
        )

        val converter by converter
        val unit by fromUnit

        var expanded by rememberState(initial = false)

        val field = @Composable {
            val text by value
            OutlinedTextField(
                readOnly = true,
                value = text,
                onValueChange = { },
                label = { Text(stringResource(id = unit.title)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) {
                        expanded = true
                    }
                },
                textStyle = Material.typography.h4.copy(
                    fontWeight = FontWeight.Medium,
                ),
                singleLine = true,
                enabled = true,
                visualTransformation = NumberFormatterTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(percent = 10),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
        }

        val units = remember(converter.uuid) {
            converter.units.groupBy { it.group }
        }

        DropDown(
            values = units,
            selected = unit,
            OnUnitSelected = { from(it); expanded = false },
            modifier = Modifier.padding(start = Material.padding.Medium),
            expanded = expanded,
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
        val primary = Material.colors.primary
        Text(
            text = "EQUALS TO",
            style = Material.typography.overline,
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

        val field = @Composable {
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
                textStyle = Material.typography.h4,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = NumberFormatterTransformation(),
                shape = RoundedCornerShape(topStartPercent = 10, topEndPercent = 10),
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
        }
        DropDown(
            values = units,
            selected = unit,
            OnUnitSelected = { toUnit(it); expanded = false },
            expanded = expanded,
            modifier = Modifier.padding(start = Material.padding.Medium),
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
        style = Material.typography.body1,
        fontWeight = FontWeight.W400,
        maxLines = 1,
    )
}

val buttons = arrayOf(
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
private fun UnitConverterViewModel.NumPad(modifier: Modifier = Modifier) {
    VerticalGrid(
        columns = 4,
        modifier = modifier
    ) {
        val childModifier =
            Modifier
                .padding(1.dp)
                .fillMaxWidth()
                .aspectRatio(1.0f)

        val lightShadowColor = if (Material.isLight) Color.White else Color.Black
        val darkShadowColor = if (Material.isLight) Color(0xFFD1D9E6) else Color.White.copy(0.1f)

        @Composable
        fun Text(text: String, size: TextUnit = 44.sp) {
            Text(
                text = text,
                fontSize = size,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.wrapContentSize(),
                color = LocalContentColor.current
            )
        }

        @Composable
        fun ColorNeoButton(onClick: () -> Unit, content: @Composable () -> Unit) {
            NeuButton(
                onClick = onClick,
                modifier = childModifier,
                color = Material.colors.primary,
                onColor = Material.colors.onPrimary,
                content = content,
                lightShadowColor = lightShadowColor,
                darkShadowColor = darkShadowColor,
            )
        }

        @Composable
        fun NeoButton(onClick: () -> Unit, content: @Composable () -> Unit) {
            NeuButton(
                onClick = onClick,
                modifier = childModifier,
                // color = Material.colors.primary,
                //   onColor = Material.colors.onPrimary,
                lightShadowColor = lightShadowColor,
                darkShadowColor = darkShadowColor,
                content = content,
                onColor = Material.colors.primary,
            )
        }

        val buttons = buttons

        buttons.forEach { resId ->
            when (resId) {
                null -> Spacer(modifier = childModifier)
                R.string.backspace -> ColorNeoButton(onClick = { backspace() }) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardBackspace,
                        contentDescription = null,
                        modifier = Modifier.wrapContentSize()
                    )
                }
                R.string.swap -> ColorNeoButton(onClick = { swap() }) {
                    Icon(
                        imageVector = Icons.Outlined.SwapVert,
                        contentDescription = null,
                        modifier = Modifier.wrapContentSize()
                    )
                }
                R.string.all_cleared -> ColorNeoButton(onClick = { clear() }) {
                    Text(text = stringResource(id = resId), size = 24.sp)
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
private fun UnitConverterViewModel.Landscape(modifier: Modifier = Modifier) {
    val padding = Material.padding

    val formula: @Composable ColumnScope.() -> Unit = {
        Converters()

        UnitFrom()

        UnitTo(
            modifier = Modifier.padding(
                top = 8.dp,
            )
        )

        Text(
            text = "About Equals",
            style = Material.typography.h4,
            modifier = Modifier
                .padding(top = padding.Small)
                .align(Alignment.Start),
            fontWeight = FontWeight.Light,
        )

        AboutEquals(
            Modifier
                .padding(start = padding.Large, top = padding.Small)
                .align(Alignment.Start),
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppBar(modifier = Modifier.rotate(false))

        Divider(
            thickness = 2.dp,
            modifier = Modifier
                .rotate(true)
                .padding(
                    horizontal = padding.Large,
                )
        )

        Column(
            modifier = Modifier
                .weight(1.1f)
                .padding(horizontal = Material.padding.Normal),
            content = formula
        )

        NumPad(
            modifier = Modifier.weight(0.9f)
        )
    }
}

@Composable
private fun UnitConverterViewModel.Portrait(modifier: Modifier = Modifier) {
    val padding = Material.padding

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar()

        Divider(
            thickness = 2.dp,
            modifier = Modifier.padding(horizontal = padding.Large, vertical = padding.Small)
        )

        Converters()

        UnitFrom(
            modifier = Modifier.padding(
                top = Material.padding.Small,
                start = 24.dp,
                end = 24.dp
            )
        )

        UnitTo(
            modifier = Modifier.padding(
                top = 12.dp,
                start = 24.dp,
                end = 24.dp
            )
        )

        Text(
            text = "About Equals",
            style = Material.typography.h4,
            modifier = Modifier
                .padding(start = padding.Large, top = padding.Normal)
                .align(Alignment.Start),
            fontWeight = FontWeight.Light,
        )

        AboutEquals(
            Modifier
                .padding(start = padding.Large, top = padding.Small)
                .align(Alignment.Start),
        )

        // capture unclaimed space
        Spacer(modifier = Modifier.weight(1f))

        NumPad(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp, top = padding.Normal)
                .wrapContentSize(Alignment.BottomCenter)
        )
    }
}

@Route
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


        val sWindow = LocalWindowSizeClass.current

        val background = Material.colors.background
        val isLight = Material.colors.isLight

        val modifier = Modifier.statusBarsPadding2(color = background, isLight)

        when (sWindow) {
            WindowSize.COMPACT, WindowSize.MEDIUM -> Portrait(modifier)
            WindowSize.LARGE, WindowSize.X_LARGE -> Landscape(modifier)
        }
    }
}