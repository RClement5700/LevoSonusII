package com.clementcorporation.levosonusii.main

import androidx.compose.ui.graphics.Color

object Constants {
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

object VoiceCommands {
    val SIGN_OUT = "sign out"
    val HOME = "home"
    val ANNOUNCEMENTS = "annoucements"
    val DEPARTMENTS = "departments"
    val EQUIPMENT = "equipment"
    val GAME_CENTER ="game center"
    val HEALTH = "health"
    val MESSAGES = "messages"
    val BENEFITS = "benefits"
    val ORDERS = "orders"
    val VOICE_PROFILE = "voice profile"
    val CURRENT_ORDER = "current order"
    val NEXT_ORDER = "next order"
  }