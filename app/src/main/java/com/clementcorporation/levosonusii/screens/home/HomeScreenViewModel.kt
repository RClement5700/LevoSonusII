package com.clementcorporation.levosonusii.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val sessionDataStore: DataStore<LSUserInfo>):
    ViewModel() {
    val expandMenu = mutableStateOf(false)
    val showProgressBar = mutableStateOf(false)

    fun getDataStore() = sessionDataStore
    suspend fun signOut() {
        Firebase.auth.signOut()
        sessionDataStore.updateData {
            LSUserInfo()
        }
        showProgressBar.value = true
        expandMenu.value = false
    }
}