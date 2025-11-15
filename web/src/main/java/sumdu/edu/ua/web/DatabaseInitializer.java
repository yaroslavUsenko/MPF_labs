package sumdu.edu.ua.web;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import sumdu.edu.ua.core.port.StorageInitializationPort;

/**
 * Компонент для ініціалізації бази даних при старті застосунку.
 * Демонструє IoC контейнер та управління життєвим циклом бінів.
 */
@Component
public class DatabaseInitializer {
    
    private final StorageInitializationPort storageInitializer;

    public DatabaseInitializer(StorageInitializationPort storageInitializer) {
        this.storageInitializer = storageInitializer;
        System.out.println("DatabaseInitializer created - демонстрація створення біну");
    }

    /**
     * Викликається після повного запуску Spring Application Context.
     * Це демонструє управління життєвим циклом біну та Application Events.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initDatabase() {
        System.out.println("🔧 Ініціалізація бази даних...");
        try {
            storageInitializer.initialize();
            System.out.println("✅ База даних успішно ініціалізована");
        } catch (Exception e) {
            System.err.println("❌ Помилка при ініціалізації бази даних: " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }
}

