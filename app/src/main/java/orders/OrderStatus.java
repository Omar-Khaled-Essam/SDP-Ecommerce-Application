package orders;

// SOLID: SRP✅ (enum for order states).
public enum OrderStatus {
    Pending,
    Shipped,
    Delivered,
    Cancelled
}
