package com.clementcorporation.levosonusii.presentation.messenger

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.MessengerListItem
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessengerViewModel @Inject constructor(
    private val userInfo: DataStore<LSUserInfo>,
    private val signOutUseCase: SignOutUseCase
    ): ViewModel() {
    private lateinit var collection: CollectionReference
    private lateinit var document: DocumentReference
    val showProgressBar = mutableStateOf(false)
    val expandMenu = mutableStateOf(false)
    val isInEditMode = mutableStateOf(false)
    private val _messengerEventsLiveData = MutableLiveData<MessengerEvents>()
    val messengerEventsLiveData: LiveData<MessengerEvents> get() = _messengerEventsLiveData

    init {
        viewModelScope.launch {
        }
    }

    fun signOut(navigate: () -> Unit) {
        viewModelScope.launch {
            showProgressBar.value = true
            expandMenu.value = false
            signOutUseCase.invoke(navigate)
        }
    }

    fun showBottomSheet() {
        viewModelScope.launch {

        }
    }

    fun hideBottomSheet() {
        viewModelScope.launch {

        }
    }

    private fun retrieveMessages() {
        viewModelScope.launch {

        }
    }

    private fun onMessageReceived() {
        viewModelScope.launch {
        }
    }
}

sealed class MessengerEvents {
    class OnMessagesRetrieved(val messages: List<MessengerListItem>): MessengerEvents()
    class OnMessageReceived(val message: MessengerListItem): MessengerEvents()
    class OnMessageSent(): MessengerEvents()
    class OnMessageClicked(val message: MessengerListItem): MessengerEvents()
}