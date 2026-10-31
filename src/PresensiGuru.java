public class PresensiGuru {
    private int idPresensi;
    private String tanggal;
    private String nipGuru;
    private String namaGuru;
    private String status;
    private String keterangan;
    private String dicatatOleh;
    
    public PresensiGuru() {}
    
    public PresensiGuru(int idPresensi, String tanggal, String nipGuru, String namaGuru,
                        String status, String keterangan, String dicatatOleh) {
        this.idPresensi = idPresensi;
        this.tanggal = tanggal;
        this.nipGuru = nipGuru;
        this.namaGuru = namaGuru;
        this.status = status;
        this.keterangan = keterangan;
        this.dicatatOleh = dicatatOleh;
    }
    
    public int getIdPresensi() { return idPresensi; }
    public void setIdPresensi(int idPresensi) { this.idPresensi = idPresensi; }
    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
    public String getNipGuru() { return nipGuru; }
    public void setNipGuru(String nipGuru) { this.nipGuru = nipGuru; }
    public String getNamaGuru() { return namaGuru; }
    public void setNamaGuru(String namaGuru) { this.namaGuru = namaGuru; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
    public String getDicatatOleh() { return dicatatOleh; }
    public void setDicatatOleh(String dicatatOleh) { this.dicatatOleh = dicatatOleh; }
}