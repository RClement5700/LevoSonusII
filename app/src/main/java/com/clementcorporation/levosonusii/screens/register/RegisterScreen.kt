package com.clementcorporation.levosonusii.screens.register

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants.BTN_HEIGHT
import com.clementcorporation.levosonusii.main.Constants.BTN_WIDTH
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.LOGO_SIZE
import com.clementcorporation.levosonusii.main.Constants.LS_BLUE
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LSAlertDialog
import com.clementcorporation.levosonusii.main.LSPasswordTextField
import com.clementcorporation.levosonusii.main.LSTextField
import com.clementcorporation.levosonusii.main.LevoSonusLogo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens

@Composable
fun RegisterScreen(navController: NavController) {
    val viewModel: RegisterViewModel = hiltViewModel()
    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = ELEVATION.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(CURVATURE.dp)
    ) {
        val configuration = LocalConfiguration.current
        Column(
            modifier = Modifier
                .padding(
                    top = when (configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> 8.dp
                        else -> 50.dp
                    }
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LevoSonusLogo(LOGO_SIZE.dp)
            if (viewModel.showNewUserDialog.value) {
                LSAlertDialog(
                    showAlertDialog = viewModel.showNewUserDialog,
                    dialogTitle = stringResource(id = R.string.register_alert_dialog_title),
                    dialogBody = remember {
                        mutableStateOf(
                            navController.context.getString(
                                R.string.register_screen_new_user_dialog_body,
                                viewModel.firstName.value,
                                viewModel.lastName.value,
                                viewModel.email.value,
                                viewModel.employeeId.value
                            )
                        )
                    },
                    onPositiveButtonClicked = {
                        viewModel.showNewUserDialog.value = false
                        viewModel.showVoiceProfileDialog.value = true
                    },
                    onNegativeButtonClicked = {
                        Toast.makeText(
                            navController.context,
                            navController.context.getString(R.string.register_screen_take_screenshot_toast_message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
            if (viewModel.showVoiceProfileDialog.value) {
                LSAlertDialog(
                    showAlertDialog = viewModel.showVoiceProfileDialog,
                    dialogTitle = stringResource(id = R.string.voice_profile_alert_dialog_title),
                    onPositiveButtonClicked = {
                        viewModel.showVoiceProfileDialog.value = false
                        navController.navigate(LevoSonusScreens.VoiceProfileScreen.name)
                    },
                    onNegativeButtonClicked = {
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                    }
                )
            }
            when(configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> LandscapeContent(
                    viewModel = viewModel,
                    navController = navController,
                )
                else -> PortraitContent(
                    viewModel = viewModel,
                    navController = navController,
                )
            }
        }
    }
}

@Composable
fun PortraitContent(viewModel: RegisterViewModel, navController: NavController, ) {
    LSTextField(
        modifier = Modifier
            .padding(PADDING.dp)
            .fillMaxWidth(),
        userInput = viewModel.email,
        label = stringResource(id = R.string.label_email_address),
        onAction = KeyboardActions {
            defaultKeyboardAction(ImeAction.Next)
        }
    ) {
        viewModel.email.value = it
        viewModel.isRegisterButtonEnabled.value = viewModel.validateInputs()
    }
    LSTextField(
        modifier = Modifier
            .padding(PADDING.dp)
            .fillMaxWidth(),
        userInput = viewModel.firstName,
        label = stringResource(id = R.string.label_first_name),
        imeAction = ImeAction.Next,
        onAction = KeyboardActions {
            defaultKeyboardAction(ImeAction.Next)
        }
    ) {
        viewModel.firstName.value = it
        viewModel.isRegisterButtonEnabled.value = viewModel.validateInputs()
    }

    LSTextField(
        modifier = Modifier
            .padding(PADDING.dp)
            .fillMaxWidth(),
        userInput = viewModel.lastName,
        label = stringResource(id = R.string.label_last_name),
        onAction = KeyboardActions {
            defaultKeyboardAction(ImeAction.Next)
        }
    ) {
        viewModel.lastName.value = it
        viewModel.isRegisterButtonEnabled.value = viewModel.validateInputs()
    }
    LSPasswordTextField(
        modifier = Modifier
            .padding(PADDING.dp)
            .fillMaxWidth(),
        userInput = viewModel.password,
        label = stringResource(id = R.string.label_password),
        onAction = KeyboardActions {
            viewModel.createUser(context = navController.context)
        }
    ) {
        viewModel.password.value = it
        viewModel.isRegisterButtonEnabled.value = viewModel.validateInputs()
    }
    Button(
        modifier = Modifier
            .height(BTN_HEIGHT.dp)
            .width(BTN_WIDTH.dp),
        shape = RoundedCornerShape(CURVATURE.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = ELEVATION.dp),
        enabled = viewModel.isRegisterButtonEnabled.value,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = LS_BLUE,
            disabledBackgroundColor = Color.LightGray
        ),
        onClick = {
            viewModel.createUser(context = navController.context)
        }) {
        if(viewModel.loading.value) {
            CircularProgressIndicator(strokeWidth = 4.dp, color = Color.White)
        } else {
            Text(
                text = stringResource(id = R.string.btn_text_register),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier.padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val prefaceText = stringResource(id = R.string.register_navigation_pretext)
        val linkedText = stringResource(id = R.string.register_navigation_link_text)
        Text(text = prefaceText, color = Color.Gray)
        Text(
            text = linkedText,
            modifier = Modifier
                .clickable {
                    navController.navigate(LevoSonusScreens.LoginScreen.name)
                }
                .padding(start = 5.dp),
            fontWeight = FontWeight.Bold,
            color = Color.Blue
        )
    }
}

@Composable
fun LandscapeContent(viewModel: RegisterViewModel, navController: NavController, ) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LSTextField(
            modifier = Modifier.padding(end = 8.dp),
            userInput = viewModel.firstName,
            label = stringResource(id = R.string.label_first_name),
            imeAction = ImeAction.Next,
            onAction = KeyboardActions {
                defaultKeyboardAction(ImeAction.Next)
            }
        ) {
            viewModel.firstName.value = it
            viewModel.isRegisterButtonEnabled.value = viewModel.validateInputs()
        }
        LSTextField(
            userInput = viewModel.lastName,
            label = stringResource(id = R.string.label_last_name),
            onAction = KeyboardActions {
                defaultKeyboardAction(ImeAction.Next)
            }
        ) {
            viewModel.lastName.value = it
            viewModel.isRegisterButtonEnabled.value = viewModel.validateInputs()
        }
    }
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LSTextField(
            modifier = Modifier.padding(end = 8.dp),
            userInput = viewModel.email,
            label = stringResource(id = R.string.label_email_address),
            onAction = KeyboardActions {
                defaultKeyboardAction(ImeAction.Next)
            }
        ) {
            viewModel.email.value = it
            viewModel.isRegisterButtonEnabled.value = viewModel.validateInputs()
        }
        LSPasswordTextField(
            userInput = viewModel.password,
            label = stringResource(id = R.string.label_password),
            onAction = KeyboardActions {
                viewModel.createUser(context = navController.context)
            }
        ) {
            viewModel.password.value = it
            viewModel.isRegisterButtonEnabled.value = viewModel.validateInputs()
        }
    }
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            modifier = Modifier
                .height(BTN_HEIGHT.dp)
                .width(BTN_WIDTH.dp),
            shape = RoundedCornerShape(CURVATURE.dp),
            elevation = ButtonDefaults.elevation(defaultElevation = ELEVATION.dp),
            enabled = viewModel.isRegisterButtonEnabled.value,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = LS_BLUE,
                disabledBackgroundColor = Color.LightGray
            ),
            onClick = {
                viewModel.createUser(context = navController.context)
            }) {
            if(viewModel.loading.value) {
                CircularProgressIndicator(strokeWidth = 4.dp, color = Color.White)
            } else {
                Text(
                    text = stringResource(id = R.string.btn_text_register),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val prefaceText = stringResource(id = R.string.register_navigation_pretext)
            val linkedText = stringResource(id = R.string.register_navigation_link_text)
            Text(text = prefaceText, color = Color.Gray)
            Text(
                text = linkedText,
                modifier = Modifier
                    .clickable {
                        navController.navigate(LevoSonusScreens.LoginScreen.name)
                    }
                    .padding(start = 5.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Blue
            )
        }
    }
}