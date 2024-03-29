package com.clementcorporation.levosonusii.util

import androidx.compose.ui.graphics.Color

object Constants {
    const val BUSINESSES_ENDPOINT = "businesses"
    const val USERS_ENDPOINT = "users"
    const val EQUIPMENT_ENDPOINT = "equipment"
    const val DEPARTMENTS_ENDPOINT = "departments"
    const val ORDER_PICKERS_PARAM = "orderPickers"
    const val FORKLIFTS_PARAM = "forklifts"
    const val VALID_PASSWORD_LENGTH = 6
    const val VALID_EMPLOYEE_ID_LENGTH = 8
    const val VALID_BUSINESS_ID_LENGTH = 4
    const val USER_INPUT = "com.clementcorporation.levosonusii.USER_INPUT"
    const val VOICE_COMMAND_KEY = "USER_INPUT"
    const val DEFAULT_VOICE_COMMAND_PROMPT = "How Can I Help?"
    const val PROMPT_KEYWORD = "prompt"
    const val ELEVATION = 8
    const val CURVATURE = 16
    const val PADDING = 8
    const val LOGO_SIZE = 50
    const val BTN_HEIGHT = 50
    const val BTN_WIDTH = 200
    val LS_BLUE = Color(0xFF32527B)
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
    const val MESSENGER = "messenger"
    const val USER_1 = "USER_1"
    const val USER_2 = "USER_2"
    const val USER_1_MESSAGES = "USER_1_MESSAGES"
    const val USER_2_MESSAGES = "USER_2_MESSAGES"
    const val DATE = "DATE"
    const val TIME = "TIME"
    const val BODY = "BODY"
    const val TIME_FORMAT = "hh:mm aa"
    const val DATE_FORMAT = "MM/dd/yyyy"
}

object OperatorTypes {
    const val FORKLIFT = "Forklift"
    const val ORDER_PICKER = "Order Picker"
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