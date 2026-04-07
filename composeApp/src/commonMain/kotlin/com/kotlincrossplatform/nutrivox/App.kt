package com.kotlincrossplatform.nutrivox

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kotlincrossplatform.nutrivox.theme.NutrivoxTheme

@Composable
fun App() {
    NutrivoxTheme {
        Surface {
            Text("Nutrivox")
        }
    }
}
