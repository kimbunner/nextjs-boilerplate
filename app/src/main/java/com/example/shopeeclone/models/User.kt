package com.example.shopeeclone.models

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val profileImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val wishlist: List<String> = listOf(), // List of product IDs
    val cartItems: List<CartItem> = listOf(),
    val isEmailVerified: Boolean = false
)

data class CartItem(
    val productId: String = "",
    val quantity: Int = 1,
    val selectedVariant: String = "", // For products with multiple variants
    val addedAt: Long = System.currentTimeMillis()
)
