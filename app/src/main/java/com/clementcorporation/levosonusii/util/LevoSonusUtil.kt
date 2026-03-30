package com.clementcorporation.levosonusii.util

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
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

    fun Modifier.invisible(invisible: Boolean): Modifier = if (invisible) {
        this.then(
            object : LayoutModifier {
                override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints
                ): MeasureResult {
                    val placeable = measurable.measure(constraints)
                    // Measure the size but return an empty placement block
                    return layout(placeable.width, placeable.height) {}
                }
            }
        )
    } else this
}