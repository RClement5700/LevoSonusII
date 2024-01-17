package com.clementcorporation.levosonusii.presentation.login

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.util.Constants.BTN_HEIGHT
import com.clementcorporation.levosonusii.util.Constants.BTN_WIDTH
import com.clementcorporation.levosonusii.util.Constants.CURVATURE
import com.clementcorporation.levosonusii.util.Constants.ELEVATION
import com.clementcorporation.levosonusii.util.Constants.LOGO_SIZE
import com.clementcorporation.levosonusii.util.Constants.LS_BLUE
import com.clementcorporation.levosonusii.util.Constants.PADDING
import com.clementcorporation.levosonusii.util.Constants.VALID_EMPLOYEE_ID_LENGTH
import com.clementcorporation.levosonusii.util.Constants.VALID_PASSWORD_LENGTH
import com.clementcorporation.levosonusii.util.LSPasswordTextField
import com.clementcorporation.levosonusii.util.LSTextField
import com.clementcorporation.levosonusii.util.LevoSonusLogo
import com.clementcorporation.levosonusii.util.LevoSonusScreens

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState = viewModel.loginScreenUiState.collectAsStateWithLifecycle().value
    val isLoading = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = ELEVATION.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(CURVATURE.dp)
    ) {
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
            LSTextField(
                modifier = Modifier
                    .padding(PADDING.dp)
                    .fillMaxWidth(),
                userInput = viewModel.employeeId,
                label = stringResource(id = R.string.label_employee_id),
                keyboardType = KeyboardType.Number,
                onAction = KeyboardActions {
                    defaultKeyboardAction(ImeAction.Next)
                }
            ) {
                if (it.length <= VALID_EMPLOYEE_ID_LENGTH) viewModel.employeeId = it
            }
            LSPasswordTextField(
                modifier = Modifier
                    .padding(PADDING.dp)
                    .fillMaxWidth(),
                userInput = viewModel.password,
                label = stringResource(id = R.string.label_password),
                onAction = KeyboardActions {
                    viewModel.signIn()
                }
            ) {
                if (it.length <= VALID_PASSWORD_LENGTH) viewModel.password = it
            }
            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> LandscapeButtonAndRegistrationContent(
                    viewModel = viewModel,
                    navController = navController,
                    isLoading = isLoading
                )
                else -> PortraitButtonAndRegistrationContent(
                    viewModel = viewModel,
                    navController = navController,
                    isLoading = isLoading
                )
            }
            when (uiState) {
                is LoginScreenUiState.OnUserDataRetrieved -> {
                    SideEffect {
                        isLoading.value = false
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                    }
                }
                is LoginScreenUiState.OnFailedToLoadUser -> {
                    SideEffect {
                        isLoading.value = false
                        Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is LoginScreenUiState.OnLoading -> {
                    SideEffect {
                        isLoading.value = uiState.isLoading
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun PortraitButtonAndRegistrationContent(
    viewModel: LoginViewModel,
    navController: NavController,
    isLoading: MutableState<Boolean>
) {
    Button(
        modifier = Modifier
            .height(BTN_HEIGHT.dp)
            .width(BTN_WIDTH.dp),
        shape = RoundedCornerShape(CURVATURE),
        elevation = elevation(defaultElevation = ELEVATION.dp),
        enabled = viewModel.validateInputs(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = LS_BLUE,
            disabledBackgroundColor = Color.LightGray
        ),
        onClick = {
            viewModel.signIn()
        }) {
        if(isLoading.value) {
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
    navController: NavController,
    isLoading: MutableState<Boolean>
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
            enabled = viewModel.validateInputs(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = LS_BLUE,
                disabledBackgroundColor = Color.LightGray
            ),
            onClick = {
                viewModel.signIn()
            }) {
            if (isLoading.value) {
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