package com.clementcorporation.levosonusii.screens.messages

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun MessagesScreen(navController: NavController, lifecycleOwner: LifecycleOwner) {
    val context = LocalContext.current
    val messagesViewModel = viewModel { MessengerViewModel(context.resources) }

    messagesViewModel.messagesEventsLiveData.observe(lifecycleOwner) { event ->
        when (event) {
            is MessagesEvents.OnMessageSent -> {}
            is MessagesEvents.OnMessageReceived -> {}
            is MessagesEvents.OnMessageClicked -> {
                //this is when a message is clicked from the messages list
                //show content
            }
            else -> { //do nothing}
            }
        }
    }
}