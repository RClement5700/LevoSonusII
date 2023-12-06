package com.clementcorporation.levosonusii.data.remote

import android.content.res.Resources
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.domain.repositories.DepartmentsRepository
import com.clementcorporation.levosonusii.util.Constants
import com.clementcorporation.levosonusii.util.LSUserInfo
import com.clementcorporation.levosonusii.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest

private const val FORKLIFT = "Forklift"
private const val ORDER_PICKER = "Order Picker"

class DepartmentsRepositoryImpl (
    private val resources: Resources,
    private val sessionDataStore: DataStore<LSUserInfo>
) : DepartmentsRepository {

    override fun fetchDepartmentsData(): Flow<Resource<List<com.clementcorporation.levosonusii.presentation.departments.Department>>> =
        callbackFlow {
            sessionDataStore.data.collectLatest { userInfo ->
                val document = FirebaseFirestore.getInstance()
                    .collection(userInfo.organization.name)
                    .document(Constants.DEPARTMENTS)
                document.get().addOnSuccessListener { snapshot ->
                    val departments = arrayListOf<com.clementcorporation.levosonusii.presentation.departments.Department>()
                    snapshot.data?.keys?.forEach { key ->
                        val department = snapshot.data?.get(key) as Map<*, *>
                        val title = department[Constants.TITLE] as String
                        val orderPickerCount = department[Constants.OP_COUNT] as String
                        val forkliftCount = department[Constants.FORKLIFT_COUNT] as String
                        val remainingOrders = department[Constants.REMAINING_ORDERS] as String
                        val icon = department[Constants.ICON_URL] as String
                        departments.add(
                            com.clementcorporation.levosonusii.presentation.departments.Department(
                                id = key,
                                title = title,
                                orderPickersCount = orderPickerCount,
                                forkliftCount = forkliftCount,
                                remainingOrders = remainingOrders,
                                iconUrl = icon,
                                isSelected = mutableStateOf(userInfo.departmentId == key)
                            )
                        )
                        departments.sortBy { it.id }
                    }
                    trySend(
                        if (departments.isNotEmpty())
                            Resource.Success(departments.toList())
                        else Resource.Error(resources.getString(R.string.departments_screen_error_message))
                    )
                    close()
                }
            }
            awaitClose {
                cancel()
            }
        }

    override suspend fun subtractOrderPickerFromDepartment() {
        sessionDataStore.data.collectLatest { userInfo ->
            val document = FirebaseFirestore.getInstance()
                .collection(userInfo.organization.name)
                .document(Constants.DEPARTMENTS)
            document.get().addOnSuccessListener { task ->
                val departmentDetails = task.data?.get(userInfo.departmentId) as Map<*, *>
                val currentTitle = departmentDetails[Constants.TITLE] as String
                var currentForkliftCount = departmentDetails[Constants.FORKLIFT_COUNT] as String
                var currentOrderPickerCount = departmentDetails[Constants.OP_COUNT] as String
                val currentRemainingOrders =
                    departmentDetails[Constants.REMAINING_ORDERS] as String
                val currentIconUrl = departmentDetails[Constants.ICON_URL] as String
                currentForkliftCount = if (userInfo.operatorType == FORKLIFT)
                    currentForkliftCount.toInt().minus(1).toString()
                else currentForkliftCount
                currentOrderPickerCount = if (userInfo.operatorType == ORDER_PICKER)
                    currentOrderPickerCount.toInt().minus(1).toString()
                else currentOrderPickerCount
                document.update(
                    userInfo.departmentId,
                    mapOf(
                        Constants.FORKLIFT_COUNT to currentForkliftCount,
                        Constants.OP_COUNT to currentOrderPickerCount,
                        Constants.REMAINING_ORDERS to currentRemainingOrders,
                        Constants.TITLE to currentTitle,
                        Constants.ICON_URL to currentIconUrl
                    )
                )
            }
        }
    }

    override suspend fun addOrderPickerToDepartment(currentDepartmentId: String) {
        sessionDataStore.data.collectLatest { userInfo ->
            val document = FirebaseFirestore.getInstance()
                .collection(userInfo.organization.name)
                .document(Constants.DEPARTMENTS)
            document.get().addOnSuccessListener { task ->
                val departmentDetails = task.data?.get(currentDepartmentId) as Map<*, *>
                val currentTitle = departmentDetails[Constants.TITLE] as String
                var currentForkliftCount = departmentDetails[Constants.FORKLIFT_COUNT] as String
                var currentOrderPickerCount = departmentDetails[Constants.OP_COUNT] as String
                val currentRemainingOrders =
                    departmentDetails[Constants.REMAINING_ORDERS] as String
                val currentIconUrl = departmentDetails[Constants.ICON_URL] as String
                currentForkliftCount = if (userInfo.operatorType == FORKLIFT)
                    currentForkliftCount.toInt().plus(1).toString()
                else currentForkliftCount
                currentOrderPickerCount = if (userInfo.operatorType == ORDER_PICKER)
                    currentOrderPickerCount.toInt().plus(1).toString()
                else currentOrderPickerCount
                document.update(
                    currentDepartmentId,
                    mapOf(
                        Constants.FORKLIFT_COUNT to currentForkliftCount,
                        Constants.OP_COUNT to currentOrderPickerCount,
                        Constants.REMAINING_ORDERS to currentRemainingOrders,
                        Constants.TITLE to currentTitle,
                        Constants.ICON_URL to currentIconUrl
                    )
                )
            }
        }
    }
}