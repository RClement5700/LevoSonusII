package com.clementcorporation.levosonusii.presentation.register

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
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
import com.clementcorporation.levosonusii.util.Constants.VALID_PASSWORD_LENGTH
import com.clementcorporation.levosonusii.util.LSAlertDialog
import com.clementcorporation.levosonusii.util.LSPasswordTextField
import com.clementcorporation.levosonusii.util.LSTextField
import com.clementcorporation.levosonusii.util.LevoSonusLogo
import com.clementcorporation.levosonusii.util.LevoSonusScreens

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val viewModel: RegisterViewModel = hiltViewModel()
    val uiState = viewModel.registerScreenUiState.collectAsStateWithLifecycle().value
    val isLoading = remember { mutableStateOf(false) }
    val centerContent = remember { mutableStateOf(false) }
    val showNewUserDialog = remember { mutableStateOf(false) }
    val showVoiceProfileDialog = remember { mutableStateOf(false) }
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
            when(uiState) {
                is RegisterScreenUiState.OnLoading -> {
                    centerContent.value = true
                    isLoading.value = true
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = LS_BLUE,
                        strokeWidth = 4.dp
                    )
                }
                is RegisterScreenUiState.OnBusinessesRetrieved -> {
                    isLoading.value = false
                    centerContent.value = false
                    when(configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                LevoSonusLogo(LOGO_SIZE.dp)
                                BusinessIdInputField(
                                    viewModel = viewModel,
                                    modifier = Modifier.fillMaxWidth(0.55f)
                                )
                            }
                            LandscapeContent(
                                viewModel = viewModel,
                                navController = navController,
                                isLoading = isLoading
                            )
                        }
                        else -> {
                            LevoSonusLogo(LOGO_SIZE.dp)
                            PortraitContent(
                                viewModel = viewModel,
                                navController = navController,
                                isLoading = isLoading
                            )
                        }
                    }
                }
                is RegisterScreenUiState.OnFailedToLoadBusinesses -> {
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
                is RegisterScreenUiState.OnUserDataRetrieved -> {
                    viewModel.onNewUserCreated()
                    showNewUserDialog.value = true
                }
                is RegisterScreenUiState.OnFailedToLoadUser -> {
                    isLoading.value = false
                    centerContent.value = true
                    Toast.makeText(
                        context,
                        uiState.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
                is RegisterScreenUiState.OnSignInSuccess -> {
                    isLoading.value = false
                    SideEffect {
                        if (uiState.isCreatingVoiceProfile)
                            navController.navigate(LevoSonusScreens.VoiceProfileScreen.name)
                        else navController.navigate(LevoSonusScreens.HomeScreen.name)
                    }
                }
                is RegisterScreenUiState.OnSignInFailure -> {
                    isLoading.value = false
                    centerContent.value = true
                    Toast.makeText(
                        context,
                        uiState.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            if (showNewUserDialog.value) {
                LSAlertDialog(
                    showAlertDialog = showNewUserDialog,
                    dialogTitle = stringResource(id = R.string.register_alert_dialog_title),
                    dialogBody = remember {
                        mutableStateOf(
                            context.getString(
                                R.string.register_screen_new_user_dialog_body,
                                viewModel.firstName,
                                viewModel.lastName,
                                viewModel.email,
                                viewModel.employeeId
                            )
                        )
                    },
                    onPositiveButtonClicked = {
                        showNewUserDialog.value = false
                        showVoiceProfileDialog.value = true
                    },
                    onNegativeButtonClicked = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.register_screen_take_screenshot_toast_message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
            if (showVoiceProfileDialog.value) {
                showNewUserDialog.value = false
                LSAlertDialog(
                    showAlertDialog = showVoiceProfileDialog,
                    dialogTitle = stringResource(id = R.string.voice_profile_alert_dialog_title),
                    onPositiveButtonClicked = {
                        showVoiceProfileDialog.value = false
                        viewModel.signIn(true)
                    },
                    onNegativeButtonClicked = {
                        showVoiceProfileDialog.value = false
                        viewModel.signIn(false)
                    }
                )
            }
        }
    }
}

@Composable
fun BusinessIdInputField(viewModel: RegisterViewModel, modifier: Modifier) {
    OutlinedTextField(
        modifier = modifier,
        value = viewModel.businessId,
        onValueChange = { query ->
            viewModel.businessId = query
            viewModel.onQueryChange()
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                tint = if (viewModel.isVerifiedBusinessId) Color.Green else Color.Gray,
                contentDescription = "Business ID Verification"
            )
        },
        label = {
            Text(
                text = stringResource(id = R.string.label_business_id),
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
fun PortraitContent(viewModel: RegisterViewModel, navController: NavController,
                    isLoading: MutableState<Boolean>
) {
    BusinessIdInputField(
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth().padding(PADDING.dp)
    )
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
        viewModel.email = it
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
        viewModel.firstName = it
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
        viewModel.lastName = it
    }
    LSPasswordTextField(
        modifier = Modifier
            .padding(PADDING.dp)
            .fillMaxWidth(),
        userInput = viewModel.password,
        label = stringResource(id = R.string.password_placeholder),
        onAction = KeyboardActions {
            viewModel.createNewUser()
        }
    ) {
        if (it.length <= VALID_PASSWORD_LENGTH) viewModel.password = it
    }
    Button(
        modifier = Modifier
            .height(BTN_HEIGHT.dp)
            .width(BTN_WIDTH.dp),
        shape = RoundedCornerShape(CURVATURE.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = ELEVATION.dp),
        enabled = viewModel.validateInputs(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = LS_BLUE,
            disabledBackgroundColor = Color.LightGray
        ),
        onClick = {
            viewModel.createNewUser()
        }) {
        if (isLoading.value) {
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
fun LandscapeContent(viewModel: RegisterViewModel, navController: NavController,
                     isLoading: MutableState<Boolean>) {
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
            viewModel.firstName = it
        }
        LSTextField(
            userInput = viewModel.lastName,
            label = stringResource(id = R.string.label_last_name),
            onAction = KeyboardActions {
                defaultKeyboardAction(ImeAction.Next)
            }
        ) {
            viewModel.lastName = it
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
            viewModel.email = it
        }
        LSPasswordTextField(
            userInput = viewModel.password,
            label = stringResource(id = R.string.password_placeholder),
            onAction = KeyboardActions {
                viewModel.createNewUser()
            }
        ) {
            viewModel.password = it
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
            enabled = viewModel.validateInputs(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = LS_BLUE,
                disabledBackgroundColor = Color.LightGray
            ),
            onClick = {
                viewModel.createNewUser()
            }) {
            if (isLoading.value) {
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