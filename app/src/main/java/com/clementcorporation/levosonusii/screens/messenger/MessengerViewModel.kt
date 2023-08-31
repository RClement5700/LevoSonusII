package com.clementcorporation.levosonusii.screens.messenger

import android.content.res.Resources
import android.icu.text.SimpleDateFormat
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.BODY
import com.clementcorporation.levosonusii.main.Constants.DATE
import com.clementcorporation.levosonusii.main.Constants.DATE_FORMAT
import com.clementcorporation.levosonusii.main.Constants.TIME
import com.clementcorporation.levosonusii.main.Constants.TIME_FORMAT
import com.clementcorporation.levosonusii.main.Constants.USER_1
import com.clementcorporation.levosonusii.main.Constants.USER_1_MESSAGES
import com.clementcorporation.levosonusii.main.Constants.USER_2
import com.clementcorporation.levosonusii.main.Constants.USER_2_MESSAGES
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MessengerViewModel(private val resources: Resources): ViewModel() {
    private val collection = FirebaseFirestore.getInstance().collection("HannafordFoods")
    private val document = collection.document(Constants.MESSENGER)
    val showProgressBar = mutableStateOf(false)
    val isInEditMode = mutableStateOf(false)
    private val _messengerEventsLiveData = MutableLiveData<MessengerEvents>()
    val messengerEventsLiveData: LiveData<MessengerEvents> get() = _messengerEventsLiveData

    @OptIn(ExperimentalMaterialApi::class)
    fun showBottomSheet(coroutineScope: CoroutineScope, bottomSheetState: ModalBottomSheetState) {
        viewModelScope.launch {
            withContext(coroutineScope.coroutineContext) {
                bottomSheetState.show()
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    fun hideBottomSheet(coroutineScope: CoroutineScope, bottomSheetState: ModalBottomSheetState) {
        viewModelScope.launch {
            withContext(coroutineScope.coroutineContext) {
                bottomSheetState.hide()
            }
        }
    }

    fun retrieveMessages(userInfo: LSUserInfo) {
        viewModelScope.launch {
            showProgressBar.value = true
            val messages = arrayListOf<MessengerListItem>()
            document.get().addOnSuccessListener { threads ->
                var user1 = ""
                var user2 = ""
                var user1Messages: Map<*, *>? = null
                var user2Messages: Map<*, *>? = null
                userInfo.messengerIds.forEach { usersThreadId ->
                    threads.data?.forEach { thread ->
                        if (thread.key == usersThreadId) {
                            val threadDetails = thread.value as Map<*, *>
                            var threadId = ""
                            var messengerItemDate = ""
                            var messengerItemTime = ""
                            var messengerItemMessage = ""
                            threadDetails.forEach { threadDetail ->
                                threadId = threadDetail.key as String
                                when (threadDetail.key) {
                                    USER_1 -> user1 = threadDetail.value as String
                                    USER_2 -> user2 = threadDetail.value as String
                                    USER_1_MESSAGES -> user1Messages = threadDetail.value as Map<*, *>
                                    USER_2_MESSAGES -> user2Messages = threadDetail.value as Map<*, *>
                                }
                            }
                            val u1FirstMessage = user1Messages?.entries?.firstOrNull()
                            var u1Body = ""
                            var u1Date = ""
                            var u1Time = ""
                            val messageDetails = u1FirstMessage?.value as Map<*, *>
                            messageDetails.forEach { detail ->
                                when (detail.key as String) {
                                    DATE -> u1Date = detail.value as String
                                    TIME -> u1Time = detail.value as String
                                    BODY -> u1Body = detail.value as String
                                }
                            }
                            val u2FirstMessage = user2Messages?.entries?.firstOrNull()
                            var u2Body = ""
                            var u2Date = ""
                            var u2Time = ""
                            val message2Details = u2FirstMessage?.value as Map<*, *>
                            message2Details.forEach { detail ->
                                when (detail.key as String) {
                                    DATE -> u2Date = detail.value as String
                                    TIME -> u2Time = detail.value as String
                                    BODY -> u2Body = detail.value as String
                                }
                            }
                            val timeFormatter = SimpleDateFormat(TIME_FORMAT, Locale.US)
                            val formatter = SimpleDateFormat(DATE_FORMAT, Locale.US)
                            val u1MessageDate: Date = formatter.parse(u1Date)
                            val u2MessageDate: Date = formatter.parse(u2Date)
                            val u1MessageTime = timeFormatter.parse(u1Time)
                            val u2MessageTime = timeFormatter.parse(u2Time)
                            messengerItemMessage =
                                if (u1MessageDate.before(u2MessageDate)) {
                                    messengerItemDate = u1Date
                                    messengerItemTime = u1Time
                                    u1Body
                                }
                                else if (u2MessageDate.before(u1MessageDate)) {
                                    messengerItemDate = u2Date
                                    messengerItemTime = u2Time
                                    u2Body
                                }
                                else {
                                    if (u1MessageTime.before(u2MessageTime)) {
                                        messengerItemDate = u1Date
                                        messengerItemTime = u1Time
                                        u1Body
                                    }
                                    else {
                                        messengerItemDate = u2Date
                                        messengerItemTime = u2Time
                                        u2Body
                                    }
                                }
                            messages.add(MessengerListItem(
                                threadId = threadId,
                                message = messengerItemMessage,
                                date = messengerItemDate,
                                time = messengerItemTime,
                                user1 = user1,
                                user2 = user2
                            ))
                        }
                    }
                }
                _messengerEventsLiveData.postValue(MessengerEvents.OnMessagesRetrieved(messages))
                showProgressBar.value = false
            }
        }
    }

    private fun onMessageReceived() {
        viewModelScope.launch {
            showProgressBar.value = true
            _messengerEventsLiveData.postValue(MessengerEvents.OnMessageReceived(MessengerListItem(
                "", "", "", "", "", "", ""
            )))
            showProgressBar.value = false
        }
    }
}

sealed class MessengerEvents {
    class OnMessagesRetrieved(val messages: List<MessengerListItem>): MessengerEvents()
    class OnMessageReceived(val message: MessengerListItem): MessengerEvents()
    class OnMessageSent(): MessengerEvents()
    class OnMessageClicked(val message: MessengerListItem): MessengerEvents()
}