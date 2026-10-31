public class Session {
    private static String kodeGuru;
    private static String username;
    private static String nama;
    private static String role;
    private static String mapel;
    
    public static void setLoginGuru(String kodeGuru, String nama, String mapel) {
        Session.kodeGuru = kodeGuru;
        Session.username = null;
        Session.nama = nama;
        Session.role = "guru";
        Session.mapel = mapel;
    }
    
    public static void setLoginAdmin(String username, String nama, String role) {
        Session.kodeGuru = null;
        Session.username = username;
        Session.nama = nama;
        Session.role = role;
        Session.mapel = null;
    }
    
    public static void clear() {
        kodeGuru = null;
        username = null;
        nama = null;
        role = null;
        mapel = null;
    }
    
    public static String getKodeGuru() { return kodeGuru; }
    public static String getUsername() { return username; }
    public static String getNama() { return nama; }
    public static String getRole() { return role; }
    public static String getMapel() { return mapel; }
    public static boolean isAdmin() { return "admin".equals(role); }
    public static boolean isGuru() { return "guru".equals(role); }
    public static boolean isKepsek() { return "kepsek".equals(role); }
    public static boolean isLoggedIn() { return role != null; }
}