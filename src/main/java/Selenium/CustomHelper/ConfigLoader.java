package Selenium.CustomHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * <h1>ConfigLoader</h1>
 * Utility class untuk membaca file konfigurasi `config.properties`
 * dan menyediakan method helper untuk mendapatkan value berdasarkan key.
 *
 * Versi ini mendukung:
 *  - get(key)
 *  - getOrDefault(key, defaultValue)
 *  - has(key)
 *  - getAll() -> untuk mendapatkan semua konfigurasi dalam bentuk Map<String, String>
 *
 * @author Kenny Ramadhan
 * @version 1.1
 */
public class ConfigLoader {

    /** Menyimpan semua konfigurasi dari file config.properties */
    private static Properties props = new Properties();

    static {
        try {
            FileInputStream fis = new FileInputStream("src/main/java/MBI/DST/Resources/config.properties");
            props.load(fis);
            fis.close();
            System.out.println("✅ Config loaded successfully. Total keys: " + props.size());
        } catch (IOException e) {
            System.err.println("⚠️ Could not load config.properties. Using defaults where possible.");
        }
    }

    /**
     * Mengambil value dari key yang ada di config.properties.
     */
    public static String get(String key) {
        return props.getProperty(key);
    }

    /**
     * Mengambil value dari key, jika tidak ditemukan maka kembalikan defaultValue.
     */
    public static String getOrDefault(String key, String defaultValue) {
        String value = props.getProperty(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    /**
     * Mengecek apakah key memiliki value yang valid.
     */
    public static boolean has(String key) {
        String value = props.getProperty(key);
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Mengambil semua konfigurasi sebagai Map<String, String>.
     * Berguna untuk set capability secara otomatis.
     */
    public static Map<String, String> getAll() {
        if (props.isEmpty()) return Collections.emptyMap();

        return props.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> String.valueOf(e.getValue())
                ));
    }
}
