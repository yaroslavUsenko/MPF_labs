package sumdu.edu.ua.persistence.jdbc;

import org.springframework.stereotype.Component;

import sumdu.edu.ua.core.port.StorageInitializationPort;

/**
 * JDBC реалізація порту ініціалізації сховища
 */
@Component
public class JdbcStorageInitializer implements StorageInitializationPort {
    @Override
    public void initialize() {
        DbInit.init();
    }
}
