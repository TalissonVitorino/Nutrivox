package com.kotlincrossplatform.nutrivox

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform