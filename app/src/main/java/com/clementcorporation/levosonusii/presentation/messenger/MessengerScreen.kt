package com.clementcorporation.levosonusii.presentation.messenger

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.domain.models.MessengerListItem
import com.clementcorporation.levosonusii.util.Constants
import com.clementcorporation.levosonusii.util.Constants.LS_BLUE
import com.clementcorporation.levosonusii.util.Constants.PADDING
import com.clementcorporation.levosonusii.util.LSAppBar
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import kotlinx.coroutines.CoroutineScope

private const val TAG = "MessengerScreen"
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessengerScreen(navController: NavController) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: MessengerViewModel = hiltViewModel()
    val bottomSheetScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    BackHandler {
        navController.popBackStack()
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        elevation = Constants.ELEVATION.dp,
        color = Color.White,
        shape = RoundedCornerShape(Constants.CURVATURE.dp)
    ) {
        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetContent = {
                    ThreadBottomSheetContent(
                        viewModel.showProgressBar,
                        lifecycleOwner,
                        viewModel,
                        bottomSheetScope,
                        bottomSheetState,
                        ""
                    )
            },
            content = {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    backgroundColor = Color.White,
                    topBar = {
                        LSAppBar(navController = navController, expandMenu = viewModel.expandMenu,
                            title = stringResource(id = R.string.messenger_screen_toolbar_title),
                            profilePicUrl = null,
                            onClickSignOut = {
                                viewModel.signOut {
                                    navController.clearBackStack(LevoSonusScreens.LoginScreen.name)
                                }
                            },
                            onClickLeftIcon = {
                                navController.popBackStack()
                            }
                        )
                    },
                ) { paddingValues ->
                    Log.e(TAG, paddingValues.toString())
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.showProgressBar.value) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .zIndex(1f)
                                    .size(50.dp),
                                strokeWidth = 2.dp,
                                color = Constants.LS_BLUE
                            )
                        }
                    }
                    Column {
                        Spacer(modifier = Modifier.height(4.dp))
                        Divider(
                            color = Constants.LS_BLUE,
                            thickness = 2.dp,
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        viewModel.messengerEventsLiveData.observe(lifecycleOwner) { event ->
                            when (event) {
                                is MessengerEvents.OnMessageSent -> {}
                                is MessengerEvents.OnMessageReceived -> {}
                                is MessengerEvents.OnMessagesRetrieved -> {
                                    items(event.messages) { message ->
                                        MessageListItemTile(
                                            viewModel = viewModel,
                                            messengerListItem = message
                                        ) {
                                            viewModel.showBottomSheet(bottomSheetScope, bottomSheetState)
                                        }
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                }

            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThreadBottomSheetContent(
    showProgressBar: MutableState<Boolean>,
    lifecycleOwner: LifecycleOwner,
    viewModel: MessengerViewModel,
    bottomSheetScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    receiverPicUrl: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PADDING.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .zIndex(1f)
                .size(35.dp)
                .clip(CircleShape)
                .border(2.dp, LS_BLUE, CircleShape),
            painter = if (receiverPicUrl.isNullOrEmpty()) {
                painterResource(id = R.drawable.levosonus_rocket_logo)
            } else {
                rememberImagePainter(data = receiverPicUrl, builder = {
                    crossfade(false)
                    placeholder(R.drawable.levosonus_rocket_logo)
                })
            },
            contentDescription = "Receiver Picture",
            contentScale = ContentScale.Crop,
        )
        Text(
            text = "Rohan",
            color = LS_BLUE,
            fontWeight = FontWeight.Bold,
        )
        Divider(
            color = LS_BLUE,
            thickness = 2.dp,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (showProgressBar.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .zIndex(1f)
                    .size(50.dp),
                strokeWidth = 2.dp,
                color = Constants.LS_BLUE
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp, 4.dp, 0.dp, 0.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
            ) {
                //items will be messages from the thread

            }
            //add text input box
            Button( //to be replace with IconButton containing send icon
                modifier = Modifier
                    .padding(Constants.PADDING.dp)
                    .fillMaxWidth()
                    .height(Constants.BTN_HEIGHT.dp),
                shape = RoundedCornerShape(Constants.CURVATURE),
                elevation = ButtonDefaults.elevation(defaultElevation = Constants.ELEVATION.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = LS_BLUE,
                    disabledBackgroundColor = Color.LightGray
                ),
                onClick = { }) {
                if (showProgressBar.value) {
                    CircularProgressIndicator(strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text(
                        text = stringResource(id = R.string.btn_text_send),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        //put message box and send button here
    }
}

@Composable
fun SendersUserIcon(modifier: Modifier, url: String) {
    Image(
        modifier = modifier,
        painter = rememberImagePainter(data = url, builder = {
            crossfade(false)
            placeholder(R.drawable.levosonus_rocket_logo)
        }),
        contentDescription = "Sender\'s Icon",
        contentScale = ContentScale.Crop
    )
}

@Composable
fun MessageListItemTile(viewModel: MessengerViewModel, messengerListItem: MessengerListItem, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Constants.PADDING.dp)
            .clickable(onClick = onClick),
        elevation = Constants.ELEVATION.dp,
        shape = RoundedCornerShape(Constants.CURVATURE.dp),
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    SendersUserIcon(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(Constants.PADDING.dp),
                        url = messengerListItem.userIconUrl
                    )
                    Text(
                        modifier = Modifier.padding(Constants.PADDING.dp),
                        text = messengerListItem.user2, //sender name
                        color = Constants.LS_BLUE,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(Constants.PADDING.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(Constants.PADDING.dp),
                        text = messengerListItem.message,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        modifier = Modifier.padding(Constants.PADDING.dp),
                        text = messengerListItem.date,
                        color = Color.Black,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                    )

                    Text(
                        modifier = Modifier.padding(Constants.PADDING.dp),
                        text = messengerListItem.time,
                        color = Color.Black,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                    )
                }
            }
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    tint = Constants.LS_BLUE,
                    imageVector = Icons.Default.ArrowRight,
                    contentDescription = ""
                )
            }
        }
    }
}