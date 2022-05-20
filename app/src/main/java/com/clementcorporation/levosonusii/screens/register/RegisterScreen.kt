package com.clementcorporation.levosonusii.screens.register

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.LSTextField
import com.clementcorporation.levosonusii.main.LevoSonusLogo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens

private const val LOGO_SIZE = 50
private const val REGISTER_BTN_HEIGHT = 50
private const val REGISTER_BTN_WIDTH = 200
private val ENABLED_BUTTON_COLOR = Color(0xFF32527B)

@Composable
fun RegisterScreen(navController: NavController) {
    val viewModel: RegisterViewModel = viewModel()
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
            .padding(Constants.PADDING),
        elevation = Constants.ELEVATION,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(Constants.CURVATURE)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 50.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LevoSonusLogo(LOGO_SIZE.dp)
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
            LSTextField(
                userInput = password,
                label = stringResource(id = R.string.label_password),
                imeAction = ImeAction.Done,
                onAction = KeyboardActions {
                    createUser(viewModel, navController, email, password) {
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                    }
                }
            ) {
                password.value = it
                isRegisterButtonEnabled.value = email.value.isNotEmpty() && password.value.isNotEmpty()
                        && firstName.value.isNotEmpty() && lastName.value.isNotEmpty()
            }
            Button(
                modifier = Modifier
                    .height(REGISTER_BTN_HEIGHT.dp)
                    .width(REGISTER_BTN_WIDTH.dp),
                shape = RoundedCornerShape(Constants.CURVATURE),
                elevation = ButtonDefaults.elevation(defaultElevation = Constants.ELEVATION),
                enabled = isRegisterButtonEnabled.value,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = ENABLED_BUTTON_COLOR,
                    disabledBackgroundColor = Color.LightGray
                ),
                onClick = {
                    createUser(viewModel, navController, email, password) {
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
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
                val prefaceText = "Already Signed Up?"
                val linkedText = "Login"
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
password: MutableState<String>, home: () -> Unit) {
    viewModel.createUserWithEmailAndPassword(
        context = navController.context,
        email = email.value,
        password = password.value,
        home = {
            navController.navigate(LevoSonusScreens.HomeScreen.name)
        }
    )
}