package com.example.dentooth

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform