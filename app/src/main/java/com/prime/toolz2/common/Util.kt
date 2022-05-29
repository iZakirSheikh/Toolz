package com.prime.toolz2.common

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.format.DateUtils.*
import android.util.Log
import android.util.TypedValue
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.annotations.Contract
import java.io.Closeable
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.time.ExperimentalTime

private const val TAG = "Utils"

/**
 * Calls the specified function [block] and returns its result if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block] function execution and returning null it as a failure.
 */
inline fun <R> runCatching(tag: String, block: () -> R): R? {
    return try {
        block()
    } catch (e: Throwable) {
        Log.e(tag, "runCatching: ${e.message}")
        null
    }
}

/**
 * Executes the given [block] function on this resource and then closes it down correctly whether an exception
 * is thrown or not.
 *
 * @param block a function to process this [Closeable] resource.
 * @param tag if an exception is thrown null is returned and a message is logged with [tag]
 * @return the result of [block] function invoked on this resource. null if exception.
 */
inline fun <T : Closeable?, R> T.use(tag: String, block: (T) -> R): R? {
    return try {
        use { block(it) }
    } catch (e: Throwable) {
        Log.i(tag, "use: ${e.message}")
        null
    }
}

/**
 * An alternative to [synchronized] using [Mutex]
 */
suspend inline fun <T> synchronised(lock: Mutex, action: () -> T): T {
    return lock.withLock(action = action)
}

object FileUtils {
    /**
     * The Unix separator character.
     */
    const val PATH_SEPARATOR = '/'

    /**
     * The extension separator character.
     * @since 1.4
     */
    private const val EXTENSION_SEPARATOR = '.'

    private const val HIDDEN_PATTERN = "/."

    /**
     * Gets the name minus the path from a full fileName.
     *
     * @param path  the fileName to query
     * @return the name of the file without the path
     */
    fun name(path: String): String = path.substring(path.lastIndexOf(PATH_SEPARATOR) + 1)

    /**
     * @return parent of path.
     */
    fun parent(path: String): String = path.replace("$PATH_SEPARATOR${name(path = path)}", "")

    /**
     * Returns the file extension or null string if there is no extension. This method is a
     * convenience method for obtaining the extension of a url and has undefined
     * results for other Strings.
     * It is Assumed that Url is file
     *
     * @param url  Url of the file
     *
     * @return extension
     */
    fun extension(url: String): String? =
        if (url.contains(EXTENSION_SEPARATOR))
            url.substring(url.lastIndexOf(EXTENSION_SEPARATOR) + 1).lowercase()
        else
            null

    /**
     * Checks if the file or its ancestors are hidden in System.
     */
    @Contract(pure = true)
    fun areAncestorsHidden(path: String): Boolean = path.contains(HIDDEN_PATTERN)
}


object MediaUtils {
    private const val ALBUM_ART_URI: String = "content://media/external/audio/albumart"

    @JvmStatic
    fun composeTrackUri(id: Long) =
        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

    @JvmStatic
    fun composeArtworkUri(albumId: Long): Uri =
        ContentUris.withAppendedId(Uri.parse(ALBUM_ART_URI), albumId)
}

object Utils {
    private const val FLAG_SHORTER = 1 shl 0
    private const val FLAG_CALCULATE_ROUNDED = 1 shl 1
    const val FLAG_SI_UNITS = 1 shl 2
    private const val FLAG_IEC_UNITS = 1 shl 3

    fun checkStoragePermission(context: Context): Boolean {
        // Verify that all required contact permissions have been granted.
        return (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    fun toFormattedDataUnit(bytes: Long): String {
        return formatBytes(bytes, FLAG_IEC_UNITS)
    }

    private fun formatBytes(sizeBytes: Long, flags: Int): String {
        val unit = if (flags and FLAG_IEC_UNITS != 0) 1024 else 1000
        val isNegative = sizeBytes < 0
        var result = if (isNegative) (-sizeBytes).toFloat() else sizeBytes.toFloat()
        var suffix = "B"
        var mult: Long = 1
        if (result > 900) {
            suffix = "KB"
            mult = unit.toLong()
            result /= unit
        }
        if (result > 900) {
            suffix = "MB"
            mult *= unit.toLong()
            result /= unit
        }
        if (result > 900) {
            suffix = "GB"
            mult *= unit.toLong()
            result /= unit
        }
        if (result > 900) {
            suffix = "TB"
            mult *= unit.toLong()
            result /= unit
        }
        if (result > 900) {
            suffix = "PB"
            mult *= unit.toLong()
            result /= unit
        }
        // Note we calculate the rounded long by ourselves, but still let String.format()
        // compute the rounded value. String.format("%f", 0.1) might not return "0.1" due to
        // floating point errors.
        val roundFactor: Int
        val roundFormat: String
        if (mult == 1L || result >= 100) {
            roundFactor = 1
            roundFormat = "%.0f"
        } else if (result < 1) {
            roundFactor = 100
            roundFormat = "%.2f"
        } else if (result < 10) {
            if (flags and FLAG_SHORTER != 0) {
                roundFactor = 10
                roundFormat = "%.1f"
            } else {
                roundFactor = 100
                roundFormat = "%.2f"
            }
        } else { // 10 <= result < 100
            if (flags and FLAG_SHORTER != 0) {
                roundFactor = 1
                roundFormat = "%.0f"
            } else {
                roundFactor = 100
                roundFormat = "%.2f"
            }
        }
        if (isNegative) {
            result = -result
        }
        val roundedString = String.format(roundFormat, result)

        // Note this might overflow if abs(result) >= Long.MAX_VALUE / 100, but that's like 80PB so
        // it's okay (for now)...
        val roundedBytes =
            if (flags and FLAG_CALCULATE_ROUNDED == 0) 0 else Math.round(result * roundFactor)
                .toLong() * mult / roundFactor
        return "$roundedString $suffix"
    }


    /**
     * @return: String representation of Time like 24:34 i.e., 24min and 128 secs.
     */
    @OptIn(ExperimentalTime::class)
    fun formatAsTime(mills: Long): String {
        var minutes: Long = mills / 1000 / 60
        val seconds: Long = mills / 1000 % 60
        return if (minutes < 60) String.format(
            Locale.getDefault(),
            "%01d:%02d",
            minutes,
            seconds
        ) else {
            val hours = minutes / 60
            minutes %= 60
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        }
    }

    /**
     * Returns a string describing 'time' as a time relative to 'now'.
     * <p>
     * Time spans in the past are formatted like "42 minutes ago". Time spans in
     * the future are formatted like "In 42 minutes".
     * <p>
     * Can use {@link #FORMAT_ABBREV_RELATIVE} flag to use abbreviated relative
     * times, like "42 mins ago".
     *
     * @param time the time to describe, in milliseconds
     * @param now the current time in milliseconds
     * @param minResolution the minimum timespan to report. For example, a time
     *            3 seconds in the past will be reported as "0 minutes ago" if
     *            this is set to MINUTE_IN_MILLIS. Pass one of 0,
     *            MINUTE_IN_MILLIS, HOUR_IN_MILLIS, DAY_IN_MILLIS,
     *            WEEK_IN_MILLIS
     * @param flags a bit mask of formatting options, such as
     *            {@link #FORMAT_NUMERIC_DATE} or
     *            {@link #FORMAT_ABBREV_RELATIVE}
     */
    @OptIn(ExperimentalTime::class)
    fun formatAsRelativeTimeSpan(mills: Long) = getRelativeTimeSpanString(
        mills,
        System.currentTimeMillis(),
        DAY_IN_MILLIS,
        FORMAT_ABBREV_RELATIVE
    ) as String
}


/**
 * This Method retreves Color From Attributes Both Default and Custom
 *
 * @param attr
 * the attribute from which color is to be retrieved
 * @param context
 * The context of Application
 *
 * @return Hex String of corresponding Color
 */
fun Context.getColorFromAttr(attr: Int): Int {
    return try {
        theme.resources.getColor(attr)
    } catch (exception: Exception) {
        val typedValue = TypedValue()
        val theme: Resources.Theme = theme
        theme.resolveAttribute(attr, typedValue, true)
        typedValue.data
    }
}


//language=RegExp
private val ISO6709LocationPattern = Pattern.compile("([+\\-][0-9.]+)([+\\-][0-9.]+)")

/**
 * This method parses the given string representing a geographic point location by coordinates in ISO 6709 format
 * and returns the latitude and the longitude in float. If `location` is not in ISO 6709 format,
 * this method returns `null`
 *
 * @param location a String representing a geographic point location by coordinates in ISO 6709 format
 * @return `null` if the given string is not as expected, an array of floats with size 2,
 * where the first element represents latitude and the second represents longitude, otherwise.
 */
val MediaMetadataRetriever.latLong: DoubleArray?
    get() = runCatching(TAG) {
        val location =
            extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION) ?: return@runCatching null
        val m: Matcher = ISO6709LocationPattern.matcher(location)
        if (m.find() && m.groupCount() == 2) {
            val latstr: String = m.group(1) ?: return@runCatching null
            val lonstr: String = m.group(2) ?: return@runCatching null
            val lat = latstr.toDouble()
            val lon = lonstr.toDouble()
            doubleArrayOf(lat, lon)
        } else null
    }


fun Window.toggleStatusBarState(hide: Boolean) {
    when (hide) {
        // Hide Status Bar.
        true -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                insetsController?.hide(WindowInsets.Type.statusBars())
            else
                addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        else -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                insetsController?.show(WindowInsets.Type.statusBars())
            else
                clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
}

