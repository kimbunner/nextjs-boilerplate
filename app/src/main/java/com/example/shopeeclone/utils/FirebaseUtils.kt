package com.example.shopeeclone.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.messaging.FirebaseMessaging
import com.example.shopeeclone.models.*
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query

object FirebaseUtils {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val messaging = FirebaseMessaging.getInstance()

    // Collections
    private const val USERS = "users"
    private const val PRODUCTS = "products"
    private const val ORDERS = "orders"
    private const val CATEGORIES = "categories"
    private const val CART = "cart"

    // Auth Operations
    suspend fun signIn(email: String, password: String) = 
        auth.signInWithEmailAndPassword(email, password).await()

    suspend fun signUp(email: String, password: String) =
        auth.createUserWithEmailAndPassword(email, password).await()

    fun signOut() = auth.signOut()

    fun getCurrentUser() = auth.currentUser

    // User Operations
    suspend fun createUserProfile(user: User) =
        firestore.collection(USERS).document(user.uid).set(user).await()

    suspend fun getUserProfile(userId: String) =
        firestore.collection(USERS).document(userId).get().await()

    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>) =
        firestore.collection(USERS).document(userId).update(updates).await()

    // Product Operations
    suspend fun getProducts(category: String? = null, limit: Long = 20) =
        firestore.collection(PRODUCTS)
            .apply {
                category?.let { whereEqualTo("category", it) }
            }
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()

    suspend fun getProduct(productId: String) =
        firestore.collection(PRODUCTS).document(productId).get().await()

    suspend fun searchProducts(query: String) =
        firestore.collection(PRODUCTS)
            .whereGreaterThanOrEqualTo("title", query)
            .whereLessThanOrEqualTo("title", query + '\uf8ff')
            .get()
            .await()

    // Cart Operations
    suspend fun addToCart(userId: String, cartItem: CartItem) =
        firestore.collection(USERS).document(userId)
            .collection(CART).add(cartItem).await()

    suspend fun getCartItems(userId: String) =
        firestore.collection(USERS).document(userId)
            .collection(CART).get().await()

    suspend fun updateCartItem(userId: String, cartItemId: String, updates: Map<String, Any>) =
        firestore.collection(USERS).document(userId)
            .collection(CART).document(cartItemId)
            .update(updates).await()

    suspend fun removeFromCart(userId: String, cartItemId: String) =
        firestore.collection(USERS).document(userId)
            .collection(CART).document(cartItemId)
            .delete().await()

    // Order Operations
    suspend fun createOrder(order: Order) =
        firestore.collection(ORDERS).add(order).await()

    suspend fun getUserOrders(userId: String) =
        firestore.collection(ORDERS)
            .whereEqualTo("userId", userId)
            .orderBy("orderDate", Query.Direction.DESCENDING)
            .get()
            .await()

    suspend fun getOrder(orderId: String) =
        firestore.collection(ORDERS).document(orderId).get().await()

    // Storage Operations
    fun getStorageReference(path: String) = storage.reference.child(path)

    suspend fun uploadImage(path: String, imageBytes: ByteArray) =
        storage.reference.child(path).putBytes(imageBytes).await()

    suspend fun getImageUrl(path: String) =
        storage.reference.child(path).downloadUrl.await()

    // FCM Operations
    suspend fun getFCMToken() = messaging.token.await()

    suspend fun subscribeTopic(topic: String) = messaging.subscribeToTopic(topic).await()

    suspend fun unsubscribeTopic(topic: String) = messaging.unsubscribeFromTopic(topic).await()

    // Error Handling
    sealed class FirebaseResult<out T> {
        data class Success<T>(val data: T) : FirebaseResult<T>()
        data class Error(val exception: Exception) : FirebaseResult<Nothing>()
    }

    suspend fun <T> safeCall(call: suspend () -> T): FirebaseResult<T> = try {
        FirebaseResult.Success(call())
    } catch (e: Exception) {
        FirebaseResult.Error(e)
    }
}
