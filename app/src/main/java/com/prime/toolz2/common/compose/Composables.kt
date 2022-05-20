package com.prime.toolz2.common.compose


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InternalFoundationTextApi
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.TextDelegate
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.primex.widgets.Material
import com.primex.widgets.TextInputField
import me.nikhilchaudhari.library.neumorphic
import me.nikhilchaudhari.library.shapes.Punched


@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    imageVector: ImageVector,
    contentDescription: String?,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        Icon(imageVector = imageVector, contentDescription = contentDescription)
    }
}


@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    bitmap: ImageBitmap,
    contentDescription: String?,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        Icon(bitmap = bitmap, contentDescription = contentDescription)
    }
}

@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    painter: Painter,
    contentDescription: String?,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        Icon(painter = painter, contentDescription = contentDescription)
    }
}


@Composable
fun ColoredOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = null,
    shape: Shape = RoundedCornerShape(50),
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = Material.colors.primary,
        disabledContentColor = Material.colors.primary.copy(ContentAlpha.disabled),
        backgroundColor = Color.Transparent
    ),
    border: BorderStroke? = BorderStroke(
        2.dp,
        color = colors.contentColor(enabled = enabled).value
    ),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
        content = content
    )
}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(50),
    elevation: Dp = 4.dp,
    color: Color = MaterialTheme.colors.surface,
    placeholder: String? = null,
    keyboardActions: KeyboardActions = KeyboardActions(),
    trailingIcon: @Composable (() -> Unit)? = null,
    query: String,
    onQueryChanged: (query: String) -> Unit,
) {
    Surface(
        shape = shape,
        modifier = Modifier
            .scale(0.85f)
            .then(modifier),
        elevation = elevation,
        color = color,
    ) {
        TextInputField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = placeholder,
            leadingIcon = Icons.Default.Search,
            trailingIcon = trailingIcon,
            keyboardActions = keyboardActions
        )
    }
}

private const val TAG = "Widgets"

@Deprecated("Not Recommended!!", level = DeprecationLevel.HIDDEN)
@OptIn(InternalFoundationTextApi::class)
private fun Modifier.AutoSize(
    upper: TextUnit,
    lower: TextUnit,
    style: MutableState<TextStyle>,
    text: AnnotatedString,
) =
    composed {

        var layoutSize by rememberState(initial = IntSize.Zero)
        // update the size information.


        var textStyle by style

        val delgate = TextDelegate(
            text = text,
            style = textStyle,
            maxLines = 1,
            softWrap = true,
            overflow = TextOverflow.Clip,
            density = LocalDensity.current,
            fontFamilyResolver = LocalFontFamilyResolver.current,
            placeholders = emptyList()
        )

        val hasVisualOverflow = delgate.layout(
            constraints = Constraints.fixed(layoutSize.width, layoutSize.height),
            LocalLayoutDirection.current
        ).hasVisualOverflow

        val fontSize = textStyle.fontSize

        val size = fontSize.value


        val new = when {
            hasVisualOverflow && size > lower.value -> size - 1
            size < upper.value && !hasVisualOverflow -> size + 1
            else -> size
        }


        Log.i(TAG, "AutoSize: $text $hasVisualOverflow   $layoutSize")

        textStyle = textStyle.copy(fontSize = new.sp)
        // return this.
        this.onGloballyPositioned {
            layoutSize = it.size
            Log.i(TAG, "AutoSize:OnPositined ${it.size}")
        }
    }


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NeuButton(
    modifier: Modifier = Modifier,
    color: Color = Material.colors.background,
    onColor: Color = LocalContentColor.current,
    corner: Dp = 12.dp,
    darkShadowColor: Color = Color(0xFFD1D9E6),
    lightShadowColor: Color = Color.White,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .padding(4.dp)
            .neumorphic(
                neuShape = Punched.Rounded(radius = corner),
                darkShadowColor = darkShadowColor,
                lightShadowColor = lightShadowColor
            ),
        content = content,
        onClick = onClick,
        // disable card elevation.
        elevation = 0.dp,
        color = color,
        shape = RoundedCornerShape(corner),
        contentColor = onColor
    )
}