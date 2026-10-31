import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class KoneksiDB {
    private static Connection connection;
    private static Properties config;

    // Baca konfigurasi dari config.properties (TIDAK ikut ke GitHub)
    private static Properties loadConfig() {
        if (config != null) return config;
        Properties p = new Properties();
        File f = new File("config.properties");
        if (!f.exists()) {
            System.out.println("config.properties tidak ditemukan di: " + f.getAbsolutePath());
            System.out.println("Salin config.properties.example menjadi config.properties lalu isi datanya.");
        } else {
            try (FileInputStream in = new FileInputStream(f)) {
                p.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = p;
        return config;
    }

    private static String buildUrl(Properties p) {
        String host = p.getProperty("db.host", "localhost");
        String port = p.getProperty("db.port", "3306");
        String name = p.getProperty("db.name", "db_siakad");
        String ssl  = p.getProperty("db.ssl", "false");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + name
                   + "?allowPublicKeyRetrieval=true&connectTimeout=10000&socketTimeout=30000";
        if (ssl.equalsIgnoreCase("true")) url += "&sslMode=REQUIRED";
        return url;
    }

    public static Connection getConnection() {
        try {
            // Sambung ulang otomatis kalau koneksi putus / sudah ditutup server
            if (connection == null || connection.isClosed() || !connection.isValid(3)) {
                connection = null;
                Properties p = loadConfig();
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                        buildUrl(p),
                        p.getProperty("db.user", "root"),
                        p.getProperty("db.password", ""));
                System.out.println("Koneksi database berhasil!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC tidak ditemukan!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Koneksi database gagal!");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Koneksi database ditutup.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}