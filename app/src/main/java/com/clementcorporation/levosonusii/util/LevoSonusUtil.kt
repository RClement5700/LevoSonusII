package com.clementcorporation.levosonusii.util

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.dp

object LevoSonusUtil {

    fun setPaddingPerConfiguration(
        configuration: Configuration,
        landscapePadding: Int,
        portraitPadding: Int
    ) = when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> landscapePadding.dp
            else -> portraitPadding.dp
        }

    fun getTopPaddingPerConfiguration(configuration: Configuration) =
        setPaddingPerConfiguration(configuration, 16, 50)

    fun arrangeContentPerConfiguration(
        centerContent: MutableState<Boolean>
    ) = if (centerContent.value) Arrangement.Center else Arrangement.Top
}