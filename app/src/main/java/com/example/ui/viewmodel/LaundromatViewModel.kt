package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AddressEntity
import com.example.data.local.LaundromatDb
import com.example.data.local.OrderEntity
import com.example.data.local.UserEntity
import com.example.data.repository.LaundromatRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// UI Screens
sealed interface Screen {
    object Onboarding : Screen
    object Home : Screen
    object BookPickup : Screen
    data class OrderConfirmation(val pendingOrder: PendingOrder) : Screen
    data class OrderTracking(val orderId: Int) : Screen
    object OrderHistory : Screen
    object ProfileSettings : Screen
    object OffersLoyalty : Screen
}

// Chat Message for Support
data class ChatMessage(
    val sender: String, // "user" or "support"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

// Garment Item structure
data class GarmentSelection(
    val category: String,
    val quantity: Int,
    val pricePerUnit: Double
)

// Temporary hold of Booking state before checkout confirmation
data class PendingOrder(
    val serviceType: String,
    val garments: List<GarmentSelection>,
    val isPerKg: Boolean,
    val weightKg: Double,
    val pickupDate: String,
    val pickupTime: String,
    val deliveryDate: String,
    val deliveryTime: String,
    val address: String,
    val specialInstructions: String,
    val promoApplied: String? = null,
    val estimatedMin: Double,
    val estimatedMax: Double
)

class LaundromatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LaundromatDb.getDatabase(application)
    private val repository = LaundromatRepository(db.dao())
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val garmentListType = Types.newParameterizedType(List::class.java, GarmentSelection::class.java)
    private val garmentsAdapter = moshi.adapter<List<GarmentSelection>>(garmentListType)

    // Flow observations from DB
    val userState: StateFlow<UserEntity?> = repository.userFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val addressesState: StateFlow<List<AddressEntity>> = repository.addressesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allOrdersState: StateFlow<List<OrderEntity>> = repository.allOrdersFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeOrdersState: StateFlow<List<OrderEntity>> = repository.activeOrdersFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getOrderByIdFlow(id: Int): Flow<OrderEntity?> = repository.getOrderByIdFlow(id)

    // Navigation Backstack management
    private val _navigationStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = MutableStateFlow<Screen>(Screen.Home).apply {
        viewModelScope.launch {
            _navigationStack.collect { stack ->
                value = stack.lastOrNull() ?: Screen.Home
            }
        }
    }

    // Active notifications tracker (for in-app Toast alerts/logs)
    private val _notifications = MutableStateFlow<List<String>>(emptyList())
    val notifications = _notifications.asStateFlow()

    // Interactive Chat Message State for specific orders
    private val _orderChats = MutableStateFlow<Map<Int, List<ChatMessage>>>(emptyMap())
    val orderChats = _orderChats.asStateFlow()

    // Seed dummy addresses if DB is empty
    init {
        viewModelScope.launch {
            // Check if user exists. If not, set navigation to Onboarding
            repository.userFlow.collect { user ->
                if (user == null) {
                    navigateTo(Screen.Onboarding)
                }
            }
        }
        viewModelScope.launch {
            repository.addressesFlow.collect { list ->
                if (list.isEmpty()) {
                    repository.insertAddress(AddressEntity(name = "Home", addressLine = "123, Pine Tree Avenue, Green Hills"))
                    repository.insertAddress(AddressEntity(name = "Office", addressLine = "Tech Park, Block B, Floor 4"))
                }
            }
        }
    }

    // Navigation Controls
    fun navigateTo(screen: Screen) {
        val currentStack = _navigationStack.value.toMutableList()
        // If transitioning to Home or Onboarding, clear stack
        if (screen is Screen.Home) {
            _navigationStack.value = listOf(Screen.Home)
        } else if (screen is Screen.Onboarding) {
            _navigationStack.value = listOf(Screen.Onboarding)
        } else {
            currentStack.add(screen)
            _navigationStack.value = currentStack
        }
    }

    fun navigateBack(): Boolean {
        val currentStack = _navigationStack.value.toMutableList()
        if (currentStack.size > 1) {
            currentStack.removeAt(currentStack.size - 1)
            _navigationStack.value = currentStack
            return true
        }
        return false
    }

    // Setup user profile
    fun setupUserProfile(name: String, phone: String, address: String) {
        viewModelScope.launch {
            val user = UserEntity(
                name = name,
                phone = phone,
                address = address,
                loyaltyPoints = 150 // Welcome loyalty points!
            )
            repository.insertUser(user)
            triggerNotification("Welcome to Wash & Fold, $name! 150 loyalty points added.")
            navigateTo(Screen.Home)
        }
    }

    // Create New Order
    fun createOrder(pending: PendingOrder) {
        viewModelScope.launch {
            val garmentsJson = try {
                garmentsAdapter.toJson(pending.garments) ?: "[]"
            } catch (e: Exception) {
                "[]"
            }

            val order = OrderEntity(
                serviceType = pending.serviceType,
                garmentsJson = garmentsJson,
                isPerKg = pending.isPerKg,
                weightKg = pending.weightKg,
                pickupDate = pending.pickupDate,
                pickupTime = pending.pickupTime,
                deliveryDate = pending.deliveryDate,
                deliveryTime = pending.deliveryTime,
                address = pending.address,
                specialInstructions = pending.specialInstructions,
                status = "PLACED",
                priceEstimateMin = pending.estimatedMin,
                priceEstimateMax = pending.estimatedMax,
                finalPrice = 0.0,
                appliedPromo = pending.promoApplied,
                paymentMethod = "COD", // Default, can change during payment screen
                paymentStatus = "PENDING"
            )

            val orderId = repository.insertOrder(order).toInt()
            triggerNotification("Booking Confirmed! Order #$orderId has been placed.")
            
            // Update user loyalty points
            val currentUser = userState.value
            if (currentUser != null) {
                repository.insertUser(currentUser.copy(loyaltyPoints = currentUser.loyaltyPoints + 15))
                triggerNotification("+15 loyalty points added to your profile!")
            }

            // Seed initial chat
            val initialMsgs = listOf(
                ChatMessage("support", "Hi ${currentUser?.name ?: "there"}! We've received your booking for ${pending.serviceType}. Our runner will contact you shortly before arriving.")
            )
            val updatedChats = _orderChats.value.toMutableMap()
            updatedChats[orderId] = initialMsgs
            _orderChats.value = updatedChats

            // Redirect to Order Tracking
            navigateTo(Screen.OrderTracking(orderId))
        }
    }

    // Simulation: Advance Order Status for demonstration
    fun advanceOrderStatus(orderId: Int) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId) ?: return@launch
            val nextStatus = when (order.status) {
                "PLACED" -> "PICKED_UP"
                "PICKED_UP" -> "IN_PROCESS"
                "IN_PROCESS" -> "READY"
                "READY" -> "OUT_FOR_DELIVERY"
                "OUT_FOR_DELIVERY" -> "DELIVERED"
                else -> "DELIVERED"
            }

            repository.updateOrderStatus(orderId, nextStatus)

            val statusMsg = when (nextStatus) {
                "PICKED_UP" -> "Runner picked up your laundry bag!"
                "IN_PROCESS" -> "Your clothes are being washed & treated."
                "READY" -> "All fresh, clean, and perfectly folded! Ready for delivery."
                "OUT_FOR_DELIVERY" -> "Your delivery rider is on the way!"
                "DELIVERED" -> "Laundry delivered! Thank you for choosing Wash & Fold."
                else -> ""
            }
            triggerNotification("Order #$orderId: $statusMsg")

            // Auto support reply on status change
            val list = _orderChats.value[orderId]?.toMutableList() ?: mutableListOf()
            list.add(ChatMessage("support", "Status Update: $statusMsg"))
            
            // If READY, finalize pricing and notify
            if (nextStatus == "READY") {
                list.add(ChatMessage("support", "We've weighed/counted your garments. Your final bill is ready. Please proceed to pay!"))
            }

            val updatedChats = _orderChats.value.toMutableMap()
            updatedChats[orderId] = list
            _orderChats.value = updatedChats
        }
    }

    // Set Final Weight/Pricing Simulation (e.g. for Payment Screen)
    fun processPayment(orderId: Int, method: String, finalAmt: Double) {
        viewModelScope.launch {
            repository.finalizePayment(orderId, "PAID", finalAmt)
            repository.updateOrderStatus(orderId, "OUT_FOR_DELIVERY") // Advance to delivery path after pay
            triggerNotification("Payment of ₹${finalAmt} successful via $method!")
            
            val list = _orderChats.value[orderId]?.toMutableList() ?: mutableListOf()
            list.add(ChatMessage("support", "Thank you for the payment via $method. Your order is now Out for Delivery!"))
            val updatedChats = _orderChats.value.toMutableMap()
            updatedChats[orderId] = list
            _orderChats.value = updatedChats
        }
    }

    // Submit user message to chat support
    fun sendSupportMessage(orderId: Int, text: String) {
        if (text.isBlank()) return
        val currentMsgs = _orderChats.value[orderId]?.toMutableList() ?: mutableListOf()
        currentMsgs.add(ChatMessage("user", text))
        
        val updated = _orderChats.value.toMutableMap()
        updated[orderId] = currentMsgs
        _orderChats.value = updated

        // Generate automated supportive reply
        viewModelScope.launch {
            delay(1200)
            val supportResponse = when {
                text.contains("starch", ignoreCase = true) -> "Got it! No starch will be applied to your cotton garments."
                text.contains("time", ignoreCase = true) || text.contains("when", ignoreCase = true) -> "Our delivery partner is estimated to arrive within the scheduled slot. You will receive an SMS reminder."
                text.contains("fragile", ignoreCase = true) || text.contains("silk", ignoreCase = true) || text.contains("care", ignoreCase = true) -> "We handle delicates separately with mild detergent and gentle air drying."
                text.contains("hello", ignoreCase = true) || text.contains("hi", ignoreCase = true) -> "Hello! I am your Wash & Fold care assistant. How can I help you today?"
                else -> "Thanks for reaching out! Our team is processing this instruction immediately. We've got you covered!"
            }
            val msgs = _orderChats.value[orderId]?.toMutableList() ?: mutableListOf()
            msgs.add(ChatMessage("support", supportResponse))
            val up = _orderChats.value.toMutableMap()
            up[orderId] = msgs
            _orderChats.value = up
            triggerNotification("New chat message from Support")
        }
    }

    // Save New Address
    fun addNewAddress(name: String, address: String) {
        viewModelScope.launch {
            repository.insertAddress(AddressEntity(name = name, addressLine = address))
            triggerNotification("New address '$name' saved successfully.")
        }
    }

    // Delete Address
    fun removeAddress(id: Int) {
        viewModelScope.launch {
            repository.deleteAddress(id)
            triggerNotification("Address deleted.")
        }
    }

    // Log Notification (mock Push/SMS)
    private fun triggerNotification(message: String) {
        val current = _notifications.value.toMutableList()
        current.add(0, "[Notification] $message")
        _notifications.value = current
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }

    // Helper: Parse Garments from Order entity
    fun parseGarments(garmentsJson: String): List<GarmentSelection> {
        return try {
            garmentsAdapter.fromJson(garmentsJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
