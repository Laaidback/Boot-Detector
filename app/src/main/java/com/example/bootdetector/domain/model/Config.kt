package com.example.bootdetector.domain.model

data class Config(
    val maxDismissals: Int = 5,
    val intervalMultiplier: Int = 20,
    val dismissalCount: Int = 0,
)
