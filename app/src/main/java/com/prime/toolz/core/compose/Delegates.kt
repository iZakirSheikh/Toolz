package com.prime.toolz.core.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.PlatformParagraphStyle
import androidx.compose.ui.text.PlatformSpanStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.prime.toolz.core.ContentAlpha
import com.primex.core.Text

/**
 * A simple `[IconButton] composable that takes [painter] as content instead of content composable.
 * @see IconButton
 */
@Composable
inline fun IconButton(
    icon: Painter,
    contentDescription: String?,
    noinline onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    IconButton(
        onClick = onClick, modifier, enabled, colors, interactionSource
    ) {
        Icon(painter = icon, contentDescription = contentDescription)
    }
}

/**
 * @see IconButton
 */
@Composable
inline fun IconButton(
    icon: ImageVector,
    contentDescription: String?,
    noinline onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    IconButton(
        rememberVectorPainter(image = icon),
        contentDescription,
        onClick,
        modifier,
        enabled,
        colors,
        interactionSource
    )
}


/**
 * Returns a string resource if the Text value is not null.
 *
 * @param value The Text value to be used to retrieve the string resource.
 * @return The string resource if the Text value is not null, otherwise null.
 */
@Composable
inline fun stringResource(value: Text?) =
    if (value == null) null else com.primex.core.stringResource(value = value)


/**
 * Returns a Composable function if the condition is true, otherwise returns null.
 *
 * @param condition The boolean condition that determines if the composable function should be returned.
 * @param content The composable function to be returned if the condition is true.
 * @return The composable function if the condition is true, otherwise null.
 */
fun composableOrNull(condition: Boolean, content: @Composable () -> Unit) = when (condition) {
    true -> content
    else -> null
}

/**
 * Returns the current route of the [NavHostController]
 */
val NavHostController.current
    @Composable inline get() = currentBackStackEntryAsState().value?.destination?.route

/**
 * ### Advanced Text Composable
 * An enhanced version of the original `Text` composable that allows direct input of a
 * `CharSequence`, reducing the need for separate composable. This advanced version eliminates the
 * requirement to convert the text to a `String` or `AnnotatedString`.
 *
 * @see androidx.compose.material3.Text
 */
@Composable
inline fun Text(
    text: CharSequence,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    noinline onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    when (text) {
        is AnnotatedString -> androidx.compose.material3.Text(
            text,
            modifier,
            color,
            fontSize,
            fontStyle,
            fontWeight,
            fontFamily,
            letterSpacing,
            textDecoration,
            textAlign,
            lineHeight,
            overflow,
            softWrap,
            maxLines,
            minLines,
            emptyMap(),
            onTextLayout,
            style
        )

        is String -> androidx.compose.material3.Text(
            text,
            modifier,
            color,
            fontSize,
            fontStyle,
            fontWeight,
            fontFamily,
            letterSpacing,
            textDecoration,
            textAlign,
            lineHeight,
            overflow,
            softWrap,
            maxLines,
            minLines,
            onTextLayout,
            style
        )

        else -> error("$text must be either AnnotatedString or String!!")
    }
}


inline fun <R : Any> AnnotatedString.Builder.withSpanStyle(
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    fontSynthesis: FontSynthesis? = null,
    fontFamily: FontFamily? = null,
    fontFeatureSettings: String? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    baselineShift: BaselineShift? = null,
    textGeometricTransform: TextGeometricTransform? = null,
    localeList: LocaleList? = null,
    background: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    shadow: Shadow? = null,
    platformStyle: PlatformSpanStyle? = null,
    drawStyle: DrawStyle? = null,
    block: AnnotatedString.Builder.() -> R
): R = withStyle(
    SpanStyle(
        color,
        fontSize,
        fontWeight,
        fontStyle,
        fontSynthesis,
        fontFamily,
        fontFeatureSettings,
        letterSpacing,
        baselineShift,
        textGeometricTransform,
        localeList,
        background,
        textDecoration,
        shadow,
        platformStyle,
        drawStyle
    ), block
)

inline fun <R : Any> AnnotatedString.Builder.withSpanStyle(
    brush: Brush?,
    alpha: Float = Float.NaN,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    fontSynthesis: FontSynthesis? = null,
    fontFamily: FontFamily? = null,
    fontFeatureSettings: String? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    baselineShift: BaselineShift? = null,
    textGeometricTransform: TextGeometricTransform? = null,
    localeList: LocaleList? = null,
    background: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    shadow: Shadow? = null,
    platformStyle: PlatformSpanStyle? = null,
    drawStyle: DrawStyle? = null,
    block: AnnotatedString.Builder.() -> R
): R = withStyle(
    SpanStyle(
        brush,
        alpha,
        fontSize,
        fontWeight,
        fontStyle,
        fontSynthesis,
        fontFamily,
        fontFeatureSettings,
        letterSpacing,
        baselineShift,
        textGeometricTransform,
        localeList,
        background,
        textDecoration,
        shadow,
        platformStyle,
        drawStyle
    ), block
)


inline fun <R : Any> AnnotatedString.Builder.withParagraphStyle(
    textAlign: TextAlign? = null,
    textDirection: TextDirection? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    textIndent: TextIndent? = null,
    platformStyle: PlatformParagraphStyle? = null,
    lineHeightStyle: LineHeightStyle? = null,
    lineBreak: LineBreak? = null,
    hyphens: Hyphens? = null,
    textMotion: TextMotion? = null,
    crossinline block: AnnotatedString.Builder.() -> R
): R = withStyle(
    ParagraphStyle(
        textAlign,
        textDirection,
        lineHeight,
        textIndent,
        platformStyle,
        lineHeightStyle,
        lineBreak,
        hyphens,
        textMotion
    ), block
)


/**
 * Applies the given text style to the receiver [AnnotatedString.Builder] and executes the provided [block].
 *
 * @param style The text style to apply to the receiver [AnnotatedString.Builder].
 * @param block The block of code to execute within the context of the modified [AnnotatedString.Builder].
 * @return The result of executing the [block].
 */
inline fun <R : Any> AnnotatedString.Builder.withStyle(
    style: TextStyle,
    crossinline block: AnnotatedString.Builder.() -> R
): R = withStyle(style.toParagraphStyle()) {
    withStyle(style.toSpanStyle(), block)
}

@Composable
inline fun rememberVectorPainter(
    image: ImageVector,
    defaultWidth: Dp = image.defaultWidth,
    defaultHeight: Dp = image.defaultHeight,
    viewportWidth: Float = image.viewportWidth,
    viewportHeight: Float = image.viewportHeight,
    name: String = image.name,
    tintColor: Color = image.tintColor,
    tintBlendMode: BlendMode = image.tintBlendMode,
    autoMirror: Boolean = image.autoMirror,
) = rememberVectorPainter(
    defaultWidth = defaultWidth,
    defaultHeight = defaultHeight,
    viewportWidth = viewportWidth,
    viewportHeight = viewportHeight,
    name = name,
    tintColor = tintColor,
    tintBlendMode = tintBlendMode,
    autoMirror = autoMirror,
    content = { _, _ -> RenderVectorGroup(group = image.root) }
)


/**
 * Creates a button composable with the specified properties
 * @param label The label text to be displayed on the button. Can be a [String], [CharSequence], or [AnnotatedString].
 * @param icon The optional icon to be displayed on the button. Accepts a [Painter] representing a vector or image asset.
 * @see androidx.compose.material3.Button
 */
@Composable
inline fun Button(
    label: CharSequence,
    noinline onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    androidx.compose.material3.Button(
        onClick,
        modifier,
        enabled,
        shape,
        colors,
        elevation,
        border,
        contentPadding,
        interactionSource,
        content = {
            if (icon != null) Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = ButtonDefaults.IconSpacing)
            )
            Text(text = label)
        }
    )
}

@Composable
inline fun OutlinedButton(
    label: CharSequence,
    noinline onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.outlinedShape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.OutlinedBorderOpacity)
    ),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    androidx.compose.material3.OutlinedButton(
        onClick,
        modifier,
        enabled,
        shape,
        colors,
        elevation,
        border,
        contentPadding,
        interactionSource
    ) {
        if (icon != null)
            Icon(painter = icon, contentDescription = null)
        Text(
            text = label,
            Modifier.padding(start = ButtonDefaults.IconSpacing)
        )
    }
}