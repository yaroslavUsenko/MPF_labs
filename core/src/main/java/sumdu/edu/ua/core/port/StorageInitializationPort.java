package sumdu.edu.ua.core.port;

/**
 * Порт для ініціалізації сховища.
 * Дозволяє web модулю ініціалізувати базу даних без прямої залежності на persistence
 */
public interface StorageInitializationPort {
    void initialize();
}
