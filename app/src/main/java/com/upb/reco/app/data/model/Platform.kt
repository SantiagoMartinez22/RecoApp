package com.upb.reco.app.data.model

enum class Platform(
    val label: String,
    val colorArgb: Long,
    val shortLabel: String,
) {
    NETFLIX("Netflix", 0xFFE50914L, "N"),
    DISNEY_PLUS("Disney+", 0xFF113CCFL, "D+"),
    HBO_MAX("HBO Max", 0xFF0063E5L, "HBO"),
    PRIME_VIDEO("Prime Video", 0xFF00A8E1L, "AMZ"),
    APPLE_TV("Apple TV+", 0xFF000000L, "TV+"),
}
