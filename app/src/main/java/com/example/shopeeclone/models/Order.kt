package com.example.shopeeclone.models

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<OrderItem> = listOf(),
    val totalAmount: Double = 0.0,
    val subtotal: Double = 0.0,
    val shippingFee: Double = 0.0,
    val discount: Double = 0.0,
    val paymentMethod: String = "",
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val orderStatus: OrderStatus = OrderStatus.PENDING,
    val shippingAddress: Address = Address(),
    val trackingNumber: String = "",
    val orderDate: Long = System.currentTimeMillis(),
    val estimatedDeliveryDate: Long? = null,
    val deliveredDate: Long? = null,
    val notes: String = ""
)

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val variant: String = "",
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val sellerId: String = ""
)

data class Address(
    val fullName: String = "",
    val phoneNumber: String = "",
    val streetAddress: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val country: String = "",
    val isDefault: Boolean = false,
    val label: String = "" // e.g., "Home", "Office"
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}

enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}
