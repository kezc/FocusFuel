package pl.wojtek.focusfuel

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform