package com.example.shopeeclone.models

data class Product(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val originalPrice: Double = 0.0,
    val discountPercentage: Int = 0,
    val category: String = "",
    val subCategory: String = "",
    val images: List<String> = listOf(), // List of image URLs
    val thumbnailUrl: String = "", // Main product image
    val rating: Float = 0f,
    val totalRatings: Int = 0,
    val reviews: List<Review> = listOf(),
    val specifications: Map<String, String> = mapOf(), // Key-value pairs of specifications
    val variants: List<ProductVariant> = listOf(),
    val sellerId: String = "",
    val sellerName: String = "",
    val stock: Int = 0,
    val soldCount: Int = 0,
    val isAvailable: Boolean = true,
    val tags: List<String> = listOf(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Review(
    val userId: String = "",
    val userName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val images: List<String> = listOf(), // Review images
    val createdAt: Long = System.currentTimeMillis(),
    val likes: Int = 0
)

data class ProductVariant(
    val id: String = "",
    val name: String = "", // e.g., "Color", "Size"
    val value: String = "", // e.g., "Red", "XL"
    val price: Double = 0.0,
    val stock: Int = 0,
    val imageUrl: String = ""
)
