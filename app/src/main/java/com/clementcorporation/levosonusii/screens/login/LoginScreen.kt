package com.clementcorporation.levosonusii.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LSTextField
import com.clementcorporation.levosonusii.main.LevoSonusLogo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens

private const val LOGO_SIZE = 50
private const val LOGIN_BTN_HEIGHT = 50
private const val LOGIN_BTN_WIDTH = 200
private val ENABLED_BUTTON_COLOR = Color(0xFF32527B)

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = viewModel()
    val employeeId = remember{
        mutableStateOf("")
    }
    val password = remember{
        mutableStateOf("")
    }
    val isLoginButtonEnabled = remember{
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(PADDING),
        elevation = ELEVATION,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(CURVATURE)
    ) {
        Column(
            modifier = Modifier.padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LevoSonusLogo(LOGO_SIZE.dp)
            LSTextField(userInput = employeeId, label = stringResource(id = R.string.label_employee_id),
                onAction = KeyboardActions {
                    defaultKeyboardAction(ImeAction.Next)
                }
            ) {
                employeeId.value = it
                isLoginButtonEnabled.value = employeeId.value.isNotEmpty() && password.value.isNotEmpty()
            }
            LSTextField(
                userInput = password,
                label = stringResource(id = R.string.label_password),
                imeAction = ImeAction.Done,
                onAction = KeyboardActions {
                    navController.navigate(LevoSonusScreens.HomeScreen.name)
                }
            ) {
                password.value = it
                isLoginButtonEnabled.value = employeeId.value.isNotEmpty() && password.value.isNotEmpty()
            }
            Button(
                modifier = Modifier
                    .height(LOGIN_BTN_HEIGHT.dp)
                    .width(LOGIN_BTN_WIDTH.dp),
                shape = RoundedCornerShape(CURVATURE),
                elevation = elevation(defaultElevation = ELEVATION),
                enabled = isLoginButtonEnabled.value,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = ENABLED_BUTTON_COLOR,
                    disabledBackgroundColor = Color.LightGray
                ),
                onClick = {
                    viewModel.signInWithEmailAndPassword(userId = employeeId.value, password = password.value, home = {
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                    })
                    viewModel.createUserWithEmailAndPassword(navController.context, email = employeeId.value.trim(),
                        password.value.trim(), home = {
                            navController.navigate(LevoSonusScreens.HomeScreen.name)
                        }
                    )
                }) {
                if(viewModel.loading.value == true) {
                    CircularProgressIndicator(strokeWidth = 4.dp, color = Color.White)
                } else {
                    Text(
                        text = stringResource(id = R.string.btn_text_login),
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
                val prefaceText = "New User?"
                val linkedText = "Sign Up"
                Text(text = prefaceText, color = Color.Gray)
                Text(
                    text = linkedText,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(LevoSonusScreens.RegisterScreen.name)
                        }
                        .padding(start = 5.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue
                )
            }
        }
    }
}