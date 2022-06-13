package com.clementcorporation.levosonusii.main

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.ui.theme.LevoSonusIITheme
import com.clementcorporation.levosonusii.navigation.LevoSonusNavigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainActivityViewModel = hiltViewModel()
            LevoSonusIITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            val showVoiceCommandWindow = remember {
                                viewModel.showVoiceCommandWindow
                            }
                            if (showVoiceCommandWindow.value) VoiceCommandWindow()
                            Box(
                                modifier = Modifier.padding(8.dp),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                /*
                                    TODO:
                                        -hide FAB on SplashScreen
                                        -build UI for VoiceCommandWindow below
                                        -handle VoiceCommand in this activity:
                                            https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
                                 */
                                LevoSonusNavigation()
                                LSFAB {
                                    showVoiceCommandWindow.value = !showVoiceCommandWindow.value
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceCommandWindow() {
    var width = 0.5f
    var height = 0.75f
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
        width = height
        height = 0.5f
    }
    Card(
        modifier = Modifier
            .fillMaxWidth(width)
            .fillMaxHeight(height)
            .zIndex(1f)
            .padding(Constants.PADDING.dp),
        shape = RoundedCornerShape(CURVATURE.dp),
        backgroundColor = Color.White,
        elevation = ELEVATION.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LevoSonusLogo(size = 50.dp, showText = false)
        }
    }
}