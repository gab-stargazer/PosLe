package org.lelestacia.posle

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform