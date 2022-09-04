package com.prime.toolz2.billing

import android.app.Activity
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

interface Monitizer : BillingManager, DefaultLifecycleObserver, Advertiser


fun Monitizer(
    billingManager: BillingManager,
    advertiser: Advertiser
): Monitizer =
    object : Monitizer, BillingManager by billingManager {

        override fun show(
            activity: Activity,
            force: Boolean,
            action: (() -> Unit)?
        ) {
            val isAdFree = billingManager[Product.DISABLE_ASD].purchased
            if (isAdFree) return // don't do anything
            advertiser.show(activity, force, action)
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            refresh()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            release()
            super.onDestroy(owner)
        }
    }

val LocalMonitizer =
    compositionLocalOf<Monitizer> {
        error("No Local Monitizer defined")
    }