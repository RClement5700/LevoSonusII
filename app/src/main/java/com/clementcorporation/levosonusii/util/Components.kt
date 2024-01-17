package com.clementcorporation.levosonusii.util

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.util.Constants.CURVATURE
import com.clementcorporation.levosonusii.util.Constants.ELEVATION
import com.clementcorporation.levosonusii.util.Constants.LS_BLUE
import com.clementcorporation.levosonusii.util.Constants.PADDING

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

@SuppressLint("PrivateResource")
@Composable
fun LSAppBar(navController: NavController, expandMenu: MutableState<Boolean>, title: String,
             profilePicUrl: String?, onClickLeftIcon: () -> Unit = {}, onClickAlertBtn: () -> Unit = {},
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
        if (profilePicUrl != null) {
            LSProfileIcon(
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.LightGray, CircleShape)
                    .clickable(onClick = onClickLeftIcon),
                imageUrl = profilePicUrl
            )
        } else {
            Image(
                modifier = Modifier
                    .rotate(180f)
                    .size(35.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClickLeftIcon),
                painter = painterResource(id = androidx.constraintlayout.widget.R.drawable.abc_ic_arrow_drop_right_black_24dp),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop
            )
        }
        Text(
            modifier = Modifier.padding(PADDING.dp),
            text = title,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
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
                    Text(stringResource(id = R.string.menu_item_account_label))
                }
                DropdownMenuItem(onClick = { /* Handle settings! */ }) {
                    Text(stringResource(id = R.string.menu_item_settings_label))
                }
                Divider()
                DropdownMenuItem(onClick = onClickSignOut) {
                    Text(stringResource(id = R.string.menu_item_sign_out_label))
                }
            }
        }
    } 
}

@Composable
fun LSProfileIcon(modifier: Modifier, imageUrl: String) {
    Image(
        modifier = modifier,
        painter = if (imageUrl.isNullOrEmpty()) {
            painterResource(id = R.drawable.levosonus_rocket_logo)
        } else {
            rememberImagePainter(data = imageUrl, builder = {
                crossfade(false)
                placeholder(R.drawable.levosonus_rocket_logo)
            })
        },
        contentDescription = "Profile Picture",
        contentScale = ContentScale.Crop
    )
}

@Composable
fun LSFAB(onClick: () -> Unit = {}) {
    FloatingActionButton(
        modifier = Modifier.zIndex(1f),
        onClick = onClick,
        shape = CircleShape,
        backgroundColor = LS_BLUE,
        elevation = FloatingActionButtonDefaults.elevation(),
        ) {
        LevoSonusLogo(size = 50.dp, showText = false)
    }
}

@Composable
fun SelectionTile(title: String? = null, icon: Int? = null,
    isSelected: MutableState<Boolean> = mutableStateOf(false)) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PADDING.dp)
            .clickable(onClick = {
                isSelected.value = true
            }),
        elevation = ELEVATION.dp,
        shape = RoundedCornerShape(CURVATURE.dp),
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            icon?.let {
                Icon(
                    modifier = Modifier.size(50.dp).padding(PADDING.dp),
                    tint = LS_BLUE,
                    painter = painterResource(id = it),
                    contentDescription = ""
                )
            }
            title?.let {
                Text(
                    modifier = Modifier.padding(PADDING.dp),
                    text = it,
                    color = LS_BLUE,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                modifier = Modifier.size(if (isSelected.value) 24.dp else 0.dp),
                tint = LS_BLUE,
                imageVector = Icons.Default.Check,
                contentDescription = "Selected Tile"
            )
        }
    }
}

@Composable
fun SelectableTile(title: String, icon: Int = R.drawable.scanner_icon, isSelected: MutableState<Boolean>,
                   onClick: () -> Unit = {}) {
    val backgroundColor = if (isSelected.value) Color.Cyan else Color.White
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PADDING.dp)
            .clickable(onClick = onClick),
        elevation = ELEVATION.dp,
        shape = RoundedCornerShape(CURVATURE.dp),
        backgroundColor = backgroundColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                modifier = Modifier.size(50.dp).padding(PADDING.dp),
                tint = LS_BLUE,
                painter = painterResource(id = icon),
                contentDescription = ""
            )
            Text(
                modifier = Modifier.padding(PADDING.dp),
                text = title,
                color = LS_BLUE,
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NavTile(title: String, icon: Int = R.drawable.scanner_icon, showIcon: MutableState<Boolean> = mutableStateOf(false), onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PADDING.dp)
            .clickable(onClick = onClick),
        elevation = ELEVATION.dp,
        shape = RoundedCornerShape(CURVATURE.dp),
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (showIcon.value) {
                Icon(
                    modifier = Modifier.size(50.dp).padding(PADDING.dp),
                    tint = LS_BLUE,
                    painter = painterResource(id = icon),
                    contentDescription = ""
                )
            }
            Text(
                modifier = Modifier.padding(PADDING.dp),
                text = title,
                color = LS_BLUE,
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    tint = LS_BLUE,
                    imageVector = Icons.Default.ArrowRight,
                    contentDescription = ""
                )
            }
        }
    }
}

@Composable
fun LSTextField(modifier: Modifier = Modifier, userInput: String = "",
                label: String = "", keyboardType: KeyboardType = KeyboardType.Text,
                imeAction: ImeAction = ImeAction.Next, onAction: KeyboardActions = KeyboardActions.Default,
                onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        modifier = modifier,
        value = userInput,
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
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = onAction
    )
}
@Composable
fun LSPasswordTextField(modifier: Modifier = Modifier, userInput: String = "", label: String = "",
                onAction: KeyboardActions = KeyboardActions.Default,
                onValueChange: (String) -> Unit = {}
) {
    var passwordVisibility: Boolean by remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = modifier,
        value = userInput,
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
        keyboardActions = onAction,
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
    )
}

@Composable
fun LSAlertDialog(showAlertDialog: MutableState<Boolean>, dialogTitle: String,
    dialogBody: MutableState<String> = mutableStateOf(""), onPositiveButtonClicked: () -> Unit = {},
                  onNegativeButtonClicked: () -> Unit = {}
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(.9f)
            .fillMaxHeight(if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
            0.6f else 0.33f),
        backgroundColor = Color.White,
        shape = RoundedCornerShape(CURVATURE.dp),
        properties = DialogProperties(),
        onDismissRequest = {
            showAlertDialog.value = false
        },
        buttons = {
            Column(modifier = Modifier.padding(PADDING.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                LevoSonusLogo(size = 40.dp, showText = true)
                Spacer(modifier = Modifier.weight(1f))
                Text(fontSize = 13.sp, fontWeight = FontWeight.Bold, text = dialogTitle)
                if (dialogBody.value.isNotEmpty()) {
                    Spacer(modifier = Modifier.weight(.1f))
                    Text(fontSize = 10.sp, fontWeight = FontWeight.Bold, text = dialogBody.value)
                }
                Spacer(modifier = Modifier.weight(.2f))
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f),
                        onClick = onPositiveButtonClicked,
                        colors = ButtonDefaults.buttonColors(backgroundColor = LS_BLUE)
                    ) {
                        Text(text = stringResource(id = R.string.alert_dialog_positive_button_text), color = Color.White)
                    }
                    Button(
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f),
                        onClick = onNegativeButtonClicked,
                        colors = ButtonDefaults.buttonColors(backgroundColor = LS_BLUE)
                    ) {
                        Text(text = stringResource(id = R.string.alert_dialog_negative_button_text), color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    )
}