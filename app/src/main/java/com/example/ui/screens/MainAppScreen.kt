package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar
import com.example.R
import com.example.data.local.AddressEntity
import com.example.data.local.OrderEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: LaundromatViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val user by viewModel.userState.collectAsStateWithLifecycle()
    val addresses by viewModel.addressesState.collectAsStateWithLifecycle()
    val activeOrders by viewModel.activeOrdersState.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            if (currentScreen !is Screen.Onboarding) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalLaundryService,
                                contentDescription = "Wash & Fold Icon",
                                tint = PrimaryTeal,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Wash & Fold",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = PrimaryTeal
                            )
                        }
                    },
                    navigationIcon = {
                        if (currentScreen !is Screen.Home) {
                            IconButton(onClick = { viewModel.navigateBack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = PrimaryTeal
                                )
                            }
                        }
                    },
                    actions = {
                        if (currentScreen !is Screen.ProfileSettings) {
                            IconButton(onClick = { viewModel.navigateTo(Screen.ProfileSettings) }) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile",
                                    tint = PrimaryTeal
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BackgroundIce
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen !is Screen.Onboarding) {
                NavigationBar(
                    containerColor = SurfaceWhite,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        selected = currentScreen is Screen.Home,
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryTeal,
                            selectedTextColor = PrimaryTeal,
                            indicatorColor = LightIceBlue
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen is Screen.OffersLoyalty,
                        onClick = { viewModel.navigateTo(Screen.OffersLoyalty) },
                        icon = { Icon(Icons.Default.CardGiftcard, contentDescription = "Offers") },
                        label = { Text("Offers") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryTeal,
                            selectedTextColor = PrimaryTeal,
                            indicatorColor = LightIceBlue
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen is Screen.OrderHistory,
                        onClick = { viewModel.navigateTo(Screen.OrderHistory) },
                        icon = { Icon(Icons.Default.History, contentDescription = "History") },
                        label = { Text("Orders") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryTeal,
                            selectedTextColor = PrimaryTeal,
                            indicatorColor = LightIceBlue
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen is Screen.ProfileSettings,
                        onClick = { viewModel.navigateTo(Screen.ProfileSettings) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryTeal,
                            selectedTextColor = PrimaryTeal,
                            indicatorColor = LightIceBlue
                        )
                    )
                }
            }
        },
        containerColor = BackgroundIce
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main content based on route
            when (val screen = currentScreen) {
                is Screen.Onboarding -> OnboardingScreen(viewModel = viewModel)
                is Screen.Home -> HomeScreen(
                    viewModel = viewModel,
                    user = user,
                    activeOrders = activeOrders
                )
                is Screen.BookPickup -> BookPickupScreen(
                    viewModel = viewModel,
                    addresses = addresses
                )
                is Screen.OrderConfirmation -> OrderConfirmationScreen(
                    viewModel = viewModel,
                    pending = screen.pendingOrder
                )
                is Screen.OrderTracking -> OrderTrackingScreen(
                    viewModel = viewModel,
                    orderId = screen.orderId
                )
                is Screen.OrderHistory -> OrderHistoryScreen(viewModel = viewModel)
                is Screen.OffersLoyalty -> OffersLoyaltyScreen(viewModel = viewModel, user = user)
                is Screen.ProfileSettings -> ProfileSettingsScreen(
                    viewModel = viewModel,
                    user = user,
                    addresses = addresses
                )
            }

            // Real-time toast notifications banner
            NotificationToastBanner(
                notifications = notifications,
                onDismiss = { viewModel.clearNotifications() }
            )
        }
    }
}

// Beautiful floating marquee notification banner
@Composable
fun NotificationToastBanner(notifications: List<String>, onDismiss: () -> Unit) {
    AnimatedVisibility(
        visible = notifications.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        val latest = notifications.firstOrNull() ?: ""
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryTeal),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Alert",
                        tint = TertiaryGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = latest.replace("[Notification] ", ""),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// ---------------- SCREEN IMPLEMENTATIONS ----------------

@Composable
fun OnboardingScreen(viewModel: LaundromatViewModel) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var step by remember { mutableIntStateOf(1) } // 1: Phone + OTP, 2: Setup Profile

    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(LightIceBlue, BackgroundIce)
                )
            )
            .padding(24.dp)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, SecondaryMint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_logo_foreground),
                        contentDescription = "Wash & Fold Logo",
                        modifier = Modifier.size(90.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Wash & Fold",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    "Freshness Delivered To Your Doorstep 🫧",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            // Input Fields Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (step == 1) {
                        Text(
                            text = "Login or Sign Up",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextDeep,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Enter your phone number to proceed with a secure OTP verification",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { if (it.length <= 10) phone = it },
                            label = { Text("Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                            placeholder = { Text("e.g., 9876543210") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryTeal,
                                focusedLabelColor = PrimaryTeal
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("phone_input")
                        )

                        if (isOtpSent) {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = { if (it.length <= 4) otpCode = it },
                                label = { Text("Enter 4-Digit OTP") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "OTP") },
                                placeholder = { Text("Try: 1234") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryTeal,
                                    focusedLabelColor = PrimaryTeal
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (!isOtpSent) {
                                    if (phone.length >= 10) {
                                        isOtpSent = true
                                    }
                                } else {
                                    if (otpCode == "1234" || otpCode.length == 4) {
                                        step = 2
                                    }
                                }
                            },
                            enabled = phone.length >= 10,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("otp_submit_button")
                        ) {
                            Text(if (!isOtpSent) "Send OTP" else "Verify OTP", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    } else {
                        // Profile Info Step
                        Text(
                            text = "Create Profile",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextDeep,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Fill in your details to start ordering right away",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryTeal,
                                focusedLabelColor = PrimaryTeal
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("name_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Pickup & Delivery Address") },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = "Address") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryTeal,
                                focusedLabelColor = PrimaryTeal
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("address_input")
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (name.isNotBlank() && address.isNotBlank()) {
                                    viewModel.setupUserProfile(name, phone, address)
                                }
                            },
                            enabled = name.isNotBlank() && address.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("onboarding_complete_button")
                        ) {
                            Text("Get Started", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            // Small Footer Info
            Text(
                "By continuing, you agree to our Terms of Service & Privacy Policy",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: LaundromatViewModel,
    user: UserEntity?,
    activeOrders: List<OrderEntity>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
    ) {
        // Welcome & Loyalty Point Board
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hello, ${user?.name ?: "Guest"}! 🫧",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextDeep
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Let's make laundry day stress-free!",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    // Loyalty Points badge
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(LightIceBlue, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${user?.loyaltyPoints ?: 0}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryTeal
                        )
                        Text(
                            text = "Points",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = PrimaryTeal
                        )
                    }
                }
            }
        }

        // Hero illustration Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = "Wash & Fold Promo Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Tint Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)))
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = TertiaryGold,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                "SPECIAL OFFER",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                color = TextDeep
                            )
                        }
                        Column {
                            Text(
                                "Flat ₹100 Off & Free Pickup",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                "Use code FRESH100 on your first booking",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // Area Availability Notice
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LightIceBlue.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = PrimaryTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Available in your area: Green Hills, Tech Park, Downtown",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = PrimaryTeal
                    )
                }
            }
        }

        // Prominent Call to Action - BOOK PICKUP
        item {
            Button(
                onClick = { viewModel.navigateTo(Screen.BookPickup) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("schedule_pickup_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Schedule",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Schedule a Pickup",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Active Order Card (If any)
        if (activeOrders.isNotEmpty()) {
            val active = activeOrders.first()
            item {
                Text(
                    text = "Current Order Status",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextDeep
                )
            }
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = SurfaceWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(Screen.OrderTracking(active.id)) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Order #${active.id}",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = TextDeep
                                )
                                Text(
                                    active.serviceType,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                            }
                            // Status Badge
                            Surface(
                                color = when (active.status) {
                                    "PLACED" -> LightIceBlue
                                    "DELIVERED" -> Color(0xFFE8F5E9)
                                    "READY" -> Color(0xFFFFF9C4)
                                    else -> Color(0xFFE0F7FA)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = active.status,
                                    color = PrimaryTeal,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Custom tiny mini-stepper horizontal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val steps = listOf("PLACED", "PICKED_UP", "IN_PROCESS", "READY", "OUT_FOR_DELIVERY", "DELIVERED")
                            val currentIndex = steps.indexOf(active.status).coerceAtLeast(0)

                            steps.forEachIndexed { index, name ->
                                val isDone = index <= currentIndex
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .padding(horizontal = 2.dp)
                                        .clip(CircleShape)
                                        .background(if (isDone) PrimaryTeal else Color.LightGray)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Next Step: " + when (active.status) {
                                    "PLACED" -> "Runner Pickup"
                                    "PICKED_UP" -> "Arriving at Facility"
                                    "IN_PROCESS" -> "Cleaning & Pressing"
                                    "READY" -> "Delivery Schedule"
                                    "OUT_FOR_DELIVERY" -> "Arriving shortly!"
                                    else -> "Completed"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = TextDeep
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Track Order",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryTeal
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Track",
                                    tint = PrimaryTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Links Section
        item {
            Text(
                text = "Discover Offers & Quick Links",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDeep
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Quick Link 1: Offers
                ElevatedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(Screen.OffersLoyalty) },
                    colors = CardDefaults.elevatedCardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = "Offers",
                            tint = SecondaryMint,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Offers & Promo",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextDeep
                        )
                        Text(
                            "Get discounts",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = TextMuted
                        )
                    }
                }

                // Quick Link 2: History
                ElevatedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(Screen.OrderHistory) },
                    colors = CardDefaults.elevatedCardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = SecondaryMint,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Order History",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextDeep
                        )
                        Text(
                            "Re-order tap",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookPickupScreen(
    viewModel: LaundromatViewModel,
    addresses: List<AddressEntity>
) {
    val serviceTypes = listOf("Wash & Fold", "Wash & Iron", "Dry Clean", "Steam Iron Only", "Shoe Cleaning")
    val garmentCategories = listOf("Shirts", "Trousers", "Dresses", "Beddings", "Shoes", "Undergarments", "Others")
    
    // State variables
    var selectedService by remember { mutableStateOf(serviceTypes.first()) }
    var usePerKg by remember { mutableStateOf(false) }
    var weightKg by remember { mutableDoubleStateOf(3.0) }
    
    // Quantity tracker for garments
    val garmentQuantities = remember { mutableStateMapOf<String, Int>() }
    // Initialize
    LaunchedEffect(Unit) {
        garmentCategories.forEach { garmentQuantities[it] = 0 }
    }

    var selectedAddress by remember { mutableStateOf(addresses.firstOrNull()?.addressLine ?: "123 Green Hills Lane") }
    var specialInstructions by remember { mutableStateOf("") }

    // Date & Time pickers
    val dates = remember {
        val list = mutableListOf<String>()
        val formatter = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        val cal = Calendar.getInstance()
        for (i in 0..4) {
            list.add(formatter.format(cal.time))
            cal.add(Calendar.DATE, 1)
        }
        list
    }
    var selectedPickupDate by remember { mutableStateOf(dates[0]) }
    var selectedPickupTime by remember { mutableStateOf("08:00 AM - 11:00 AM") }

    var selectedDeliveryDate by remember { mutableStateOf(dates[2]) } // Auto suggest 48 hours later
    var selectedDeliveryTime by remember { mutableStateOf("05:00 PM - 08:00 PM") }

    val timeSlots = listOf(
        "08:00 AM - 11:00 AM",
        "11:00 AM - 02:00 PM",
        "02:00 PM - 05:00 PM",
        "05:00 PM - 08:00 PM"
    )

    // Compute estimate pricing
    val estimate = remember(selectedService, garmentQuantities.values.toList(), usePerKg, weightKg) {
        val baseRate = when (selectedService) {
            "Wash & Fold" -> if (usePerKg) 49.0 else 12.0
            "Wash & Iron" -> if (usePerKg) 69.0 else 18.0
            "Dry Clean" -> 79.0
            "Steam Iron Only" -> 9.0
            "Shoe Cleaning" -> 99.0
            else -> 15.0
        }

        var priceSum = 0.0
        if (usePerKg) {
            priceSum = baseRate * weightKg
        } else {
            garmentQuantities.forEach { (cat, qty) ->
                val modifier = when (cat) {
                    "Shirts" -> 1.0
                    "Trousers" -> 1.2
                    "Dresses" -> 1.8
                    "Beddings" -> 3.0
                    "Shoes" -> 4.5
                    "Undergarments" -> 0.6
                    else -> 1.0
                }
                priceSum += qty * baseRate * modifier
            }
        }

        val finalPrice = if (priceSum == 0.0) baseRate * 3 else priceSum
        val min = (finalPrice * 0.9).coerceAtLeast(40.0)
        val max = finalPrice * 1.15
        Pair(min.toInt(), max.toInt())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
    ) {
        item {
            Text(
                "Customize Your Order",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextDeep
            )
        }

        // 1. Select Service
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "1. Select Service Type",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextDeep
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(serviceTypes) { type ->
                            val isSelected = selectedService == type
                            val icon = when (type) {
                                "Wash & Fold" -> Icons.Default.LocalLaundryService
                                "Wash & Iron" -> Icons.Default.Iron
                                "Dry Clean" -> Icons.Default.Dry
                                "Steam Iron Only" -> Icons.Default.Checkroom
                                else -> Icons.Default.InvertColors
                            }

                            Column(
                                modifier = Modifier
                                    .width(100.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) LightIceBlue else Color.Transparent)
                                    .border(
                                        2.dp,
                                        if (isSelected) PrimaryTeal else Color.LightGray.copy(alpha = 0.5f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedService = type
                                        if (type == "Dry Clean" || type == "Shoe Cleaning") {
                                            usePerKg = false
                                        }
                                    }
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = type,
                                    tint = if (isSelected) PrimaryTeal else TextMuted,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) PrimaryTeal else TextDeep,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Select Items Quantities or Per Kg Pricing
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "2. Quantities / Pricing",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextDeep
                        )

                        if (selectedService == "Wash & Fold" || selectedService == "Wash & Iron") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("By Kg", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.width(4.dp))
                                Switch(
                                    checked = usePerKg,
                                    onCheckedChange = { usePerKg = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = PrimaryTeal, checkedTrackColor = LightIceBlue)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (usePerKg) {
                        Column {
                            Text(
                                "Approximate Weight (Kg)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextDeep
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "${"%.1f".format(weightKg)} kg",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PrimaryTeal)
                                )
                                Slider(
                                    value = weightKg.toFloat(),
                                    onValueChange = { weightKg = it.toDouble() },
                                    valueRange = 1f..15f,
                                    modifier = Modifier.fillMaxWidth(0.8f)
                                )
                            }
                        }
                    } else {
                        // Render Item List
                        garmentCategories.forEach { category ->
                            val qty = garmentQuantities[category] ?: 0
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val icon = when (category) {
                                        "Shirts" -> Icons.Default.Checkroom
                                        "Trousers" -> Icons.Default.Label
                                        "Shoes" -> Icons.Default.Storefront
                                        else -> Icons.Default.Layers
                                    }
                                    Icon(icon, contentDescription = category, tint = SecondaryMint, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(category, style = MaterialTheme.typography.bodyMedium, color = TextDeep)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (qty > 0) garmentQuantities[category] = qty - 1 },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.RemoveCircle, contentDescription = "Decrease", tint = SecondaryMint)
                                    }
                                    Text(
                                        "$qty",
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextDeep
                                    )
                                    IconButton(
                                        onClick = { garmentQuantities[category] = qty + 1 },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.AddCircle, contentDescription = "Increase", tint = SecondaryMint)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Pickup Slot & Delivery Slot Picker
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "3. Select Date & Time slots",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextDeep
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Pickup Date & Time", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(dates) { date ->
                            val isSel = selectedPickupDate == date
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) PrimaryTeal else Color.LightGray.copy(alpha = 0.3f))
                                    .clickable {
                                        selectedPickupDate = date
                                        // Auto suggest delivery date (2 days later)
                                        val idx = dates.indexOf(date)
                                        val delIdx = (idx + 2).coerceAtMost(dates.size - 1)
                                        selectedDeliveryDate = dates[delIdx]
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(date, color = if (isSel) Color.White else TextDeep, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(timeSlots) { slot ->
                            val isSel = selectedPickupTime == slot
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) PrimaryTeal else Color.LightGray.copy(alpha = 0.3f))
                                    .clickable { selectedPickupTime = slot }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(slot, color = if (isSel) Color.White else TextDeep, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Delivery Date & Time (Approx. 24-48 hrs turnaround)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(dates) { date ->
                            val isSel = selectedDeliveryDate == date
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) PrimaryTeal else Color.LightGray.copy(alpha = 0.3f))
                                    .clickable { selectedDeliveryDate = date }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(date, color = if (isSel) Color.White else TextDeep, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(timeSlots) { slot ->
                            val isSel = selectedDeliveryTime == slot
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) PrimaryTeal else Color.LightGray.copy(alpha = 0.3f))
                                    .clickable { selectedDeliveryTime = slot }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(slot, color = if (isSel) Color.White else TextDeep, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        // 4. Address Selection & Instructions
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "4. Address & Instructions",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextDeep
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (addresses.isNotEmpty()) {
                        addresses.forEach { addr ->
                            val isSel = selectedAddress == addr.addressLine
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) LightIceBlue.copy(alpha = 0.5f) else Color.Transparent)
                                    .clickable { selectedAddress = addr.addressLine }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = isSel, onClick = { selectedAddress = addr.addressLine }, colors = RadioButtonDefaults.colors(selectedColor = PrimaryTeal))
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text(addr.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = PrimaryTeal)
                                    Text(addr.addressLine, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    } else {
                        Text("No addresses saved. Please add one.", style = MaterialTheme.typography.bodySmall, color = Color.Red)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = specialInstructions,
                        onValueChange = { specialInstructions = it },
                        label = { Text("Special Instructions") },
                        placeholder = { Text("e.g., Handle silk gently, no starch on collar") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryTeal, focusedLabelColor = PrimaryTeal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            }
        }

        // Estimated Summary & Proceed Button
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LightIceBlue.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Estimated Cost", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextDeep)
                            Text("Excluding weight final count", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = TextMuted)
                        }
                        Text(
                            "₹${estimate.first} - ₹${estimate.second}",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = PrimaryTeal)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val activeGarmentsList = garmentQuantities.filter { it.value > 0 }.map {
                                GarmentSelection(it.key, it.value, 15.0)
                            }
                            val finalGarments = if (usePerKg) emptyList() else activeGarmentsList

                            viewModel.navigateTo(
                                Screen.OrderConfirmation(
                                    PendingOrder(
                                        serviceType = selectedService,
                                        garments = finalGarments,
                                        isPerKg = usePerKg,
                                        weightKg = weightKg,
                                        pickupDate = selectedPickupDate,
                                        pickupTime = selectedPickupTime,
                                        deliveryDate = selectedDeliveryDate,
                                        deliveryTime = selectedDeliveryTime,
                                        address = selectedAddress,
                                        specialInstructions = specialInstructions,
                                        estimatedMin = estimate.first.toDouble(),
                                        estimatedMax = estimate.second.toDouble()
                                    )
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("book_order_button")
                    ) {
                        Text("Verify & Review Booking", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderConfirmationScreen(
    viewModel: LaundromatViewModel,
    pending: PendingOrder
) {
    var promoCodeInput by remember { mutableStateOf("") }
    var discountApplied by remember { mutableDoubleStateOf(0.0) }
    var promoError by remember { mutableStateOf("") }
    var promoSuccess by remember { mutableStateOf("") }

    val finalMin = remember(pending.estimatedMin, discountApplied) {
        (pending.estimatedMin - discountApplied).coerceAtLeast(0.0)
    }
    val finalMax = remember(pending.estimatedMax, discountApplied) {
        (pending.estimatedMax - discountApplied).coerceAtLeast(0.0)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
    ) {
        item {
            Text(
                "Review Order Summary",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextDeep
            )
        }

        // Details Item Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Booking Details",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Service Type", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                        Text(pending.serviceType, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextDeep)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pickup slot", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                        Text("${pending.pickupDate} (${pending.pickupTime})", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextDeep)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery slot", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                        Text("${pending.deliveryDate} (${pending.deliveryTime})", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextDeep)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pricing Mode", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                        Text(if (pending.isPerKg) "By Weight (approx. ${pending.weightKg} kg)" else "By Item Count (${pending.garments.sumOf { it.quantity }} items)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextDeep)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery Address", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                        Text(pending.address, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), maxLines = 1, overflow = TextOverflow.Ellipsis, color = TextDeep)
                    }

                    if (pending.specialInstructions.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Instructions", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                            Text(pending.specialInstructions, style = MaterialTheme.typography.bodySmall, color = TextDeep)
                        }
                    }
                }
            }
        }

        // Promo Codes Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Promo Codes & Coupons",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextDeep
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = promoCodeInput,
                            onValueChange = { promoCodeInput = it },
                            label = { Text("Enter Promo Code") },
                            placeholder = { Text("e.g. FRESH100") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryTeal, focusedLabelColor = PrimaryTeal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("promo_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (promoCodeInput.trim().equals("FRESH100", ignoreCase = true)) {
                                    discountApplied = 100.0
                                    promoSuccess = "Coupons applied successfully! ₹100 Saved."
                                    promoError = ""
                                } else if (promoCodeInput.trim().equals("FRESH30", ignoreCase = true)) {
                                    discountApplied = pending.estimatedMin * 0.3
                                    promoSuccess = "Coupons applied! 30% discount saved."
                                    promoError = ""
                                } else {
                                    promoError = "Invalid Promo Code."
                                    promoSuccess = ""
                                    discountApplied = 0.0
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Apply")
                        }
                    }

                    if (promoError.isNotBlank()) {
                        Text(promoError, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    }
                    if (promoSuccess.isNotBlank()) {
                        Text(promoSuccess, color = Color(0xFF2E7D32), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }

        // Final Price estimate card & Book button
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightIceBlue.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Estimated Bill Total", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextDeep)
                            if (discountApplied > 0) {
                                Text("Discount: - ₹${discountApplied.toInt()}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32))
                            }
                        }
                        Text(
                            "₹${finalMin.toInt()} - ₹${finalMax.toInt()}",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = PrimaryTeal)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.createOrder(
                                pending.copy(
                                    estimatedMin = finalMin,
                                    estimatedMax = finalMax,
                                    promoApplied = if (discountApplied > 0.0) promoCodeInput else null
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_order_button")
                    ) {
                        Text("Confirm Order Booking", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTrackingScreen(
    viewModel: LaundromatViewModel,
    orderId: Int
) {
    val orderFlow = remember(orderId) { viewModel.getOrderByIdFlow(orderId) }
    val order by orderFlow.collectAsStateWithLifecycle(initialValue = null)
    val chats by viewModel.orderChats.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("track") } // "track" or "support"
    var typedMessage by remember { mutableStateOf("") }

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryTeal)
        }
        return
    }

    val currentOrder = order!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Tab selector (Tracking vs Chat support)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { activeTab = "track" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == "track") PrimaryTeal else LightIceBlue
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Navigation, contentDescription = "Track")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Order Status", color = if (activeTab == "track") Color.White else PrimaryTeal)
                }
            }

            Button(
                onClick = { activeTab = "support" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == "support") PrimaryTeal else LightIceBlue
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Chat, contentDescription = "Support")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Support Chat", color = if (activeTab == "support") Color.White else PrimaryTeal)
                }
            }
        }

        if (activeTab == "track") {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Interactive simulation cockpit for customers to step through states
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = TertiaryGold.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Science, contentDescription = "Demo", tint = PrimaryTeal)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Sandbox Simulation Cockpit",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextDeep
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Advance order status below to mock live notification SMS updates & billing counts",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.advanceOrderStatus(orderId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("advance_simulation_button")
                                ) {
                                    Text("Step Status →")
                                }

                                if (currentOrder.status == "READY") {
                                    Button(
                                        onClick = { viewModel.navigateTo(Screen.OffersLoyalty) }, // view promos/pay
                                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryMint),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Offers List")
                                    }
                                }
                            }
                        }
                    }
                }

                // Main Stepper Card
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                "Live Status Stepper",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryTeal
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val steps = listOf(
                                Pair("PLACED", "Order Placed & Scheduled"),
                                Pair("PICKED_UP", "Picked Up by Runner"),
                                Pair("IN_PROCESS", "Washing, Cleaning & Ironing"),
                                Pair("READY", "Washed, Folded & Ready!"),
                                Pair("OUT_FOR_DELIVERY", "Out for Delivery"),
                                Pair("DELIVERED", "Laundry Delivered Fresh")
                            )

                            val currentIndex = steps.indexOfFirst { it.first == currentOrder.status }.coerceAtLeast(0)

                            steps.forEachIndexed { index, pair ->
                                val isPast = index < currentIndex
                                val isCurrent = index == currentIndex
                                val isFuture = index > currentIndex

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Visual line
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(36.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isPast) PrimaryTeal
                                                    else if (isCurrent) TertiaryGold
                                                    else Color.LightGray
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isPast) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "Done",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            } else {
                                                Text(
                                                    "${index + 1}",
                                                    color = if (isCurrent) TextDeep else Color.DarkGray,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                                )
                                            }
                                        }
                                        if (index < steps.size - 1) {
                                            Box(
                                                modifier = Modifier
                                                    .width(2.dp)
                                                    .height(40.dp)
                                                    .background(if (isPast) PrimaryTeal else Color.LightGray)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.padding(top = 2.dp)) {
                                        Text(
                                            text = pair.second,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = if (isCurrent) PrimaryTeal else if (isPast) TextDeep else TextMuted
                                        )
                                        if (isCurrent) {
                                            val estText = when (currentOrder.status) {
                                                "PLACED" -> "Runner arriving within your selected slot"
                                                "PICKED_UP" -> "Runner transporting garments safely"
                                                "IN_PROCESS" -> "Processing garments in eco detergents"
                                                "READY" -> "Final bills generated! Please settle payment."
                                                "OUT_FOR_DELIVERY" -> "Rider arriving shortly"
                                                else -> "Transaction completed!"
                                            }
                                            Text(
                                                text = estText,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = TextMuted
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Billing card if status is READY/PAID
                if (currentOrder.status == "READY" || currentOrder.status == "OUT_FOR_DELIVERY" || currentOrder.status == "DELIVERED") {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = LightIceBlue),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Itemized Payment Summary",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryTeal
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                val baseEstimate = currentOrder.priceEstimateMin
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Calculated Weight / Item Charge", style = MaterialTheme.typography.bodyMedium, color = TextDeep)
                                    Text("₹$baseEstimate", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextDeep)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Logistics & Express Service Fee", style = MaterialTheme.typography.bodyMedium, color = TextDeep)
                                    Text("₹20.00", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextDeep)
                                }
                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White)
                                
                                val totalToPay = baseEstimate + 20.0
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Final Amount Due", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = TextDeep)
                                    Text("₹$totalToPay", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = PrimaryTeal))
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                if (currentOrder.paymentStatus == "PENDING") {
                                    Text("Select Payment Gateway", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextMuted)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    val paymentModes = listOf("UPI Gateway", "Debit / Credit Cards", "Net Banking", "Cash on Delivery")
                                    var chosenPayMode by remember { mutableStateOf(paymentModes.first()) }
                                    
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(paymentModes) { mode ->
                                            val isSel = chosenPayMode == mode
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSel) PrimaryTeal else Color.White)
                                                    .border(1.dp, PrimaryTeal, RoundedCornerShape(8.dp))
                                                    .clickable { chosenPayMode = mode }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(mode, color = if (isSel) Color.White else PrimaryTeal, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = { viewModel.processPayment(orderId, chosenPayMode, totalToPay) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("pay_now_button")
                                    ) {
                                        Text("Settle Bill & Pay ₹$totalToPay", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                    }
                                } else {
                                    // Paid Receipt Details
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Verified, contentDescription = "Paid", tint = Color(0xFF2E7D32))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Bill Settled via Cards/UPI", color = Color(0xFF2E7D32), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                        }
                                        Button(
                                            onClick = { }, // Invoice trigger mockup
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Download, contentDescription = "Download", tint = PrimaryTeal, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Invoice", color = PrimaryTeal, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Live Interactive Chat support Tab
            val activeChats = chats[orderId] ?: emptyList()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    reverseLayout = true,
                    contentPadding = PaddingValues(bottom = 16.dp, top = 8.dp)
                ) {
                    items(activeChats.reversed()) { msg ->
                        val isUser = msg.sender == "user"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 0.dp,
                                            bottomEnd = if (isUser) 0.dp else 16.dp
                                        )
                                    )
                                    .background(if (isUser) PrimaryTeal else Color.LightGray.copy(alpha = 0.4f))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .widthIn(max = 260.dp)
                            ) {
                                Text(
                                    msg.message,
                                    color = if (isUser) Color.White else TextDeep,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Chat sender board
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = typedMessage,
                        onValueChange = { typedMessage = it },
                        placeholder = { Text("Ask support about your laundry...") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryTeal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (typedMessage.isNotBlank()) {
                                viewModel.sendSupportMessage(orderId, typedMessage)
                                typedMessage = ""
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(PrimaryTeal)
                            .size(48.dp)
                            .testTag("send_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryScreen(viewModel: LaundromatViewModel) {
    val orders by viewModel.allOrdersState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "Your Booking History",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = TextDeep,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Empty",
                        tint = SecondaryMint,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No orders yet — schedule your first pickup!",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextDeep
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.BookPickup) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                    ) {
                        Text("Book Now")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(orders) { order ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Order #${order.id}",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = TextDeep
                                    )
                                    Text(
                                        order.serviceType,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextMuted
                                    )
                                }
                                Surface(
                                    color = if (order.status == "DELIVERED") Color(0xFFE8F5E9) else LightIceBlue,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        order.status,
                                        color = PrimaryTeal,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Date: ${order.pickupDate}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextDeep
                                    )
                                    Text(
                                        "Price Estimate: ₹${order.priceEstimateMin.toInt()}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = PrimaryTeal
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { viewModel.navigateTo(Screen.OrderTracking(order.id)) },
                                        colors = ButtonDefaults.buttonColors(containerColor = LightIceBlue),
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        Text("Details", color = PrimaryTeal, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                    }

                                    // ONE TAP REORDER BUTTON
                                    Button(
                                        onClick = {
                                            viewModel.navigateTo(
                                                Screen.OrderConfirmation(
                                                    PendingOrder(
                                                        serviceType = order.serviceType,
                                                        garments = viewModel.parseGarments(order.garmentsJson),
                                                        isPerKg = order.isPerKg,
                                                        weightKg = order.weightKg,
                                                        pickupDate = "Tomorrow",
                                                        pickupTime = "08:00 AM - 11:00 AM",
                                                        deliveryDate = "Day after Tomorrow",
                                                        deliveryTime = "05:00 PM - 08:00 PM",
                                                        address = order.address,
                                                        specialInstructions = order.specialInstructions,
                                                        estimatedMin = order.priceEstimateMin,
                                                        estimatedMax = order.priceEstimateMax
                                                    )
                                                )
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Replay, contentDescription = "Repeat", tint = Color.White, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Repeat", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OffersLoyaltyScreen(
    viewModel: LaundromatViewModel,
    user: UserEntity?
) {
    val promoCodes = listOf(
        Pair("FRESH100", "Get ₹100 Off on your first order. Minimum bill ₹200."),
        Pair("FRESH30", "Enjoy flat 30% discount on Dry Cleaning and steam irons!"),
        Pair("FREEWASH", "Refer 2 friends and claim one free wash up to 5kg.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
    ) {
        Text(
            "Offers & Referral Center",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = TextDeep,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // Loyalty Points Dashboard card
        ElevatedCard(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = PrimaryTeal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Your Loyalty Point Hub",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Redeem your point collection for premium laundry benefits",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "${user?.loyaltyPoints ?: 0} points",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, color = TertiaryGold)
                        )
                        Text(
                            "Next Reward: Free Wash & Fold (200 points)",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 11.sp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(TertiaryGold)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Redeem", color = TextDeep, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // Referral Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CardGiftcard, contentDescription = "Gift", tint = SecondaryMint)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Refer & Earn ₹100",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextDeep
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Invite your friends to try Wash & Fold! They get ₹100 off, and you get ₹100 once their first order is delivered.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightIceBlue)
                        .border(1.dp, PrimaryTeal, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Your Code: WASHFOLD99",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = PrimaryTeal
                        )
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = PrimaryTeal, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Available Promo list
        Text(
            "Promo Codes",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextDeep,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        promoCodes.forEach { (code, desc) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            code,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = PrimaryTeal)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    Button(
                        onClick = { }, // copy promo trigger
                        colors = ButtonDefaults.buttonColors(containerColor = LightIceBlue)
                    ) {
                        Text("Copy", color = PrimaryTeal, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSettingsScreen(
    viewModel: LaundromatViewModel,
    user: UserEntity?,
    addresses: List<AddressEntity>
) {
    var newAddrName by remember { mutableStateOf("") }
    var newAddrLine by remember { mutableStateOf("") }
    var showAddAddress by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
    ) {
        item {
            Text(
                "Profile & App Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextDeep
            )
        }

        // 1. User Contact Profile
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar",
                        tint = PrimaryTeal,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(user?.name ?: "Guest", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextDeep)
                        Text("+91 ${user?.phone ?: "0000000000"}", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                        Text(user?.address ?: "No Address Selected", style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        // 2. Saved Addresses Manager
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Saved Addresses",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextDeep
                        )

                        IconButton(onClick = { showAddAddress = !showAddAddress }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Add Address", tint = PrimaryTeal)
                        }
                    }

                    if (showAddAddress) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            OutlinedTextField(
                                value = newAddrName,
                                onValueChange = { newAddrName = it },
                                label = { Text("Address Name (e.g. Home, Cabin)") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryTeal, focusedLabelColor = PrimaryTeal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newAddrLine,
                                onValueChange = { newAddrLine = it },
                                label = { Text("Full Address Details") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryTeal, focusedLabelColor = PrimaryTeal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    if (newAddrName.isNotBlank() && newAddrLine.isNotBlank()) {
                                        viewModel.addNewAddress(newAddrName, newAddrLine)
                                        newAddrName = ""
                                        newAddrLine = ""
                                        showAddAddress = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Save Address")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (addresses.isNotEmpty()) {
                        addresses.forEach { addr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = "Pin", tint = SecondaryMint, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(addr.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextDeep)
                                        Text(addr.addressLine, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                                IconButton(onClick = { viewModel.removeAddress(addr.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f))
                                }
                            }
                        }
                    } else {
                        Text("No saved addresses.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
            }
        }

        // 3. FAQ Help Center
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Help Center & FAQ",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextDeep
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    FAQItem("What is the turnaround time?", "Standard services are processed and delivered back within 24 to 48 hours. Express option is available upon request.")
                    FAQItem("How do you weigh the clothes?", "Our runner weighs your bags at pick up, and exact counts are double checked at our wash facilities before raising billing invoice details.")
                    FAQItem("Is dry cleaning done separately?", "Yes! Delicates, silk, and dry clean garments are treated individually under precise temperatures.")
                }
            }
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(question, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextDeep, modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = "Expand",
                tint = PrimaryTeal
            )
        }
        AnimatedVisibility(visible = expanded) {
            Text(
                text = answer,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }
    }
}
