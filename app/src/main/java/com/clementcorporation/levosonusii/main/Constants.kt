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
    const val USERS = "users"
    const val DEPARTMENT_ID = "departmentId"
    const val MACHINE_ID = "machineId"
    const val SCANNER_ID = "scannerId"
    const val HEADSET_ID = "headsetId"
    const val NAME = "name"
    const val EMAIL = "emailAddress"
    const val PIC_URL = "profilePicUrl"
    const val USER_ID = "userId"
    const val VOICE_PROFILE = "voiceProfile"
    const val OP_TYPE = "operatorType"
    const val DEPARTMENTS = "departments"
    const val EQUIPMENT = "equipment"
    const val ELECTRIC_PALLET_JACK = "ELECTRIC_PALLET_JACK"
    const val FORKLIFT = "FORKLIFT"
    const val SCANNER = "SCANNER"
    const val HEADSET = "HEADSET"
    const val FORKLIFT_COUNT = "forkliftCount"
    const val OP_COUNT = "orderPickerCount"
    const val TITLE = "title"
    const val REMAINING_ORDERS = "remainingOrders"
    const val ICON_URL = "icon"
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