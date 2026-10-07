package ru.dmiaa.core;

import java.io.IOException;
import java.util.Properties;

public class ConfigurationLoader {
    public static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(ConfigurationLoader.class); // лог
    private static Properties properties;

    /**
     * Загружает пропрети из файла application.properties
     */
    public static void loadProperty() {
        try {
            properties = new Properties();
            //копируем текущие системные проперти
            properties = System.getProperties();
            //загружаем наши
            properties.load(ConfigurationLoader.class.getResourceAsStream("/application.properties"));
            //устанавливаем проперти
            System.setProperties(properties);
        } catch (IOException e) {
            log.error("Ошибка загрузки проперти", e);
        }
    }

    /**
     * Загружает пропрети из указанного файла ресурсов *.properties
     */
    public static void loadFromResource(String fromResource) {
        try {
            properties = new Properties();
            //копируем текущие системные проперти
            properties = System.getProperties();
            //загружаем наши
            properties.load(ConfigurationLoader.class.getResourceAsStream(fromResource));
            //устанавливаем проперти
            System.setProperties(properties);
        } catch (IOException e) {
            log.error("Ошибка загрузки проперти", e);
        }
    }

    public static boolean hasProperty(String name) {
        return properties.contains(name);
    }

    public static String getProperty(String name) {
        return properties.getProperty(name);
    }
}