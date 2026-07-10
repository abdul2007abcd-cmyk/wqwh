package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val phone: String,
    val address: String,
    val loyaltyPoints: Int = 100
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // e.g., "Home", "Office"
    val addressLine: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serviceType: String,
    val garmentsJson: String, // list of items as JSON array
    val isPerKg: Boolean,
    val weightKg: Double = 0.0,
    val pickupDate: String,
    val pickupTime: String,
    val deliveryDate: String,
    val deliveryTime: String,
    val address: String,
    val specialInstructions: String,
    val status: String, // PLACED, PICKED_UP, IN_PROCESS, READY, OUT_FOR_DELIVERY, DELIVERED
    val priceEstimateMin: Double,
    val priceEstimateMax: Double,
    val finalPrice: Double = 0.0,
    val appliedPromo: String? = null,
    val paymentMethod: String, // UPI, CARD, NET_BANKING, COD
    val paymentStatus: String, // PENDING, PAID
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface LaundromatDao {
    // User profile
    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    fun getUserFlow(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Saved addresses
    @Query("SELECT * FROM addresses ORDER BY id DESC")
    fun getAddressesFlow(): Flow<List<AddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Query("DELETE FROM addresses WHERE id = :id")
    suspend fun deleteAddress(id: Int)

    // Orders
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrdersFlow(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status != 'DELIVERED' ORDER BY timestamp DESC")
    fun getActiveOrdersFlow(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    fun getOrderByIdFlow(id: Int): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: Int): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET status = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: Int, status: String)

    @Query("UPDATE orders SET paymentStatus = :paymentStatus, finalPrice = :finalPrice WHERE id = :id")
    suspend fun finalizePayment(id: Int, paymentStatus: String, finalPrice: Double)
}

@Database(entities = [UserEntity::class, AddressEntity::class, OrderEntity::class], version = 1, exportSchema = false)
abstract class LaundromatDb : RoomDatabase() {
    abstract fun dao(): LaundromatDao

    companion object {
        @Volatile
        private var INSTANCE: LaundromatDb? = null

        fun getDatabase(context: android.content.Context): LaundromatDb {
            return INSTANCE ?: synchronized(this) {
                val isTest = try {
                    Class.forName("org.robolectric.Robolectric")
                    true
                } catch (e: ClassNotFoundException) {
                    false
                }
                val instance = if (isTest) {
                    androidx.room.Room.inMemoryDatabaseBuilder(
                        context.applicationContext,
                        LaundromatDb::class.java
                    ).allowMainThreadQueries().build()
                } else {
                    androidx.room.Room.databaseBuilder(
                        context.applicationContext,
                        LaundromatDb::class.java,
                        "wash_and_fold_db"
                    ).fallbackToDestructiveMigration().build()
                }
                INSTANCE = instance
                instance
            }
        }
    }
}
