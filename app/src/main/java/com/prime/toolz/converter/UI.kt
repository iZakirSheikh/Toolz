@file:Suppress("NOTHING_TO_INLINE")

package com.prime.toolz.converter

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.SwapVerticalCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prime.toolz.LocalWindowSizeClass
import com.prime.toolz.LocalsProvider
import com.prime.toolz.Material
import com.prime.toolz.R
import com.prime.toolz.core.ContentAlpha
import com.prime.toolz.core.ContentPadding
import com.prime.toolz.core.billing.Banner
import com.prime.toolz.core.billing.Placement
import com.prime.toolz.core.billing.Product
import com.prime.toolz.core.billing.purchased
import com.prime.toolz.core.converter.Converter
import com.prime.toolz.core.converter.Unet
import com.prime.toolz.core.math.NumUtil
import com.prime.toolz.preference
import com.prime.toolz.purchase
import com.prime.toolz.settings.Settings
import com.primex.core.Text
import com.primex.core.drawHorizontalDivider
import com.primex.core.get
import com.primex.core.rememberState
import com.primex.core.resolve
import com.primex.core.resources
import com.primex.core.rotate
import com.primex.core.stringHtmlResource
import com.primex.core.withSpanStyle
import com.primex.material3.IconButton
import com.primex.material3.Text

private const val TAG = "UnitConverter"

private val NumberFormatTransformation: VisualTransformation
    @Composable get() {
        val separator by preference(Settings.KEY_GROUP_SEPARATOR)
        return VisualTransformation {
            val text = it.text
            val transformed = run {
                // split into respective components.
                // maybe remove this and replace with already formatted text.
                val (w, f, e) = NumUtil.split(text)
                val whole =
                    if (!w.isNullOrBlank()) NumUtil.addThousandSeparators(w, separator) else ""
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
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    state: UnitConverter,
    modifier: Modifier = Modifier
) {
    SmallTopAppBar(
        title = {
            Text(
                text = stringHtmlResource(id = R.string.unit_converter_html),
                style = Material.typography.titleLarge,
                fontWeight = FontWeight.Light
            )
        },
        scrollBehavior = null,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = Material.colorScheme.surfaceColorAtElevation(2.dp)
        ),
        modifier = modifier,
        navigationIcon = {
            // TODO: Make this point to AboutUs Page.
            IconButton(
                icon = Icons.Outlined.Handyman,
                contentDescription = stringResource(id = R.string.app_name),
                onClick = { /*TODO*/ }
            )
        },
        actions = {
            val provider = LocalsProvider.current
            val msg = stringResource(id = R.string.coming_soon_msg)
            com.primex.material3.OutlinedButton(
                label = "ADD",
                modifier = Modifier.scale(0.85f),
                onClick = {
                    provider.snack(msg)
                }
            )

            // More actions.
            IconButton(
                icon = Icons.Outlined.MoreVert,
                contentDescription = "More options",
                onClick = { /*TODO*/ }
            )
        }
    )
}

@Composable
private fun SideBar(
    state: UnitConverter,
    modifier: Modifier = Modifier
) {
    /*TODO: Impl vertical navRail*/
    TopBar(state = state, modifier.rotate(false))
}

@Composable
private fun Converter(
    value: Converter,
    checked: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (checked) Material.colorScheme.primaryContainer else Color.Transparent
    val contentColor =
        if (checked) Material.colorScheme.primary else Material.colorScheme.onSurface.copy(0.7f)
    val stroke =
        if (checked) null else BorderStroke(1.dp, Material.colorScheme.onSurface.copy(0.12f))
    Surface(
        onClick = onTap,
        modifier = modifier.semantics { role = Role.Button },
        enabled = !checked,
        shape = Material.shapes.medium,
        color = containerColor,
        contentColor = contentColor,
        border = stroke
    ) {
        Column(
            Modifier
                .defaultMinSize(
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = ButtonDefaults.MinHeight
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Primary Icon.
            Icon(
                painter = painterResource(id = value.drawableRes),
                contentDescription = "icon"
            )
            // Text label
            Text(
                text = value.title.get,
                modifier = Modifier.padding(top = ButtonDefaults.IconSpacing),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
@NonRestartableComposable
private fun Converters(
    value: List<Converter>,
    onTap: (value: Converter) -> Unit,
    current: Converter,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyRow(
        contentPadding = contentPadding,
        modifier = modifier,
        content = {
            items(value) { item ->
                val selected = item.uuid == current.uuid
                Converter(
                    value = item,
                    checked = selected,
                    onTap = { onTap(item) },
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    )
}

@Composable
@NonRestartableComposable
private fun MenuItem(
    value: Unet,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
    checked: Boolean = false,
) {
    val container = if (checked) Material.colorScheme.secondaryContainer else Color.Transparent
    val color = if (checked) Material.colorScheme.secondary else LocalContentColor.current
    val bg =
        if (checked)
            Modifier.drawBehind {
                drawRect(brush = Brush.horizontalGradient(listOf(container, Color.Transparent)))
                drawRect(color = color, size = size.copy(width = 4.dp.toPx()))
            }
        else
            Modifier
    DropdownMenuItem(
        modifier = modifier.then(bg),
        onClick = onItemClick,
        enabled = !checked,
        leadingIcon = {
            Text(
                text = value.code.get,
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = color
            )
        },
        text = {
            Text(
                text = value.title.get,
                modifier = Modifier.padding(start = ContentPadding.normal),
                style = MaterialTheme.typography.bodySmall,
                color = color,
            )
        }
    )
}

@Composable
private inline fun ColumnScope.Menu(
    selected: Unet,
    crossinline onItemClick: (item: Unet?) -> Unit,
    values: Map<CharSequence, List<Unet>>,
) {
    val primary = Material.colorScheme.primary
    values.forEach { (title, list) ->
        // list header
        Text(
            text = title,
            fontWeight = FontWeight.Light,
            maxLines = 2,
            color = primary,
            modifier = Modifier
                .padding(
                    start = ContentPadding.normal,
                    top = ContentPadding.normal,
                    end = ContentPadding.normal,
                    bottom = ContentPadding.medium
                )
                .fillMaxWidth()
                .drawHorizontalDivider(color = primary)
                .padding(bottom = ContentPadding.medium),
            style = Material.typography.headlineLarge
        )

        // emit the list of this title
        list.forEach { value ->
            MenuItem(
                value = value,
                checked = selected == value,
                onItemClick = {
                    onItemClick(value)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private inline fun MenuBox(
    expanded: Boolean,
    selected: Unet,
    crossinline onItemClick: (item: Unet?) -> Unit,
    values: Map<CharSequence, List<Unet>>,
    modifier: Modifier = Modifier,
    crossinline field: @Composable ExposedDropdownMenuBoxScope.() -> Unit,
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { /*expanded = it*/ },
        content = {
            // The field of that this menu exposes
            field()
            // Build the Menu
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onItemClick(null) },
                modifier = Modifier.exposedDropdownSize(true),
                content = {
                    Menu(selected = selected, onItemClick = onItemClick, values = values)
                }
            )
        }
    )
}

private val FIELD_MIN_HEIGHT = 94.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ValueField(
    value: String,
    onValueChange: (value: TextFieldValue) -> Unit,
    visualTransformation: VisualTransformation,
    checked: Unet,
    values: Map<CharSequence, List<Unet>>,
    onItemClick: (item: Unet?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Header
        Text(
            text = "FROM",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.rotate(false),
        )

        // Filed
        var expanded by rememberState(initial = false)
        MenuBox(
            expanded = expanded,
            selected = checked,
            onItemClick = { onItemClick(it); expanded = false },
            values = values,
            modifier = Modifier.padding(start = ContentPadding.medium),
        ) {
            OutlinedTextField(
                value = TextFieldValue(value, TextRange(value.length)),
                onValueChange = onValueChange,
                readOnly = false,
                singleLine = true,

                // a workaround for keyboard issue.
                // using readOnly introduces another issue.
                enabled = !expanded,
                visualTransformation = visualTransformation,
                shape = RoundedCornerShape(percent = 10),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),

                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    cursorColor = Color.Transparent,
                    focusedTextColor = Material.colorScheme.onSurface.copy(0.76f),
                    unfocusedTextColor = Material.colorScheme.onSurface.copy(ContentAlpha.medium),
                ),

                label = { Text(text = checked.title.get) },

                trailingIcon = {
                    val rotate by animateFloatAsState(targetValue = if (expanded) 0f else 180f)
                    IconButton(
                        onClick = { expanded = !expanded },
                        icon = Icons.Outlined.KeyboardArrowUp,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotate)
                    )
                },

                textStyle = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .heightIn(min = FIELD_MIN_HEIGHT),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultField(
    value: String,
    visualTransformation: VisualTransformation,
    checked: Unet,
    values: Map<CharSequence, List<Unet>>,
    onItemClick: (item: Unet?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Header
        Text(
            text = "EQUALS TO",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .rotate(false),
        )

        var expanded by rememberState(initial = false)
        MenuBox(
            expanded = expanded,
            selected = checked,
            onItemClick = { onItemClick(it); expanded = false },
            values = values,
            modifier = Modifier.padding(start = ContentPadding.medium),
        ) {
            TextField(
                readOnly = true,
                value = value,
                onValueChange = { },
                singleLine = true,
                visualTransformation = visualTransformation,
                shape = RoundedCornerShape(topStartPercent = 10, topEndPercent = 10),
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),


                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .heightIn(min = FIELD_MIN_HEIGHT),

                label = { Text(text = checked.title.get) },

                trailingIcon = {
                    val rotate by animateFloatAsState(targetValue = if (expanded) 0f else 180f)
                    IconButton(
                        onClick = { expanded = !expanded },
                        icon = Icons.Outlined.KeyboardArrowUp,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotate)
                    )
                },
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    letterSpacing = 0.35.sp,
                    fontWeight = FontWeight.Normal
                ),
            )
        }
    }
}

@Composable
private fun Actions(
    state: UnitConverter,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {

        OutlinedButton(
            onClick = { state.swap() },
            border = BorderStroke(1.dp, Material.colorScheme.onSurface.copy(0.12f)),
            modifier = Modifier
                .padding(start = ContentPadding.normal)
                .scale(0.85f)
        ) {
            Icon(imageVector = Icons.Outlined.SwapVerticalCircle, contentDescription = "SWAP")
            com.primex.material3.Text(
                text = "SWAP",
                Modifier.padding(start = ButtonDefaults.IconSpacing)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            icon = Icons.Outlined.Clear,
            contentDescription = "Clear",
            onClick = { state.clear() },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = Material.colorScheme.primary
            )
        )

        val clipboard = LocalClipboardManager.current
        val resources = LocalContext.resources
        IconButton(
            icon = Icons.Outlined.FileCopy,
            contentDescription = "Copy",
            onClick = { with(state) { resources.copy(clipboard) } },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = Material.colorScheme.primary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutEquals(
    values: Map<Text, String>,
    modifier: Modifier = Modifier,
) {
    val color = LocalContentColor.current
   com.primex.material3.Text(
        modifier = modifier,
        text = buildAnnotatedString {
            values.forEach { (text, value) ->
                // append value
                append(value)
                // append space
                append(" ")
                // append code
                val code = text.get
                withSpanStyle(color.copy(ContentAlpha.medium), fontStyle = FontStyle.Italic) {
                    append(code)
                    append("    ")
                }
            }
        },
        style = MaterialTheme.typography.labelLarge,
        maxLines = 1,
        color = color
    )
}

@Composable
private inline fun ColumnScope.TextFields(
    state: UnitConverter
) {
    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(Color.Transparent, Color.Transparent)
    ) {
        val visualTransformation = NumberFormatTransformation
        val resources = LocalContext.current.resources
        val values = remember(state.converter.uuid) {
            state.converter.units.groupBy { resources.resolve(it.group) }
        }

        ValueField(
            modifier = Modifier
                .padding(
                    top = ContentPadding.normal,
                    start = 24.dp,
                    end = 24.dp
                ),
            visualTransformation = visualTransformation,
            values = values,
            checked = state.fromUnit,
            value = state.value,
            onItemClick = { if (it != null) state.fromUnit = it },
            onValueChange = { state.value = it.text }
        )

        ResultField(
            modifier = Modifier.padding(
                top = ContentPadding.normal,
                start = 24.dp,
                end = 24.dp
            ),
            visualTransformation = visualTransformation,
            values = values,
            checked = state.toUnit,
            value = state.result,
            onItemClick = { if (it != null) state.toUnit = it },
        )
    }
}

@Composable
fun Compact(
    state: UnitConverter,
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = { TopBar(state) }, modifier = modifier) {
        Column(
            // Add vertical scroll in case it is not fitting properly.
            Modifier
                .padding(it)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            Converters(
                value = state.converters,
                modifier = Modifier
                    .sizeIn(minHeight = 110.dp)
                    .padding(top = ContentPadding.large),
                contentPadding = PaddingValues(horizontal = 24.dp),
                current = state.converter,
                onTap = {
                    state.converter = it
                }
            )

            TextFields(state = state)

            Actions(
                state = state,
                Modifier
                    .align(Alignment.End)
                    .padding(top = ContentPadding.normal)
                    .padding(horizontal = ContentPadding.normal)
            )

            Text(
                text = "About Equals",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .padding(
                        start = ContentPadding.xLarge,
                        top = 22.dp
                    )
                    .align(Alignment.Start),
                fontWeight = FontWeight.Light,
            )

            AboutEquals(
                state.more,
                modifier = Modifier
                    .padding(horizontal = 28.dp, vertical = ContentPadding.medium),
            )
            // Ad Banner
            val purchase by purchase(id = Product.DISABLE_ADS)
            if (!purchase.purchased)
                Banner(
                    placementID = Placement.BANNER_SETTINGS,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
        }
    }
}

@Composable
private fun Expanded(
    state: UnitConverter,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        SideBar(state = state)
        Converters(
            value = state.converters,
            modifier = Modifier
                .rotate(false)
                .sizeIn(minHeight = 110.dp)
                .padding(top = ContentPadding.large),
            contentPadding = PaddingValues(horizontal = 24.dp),
            current = state.converter,
            onTap = {
                state.converter = it
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .systemBarsPadding()
                .imePadding()
        ) {
            TextFields(state = state)
            Actions(
                state = state,
                Modifier
                    .padding(top = ContentPadding.small)
                    .padding(horizontal = ContentPadding.normal)
            )
            AboutEquals(
                state.more,
                modifier = Modifier
                    .padding(horizontal = 28.dp, vertical = ContentPadding.normal),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
@NonRestartableComposable
private fun Medium(state: UnitConverter, modifier: Modifier = Modifier) {
    Compact(state = state, modifier)
}

@Composable
@NonRestartableComposable
fun UnitConverter(state: UnitConverter) {
    when (LocalWindowSizeClass.current.widthSizeClass) {
        WindowWidthSizeClass.Compact -> Compact(state = state)
        WindowWidthSizeClass.Medium -> Medium(state = state)
        WindowWidthSizeClass.Expanded -> Expanded(state = state)
    }
}