package com.kotlincrossplatform.nutrivox

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Nutrivox",
    ) {
        App()
    }
}