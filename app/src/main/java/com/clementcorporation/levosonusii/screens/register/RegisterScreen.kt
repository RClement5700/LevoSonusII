package com.clementcorporation.levosonusii.screens.register

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.clementcorporation.levosonusii.main.Constants.ENABLED_BUTTON_COLOR
import com.clementcorporation.levosonusii.main.Constants.LOGO_SIZE
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LSAlertDialog
import com.clementcorporation.levosonusii.main.LSPasswordTextField
import com.clementcorporation.levosonusii.main.LSTextField
import com.clementcorporation.levosonusii.main.LevoSonusLogo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens

@Composable
fun RegisterScreen(navController: NavController) {
    val viewModel: RegisterViewModel = hiltViewModel()
    val email = remember{
        mutableStateOf("")
    }
    val password = remember{
        mutableStateOf("")
    }
    val firstName = remember{
        mutableStateOf("")
    }
    val lastName = remember{
        mutableStateOf("")
    }
    val isRegisterButtonEnabled = remember{
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(PADDING.dp),
        elevation = ELEVATION.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(CURVATURE.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 50.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LevoSonusLogo(LOGO_SIZE.dp)
            val showNewUserDialog = remember {
                mutableStateOf(false)
            }
            val showVoiceProfileDialog = remember {
                mutableStateOf(false)
            }
            if (showNewUserDialog.value) {
                LSAlertDialog(
                    showAlertDialog = showNewUserDialog,
                    dialogTitle = stringResource(id = R.string.register_alert_dialog_title),
                    dialogBody = remember {
                        mutableStateOf(
                            "\nName: ${firstName.value} ${lastName.value} \nEmail: ${email.value} \nEmployee ID: ${viewModel.employeeId.value} \nReady to Proceed?"
                        )
                    },
                    onPositiveButtonClicked = {
                        showNewUserDialog.value = false
                        showVoiceProfileDialog.value = true
                    },
                    onNegativeButtonClicked = {
                        Toast.makeText(navController.context, "Take a screenshot to save your credentials",
                            Toast.LENGTH_LONG).show()
                    }
                )
            }
            if (showVoiceProfileDialog.value) {
                LSAlertDialog(
                    showAlertDialog = showVoiceProfileDialog,
                    dialogTitle = stringResource(id = R.string.voice_profile_alert_dialog_title),
                    onPositiveButtonClicked = {
                        showVoiceProfileDialog.value = false
                        navController.navigate(LevoSonusScreens.VoiceProfileScreen.name)
                    },
                    onNegativeButtonClicked = {
                        navController.navigate(LevoSonusScreens.HomeScreen.name)

                    }
                )
            }
            LSTextField(userInput = email, label = stringResource(id = R.string.label_email_address),
                onAction = KeyboardActions {
                    defaultKeyboardAction(ImeAction.Next)
                }
            ) {
                email.value = it
                isRegisterButtonEnabled.value = email.value.isNotEmpty() && password.value.isNotEmpty()
                        && firstName.value.isNotEmpty() && lastName.value.isNotEmpty()
            }
            LSTextField(
                userInput = firstName,
                label = stringResource(id = R.string.label_first_name),
                imeAction = ImeAction.Next,
                onAction = KeyboardActions {
                    defaultKeyboardAction(ImeAction.Next)
                }
            ) {
                firstName.value = it
                isRegisterButtonEnabled.value = email.value.isNotEmpty() && password.value.isNotEmpty()
                        && firstName.value.isNotEmpty() && lastName.value.isNotEmpty()
            }

            LSTextField(userInput = lastName, label = stringResource(id = R.string.label_last_name),
                onAction = KeyboardActions {
                    defaultKeyboardAction(ImeAction.Next)
                }
            ) {
                lastName.value = it
                isRegisterButtonEnabled.value = email.value.isNotEmpty() && password.value.isNotEmpty()
                        && firstName.value.isNotEmpty() && lastName.value.isNotEmpty()
            }
            LSPasswordTextField(
                userInput = password,
                label = stringResource(id = R.string.label_password),
                onAction = KeyboardActions {
                    createUser(viewModel, navController, email, password, firstName, lastName) {
                        showNewUserDialog.value = true
                    }
                }
            ) {
                password.value = it
                isRegisterButtonEnabled.value = email.value.isNotEmpty() && password.value.isNotEmpty()
                        && firstName.value.isNotEmpty() && lastName.value.isNotEmpty()
            }
            Button(
                modifier = Modifier
                    .height(BTN_HEIGHT.dp)
                    .width(BTN_WIDTH.dp),
                shape = RoundedCornerShape(CURVATURE.dp),
                elevation = ButtonDefaults.elevation(defaultElevation = ELEVATION.dp),
                enabled = isRegisterButtonEnabled.value,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = ENABLED_BUTTON_COLOR,
                    disabledBackgroundColor = Color.LightGray
                ),
                onClick = {
                    createUser(viewModel, navController, email, password, firstName, lastName) {
                        showNewUserDialog.value = true
                    }
                }) {
                if(viewModel.loading.value == true) {
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
    }

}

private fun createUser(viewModel: RegisterViewModel, navController: NavController, email: MutableState<String>,
password: MutableState<String>, firstName: MutableState<String>, lastName: MutableState<String>, goToNextScreen: () -> Unit) {
    viewModel.createUserWithEmailAndPassword(
        context = navController.context,
        email = email.value,
        password = password.value,
        firstName = firstName.value,
        lastName = lastName.value,
        goToNextScreen = goToNextScreen
    )
}