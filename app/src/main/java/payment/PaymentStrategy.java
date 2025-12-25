package payment;

// SOLID: ISP✅ (small interface), OCP✅ (adding new payment types can be done by implementing this interface).
public interface PaymentStrategy {
    void pay(double amount);
}
