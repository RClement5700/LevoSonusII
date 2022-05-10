package com.clementcorporation.levosonusii.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LSTextField
import com.clementcorporation.levosonusii.main.LevoSonusLogo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens

private const val EMP_ID_PLACEHOLDER = "Employee Id"
private const val EQUIP_ID_PLACEHOLDER = "Equipment Id"
private const val LOGO_SIZE = 50
private const val LOGIN_BTN_HEIGHT = 50
private const val LOGIN_BTN_WIDTH = 200

@Composable
fun LoginScreen(navController: NavController) {
    val employeeId = remember{
        mutableStateOf("")
    }
    val equipmentId = remember{
        mutableStateOf("")
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
            modifier = Modifier.padding(PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LevoSonusLogo(size = LOGO_SIZE.dp)
            LSTextField(value = employeeId.value, label = EMP_ID_PLACEHOLDER) {
                employeeId.value = it
            }
            LSTextField(value = equipmentId.value, label = EQUIP_ID_PLACEHOLDER) {
                equipmentId.value = it
            }
            Button(
                modifier = Modifier
                    .height(LOGIN_BTN_HEIGHT.dp)
                    .width(LOGIN_BTN_WIDTH.dp),
                shape = RoundedCornerShape(CURVATURE),
                elevation = elevation(defaultElevation = ELEVATION),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF32527B),
                    disabledBackgroundColor = Color.Gray
                ),
                onClick = {
                navController.navigate(LevoSonusScreens.HomeScreen.name) //if credentials ok
            }) {
                Text(text = "Login", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}