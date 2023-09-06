package com.clementcorporation.levosonusii.screens.voiceprofile

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.LS_BLUE
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.NavTile
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens

private const val TAG = "VoiceProfileScreen"
@Composable
fun VoiceProfileScreen(navController: NavController, showVoiceCommandActivity: (String) -> Unit) {
    val viewModel: VoiceProfileViewModel = hiltViewModel()
    BackHandler {
        navController.popBackStack()
        navController.navigate(LevoSonusScreens.HomeScreen.name)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        elevation = Constants.ELEVATION.dp,
        color = Color.White,
        shape = RoundedCornerShape(Constants.CURVATURE.dp)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.White,
            topBar = {
                LSAppBar(navController = navController, expandMenu = viewModel.expandMenu,
                    title = stringResource(id = R.string.voice_profile_screen_toolbar_title),
                    profilePicUrl = null,
                    onClickSignOut = {
                        viewModel.signOut()
                        navController.popBackStack()
                        navController.navigate(LevoSonusScreens.LoginScreen.name)
                    },
                    onClickLeftIcon = {
                        navController.popBackStack()
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
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
                    VoiceProfileWarningDialog(viewModel, showVoiceCommandActivity)
                }
                Column(
                    modifier = Modifier.verticalScroll(
                        enabled = true,
                        state = rememberScrollState()
                    )
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Divider(
                        color = LS_BLUE,
                        thickness = 2.dp,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    viewModel.getVoiceProfileDataStore().data.collectAsState(
                        initial = VoiceProfile()
                    ).value.voiceProfileMap.keys.sorted().forEach { key ->
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
fun VoiceProfileWarningDialog(warningDialogTitle: MutableState<String>, showWarningDialog: MutableState<Boolean>,
                              viewModel: VoiceProfileViewModel, showVoiceCommandActivity: (String) -> Unit
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
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.33f)
                .padding(Constants.PADDING.dp)
                .zIndex(1f),
            elevation = Constants.ELEVATION.dp,
            shape = RoundedCornerShape(Constants.CURVATURE.dp),
            backgroundColor = Color.White,
            border = BorderStroke(2.dp, LS_BLUE)
        ) {
            Column(
                modifier = Modifier.padding(Constants.PADDING.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current
                Text(
                    text = context.getString(
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
                Divider(
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
                        elevation = ButtonDefaults.elevation(defaultElevation = Constants.ELEVATION.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = LS_BLUE,
                            disabledBackgroundColor = Color.LightGray
                        ),
                        onClick = {
                            showVoiceCommandActivity(warningDialogTitle.value)
                            showWarningDialog.value = false
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
                        elevation = ButtonDefaults.elevation(defaultElevation = Constants.ELEVATION.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            disabledBackgroundColor = Color.LightGray
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