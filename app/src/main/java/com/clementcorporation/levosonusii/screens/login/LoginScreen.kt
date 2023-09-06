package com.clementcorporation.levosonusii.screens.login

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.runtime.Composable
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
import com.clementcorporation.levosonusii.main.LSPasswordTextField
import com.clementcorporation.levosonusii.main.LSTextField
import com.clementcorporation.levosonusii.main.LevoSonusLogo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = hiltViewModel()

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
                ).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LevoSonusLogo(LOGO_SIZE.dp)
            LSTextField(
                modifier = Modifier.padding(PADDING.dp).fillMaxWidth(),
                userInput = viewModel.employeeId,
                label = stringResource(id = R.string.label_employee_id),
                onAction = KeyboardActions {
                    defaultKeyboardAction(ImeAction.Next)
                }
            ) {
                viewModel.employeeId.value = it
                viewModel.isLoginButtonEnabled.value = viewModel.employeeId.value.isNotEmpty() && viewModel.password.value.isNotEmpty()
            }
            LSPasswordTextField(
                modifier = Modifier.padding(PADDING.dp).fillMaxWidth(),
                userInput = viewModel.password,
                label = stringResource(id = R.string.label_password),
                onAction = KeyboardActions {
                    viewModel.signInWithEmailAndPassword {
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                    }
                }
            ) {
                viewModel.password.value = it
                viewModel.isLoginButtonEnabled.value = viewModel.employeeId.value.isNotEmpty() && viewModel.password.value.isNotEmpty()
            }
            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> LandscapeButtonAndRegistrationContent(
                    viewModel = viewModel,
                    navController = navController
                )
                else -> PortraitButtonAndRegistrationContent(
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun PortraitButtonAndRegistrationContent(
    viewModel: LoginViewModel,
    navController: NavController
) {
    Button(
        modifier = Modifier
            .height(BTN_HEIGHT.dp)
            .width(BTN_WIDTH.dp),
        shape = RoundedCornerShape(CURVATURE),
        elevation = elevation(defaultElevation = ELEVATION.dp),
        enabled = viewModel.isLoginButtonEnabled.value,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = LS_BLUE,
            disabledBackgroundColor = Color.LightGray
        ),
        onClick = {
            viewModel.signInWithEmailAndPassword {
                navController.navigate(LevoSonusScreens.HomeScreen.name)
            }
        }) {
        if(viewModel.showProgressBar.value) {
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

@Composable
fun LandscapeButtonAndRegistrationContent(
    viewModel: LoginViewModel,
    navController: NavController
) {
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
            shape = RoundedCornerShape(CURVATURE),
            elevation = elevation(defaultElevation = ELEVATION.dp),
            enabled = viewModel.isLoginButtonEnabled.value,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = LS_BLUE,
                disabledBackgroundColor = Color.LightGray
            ),
            onClick = {
                viewModel.signInWithEmailAndPassword{
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                }
            }) {
            if (viewModel.showProgressBar.value) {
                CircularProgressIndicator(strokeWidth = 2.dp, color = Color.White)
            } else {
                Text(
                    text = stringResource(id = R.string.btn_text_login),
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