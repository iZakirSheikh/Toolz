package com.prime.toolz2.common.compose


import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView

private const val TAG = "View"

@Composable
fun Ticker(
    modifier: Modifier = Modifier,
    charList: String = TickerUtils.provideNumberList(),
    color: Color = LocalContentColor.current,
    duration: Long = 500,
    prefScrollingDirection: TickerView.ScrollingDirection = TickerView.ScrollingDirection.ANY,
    size: TextUnit = 16.sp,
    text: String,
    font: Typeface = Typeface.DEFAULT
) {
    val sizePx = with(LocalDensity.current) { size.toPx() }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TickerView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }) {
        it.setCharacterLists(charList)
        it.textColor = color.toArgb()
        it.textSize = sizePx
        it.animationDuration = duration
        it.setPreferredScrollingDirection(prefScrollingDirection)
        it.textSize = sizePx
        it.typeface = font
        it.text = text
    }
}


@Composable
fun Seekbar(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    thumb: Drawable? = null,
    progress: Float,
    onValueChange: (Float) -> Unit,
) {
    var isDragging by androidx.compose.runtime.remember {
        mutableStateOf(false)
    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            SeekBar(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                max = 1000
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser)
                            onValueChange(progress.toFloat() / 1000)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        isDragging = true
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        isDragging = false
                    }
                })

            }
        }
    ) {
        if (!isDragging)
            it.progress = ((progress * 1000).toInt())
        if (thumb != null)
            it.thumb = thumb
        // it.setBackgroundColor(color.toArgb())
        it.progressTintList = ColorStateList.valueOf(color.toArgb())
        it.progressDrawable?.let {
            val layer = it as LayerDrawable
            layer.getDrawable(0).setTint(color.copy(0.45f).toArgb())
        }
    }
}