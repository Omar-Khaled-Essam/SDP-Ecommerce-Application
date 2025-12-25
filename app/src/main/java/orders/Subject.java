package orders;

// SOLID: ISP✅ (small interface).
public interface Subject {
    void attach(Observer observer);

    void notifyObservers();
}
