package com.example.data.repository

import com.example.data.local.AddressEntity
import com.example.data.local.LaundromatDao
import com.example.data.local.OrderEntity
import com.example.data.local.UserEntity
import kotlinx.coroutines.flow.Flow

class LaundromatRepository(private val dao: LaundromatDao) {
    val userFlow: Flow<UserEntity?> = dao.getUserFlow()
    val addressesFlow: Flow<List<AddressEntity>> = dao.getAddressesFlow()
    val allOrdersFlow: Flow<List<OrderEntity>> = dao.getAllOrdersFlow()
    val activeOrdersFlow: Flow<List<OrderEntity>> = dao.getActiveOrdersFlow()

    fun getOrderByIdFlow(id: Int): Flow<OrderEntity?> = dao.getOrderByIdFlow(id)
    suspend fun getOrderById(id: Int): OrderEntity? = dao.getOrderById(id)

    suspend fun insertUser(user: UserEntity) = dao.insertUser(user)
    suspend fun insertAddress(address: AddressEntity) = dao.insertAddress(address)
    suspend fun deleteAddress(id: Int) = dao.deleteAddress(id)

    suspend fun insertOrder(order: OrderEntity): Long = dao.insertOrder(order)
    suspend fun updateOrderStatus(id: Int, status: String) = dao.updateOrderStatus(id, status)
    suspend fun finalizePayment(id: Int, paymentStatus: String, finalPrice: Double) = dao.finalizePayment(id, paymentStatus, finalPrice)
}
