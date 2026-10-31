import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    
    // ==================== SISWA ====================
    public static List<String[]> getAllSiswa() {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT nis, nama, jk, tempat_lahir, tgl_lahir, agama, alamat, kelas, tahun_masuk FROM tbl_siswa ORDER BY nis";
        try (Statement stmt = KoneksiDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("nis") != null ? rs.getString("nis") : "",
                    rs.getString("nama"),
                    rs.getString("jk"),
                    rs.getString("tempat_lahir"),
                    rs.getString("tgl_lahir") != null ? rs.getString("tgl_lahir") : "",
                    rs.getString("agama"),
                    rs.getString("alamat"),
                    rs.getString("kelas"),
                    rs.getString("tahun_masuk")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public static String[] getSiswaByNIS(String nis) {
        String query = "SELECT nis, nama, jk, tempat_lahir, tgl_lahir, agama, alamat, kelas, tahun_masuk FROM tbl_siswa WHERE nis = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, nis);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new String[]{
                    rs.getString("nis") != null ? rs.getString("nis") : "",
                    rs.getString("nama"),
                    rs.getString("jk"),
                    rs.getString("tempat_lahir"),
                    rs.getString("tgl_lahir") != null ? rs.getString("tgl_lahir") : "",
                    rs.getString("agama"),
                    rs.getString("alamat"),
                    rs.getString("kelas"),
                    rs.getString("tahun_masuk")
                };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    public static boolean tambahSiswa(String nis, String nama, String jk, String tempatLahir, String tglLahir, 
                                      String agama, String alamat, String kelas, String tahunMasuk) {
        String query = "INSERT INTO tbl_siswa (nis, nama, jk, tempat_lahir, tgl_lahir, agama, alamat, kelas, tahun_masuk) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, nis);
            pstmt.setString(2, nama);
            pstmt.setString(3, jk);
            pstmt.setString(4, tempatLahir);
            pstmt.setString(5, tglLahir);
            pstmt.setString(6, agama);
            pstmt.setString(7, alamat);
            pstmt.setString(8, kelas);
            pstmt.setString(9, tahunMasuk);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static boolean updateSiswa(String nisLama, String nisBaru, String nama, String jk, String tempatLahir, 
                                      String tglLahir, String agama, String alamat, String kelas, String tahunMasuk) {
        String query = "UPDATE tbl_siswa SET nis = ?, nama = ?, jk = ?, tempat_lahir = ?, tgl_lahir = ?, agama = ?, alamat = ?, kelas = ?, tahun_masuk = ? WHERE nis = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, nisBaru);
            pstmt.setString(2, nama);
            pstmt.setString(3, jk);
            pstmt.setString(4, tempatLahir);
            pstmt.setString(5, tglLahir);
            pstmt.setString(6, agama);
            pstmt.setString(7, alamat);
            pstmt.setString(8, kelas);
            pstmt.setString(9, tahunMasuk);
            pstmt.setString(10, nisLama);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static boolean hapusSiswa(String nis) {
        String query = "DELETE FROM tbl_siswa WHERE nis = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, nis);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static List<String[]> getSiswaByKelas(String kelas) {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT nis, nama, jk, tempat_lahir, tgl_lahir, agama, alamat, kelas, tahun_masuk FROM tbl_siswa WHERE kelas = ? ORDER BY nama";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, kelas);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("nis") != null ? rs.getString("nis") : "",
                    rs.getString("nama"),
                    rs.getString("jk"),
                    rs.getString("tempat_lahir"),
                    rs.getString("tgl_lahir") != null ? rs.getString("tgl_lahir") : "",
                    rs.getString("agama"),
                    rs.getString("alamat"),
                    rs.getString("kelas"),
                    rs.getString("tahun_masuk")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    // ==================== GURU ====================
    public static List<String[]> getAllGuru() {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT kode_guru, jk, nama_guru, mapel, status FROM tbl_guru ORDER BY kode_guru";
        try (Statement stmt = KoneksiDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("kode_guru"),
                    rs.getString("jk") != null ? rs.getString("jk") : "",
                    rs.getString("nama_guru"),
                    rs.getString("mapel"),
                    rs.getString("status") != null ? rs.getString("status") : "aktif"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public static boolean tambahGuru(String kodeGuru, String jk, String nama, String mapel) {
        String query = "INSERT INTO tbl_guru (kode_guru, jk, nama_guru, mapel, password, status) VALUES (?, ?, ?, ?, MD5('guru123'), 'aktif')";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, kodeGuru);
            pstmt.setString(2, jk);
            pstmt.setString(3, nama);
            pstmt.setString(4, mapel);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static boolean updateGuru(String kodeGuruLama, String kodeGuruBaru, String jk, String nama, String mapel) {
        String query = "UPDATE tbl_guru SET kode_guru = ?, jk = ?, nama_guru = ?, mapel = ? WHERE kode_guru = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, kodeGuruBaru);
            pstmt.setString(2, jk);
            pstmt.setString(3, nama);
            pstmt.setString(4, mapel);
            pstmt.setString(5, kodeGuruLama);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static boolean hapusGuru(String kodeGuru) {
        String query = "DELETE FROM tbl_guru WHERE kode_guru = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, kodeGuru);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    // ==================== KELAS ====================
    public static List<String[]> getAllKelas() {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT id_kelas, nama_kelas, wali_kelas FROM tbl_kelas ORDER BY nama_kelas";
        try (Statement stmt = KoneksiDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("id_kelas"),
                    rs.getString("nama_kelas"),
                    rs.getString("wali_kelas")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public static boolean tambahKelas(String id, String nama, String wali) {
        String query = "INSERT INTO tbl_kelas (id_kelas, nama_kelas, wali_kelas) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.setString(2, nama);
            pstmt.setString(3, wali);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static boolean updateKelas(String idLama, String idBaru, String nama, String wali) {
        String query = "UPDATE tbl_kelas SET id_kelas = ?, nama_kelas = ?, wali_kelas = ? WHERE id_kelas = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, idBaru);
            pstmt.setString(2, nama);
            pstmt.setString(3, wali);
            pstmt.setString(4, idLama);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static boolean hapusKelas(String id) {
        String query = "DELETE FROM tbl_kelas WHERE id_kelas = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    // ==================== NILAI ====================
    public static List<Object[]> getAllNilai() {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT nis, nama_siswa, mata_pelajaran, guru_pengajar, tugas, uts, uas, nilai_akhir FROM tbl_nilai";
        try (Statement stmt = KoneksiDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("nis"),
                    rs.getString("nama_siswa"),
                    rs.getString("mata_pelajaran") != null ? rs.getString("mata_pelajaran") : "",
                    rs.getString("guru_pengajar") != null ? rs.getString("guru_pengajar") : "",
                    rs.getDouble("tugas"),
                    rs.getDouble("uts"),
                    rs.getDouble("uas"),
                    rs.getDouble("nilai_akhir")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public static boolean simpanNilai(String nis, String nama, String mataPelajaran, String guruPengajar,
                                      double tugas, double uts, double uas, double akhir) {
        String cekQuery = "SELECT COUNT(*) FROM tbl_nilai WHERE nis = ? AND mata_pelajaran = ?";
        try (PreparedStatement cekStmt = KoneksiDB.getConnection().prepareStatement(cekQuery)) {
            cekStmt.setString(1, nis);
            cekStmt.setString(2, mataPelajaran);
            ResultSet rs = cekStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                String updateQuery = "UPDATE tbl_nilai SET guru_pengajar = ?, tugas = ?, uts = ?, uas = ?, nilai_akhir = ? WHERE nis = ? AND mata_pelajaran = ?";
                try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(updateQuery)) {
                    pstmt.setString(1, guruPengajar);
                    pstmt.setDouble(2, tugas);
                    pstmt.setDouble(3, uts);
                    pstmt.setDouble(4, uas);
                    pstmt.setDouble(5, akhir);
                    pstmt.setString(6, nis);
                    pstmt.setString(7, mataPelajaran);
                    return pstmt.executeUpdate() > 0;
                }
            } else {
                String insertQuery = "INSERT INTO tbl_nilai (nis, nama_siswa, mata_pelajaran, guru_pengajar, tugas, uts, uas, nilai_akhir) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(insertQuery)) {
                    pstmt.setString(1, nis);
                    pstmt.setString(2, nama);
                    pstmt.setString(3, mataPelajaran);
                    pstmt.setString(4, guruPengajar);
                    pstmt.setDouble(5, tugas);
                    pstmt.setDouble(6, uts);
                    pstmt.setDouble(7, uas);
                    pstmt.setDouble(8, akhir);
                    return pstmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static List<String[]> getAllMataPelajaranFromGuru() {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT nama_guru, mapel FROM tbl_guru ORDER BY mapel";
        try (Statement stmt = KoneksiDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("nama_guru"),
                    rs.getString("mapel")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    // ==================== ABSENSI ====================
    public static List<Object[]> getAllAbsensi() {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT tanggal, kelas, nis, nama_siswa, status FROM tbl_absensi ORDER BY tanggal DESC";
        try (Statement stmt = KoneksiDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getDate("tanggal"),
                    rs.getString("kelas"),
                    rs.getString("nis"),
                    rs.getString("nama_siswa"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public static boolean simpanAbsensi(String tanggal, String kelas, String nis, String nama, String status) {
        String query = "INSERT INTO tbl_absensi (tanggal, kelas, nis, nama_siswa, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, tanggal);
            pstmt.setString(2, kelas);
            pstmt.setString(3, nis);
            pstmt.setString(4, nama);
            pstmt.setString(5, status);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static boolean hapusAbsensiByTanggalDanKelas(String tanggal, String kelas) {
        String query = "DELETE FROM tbl_absensi WHERE tanggal = ? AND kelas = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, tanggal);
            pstmt.setString(2, kelas);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    // ==================== PEMBAYARAN SPP ====================
    public static List<Object[]> getAllPembayaran() {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT id_pembayaran, nis, nama_siswa, jenis_tagihan, bulan, total_tagihan, dibayar, sisa_tagihan, status, tgl_bayar, metode FROM tbl_pembayaran ORDER BY tgl_bayar DESC";
        try (Statement stmt = KoneksiDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_pembayaran"),
                    rs.getString("nis"),
                    rs.getString("nama_siswa"),
                    rs.getString("jenis_tagihan"),
                    rs.getString("bulan") != null ? rs.getString("bulan") : "",
                    rs.getDouble("total_tagihan"),
                    rs.getDouble("dibayar"),
                    rs.getDouble("sisa_tagihan"),
                    rs.getString("status"),
                    rs.getDate("tgl_bayar") != null ? rs.getDate("tgl_bayar").toString() : "",
                    rs.getString("metode") != null ? rs.getString("metode") : "Tunai"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public static boolean simpanPembayaran(String nis, String nama, String jenisTagihan, String bulan, 
                                           double totalTagihan, double dibayar, double sisaTagihan, 
                                           String status, String tglBayar, String catatan) {
        String query = "INSERT INTO tbl_pembayaran (nis, nama_siswa, jenis_tagihan, bulan, total_tagihan, dibayar, sisa_tagihan, status, tgl_bayar, catatan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, nis);
            pstmt.setString(2, nama);
            pstmt.setString(3, jenisTagihan);
            pstmt.setString(4, bulan);
            pstmt.setDouble(5, totalTagihan);
            pstmt.setDouble(6, dibayar);
            pstmt.setDouble(7, sisaTagihan);
            pstmt.setString(8, status);
            pstmt.setString(9, tglBayar);
            pstmt.setString(10, catatan);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static double getTotalTunggakanSiswa(String nis) {
        double total = 0;
        String query = "SELECT SUM(sisa_tagihan) as sisa FROM tbl_pembayaran WHERE nis = ? AND status != 'Lunas'";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, nis);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("sisa");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return total;
    }
    
    // ==================== SPP (Legacy) ====================
    public static List<Object[]> getAllSPP() {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT nis, nama_siswa, bulan, nominal, status, tgl_bayar FROM tbl_spp ORDER BY tgl_bayar DESC";
        try (Statement stmt = KoneksiDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("nis"),
                    rs.getString("nama_siswa"),
                    rs.getString("bulan"),
                    rs.getDouble("nominal"),
                    rs.getString("status"),
                    rs.getDate("tgl_bayar")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public static boolean simpanSPP(String nis, String nama, String bulan, double nominal, String status, String tglBayar) {
        String cekQuery = "SELECT COUNT(*) FROM tbl_spp WHERE nis = ? AND bulan = ?";
        try (PreparedStatement cekStmt = KoneksiDB.getConnection().prepareStatement(cekQuery)) {
            cekStmt.setString(1, nis);
            cekStmt.setString(2, bulan);
            ResultSet rs = cekStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                String updateQuery = "UPDATE tbl_spp SET nominal = ?, status = ?, tgl_bayar = ? WHERE nis = ? AND bulan = ?";
                try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(updateQuery)) {
                    pstmt.setDouble(1, nominal);
                    pstmt.setString(2, status);
                    pstmt.setString(3, tglBayar);
                    pstmt.setString(4, nis);
                    pstmt.setString(5, bulan);
                    return pstmt.executeUpdate() > 0;
                }
            } else {
                String insertQuery = "INSERT INTO tbl_spp (nis, nama_siswa, bulan, nominal, status, tgl_bayar) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(insertQuery)) {
                    pstmt.setString(1, nis);
                    pstmt.setString(2, nama);
                    pstmt.setString(3, bulan);
                    pstmt.setDouble(4, nominal);
                    pstmt.setString(5, status);
                    pstmt.setString(6, tglBayar);
                    return pstmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static double getTotalSPP() {
        double total = 0;
        String query = "SELECT SUM(dibayar) as total FROM tbl_pembayaran WHERE status = 'Lunas'";
        try (Statement stmt = KoneksiDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return total;
    }
    
    // ==================== EVENT KALENDER AKADEMIK ====================
    public static void createTableEvent() {
        String sql = "CREATE TABLE IF NOT EXISTS tbl_event (" +
                     "id_event INT PRIMARY KEY AUTO_INCREMENT," +
                     "judul VARCHAR(200) NOT NULL," +
                     "deskripsi TEXT," +
                     "tgl_mulai DATE NOT NULL," +
                     "tgl_selesai DATE," +
                     "tipe_event VARCHAR(50)," +
                     "warna VARCHAR(20)," +
                     "kelas_target VARCHAR(100) DEFAULT 'Semua Kelas'," +
                     "dibuat_oleh VARCHAR(100)," +
                     "tgl_dibuat TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                     ")";
        try (Statement stmt = KoneksiDB.getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {}
    }
    
    // ==================== LOGIN ====================
    public static Object[] loginGuru(String kodeGuru, String password) {
        String query = "SELECT kode_guru, nama_guru, mapel FROM tbl_guru WHERE kode_guru = ? AND password = MD5(?) AND status = 'aktif'";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, kodeGuru);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String updateQuery = "UPDATE tbl_guru SET last_login = NOW() WHERE kode_guru = ?";
                try (PreparedStatement updateStmt = KoneksiDB.getConnection().prepareStatement(updateQuery)) {
                    updateStmt.setString(1, kodeGuru);
                    updateStmt.executeUpdate();
                }
                return new Object[]{rs.getString("kode_guru"), rs.getString("nama_guru"), rs.getString("mapel")};
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    public static Object[] loginAdmin(String username, String password, String role) {
        String query = "SELECT username, nama_lengkap, role FROM tbl_user WHERE username = ? AND password = MD5(?) AND role = ? AND status = 'aktif'";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String updateQuery = "UPDATE tbl_user SET last_login = NOW() WHERE username = ?";
                try (PreparedStatement updateStmt = KoneksiDB.getConnection().prepareStatement(updateQuery)) {
                    updateStmt.setString(1, username);
                    updateStmt.executeUpdate();
                }
                return new Object[]{rs.getString("username"), rs.getString("nama_lengkap"), rs.getString("role")};
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    public static boolean cekKodeGuru(String kodeGuru) {
        String query = "SELECT COUNT(*) FROM tbl_guru WHERE kode_guru = ? AND status = 'aktif'";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, kodeGuru);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    
    public static boolean resetPasswordGuru(String kodeGuru) {
        String query = "UPDATE tbl_guru SET password = MD5('guru123') WHERE kode_guru = ? AND status = 'aktif'";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, kodeGuru);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static String getNamaGuruByKode(String kodeGuru) {
        String query = "SELECT nama_guru FROM tbl_guru WHERE kode_guru = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, kodeGuru);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nama_guru");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    public static boolean gantiPasswordGuru(String kodeGuru, String passwordLama, String passwordBaru) {
        String cekQuery = "SELECT COUNT(*) FROM tbl_guru WHERE kode_guru = ? AND password = MD5(?)";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(cekQuery)) {
            pstmt.setString(1, kodeGuru);
            pstmt.setString(2, passwordLama);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                String updateQuery = "UPDATE tbl_guru SET password = MD5(?) WHERE kode_guru = ?";
                try (PreparedStatement pstmt2 = KoneksiDB.getConnection().prepareStatement(updateQuery)) {
                    pstmt2.setString(1, passwordBaru);
                    pstmt2.setString(2, kodeGuru);
                    return pstmt2.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    
    public static boolean gantiPasswordAdmin(String username, String passwordLama, String passwordBaru) {
        String cekQuery = "SELECT COUNT(*) FROM tbl_user WHERE username = ? AND password = MD5(?)";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(cekQuery)) {
            pstmt.setString(1, username);
            pstmt.setString(2, passwordLama);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                String updateQuery = "UPDATE tbl_user SET password = MD5(?) WHERE username = ?";
                try (PreparedStatement pstmt2 = KoneksiDB.getConnection().prepareStatement(updateQuery)) {
                    pstmt2.setString(1, passwordBaru);
                    pstmt2.setString(2, username);
                    return pstmt2.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    
    // ==================== JADWAL PELAJARAN ====================
    public static List<Object[]> getAllJadwal() {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT id_jadwal, hari, jam_mulai, jam_selesai, kelas, mapel, kode_guru, nama_guru, tahun_ajaran, semester FROM tbl_jadwal_pelajaran ORDER BY FIELD(hari,'Senin','Selasa','Rabu','Kamis','Jumat'), jam_mulai";
        try (Statement stmt = KoneksiDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_jadwal"),
                    rs.getString("hari"),
                    rs.getString("jam_mulai"),
                    rs.getString("jam_selesai"),
                    rs.getString("kelas"),
                    rs.getString("mapel"),
                    rs.getString("kode_guru"),
                    rs.getString("nama_guru"),
                    rs.getString("tahun_ajaran"),
                    rs.getString("semester")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public static List<Object[]> getJadwalByGuru(String kodeGuru) {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT id_jadwal, hari, jam_mulai, jam_selesai, kelas, mapel, kode_guru, nama_guru, tahun_ajaran, semester FROM tbl_jadwal_pelajaran WHERE kode_guru = ? ORDER BY FIELD(hari,'Senin','Selasa','Rabu','Kamis','Jumat'), jam_mulai";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, kodeGuru);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_jadwal"),
                    rs.getString("hari"),
                    rs.getString("jam_mulai"),
                    rs.getString("jam_selesai"),
                    rs.getString("kelas"),
                    rs.getString("mapel"),
                    rs.getString("kode_guru"),
                    rs.getString("nama_guru"),
                    rs.getString("tahun_ajaran"),
                    rs.getString("semester")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public static List<Object[]> getJadwalByHari(String hari) {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT id_jadwal, hari, jam_mulai, jam_selesai, kelas, mapel, kode_guru, nama_guru, tahun_ajaran, semester FROM tbl_jadwal_pelajaran WHERE hari = ? ORDER BY jam_mulai";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, hari);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_jadwal"),
                    rs.getString("hari"),
                    rs.getString("jam_mulai"),
                    rs.getString("jam_selesai"),
                    rs.getString("kelas"),
                    rs.getString("mapel"),
                    rs.getString("kode_guru"),
                    rs.getString("nama_guru"),
                    rs.getString("tahun_ajaran"),
                    rs.getString("semester")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public static boolean cekBentrokJadwal(String hari, String jamMulai, String jamSelesai, 
                                            String kelas, String kodeGuru, int idExclude) {
        String query = "SELECT COUNT(*) FROM tbl_jadwal_pelajaran " +
                       "WHERE hari = ? AND id_jadwal != ? " +
                       "AND (kelas = ? OR kode_guru = ?) " +
                       "AND (jam_mulai < ? AND jam_selesai > ?)";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, hari);
            pstmt.setInt(2, idExclude);
            pstmt.setString(3, kelas);
            pstmt.setString(4, kodeGuru);
            pstmt.setString(5, jamSelesai);
            pstmt.setString(6, jamMulai);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    
    public static boolean tambahJadwal(String hari, String jamMulai, String jamSelesai,
                                        String kelas, String mapel, String kodeGuru, String namaGuru,
                                        String tahunAjaran, String semester) {
        String query = "INSERT INTO tbl_jadwal_pelajaran (hari, jam_mulai, jam_selesai, kelas, mapel, kode_guru, nama_guru, tahun_ajaran, semester) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, hari);
            pstmt.setString(2, jamMulai);
            pstmt.setString(3, jamSelesai);
            pstmt.setString(4, kelas);
            pstmt.setString(5, mapel);
            pstmt.setString(6, kodeGuru);
            pstmt.setString(7, namaGuru);
            pstmt.setString(8, tahunAjaran);
            pstmt.setString(9, semester);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static boolean updateJadwal(int idJadwal, String hari, String jamMulai, String jamSelesai,
                                        String kelas, String mapel, String kodeGuru, String namaGuru,
                                        String tahunAjaran, String semester) {
        String query = "UPDATE tbl_jadwal_pelajaran SET hari=?, jam_mulai=?, jam_selesai=?, kelas=?, mapel=?, kode_guru=?, nama_guru=?, tahun_ajaran=?, semester=? WHERE id_jadwal=?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, hari);
            pstmt.setString(2, jamMulai);
            pstmt.setString(3, jamSelesai);
            pstmt.setString(4, kelas);
            pstmt.setString(5, mapel);
            pstmt.setString(6, kodeGuru);
            pstmt.setString(7, namaGuru);
            pstmt.setString(8, tahunAjaran);
            pstmt.setString(9, semester);
            pstmt.setInt(10, idJadwal);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static boolean hapusJadwal(int idJadwal) {
        String query = "DELETE FROM tbl_jadwal_pelajaran WHERE id_jadwal = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, idJadwal);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    // ==================== PRESENSI GURU ====================
    public static List<Object[]> getAllPresensiGuru() {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT id_presensi, tanggal, kode_guru, nama_guru, status, keterangan, dicatat_oleh FROM tbl_presensi_guru ORDER BY tanggal DESC, nama_guru";
        try (Statement stmt = KoneksiDB.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_presensi"),
                    rs.getString("tanggal"),
                    rs.getString("kode_guru"),
                    rs.getString("nama_guru"),
                    rs.getString("status"),
                    rs.getString("keterangan") != null ? rs.getString("keterangan") : "",
                    rs.getString("dicatat_oleh") != null ? rs.getString("dicatat_oleh") : ""
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public static List<Object[]> getPresensiGuruByKode(String kodeGuru) {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT id_presensi, tanggal, kode_guru, nama_guru, status, keterangan, dicatat_oleh FROM tbl_presensi_guru WHERE kode_guru = ? ORDER BY tanggal DESC";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, kodeGuru);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_presensi"),
                    rs.getString("tanggal"),
                    rs.getString("kode_guru"),
                    rs.getString("nama_guru"),
                    rs.getString("status"),
                    rs.getString("keterangan") != null ? rs.getString("keterangan") : "",
                    rs.getString("dicatat_oleh") != null ? rs.getString("dicatat_oleh") : ""
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public static boolean simpanPresensiGuru(String tanggal, String kodeGuru, String namaGuru, 
                                              String status, String keterangan, String dicatatOleh) {
        String cekQuery = "SELECT COUNT(*) FROM tbl_presensi_guru WHERE tanggal = ? AND kode_guru = ?";
        try (PreparedStatement cekStmt = KoneksiDB.getConnection().prepareStatement(cekQuery)) {
            cekStmt.setString(1, tanggal);
            cekStmt.setString(2, kodeGuru);
            ResultSet rs = cekStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                String upd = "UPDATE tbl_presensi_guru SET status = ?, keterangan = ?, dicatat_oleh = ? WHERE tanggal = ? AND kode_guru = ?";
                try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(upd)) {
                    pstmt.setString(1, status);
                    pstmt.setString(2, keterangan);
                    pstmt.setString(3, dicatatOleh);
                    pstmt.setString(4, tanggal);
                    pstmt.setString(5, kodeGuru);
                    return pstmt.executeUpdate() > 0;
                }
            } else {
                String ins = "INSERT INTO tbl_presensi_guru (tanggal, kode_guru, nama_guru, status, keterangan, dicatat_oleh) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(ins)) {
                    pstmt.setString(1, tanggal);
                    pstmt.setString(2, kodeGuru);
                    pstmt.setString(3, namaGuru);
                    pstmt.setString(4, status);
                    pstmt.setString(5, keterangan);
                    pstmt.setString(6, dicatatOleh);
                    return pstmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static boolean hapusPresensiGuru(int idPresensi) {
        String query = "DELETE FROM tbl_presensi_guru WHERE id_presensi = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, idPresensi);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public static boolean guruMengajarKelasHariIni(String kodeGuru, String hariIni, String kelas) {
        String query = "SELECT COUNT(*) FROM tbl_jadwal_pelajaran WHERE kode_guru = ? AND hari = ? AND kelas = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setString(1, kodeGuru);
            pstmt.setString(2, hariIni);
            pstmt.setString(3, kelas);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    
    public static String getHariIni() {
        String[] hari = {"Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
        java.util.Calendar cal = java.util.Calendar.getInstance();
        return hari[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1];
    }
    
    public static String getTahunAjaranSekarang() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int tahun = cal.get(java.util.Calendar.YEAR);
        int bulan = cal.get(java.util.Calendar.MONTH) + 1;
        int thnMulai = tahun;
        if (bulan < 7) thnMulai = tahun - 1;
        return thnMulai + "/" + (thnMulai + 1);
    }
}