package com.clementcorporation.levosonusii.presentation.login

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import com.clementcorporation.levosonusii.util.Constants.VALID_BUSINESS_ID_LENGTH
import com.clementcorporation.levosonusii.util.Constants.VALID_PASSWORD_LENGTH
import com.clementcorporation.levosonusii.util.LSPasswordTextField
import com.clementcorporation.levosonusii.util.LevoSonusLogo
import com.clementcorporation.levosonusii.util.LevoSonusScreens

@Composable
fun LoginScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState = viewModel.loginScreenUiState.collectAsStateWithLifecycle().value
    val isLoading = remember { mutableStateOf(false) }
    val centerContent = remember { mutableStateOf(false) }
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
            verticalArrangement = if (centerContent.value) Arrangement.Center else Arrangement.Top
            ) {
            when (uiState) {
                is LoginScreenUiState.OnUserDataRetrieved -> {
                    SideEffect {
                        isLoading.value = false
                        centerContent.value = false
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                    }
                }
                is LoginScreenUiState.OnFailedToLoadUser -> {
                    isLoading.value = false
                    centerContent.value = false
                    Snackbar(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(8.dp),
                        elevation = 4.dp
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.message,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
                is LoginScreenUiState.OnFailedToLoadBusinesses -> {
                    isLoading.value = false
                    centerContent.value = true
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clickable { viewModel.fetchBusinesses() },
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.failed_to_load_error_message),
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Icon(
                            modifier = Modifier.size(48.dp),
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Try Again",
                            tint = Color.Green
                        )
                    }
                }
                is LoginScreenUiState.OnLoading -> {
                    centerContent.value = true
                    isLoading.value = true
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = LS_BLUE,
                        strokeWidth = 4.dp
                    )
                }

                is LoginScreenUiState.OnBusinessesRetrieved -> {
                    isLoading.value = false
                    centerContent.value = false
                    viewModel.onBusinessRetrieved()
                    LevoSonusLogo(LOGO_SIZE.dp)
                    EmployeeIdInputField(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PADDING.dp)
                    )
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
                }
            }
        }
    }
}

@Composable
fun EmployeeIdInputField(viewModel: LoginViewModel, modifier: Modifier) {
    OutlinedTextField(
        modifier = modifier,
        value = viewModel.employeeId,
        onValueChange = { query ->
            viewModel.employeeId = query
            if (viewModel.employeeId.length >= VALID_BUSINESS_ID_LENGTH) viewModel.onQueryChange()
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                tint = if (viewModel.isVerifiedEmployeeId) Color.Green else Color.Gray,
                contentDescription = "Employee ID Verification"
            )
        },
        label = {
            Text(
                text = stringResource(id = R.string.label_employee_id),
                color = Color.LightGray
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = Color.Black,
            focusedBorderColor = Color.Blue,
            textColor = Color.Black
        ),
        shape = RoundedCornerShape(CURVATURE.dp),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions.Default
    )
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