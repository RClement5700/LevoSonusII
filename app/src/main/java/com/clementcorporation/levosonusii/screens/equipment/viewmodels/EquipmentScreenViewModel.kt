package com.clementcorporation.levosonusii.screens.equipment.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clementcorporation.levosonusii.screens.equipment.model.Equipment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EquipmentScreenViewModel: ViewModel() {
    init {
        getElectricPalletJacks()
        getForklifts()
    }
    val forklifts = MutableStateFlow(arrayListOf<Equipment.Forklift>())
    val electricPalletJx = MutableStateFlow(arrayListOf<Equipment.ElectricPalletJack>())

    private fun getForklifts() {
        viewModelScope.launch {
            FirebaseFirestore.getInstance().collection("HannafordFoods")
                .document("equipment")
                .get()
                .addOnSuccessListener { document ->
                    val data = document.get("forklifts") as List<*>
                    data.forEach { serialNumber ->
                        forklifts.update {
                            it.add(Equipment.Forklift(serialNumber as String))
                            it
                        }
                    }
                }
        }
    }
    private fun getElectricPalletJacks() {
        viewModelScope.launch {
            FirebaseFirestore.getInstance().collection("HannafordFoods")
                .document("equipment")
                .get()
                .addOnSuccessListener { document ->
                    val data = document.get("electricPalletJacks") as List<*>
                    data.forEach { serialNumber ->
                        electricPalletJx.update {
                            it.add(Equipment.ElectricPalletJack(serialNumber as String))
                            it
                        }
                    }
                }
        }
    }
}