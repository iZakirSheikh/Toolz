package com.prime.toolz2.common

import android.app.Activity
import android.util.Log
import androidx.compose.material.SnackbarDuration
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestAppUpdateInfo
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.android.play.core.review.ReviewManagerFactory
import com.prime.toolz2.MainActivity
import com.prime.toolz2.common.compose.SnackDataChannel
import com.prime.toolz2.common.compose.send
import com.prime.toolz2.settings.GlobalKeys
import com.primex.core.activity
import com.primex.core.runCatching
import com.primex.preferences.longPreferenceKey
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private const val TAG = "Util"

