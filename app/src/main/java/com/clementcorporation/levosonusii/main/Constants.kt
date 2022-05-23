package com.clementcorporation.levosonusii.main

import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.stringPreferencesKey


object Constants {
    const val ELEVATION = 8
    const val CURVATURE = 16
    const val PADDING = 8
    const val LOGO_SIZE = 50
    const val BTN_HEIGHT = 50
    const val BTN_WIDTH = 200
    val ENABLED_BUTTON_COLOR = Color(0xFF32527B)
    val EMPLOYEE_ID = stringPreferencesKey("employee_id")
}