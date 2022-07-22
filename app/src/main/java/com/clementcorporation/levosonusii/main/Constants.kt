package com.clementcorporation.levosonusii.main

import androidx.compose.ui.graphics.Color

object Constants {
    const val STORAGE_BASE_URL = "https://firebasestorage.googleapis.com/v0/b/levosonus.appspot.com/o/"
    const val STORAGE_APPENDED_URL = ""
    const val USER_INPUT = "com.clementcorporation.levosonusii.USER_INPUT"
    const val PROMPT_KEYWORD = "prompt"
    const val ELEVATION = 8
    const val CURVATURE = 16
    const val PADDING = 8
    const val LOGO_SIZE = 50
    const val BTN_HEIGHT = 50
    const val BTN_WIDTH = 200
    val LS_BLUE = Color(0xFF32527B)
}

enum class VoiceProfileConstants {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    ZERO,
    JARVIS,
    YES,
    NO,
    LOGIN,
    REGISTER,
    ORDERS
}

enum class VoiceCommands {
    JARVIS,
    LOGIN,
    REGISTER,
    OPEN
}