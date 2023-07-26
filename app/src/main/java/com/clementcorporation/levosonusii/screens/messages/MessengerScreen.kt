package com.clementcorporation.levosonusii.screens.messages

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.clementcorporation.levosonusii.screens.equipment.TAG
import com.clementcorporation.levosonusii.screens.home.HomeScreenViewModel

@Composable
fun MessengerScreen(navController: NavController, lifecycleOwner: LifecycleOwner) {
    val context = LocalContext.current
    val viewModel: MessengerViewModel = viewModel { MessengerViewModel(context.resources) }
    val hsViewModel: HomeScreenViewModel = hiltViewModel()
    val userInfo = hsViewModel.getUserInfo().data.collectAsState(initial = LSUserInfo()).value
    viewModel.retrieveMessages(userInfo)
    BackHandler {
        navController.popBackStack()
        navController.navigate(LevoSonusScreens.HomeScreen.name)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        elevation = Constants.ELEVATION.dp,
        color = Color.White,
        shape = RoundedCornerShape(Constants.CURVATURE.dp)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.White,
            topBar = {
                LSAppBar(navController = navController, expandMenu = hsViewModel.expandMenu,
                    title = "Messages",
                    profilePicUrl = null,
                    onClickSignOut = {
                        hsViewModel.signOut()
                        navController.popBackStack()
                        navController.navigate(LevoSonusScreens.LoginScreen.name)

                    },
                    onClickLeftIcon = {
                        navController.popBackStack()
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                    }
                )
            }
        ) { paddingValue ->
            Log.e(TAG, paddingValue.toString())
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
                viewModel.messagesEventsLiveData.observe(lifecycleOwner) { event ->
                    when (event) {
                        is MessagesEvents.OnMessageSent -> {}
                        is MessagesEvents.OnMessageReceived -> {}
                        is MessagesEvents.OnMessagesRetrieved -> {
                            items(event.messages) { message ->
                                MessageListItemTile(viewModel = viewModel, messengerListItem = message) {
                                    viewModel.openMessageThread(message.threadId)
                                }
                            }
                        }
                        is MessagesEvents.OnMessageClicked -> {
                            navController.navigate(LevoSonusScreens.MessagesScreen.name)
                        }
                        else -> { //do nothing}
                        }
                    }
                }
            }
        }
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