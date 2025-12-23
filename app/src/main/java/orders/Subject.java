package orders;

public interface Subject {
    void attach(Observer observer);
    void notifyObservers();
}
