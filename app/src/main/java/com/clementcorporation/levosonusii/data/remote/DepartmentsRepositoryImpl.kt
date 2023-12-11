package com.clementcorporation.levosonusii.data.remote

import android.content.res.Resources
import androidx.compose.runtime.mutableStateOf
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.domain.models.Department
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.repositories.DepartmentsRepository
import com.clementcorporation.levosonusii.util.Constants
import com.clementcorporation.levosonusii.util.Constants.OPERATOR_TYPE_ELECTRIC_PALLET_JACK
import com.clementcorporation.levosonusii.util.Constants.OPERATOR_TYPE_FORKLIFT
import com.clementcorporation.levosonusii.util.Response
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class DepartmentsRepositoryImpl (
    private val resources: Resources,
) : DepartmentsRepository {

    override fun fetchDepartmentsData(userInfo: LSUserInfo): Flow<Response<List<Department>>> =
        callbackFlow {
            val document = FirebaseFirestore.getInstance()
                .collection(userInfo.organization.name)
                .document(Constants.DEPARTMENTS)
            document.get().addOnSuccessListener { snapshot ->
                val departments = arrayListOf<Department>()
                snapshot.data?.keys?.forEach { key ->
                    val department = snapshot.data?.get(key) as Map<*, *>
                    val title = department[Constants.TITLE] as String
                    val orderPickerCount = department[Constants.OP_COUNT] as String
                    val forkliftCount = department[Constants.FORKLIFT_COUNT] as String
                    val remainingOrders = department[Constants.REMAINING_ORDERS] as String
                    val icon = department[Constants.ICON_URL] as String
                    departments.add(
                        Department(
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
                        Response.Success(departments.toList())
                    else Response.Error(resources.getString(R.string.departments_screen_error_message))
                )
                close()
            }
            awaitClose {
                cancel()
            }
        }

    override fun subtractOrderPickerFromDepartment(userInfo: LSUserInfo) {
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
                currentForkliftCount = if (userInfo.operatorType == OPERATOR_TYPE_FORKLIFT)
                    currentForkliftCount.toInt().minus(1).toString()
                else currentForkliftCount
                currentOrderPickerCount = if (userInfo.operatorType == OPERATOR_TYPE_ELECTRIC_PALLET_JACK)
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

    override fun addOrderPickerToDepartment(userInfo: LSUserInfo, currentDepartmentId: String) {
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
            currentForkliftCount = if (userInfo.operatorType == OPERATOR_TYPE_FORKLIFT)
                currentForkliftCount.toInt().plus(1).toString()
            else currentForkliftCount
            currentOrderPickerCount = if (userInfo.operatorType == OPERATOR_TYPE_ELECTRIC_PALLET_JACK)
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