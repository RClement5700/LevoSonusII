package com.clementcorporation.levosonusii.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.runtime.Composable
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
import com.clementcorporation.levosonusii.main.LSPasswordTextField
import com.clementcorporation.levosonusii.main.LSTextField
import com.clementcorporation.levosonusii.main.LevoSonusLogo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = hiltViewModel()
    val employeeId = remember{
        mutableStateOf("")
    }
    val password = remember{
        mutableStateOf("")
    }
    val isLoginButtonEnabled = remember{
        mutableStateOf(false)
    }
    val showProgressBar = remember{
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
            LSPasswordTextField(
                userInput = password,
                label = stringResource(id = R.string.label_password),
                onAction = KeyboardActions {
                    showProgressBar.value = true
                    viewModel.signInWithEmailAndPassword(userId = employeeId.value, password = password.value, home = {
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                    })
                }
            ) {
                password.value = it
                isLoginButtonEnabled.value = employeeId.value.isNotEmpty() && password.value.isNotEmpty()
            }
            Button(
                modifier = Modifier
                    .height(BTN_HEIGHT.dp)
                    .width(BTN_WIDTH.dp),
                shape = RoundedCornerShape(CURVATURE),
                elevation = elevation(defaultElevation = ELEVATION.dp),
                enabled = isLoginButtonEnabled.value,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = ENABLED_BUTTON_COLOR,
                    disabledBackgroundColor = Color.LightGray
                ),
                onClick = {
                    showProgressBar.value = true
                    viewModel.signInWithEmailAndPassword(userId = employeeId.value, password = password.value, home = {
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                    })
                }) {
                if(showProgressBar.value) {
                    CircularProgressIndicator(strokeWidth = 2.dp, color = Color.White)
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
                val prefaceText = stringResource(id = R.string.login_navigation_pretext)
                val linkedText = stringResource(id = R.string.login_navigation_link_text)
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