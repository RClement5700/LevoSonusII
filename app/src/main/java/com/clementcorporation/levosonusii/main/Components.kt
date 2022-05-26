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
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.PADDING

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
fun LSAppBar(navController: NavController, expandMenu: MutableState<Boolean>, employeeName: String,
             onClickProfilePic: () -> Unit = {}, onClickAlertBtn: () -> Unit = {},
             onClickSignOut: () -> Unit = {}
) {
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
                .size(35.dp)
                .clip(CircleShape)
                .border(2.dp, Color.LightGray, CircleShape)
                .clickable(onClick = onClickProfilePic)
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
            onClick = onClickAlertBtn
        ) {
            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Alert Button")
        }
        Box(contentAlignment = Alignment.BottomEnd) {
            IconButton(
                onClick = {
                    expandMenu.value = !expandMenu.value
                }
            ) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu Button")
            }
            DropdownMenu(
                expanded = expandMenu.value,
                onDismissRequest = { expandMenu.value = false }
            ) {
                DropdownMenuItem(onClick = { /* Handle refresh! */ }) {
                    Text("Account")
                }
                DropdownMenuItem(onClick = { /* Handle settings! */ }) {
                    Text("Settings")
                }
                Divider()
                DropdownMenuItem(onClick = onClickSignOut) {
                    Text("Sign Out")
                }
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
        LevoSonusLogo(size = 50.dp, showText = false)
    }
}

@Composable
fun NavTile(title: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PADDING.dp)
            .clickable(onClick = onClick),
        elevation = Constants.ELEVATION.dp,
        shape = RoundedCornerShape(CURVATURE.dp),
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(PADDING.dp),
                text = title,
                color = Constants.ENABLED_BUTTON_COLOR,
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    tint = Constants.ENABLED_BUTTON_COLOR,
                    imageVector = Icons.Default.ArrowRight,
                    contentDescription = "Open Voice Profile"
                )
            }
        }
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
@Composable
fun LSPasswordTextField(userInput: MutableState<String> = mutableStateOf(""), label: String = "",
                onAction: KeyboardActions = KeyboardActions.Default,
                onValueChange: (String) -> Unit = {}
) {
    var passwordVisibility: Boolean by remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PADDING.dp),
        value = userInput.value,
        onValueChange = onValueChange,
        trailingIcon = {
            IconButton(onClick = {
                passwordVisibility = !passwordVisibility
            }) {
                Icon(
                    imageVector = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    tint = Color.Black,
                    contentDescription = "Show Password"
                )
            }
        },
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
        keyboardActions = onAction,
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
    )
}