package com.clementcorporation.levosonusii.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val LOGO_DESCRIPTION = "Levo Sonus Logo"
@Composable
fun LevoSonusLogo(size: Dp = 96.dp, showText: Boolean = true) {
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier.size(size),
            shape = CircleShape,
            elevation = 2.dp
        ) {
            Image(
                painter = painterResource(R.drawable.levosonus_rocket_logo),
                contentDescription = LOGO_DESCRIPTION,
                contentScale = ContentScale.Crop
            )
        }
        if (showText) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.h6,
                fontStyle = FontStyle.Italic,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LSAppBar(employeeName: String, onClickProfilePic: () -> Unit = {}, onClickAlertBtn: () -> Unit = {}
) {
    var expandMenu by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PADDING.dp)
            .background(shape = RoundedCornerShape(CURVATURE.dp), color = Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        LSProfileIcon(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(2.dp, Color.LightGray, CircleShape)
                .clickable {
                    onClickProfilePic
                }
        )
        Text(
            modifier = Modifier.padding(PADDING.dp),
            text = employeeName,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = {
                onClickAlertBtn
            }
        ) {
            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Menu Button")
        }
        IconButton(
            onClick = {
                expandMenu = true
            }
        ) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu Button")
        }
        DropdownMenu(
            offset = DpOffset(x = 100.dp, y = 0.dp), //x = Alignment.End ?
            expanded = expandMenu,
            onDismissRequest = { expandMenu = false }
        ) {
            DropdownMenuItem(onClick = { /* Handle refresh! */ }) {
                Text("Account")
            }
            DropdownMenuItem(onClick = { /* Handle settings! */ }) {
                Text("Settings")
            }
            Divider()
            DropdownMenuItem(onClick = { Firebase.auth.signOut()}) {
                Text("Sign Out")
            }
        }
    }
}

@Composable
fun LSProfileIcon(modifier: Modifier) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.levosonus_rocket_logo), //replace with user uploaded profile pic
        contentDescription = "Profile Picture",
        contentScale = ContentScale.Crop
    )
}

@Composable
fun LSFAB() {
    FloatingActionButton(
        onClick = { /*TODO - show VoiceInputWindow*/ },
        shape = CircleShape,
        backgroundColor = Constants.ENABLED_BUTTON_COLOR,
        elevation = FloatingActionButtonDefaults.elevation(),
        ) {
        LevoSonusLogo(50.dp, showText = false)
    }
}




@Composable
fun LSTextField(userInput: MutableState<String> = mutableStateOf(""), label: String = "",
                imeAction: ImeAction = ImeAction.Next, onAction: KeyboardActions = KeyboardActions.Default,
                onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PADDING.dp),
        value = userInput.value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = imeAction),
        keyboardActions = onAction
    )
}