package com.clementcorporation.levosonusii.presentation.equipment.scanners

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.domain.use_cases.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannersScreenViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase
): ViewModel() {

    val showProgressBar = mutableStateOf(true)
    val expandMenu = mutableStateOf(false)
    fun signOut(navigate: () -> Unit) {
        viewModelScope.launch {
            showProgressBar.value = true
            expandMenu.value = false
            signOutUseCase.invoke(navigate)
        }
    }
}