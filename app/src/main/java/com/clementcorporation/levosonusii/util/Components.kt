package com.clementcorporation.levosonusii.util

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.util.Constants.CURVATURE
import com.clementcorporation.levosonusii.util.Constants.ELEVATION
import com.clementcorporation.levosonusii.util.Constants.LS_BLUE
import com.clementcorporation.levosonusii.util.Constants.PADDING
import kotlinx.coroutines.launch

private const val LOGO_DESCRIPTION = "LevoSonus Logo"
@Composable
fun LevoSonusLogo(size: Dp = 96.dp, showText: Boolean = true) {
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier.size(size),
            shape = CircleShape,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
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
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = Color.Gray
            )
        }
    }
}

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
        if (profilePicUrl?.isNotEmpty() == true) {
            LSProfileIcon(
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.LightGray, CircleShape)
                    .clickable(onClick = onClickLeftIcon),
                imageUrl = profilePicUrl
            )
        } else {
            IconButton(
                onClick = {},
                modifier = Modifier,
                enabled = true,
                colors = IconButtonDefaults.iconButtonColors(),
                interactionSource = null,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.ArrowCircleLeft, contentDescription = "Back Button")

            }
//            Image(
//                modifier = Modifier
//                    .rotate(180f)
//                    .size(35.dp)
//                    .clip(CircleShape)
//                    .clickable(onClick = onClickLeftIcon),
//                painter = painterResource(id = android.),
//                contentDescription = "Profile Picture",
//                contentScale = ContentScale.Crop
//            )
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
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.menu_item_account_label)) },
                    onClick = { /* Handle refresh! */ }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.menu_item_settings_label)) },
                    onClick = { /* Handle settings! */ }
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.menu_item_sign_out_label)) },
                    onClick = onClickSignOut
                )
            }
        }
    } 
}

@Composable
fun LSProfileIcon(modifier: Modifier, imageUrl: String) {
    Image(
        modifier = modifier,
        painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = imageUrl).apply(block = fun ImageRequest.Builder.() {
                    crossfade(true)
                    placeholder(R.drawable.levosonus_rocket_logo)
                }).build()
            ),
        contentDescription = "Profile Picture",
        contentScale = ContentScale.Crop
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LSFAB(onClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Top
    ){
        val infoNote = stringResource(R.string.ls_fab_tooltip_note)
        val tooltipState = rememberTooltipState(
            initialIsVisible = true,
            isPersistent = true
        )
        val coroutineScope = rememberCoroutineScope()
        TooltipBox(
            positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Start),
            tooltip = {
                PlainTooltip(
                    shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp),
                    containerColor = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = infoNote,
                        color = LS_BLUE
                    )
                }
            },
            state = tooltipState
        ) {}
        SideEffect {
            coroutineScope.launch {
                tooltipState.show()
            }
        }
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(LS_BLUE)
                    .blur(radius = 20.dp)
                    .alpha(0.7f)
            )
            FloatingActionButton(
                modifier = Modifier.zIndex(1f),
                shape = FloatingActionButtonDefaults.largeShape,
                onClick = onClick,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = ELEVATION.dp),
            ) {
                LevoSonusLogo(size = 56.dp, showText = false)
            }
        }
    }
}

@Composable
fun LSSurface(content : @Composable () -> Unit) {
    Surface (
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
            .padding(top = 16.dp)
            .windowInsetsPadding(WindowInsets.navigationBars),
        shadowElevation = ELEVATION.dp,
        shape = RoundedCornerShape(CURVATURE.dp),
        content = content
    )
}

@Composable
fun NavTile(title: String, icon: Int = R.drawable.scanner_icon, showIcon: MutableState<Boolean> = mutableStateOf(false), onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PADDING.dp)
            .background(color = Color.White)
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(ELEVATION.dp),
        shape = RoundedCornerShape(CURVATURE.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (showIcon.value) {
                Icon(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(PADDING.dp),
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowRight,
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
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Black,
            focusedBorderColor = Color.Blue,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = LS_BLUE
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
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Black,
            focusedBorderColor = Color.Blue,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = LS_BLUE
        ),
        shape = RoundedCornerShape(CURVATURE.dp),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
        keyboardActions = onAction,
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LSAlertDialog(showAlertDialog: MutableState<Boolean>, dialogTitle: String,
    dialogBody: MutableState<String> = mutableStateOf(""), onPositiveButtonClicked: () -> Unit = {},
                  onNegativeButtonClicked: () -> Unit = {}
) {
    BasicAlertDialog(
        modifier = Modifier
            .background(color = Color.White)
            .clip(RoundedCornerShape(CURVATURE.dp))
            .fillMaxWidth(.9f)
            .fillMaxHeight(
                if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
                    0.6f else 0.33f
            ),
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = {
            showAlertDialog.value = false
        },
        content = {
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
                        colors = ButtonDefaults.buttonColors()//(backgroundColor = LS_BLUE)
                    ) {
                        Text(text = stringResource(id = R.string.alert_dialog_positive_button_text), color = Color.White)
                    }
                    Button(
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f),
                        onClick = onNegativeButtonClicked,
                        colors = ButtonDefaults.buttonColors()//(backgroundColor = LS_BLUE)
                    ) {
                        Text(text = stringResource(id = R.string.alert_dialog_negative_button_text), color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    )
}