public class JadwalPelajaran {
    private int idJadwal;
    private String hari;
    private String jamMulai;
    private String jamSelesai;
    private String kelas;
    private String mapel;
    private String nipGuru;
    private String namaGuru;
    private String tahunAjaran;
    private String semester;
    
    public JadwalPelajaran() {}
    
    public JadwalPelajaran(int idJadwal, String hari, String jamMulai, String jamSelesai,
                           String kelas, String mapel, String nipGuru, String namaGuru,
                           String tahunAjaran, String semester) {
        this.idJadwal = idJadwal;
        this.hari = hari;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.kelas = kelas;
        this.mapel = mapel;
        this.nipGuru = nipGuru;
        this.namaGuru = namaGuru;
        this.tahunAjaran = tahunAjaran;
        this.semester = semester;
    }
    
    public int getIdJadwal() { return idJadwal; }
    public void setIdJadwal(int idJadwal) { this.idJadwal = idJadwal; }
    public String getHari() { return hari; }
    public void setHari(String hari) { this.hari = hari; }
    public String getJamMulai() { return jamMulai; }
    public void setJamMulai(String jamMulai) { this.jamMulai = jamMulai; }
    public String getJamSelesai() { return jamSelesai; }
    public void setJamSelesai(String jamSelesai) { this.jamSelesai = jamSelesai; }
    public String getKelas() { return kelas; }
    public void setKelas(String kelas) { this.kelas = kelas; }
    public String getMapel() { return mapel; }
    public void setMapel(String mapel) { this.mapel = mapel; }
    public String getNipGuru() { return nipGuru; }
    public void setNipGuru(String nipGuru) { this.nipGuru = nipGuru; }
    public String getNamaGuru() { return namaGuru; }
    public void setNamaGuru(String namaGuru) { this.namaGuru = namaGuru; }
    public String getTahunAjaran() { return tahunAjaran; }
    public void setTahunAjaran(String tahunAjaran) { this.tahunAjaran = tahunAjaran; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    @Override
    public String toString() {
        return hari + " " + jamMulai + "-" + jamSelesai + " " + mapel + " (" + kelas + ")";
    }
}