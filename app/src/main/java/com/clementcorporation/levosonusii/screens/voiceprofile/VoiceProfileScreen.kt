package com.clementcorporation.levosonusii.screens.voiceprofile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.LSAlertDialog
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens

/*
    TODO:
        -on positive pushed: create voice profile
 */
@Composable
fun VoiceProfileScreen(navController: NavController) {
    val viewModel: VoiceProfileViewModel = hiltViewModel()
    val showAlertDialog = remember { mutableStateOf(true) }
    showAlertDialog.value = viewModel.getDataStore().data.collectAsState(initial = LSUserInfo()).value.voiceProfileId.isEmpty()
    val dialogTitle = stringResource(id = R.string.voice_profile_alert_dialog_title)
    if (showAlertDialog.value) {
        LSAlertDialog(
            showAlertDialog = showAlertDialog, dialogTitle = dialogTitle,
            onPositiveButtonClicked = {showAlertDialog.value = false},
            onNegativeButtonClicked = {navController.navigate(LevoSonusScreens.HomeScreen.name)}
        )
    }
}