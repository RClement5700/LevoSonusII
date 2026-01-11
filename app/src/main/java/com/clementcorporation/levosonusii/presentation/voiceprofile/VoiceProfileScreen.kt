package com.clementcorporation.levosonusii.presentation.voiceprofile

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.util.Constants
import com.clementcorporation.levosonusii.util.Constants.ELEVATION
import com.clementcorporation.levosonusii.util.Constants.LS_BLUE
import com.clementcorporation.levosonusii.util.LSAppBar
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import com.clementcorporation.levosonusii.util.NavTile

private const val TAG = "VoiceProfileScreen"
@Composable
fun VoiceProfileScreen(navController: NavController, showVoiceCommandActivity: (String) -> Unit) {
    val viewModel: VoiceProfileViewModel = hiltViewModel()
    val userInfo = viewModel.getDataStore().data.collectAsStateWithLifecycle(
        initialValue = LSUserInfo()
    ).value
    BackHandler {
        navController.apply {
            popBackStack()
            navigate(LevoSonusScreens.HomeScreen.name)
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        shadowElevation = ELEVATION.dp,
        color = Color.White,
        shape = RoundedCornerShape(Constants.CURVATURE.dp)
    ) {
        Scaffold(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxSize(),
            topBar = {
                LSAppBar(navController = navController, expandMenu = viewModel.expandMenu,
                    title = stringResource(id = R.string.voice_profile_screen_toolbar_title),
                    profilePicUrl = null,
                    onClickSignOut = {
                        viewModel.signOut {
                            navController.navigate(LevoSonusScreens.LoginScreen.name)
                        }
                    },
                    onClickLeftIcon = {
                        navController.apply {
                            popBackStack()
                            navigate(LevoSonusScreens.HomeScreen.name)
                        }
                    }
                )
            }
        ) {
            Log.e(TAG, it.toString())
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.showProgressBar.value) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .zIndex(1f)
                            .size(50.dp),
                        strokeWidth = 2.dp,
                        color = LS_BLUE
                    )
                }
                if (viewModel.showWarningDialog.value) {
                    VoiceProfileWarningDialog(
                        viewModel,
                        showVoiceCommandActivity
                    )
                }
                Column(
                    modifier = Modifier.verticalScroll(
                        enabled = true,
                        state = rememberScrollState()
                    ),
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(
                        color = LS_BLUE,
                        thickness = 2.dp,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    userInfo.voiceProfile.keys.sorted().forEach { key ->
                        NavTile(title = key) {
                            viewModel.warningDialogTitle.value = key
                            viewModel.showWarningDialog.value = true
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceProfileWarningDialog(
    viewModel: VoiceProfileViewModel,
    showVoiceCommandActivity: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
            .padding(start = 32.dp, end = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.33f)
                .padding(Constants.PADDING.dp)
                .zIndex(1f),
            elevation = CardDefaults.elevatedCardElevation(ELEVATION.dp),
            shape = RoundedCornerShape(Constants.CURVATURE.dp),
            border = BorderStroke(2.dp, LS_BLUE)
        ) {
            Column(
                modifier = Modifier.padding(Constants.PADDING.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(
                        R.string.voice_profile_are_you_sure_dialog_title
                    ),
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = viewModel.warningDialogTitle.value,
                    textAlign = TextAlign.Center,
                    color = LS_BLUE,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(
                    color = LS_BLUE,
                    thickness = 2.dp,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight())
                {
                    Button(
                        modifier = Modifier
                            .padding(Constants.PADDING.dp)
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(Constants.CURVATURE),
                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = ELEVATION.dp),
                        colors = ButtonDefaults.buttonColors(
//                            backgroundColor = LS_BLUE,
//                            disabledBackgroundColor = Color.LightGray
                        ),
                        onClick = {
                            showVoiceCommandActivity(viewModel.warningDialogTitle.value)
                            viewModel.showWarningDialog.value = false
                        }) {
                        if(viewModel.showProgressBar.value) {
                            CircularProgressIndicator(strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Text(
                                text = stringResource(id = R.string.voice_profile_dialog_btn_text_yes),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Button(
                        modifier = Modifier
                            .padding(Constants.PADDING.dp)
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(Constants.CURVATURE),
                        border = BorderStroke(2.dp, LS_BLUE),
                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = ELEVATION.dp),
                        colors = ButtonDefaults.buttonColors(
//                            backgroundColor = Color.White,
//                            disabledBackgroundColor = Color.LightGray
                        ),
                        onClick = {
                            viewModel.showWarningDialog.value = false
                        }) {
                        Text(
                            text = stringResource(id = R.string.voice_profile_dialog_btn_text_no),
                            color = LS_BLUE,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}