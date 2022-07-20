package com.prime.toolz2.ui.converter

import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import com.prime.toolz2.common.compose.LocalNavController
import com.prime.toolz2.settings.SettingsRoute
import cz.levinzonr.saferoute.core.annotations.Route
import cz.levinzonr.saferoute.core.annotations.RouteNavGraph
import cz.levinzonr.saferoute.core.navigateTo

@Route(navGraph = RouteNavGraph(start = true))
@Composable
fun UnitConverter(viewModel: UnitConverterViewModel) {
    val navigator = LocalNavController.current
    Button(onClick = {
        val direction = SettingsRoute()
        navigator.navigateTo(direction)
    }) {

    }
}