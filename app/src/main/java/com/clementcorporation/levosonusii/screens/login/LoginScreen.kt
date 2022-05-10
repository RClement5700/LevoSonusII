package com.clementcorporation.levosonusii.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LSTextField
import com.clementcorporation.levosonusii.main.LevoSonusLogo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import kotlinx.coroutines.launch

private const val LOGO_SIZE = 50
private const val LOGIN_BTN_HEIGHT = 50
private const val LOGIN_BTN_WIDTH = 200
private val ENABLED_BUTTON_COLOR = Color(0xFF32527B)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(navController: NavController) {
    val employeeId = remember{
        mutableStateOf("")
    }
    val equipmentId = remember{
        mutableStateOf("")
    }
    val isLoginButtonEnabled = remember{
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val scrollToPosition = remember { mutableStateOf(0F) }
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(PADDING),
        elevation = ELEVATION,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(CURVATURE)
    ) {
        Column(
            modifier = Modifier.padding(PADDING).verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LevoSonusLogo(size = LOGO_SIZE.dp)
            LSTextField(userInput = employeeId, label = stringResource(id = R.string.label_employee_id),
                onAction = KeyboardActions {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(scrollToPosition.value.toInt())
                    }
                    defaultKeyboardAction(ImeAction.Next)
                }
            ) {
                employeeId.value = it
                isLoginButtonEnabled.value = employeeId.value.isNotEmpty() && equipmentId.value.isNotEmpty()
            }
            LSTextField(
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    scrollToPosition.value = coordinates.positionInParent().y
                },
                userInput = equipmentId,
                label = stringResource(id = R.string.label_equipment_id),
                imeAction = ImeAction.Done,
                onAction = KeyboardActions {
                    navController.navigate(LevoSonusScreens.HomeScreen.name)
                }
            ) {
                equipmentId.value = it
                isLoginButtonEnabled.value = employeeId.value.isNotEmpty() && equipmentId.value.isNotEmpty()
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
                navController.navigate(LevoSonusScreens.HomeScreen.name) //if credentials ok
            }) {
                Text(
                    text = stringResource(id = R.string.btn_text_login),
                    color = Color.White,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}

/*
TODO:
create login function
create login view model
complete if/else credential statement on SplashScreen
scroll to focused view when "Next" is clicked on the keyboard
    -https://stackoverflow.com/questions/67586828/jetpack-compose-code-to-scroll-down-to-the-position-of-a-specific-ui-element-on
    -make Column a LazyColumn and make each of the userInputs an item
 */