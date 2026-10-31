import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CetakRaportPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> semesterCombo;
    private JComboBox<String> tahunAjaranCombo;
    private JTextField nisField;
    private JLabel namaLabel, kelasLabel, waliKelasLabel;
    private JLabel absenSakitLabel, absenIzinLabel, absenAlpaLabel;
    private JButton cariBtn, generateRaportBtn, backBtn;
    private JTextArea catatanArea;
    private String currentNIS = "";
    private String currentNama = "";
    private String currentKelas = "";
    private String currentWaliKelas = "";
    
    public CetakRaportPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 242, 245));
        
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("CETAK RAPORT SISWA (Cari pakai NIS)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        filterPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        formPanel.add(new JLabel("NIS Siswa:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        nisField = new JTextField();
        nisField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(nisField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.15;
        cariBtn = new JButton("CARI SISWA");
        cariBtn.setBackground(new Color(52, 152, 219));
        cariBtn.setForeground(Color.WHITE);
        cariBtn.setFocusPainted(false);
        formPanel.add(cariBtn, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Nama Siswa:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        namaLabel = new JLabel("-");
        namaLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        namaLabel.setForeground(new Color(41, 128, 185));
        formPanel.add(namaLabel, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Kelas:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        kelasLabel = new JLabel("-");
        kelasLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        kelasLabel.setForeground(new Color(41, 128, 185));
        formPanel.add(kelasLabel, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Wali Kelas:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        waliKelasLabel = new JLabel("-");
        waliKelasLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        waliKelasLabel.setForeground(new Color(155, 89, 182));
        formPanel.add(waliKelasLabel, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        semesterCombo = new JComboBox<>(new String[]{"Ganjil", "Genap"});
        semesterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(semesterCombo, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Tahun Ajaran:"), gbc);
        gbc.gridx = 3;
        tahunAjaranCombo = new JComboBox<>();
        tahunAjaranCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int thnSkr = cal.get(java.util.Calendar.YEAR);
        int blnSkr = cal.get(java.util.Calendar.MONTH) + 1;
        int thnMulai = thnSkr;
        if (blnSkr < 7) thnMulai = thnSkr - 1;
        tahunAjaranCombo.addItem((thnMulai - 1) + "/" + thnMulai);
        tahunAjaranCombo.addItem(thnMulai + "/" + (thnMulai + 1));
        tahunAjaranCombo.addItem((thnMulai + 1) + "/" + (thnMulai + 2));
        tahunAjaranCombo.setSelectedItem(thnMulai + "/" + (thnMulai + 1));
        formPanel.add(tahunAjaranCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Rekap Ketidakhadiran:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        JPanel absenPreviewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        absenPreviewPanel.setBackground(Color.WHITE);
        absenPreviewPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        absenSakitLabel = new JLabel("Sakit: 0 hari");
        absenSakitLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        absenSakitLabel.setForeground(new Color(52, 152, 219));
        
        absenIzinLabel = new JLabel("Izin: 0 hari");
        absenIzinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        absenIzinLabel.setForeground(new Color(241, 196, 15));
        
        absenAlpaLabel = new JLabel("Alpa: 0 hari");
        absenAlpaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        absenAlpaLabel.setForeground(new Color(231, 76, 60));
        
        absenPreviewPanel.add(absenSakitLabel);
        absenPreviewPanel.add(absenIzinLabel);
        absenPreviewPanel.add(absenAlpaLabel);
        
        formPanel.add(absenPreviewPanel, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Catatan Wali Kelas:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        catatanArea = new JTextArea(3, 30);
        catatanArea.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        catatanArea.setLineWrap(true);
        catatanArea.setWrapStyleWord(true);
        catatanArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        catatanArea.setText("Selamat dan sukses atas prestasi kamu semester ini. Semangat terus dalam menggali ilmu.");
        JScrollPane catatanScroll = new JScrollPane(catatanArea);
        catatanScroll.setPreferredSize(new Dimension(400, 60));
        formPanel.add(catatanScroll, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(Color.WHITE);
        
        generateRaportBtn = new JButton("GENERATE RAPORT PDF");
        generateRaportBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        generateRaportBtn.setBackground(new Color(46, 204, 113));
        generateRaportBtn.setForeground(Color.WHITE);
        generateRaportBtn.setFocusPainted(false);
        generateRaportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateRaportBtn.setEnabled(false);
        
        backBtn = new JButton("KEMBALI");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backBtn.setBackground(new Color(52, 73, 94));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnPanel.add(generateRaportBtn);
        btnPanel.add(backBtn);
        formPanel.add(btnPanel, gbc);
        
        filterPanel.add(formPanel, BorderLayout.CENTER);
        add(filterPanel, BorderLayout.NORTH);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel tableTitle = new JLabel("PREVIEW NILAI SISWA");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setForeground(new Color(41, 128, 185));
        tableContainer.add(tableTitle, BorderLayout.NORTH);
        
        String[] columns = {"No", "Mata Pelajaran", "Guru", "Tugas", "UTS", "UAS", "Nilai Akhir", "Predikat"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        int[] widths = {40, 180, 130, 70, 70, 70, 90, 90};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        add(tableContainer, BorderLayout.CENTER);
        
        nisField.addActionListener(e -> cariSiswa());
        cariBtn.addActionListener(e -> cariSiswa());
        semesterCombo.addActionListener(e -> {
            if (!currentNIS.isEmpty()) updatePreviewAbsensi();
        });
        generateRaportBtn.addActionListener(e -> generateRaportPDF());
        backBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "menuUtama");
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof MenuUtamaPanel) {
                    ((MenuUtamaPanel) comp).showDashboard();
                    break;
                }
            }
        });
    }
    
    private void cariSiswa() {
        String nis = nisField.getText().trim();
        if (nis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan NIS terlebih dahulu!");
            return;
        }
        
        String[] siswa = Database.getSiswaByNIS(nis);
        if (siswa != null) {
            currentNIS = nis;
            currentNama = siswa[1];
            currentKelas = siswa[9];
            currentWaliKelas = cariWaliKelas(currentKelas);
            
            namaLabel.setText(currentNama);
            kelasLabel.setText(currentKelas);
            waliKelasLabel.setText(currentWaliKelas);
            
            loadNilaiPreview();
            updatePreviewAbsensi();
            generateRaportBtn.setEnabled(true);
            
            JOptionPane.showMessageDialog(this, "Siswa ditemukan:\nNama: " + currentNama + "\nKelas: " + currentKelas + "\nWali Kelas: " + currentWaliKelas);
        } else {
            JOptionPane.showMessageDialog(this, "Siswa dengan NIS " + nis + " tidak ditemukan!");
            namaLabel.setText("-");
            kelasLabel.setText("-");
            waliKelasLabel.setText("-");
            tableModel.setRowCount(0);
            currentNIS = "";
            generateRaportBtn.setEnabled(false);
            resetPreviewAbsensi();
        }
    }
    
    private void updatePreviewAbsensi() {
        if (currentNIS.isEmpty()) return;
        int[] kehadiran = hitungKehadiran();
        absenSakitLabel.setText("Sakit: " + kehadiran[0] + " hari");
        absenIzinLabel.setText("Izin: " + kehadiran[1] + " hari");
        absenAlpaLabel.setText("Alpa: " + kehadiran[2] + " hari");
    }
    
    private void resetPreviewAbsensi() {
        absenSakitLabel.setText("Sakit: 0 hari");
        absenIzinLabel.setText("Izin: 0 hari");
        absenAlpaLabel.setText("Alpa: 0 hari");
    }
    
    private String cariWaliKelas(String namaKelas) {
        List<String[]> kelasList = Database.getAllKelas();
        for (String[] kelas : kelasList) {
            if (kelas.length > 2 && kelas[1].equalsIgnoreCase(namaKelas)) {
                return kelas[2];
            }
        }
        return "-";
    }
    
    private void loadNilaiPreview() {
        tableModel.setRowCount(0);
        List<Object[]> nilaiSiswa = Database.getAllNilai();
        
        int no = 1;
        double totalNilai = 0;
        int mapelCount = 0;
        
        for (Object[] nilai : nilaiSiswa) {
            String nisn = nilai[0].toString();
            if (nisn.equals(currentNIS)) {
                String mapel = nilai[2].toString();
                String guru = nilai[3].toString();
                double tugas = (double) nilai[4];
                double uts = (double) nilai[5];
                double uas = (double) nilai[6];
                double akhir = (double) nilai[7];
                
                String predikat = getPredikat(akhir);
                
                tableModel.addRow(new Object[]{no++, mapel, guru, tugas, uts, uas, akhir, predikat});
                totalNilai += akhir;
                mapelCount++;
            }
        }
        
        if (tableModel.getRowCount() == 0) {
            tableModel.addRow(new Object[]{"-", "Belum ada data nilai", "-", "-", "-", "-", "-", "-"});
        } else {
            double rataRata = totalNilai / mapelCount;
            String predikatAkhir = getPredikat(rataRata);
            tableModel.addRow(new Object[]{"", "TOTAL / RATA-RATA", "", "", "", "", String.format("%.1f", rataRata), predikatAkhir});
        }
    }
    
    private String getPredikat(double nilai) {
        if (nilai >= 90) return "A";
        else if (nilai >= 80) return "A-";
        else if (nilai >= 70) return "B+";
        else if (nilai >= 60) return "B";
        else return "C";
    }
    
    private int[] hitungKehadiran() {
        int sakit = 0, izin = 0, alpa = 0;
        List<Object[]> absensiList = Database.getAllAbsensi();
        
        for (Object[] absen : absensiList) {
            String nis = absen[2].toString();
            String status = absen[4].toString();
            
            if (nis.equals(currentNIS)) {
                switch (status) {
                    case "Sakit": sakit++; break;
                    case "Izin": izin++; break;
                    case "Alpa": alpa++; break;
                }
            }
        }
        return new int[]{sakit, izin, alpa};
    }
    
    private void generateRaportPDF() {
        if (currentNIS.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cari siswa terlebih dahulu!");
            return;
        }
        
        try {
            String semester = (String) semesterCombo.getSelectedItem();
            String tahunAjaran = (String) tahunAjaranCombo.getSelectedItem();
            String catatanWali = catatanArea.getText().trim();
            if (catatanWali.isEmpty()) catatanWali = "-";
            
            int[] kehadiran = hitungKehadiran();
            int sakit = kehadiran[0];
            int izin = kehadiran[1];
            int alpa = kehadiran[2];
            
            JFileChooser fileChooser = new JFileChooser();
            String fileName = "Raport_" + currentNIS + "_" + currentNama.replace(" ", "_") + "_" + semester + "_" + tahunAjaran.replace("/", "-") + ".pdf";
            fileChooser.setSelectedFile(new java.io.File(fileName));
            int result = fileChooser.showSaveDialog(this);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".pdf")) filePath += ".pdf";
                
                com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4, 40, 40, 40, 40);
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();
                
                com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 22, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font subTitleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 16, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10, com.itextpdf.text.Font.NORMAL);
                com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font boldFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font italicFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10, com.itextpdf.text.Font.ITALIC);
                com.itextpdf.text.Font smallFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10, com.itextpdf.text.Font.NORMAL);
                
                SimpleDateFormat sdfHari = new SimpleDateFormat("EEEE", new Locale("id", "ID"));
                SimpleDateFormat sdfTgl = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                String hari = sdfHari.format(new Date());
                String tanggalLengkap = sdfTgl.format(new Date());
                String kotaTanggal = "Jakarta, " + hari + " " + tanggalLengkap;
                
                // KOP SURAT
                com.itextpdf.text.pdf.PdfPTable kopTable = new com.itextpdf.text.pdf.PdfPTable(3);
                kopTable.setWidthPercentage(100);
                kopTable.setWidths(new float[]{0.20f, 0.60f, 0.20f});
                
                com.itextpdf.text.pdf.PdfPCell logoCell = new com.itextpdf.text.pdf.PdfPCell();
                logoCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                logoCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                logoCell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
                try {
                    java.net.URL imgUrl = getClass().getResource("/images/smapgri4.png");
                    if (imgUrl != null) {
                        com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(imgUrl);
                        logo.scaleToFit(85, 85);
                        logoCell.addElement(logo);
                    }
                } catch (Exception e) {}
                kopTable.addCell(logoCell);
                
                com.itextpdf.text.pdf.PdfPCell judulCell = new com.itextpdf.text.pdf.PdfPCell();
                judulCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                judulCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                judulCell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
                
                com.itextpdf.text.Paragraph namaSekolah = new com.itextpdf.text.Paragraph("SMA PGRI 4 JAKARTA", titleFont);
                namaSekolah.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                judulCell.addElement(namaSekolah);
                
                com.itextpdf.text.Paragraph alamatSekolah = new com.itextpdf.text.Paragraph("Jl. Cipayung Raya, Jakarta Timur 13840", smallFont);
                alamatSekolah.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                judulCell.addElement(alamatSekolah);
                
                com.itextpdf.text.Paragraph raportTitle = new com.itextpdf.text.Paragraph("LAPORAN HASIL BELAJAR (RAPORT)", subTitleFont);
                raportTitle.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                judulCell.addElement(raportTitle);
                
                kopTable.addCell(judulCell);
                
                com.itextpdf.text.pdf.PdfPCell logoKananCell = new com.itextpdf.text.pdf.PdfPCell();
                logoKananCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                logoKananCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                logoKananCell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
                try {
                    java.net.URL imgKananUrl = getClass().getResource("/images/logopgri.png");
                    if (imgKananUrl != null) {
                        com.itextpdf.text.Image logoKanan = com.itextpdf.text.Image.getInstance(imgKananUrl);
                        logoKanan.scaleToFit(85, 85);
                        logoKananCell.addElement(logoKanan);
                    }
                } catch (Exception e) {}
                kopTable.addCell(logoKananCell);
                
                document.add(kopTable);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.pdf.PdfPTable lineTable = new com.itextpdf.text.pdf.PdfPTable(1);
                lineTable.setWidthPercentage(100);
                com.itextpdf.text.pdf.PdfPCell lineCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(" "));
                lineCell.setBorder(com.itextpdf.text.pdf.PdfPCell.BOTTOM);
                lineCell.setBorderWidthBottom(1.5f);
                lineTable.addCell(lineCell);
                document.add(lineTable);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                // INFO SISWA
                com.itextpdf.text.pdf.PdfPTable infoTable = new com.itextpdf.text.pdf.PdfPTable(2);
                infoTable.setWidthPercentage(80);
                infoTable.setWidths(new float[]{0.30f, 0.70f});
                infoTable.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                
                addInfoRow(infoTable, "Nama Siswa", currentNama, normalFont);
                addInfoRow(infoTable, "NIS", currentNIS, normalFont);
                addInfoRow(infoTable, "Kelas", currentKelas, normalFont);
                addInfoRow(infoTable, "Wali Kelas", currentWaliKelas, normalFont);
                addInfoRow(infoTable, "Semester", semester, normalFont);
                addInfoRow(infoTable, "Tahun Ajaran", tahunAjaran, normalFont);
                
                document.add(infoTable);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                // TABEL NILAI
                com.itextpdf.text.pdf.PdfPTable nilaiTable = new com.itextpdf.text.pdf.PdfPTable(8);
                nilaiTable.setWidthPercentage(100);
                nilaiTable.setWidths(new float[]{0.4f, 2.0f, 1.5f, 0.7f, 0.7f, 0.7f, 0.8f, 0.9f});
                
                String[] nilaiHeaders = {"No", "Mata Pelajaran", "Guru Pengajar", "Tugas", "UTS", "UAS", "Nilai Akhir", "Predikat"};
                for (String header : nilaiHeaders) {
                    com.itextpdf.text.pdf.PdfPCell headerCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(header, headerFont));
                    headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 220, 240));
                    headerCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    headerCell.setPadding(5);
                    nilaiTable.addCell(headerCell);
                }
                
                double totalNilai = 0;
                int mapelCount = 0;
                
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String no = tableModel.getValueAt(i, 0) != null ? tableModel.getValueAt(i, 0).toString() : "";
                    if (no.equals("") || no.equals("-")) continue;
                    
                    String mapel = tableModel.getValueAt(i, 1) != null ? tableModel.getValueAt(i, 1).toString() : "";
                    String guru = tableModel.getValueAt(i, 2) != null ? tableModel.getValueAt(i, 2).toString() : "";
                    String tugas = tableModel.getValueAt(i, 3) != null ? tableModel.getValueAt(i, 3).toString() : "";
                    String uts = tableModel.getValueAt(i, 4) != null ? tableModel.getValueAt(i, 4).toString() : "";
                    String uas = tableModel.getValueAt(i, 5) != null ? tableModel.getValueAt(i, 5).toString() : "";
                    String akhir = tableModel.getValueAt(i, 6) != null ? tableModel.getValueAt(i, 6).toString() : "";
                    String predikat = tableModel.getValueAt(i, 7) != null ? tableModel.getValueAt(i, 7).toString() : "";
                    
                    try {
                        totalNilai += Double.parseDouble(akhir);
                        mapelCount++;
                    } catch (NumberFormatException e) {}
                    
                    com.itextpdf.text.pdf.PdfPCell c1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(no, normalFont));
                    c1.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    c1.setPadding(5);
                    nilaiTable.addCell(c1);
                    
                    com.itextpdf.text.pdf.PdfPCell c2 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(mapel, normalFont));
                    c2.setPadding(5);
                    nilaiTable.addCell(c2);
                    
                    com.itextpdf.text.pdf.PdfPCell c3 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(guru, normalFont));
                    c3.setPadding(5);
                    nilaiTable.addCell(c3);
                    
                    com.itextpdf.text.pdf.PdfPCell c4 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(tugas, normalFont));
                    c4.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    c4.setPadding(5);
                    nilaiTable.addCell(c4);
                    
                    com.itextpdf.text.pdf.PdfPCell c5 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(uts, normalFont));
                    c5.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    c5.setPadding(5);
                    nilaiTable.addCell(c5);
                    
                    com.itextpdf.text.pdf.PdfPCell c6 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(uas, normalFont));
                    c6.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    c6.setPadding(5);
                    nilaiTable.addCell(c6);
                    
                    com.itextpdf.text.pdf.PdfPCell c7 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(akhir, boldFont));
                    c7.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    c7.setPadding(5);
                    nilaiTable.addCell(c7);
                    
                    com.itextpdf.text.pdf.PdfPCell c8 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(predikat, normalFont));
                    c8.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    c8.setPadding(5);
                    nilaiTable.addCell(c8);
                }
                
                document.add(nilaiTable);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                double rataRata = mapelCount > 0 ? totalNilai / mapelCount : 0;
                String predikatAkhir = getPredikat(rataRata);
                
                com.itextpdf.text.Paragraph rataText = new com.itextpdf.text.Paragraph("Rata-rata Nilai : " + String.format("%.1f", rataRata) + "     Predikat : " + predikatAkhir, boldFont);
                rataText.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(rataText);
                
                // HALAMAN 2
                document.newPage();
                
                com.itextpdf.text.Paragraph judulLanjutan = new com.itextpdf.text.Paragraph("LAPORAN HASIL BELAJAR (RAPORT) - Lanjutan", subTitleFont);
                judulLanjutan.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(judulLanjutan);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph infoSingkat = new com.itextpdf.text.Paragraph(
                    "Nama: " + currentNama + "   |   NIS: " + currentNIS + "   |   Kelas: " + currentKelas + 
                    "   |   Semester: " + semester + "   |   Tahun Ajaran: " + tahunAjaran, normalFont);
                infoSingkat.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(infoSingkat);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph ketTitle = new com.itextpdf.text.Paragraph("KETIDAKHADIRAN", headerFont);
                document.add(ketTitle);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.pdf.PdfPTable ketTable = new com.itextpdf.text.pdf.PdfPTable(2);
                ketTable.setWidthPercentage(50);
                ketTable.setWidths(new float[]{0.4f, 0.6f});
                ketTable.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                
                addInfoRow(ketTable, "Sakit", sakit + " hari", normalFont);
                addInfoRow(ketTable, "Izin", izin + " hari", normalFont);
                addInfoRow(ketTable, "Alpa", alpa + " hari", normalFont);
                
                document.add(ketTable);
                document.add(new com.itextpdf.text.Paragraph(" "));
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph catatanTitle = new com.itextpdf.text.Paragraph("CATATAN WALI KELAS", headerFont);
                document.add(catatanTitle);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.pdf.PdfPTable catatanTable = new com.itextpdf.text.pdf.PdfPTable(1);
                catatanTable.setWidthPercentage(100);
                com.itextpdf.text.pdf.PdfPCell catatanCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(catatanWali, italicFont));
                catatanCell.setBorder(com.itextpdf.text.pdf.PdfPCell.BOX);
                catatanCell.setPadding(10);
                catatanCell.setMinimumHeight(60);
                catatanTable.addCell(catatanCell);
                document.add(catatanTable);
                document.add(new com.itextpdf.text.Paragraph(" "));
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph tanggapanTitle = new com.itextpdf.text.Paragraph("TANGGAPAN ORANG TUA/WALI", headerFont);
                document.add(tanggapanTitle);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.pdf.PdfPTable tanggapanTable = new com.itextpdf.text.pdf.PdfPTable(1);
                tanggapanTable.setWidthPercentage(100);
                com.itextpdf.text.pdf.PdfPCell tanggapanCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(" ", normalFont));
                tanggapanCell.setBorder(com.itextpdf.text.pdf.PdfPCell.BOX);
                tanggapanCell.setPadding(10);
                tanggapanCell.setMinimumHeight(100);
                tanggapanTable.addCell(tanggapanCell);
                document.add(tanggapanTable);
                document.add(new com.itextpdf.text.Paragraph(" "));
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                // TTD 3 KOLOM SEJAJAR
                com.itextpdf.text.pdf.PdfPTable ttdTable = new com.itextpdf.text.pdf.PdfPTable(3);
                ttdTable.setWidthPercentage(100);
                ttdTable.setWidths(new float[]{0.33f, 0.34f, 0.33f});
                ttdTable.setSpacingBefore(10);
                
                // KOLOM 1: WALI KELAS
                com.itextpdf.text.pdf.PdfPCell waliKelasCell = new com.itextpdf.text.pdf.PdfPCell();
                waliKelasCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                waliKelasCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                waliKelasCell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_TOP);
                waliKelasCell.setFixedHeight(120);
                waliKelasCell.setPadding(5);
                
                com.itextpdf.text.Paragraph waliKelasJabatan = new com.itextpdf.text.Paragraph("Wali Kelas,", normalFont);
                waliKelasJabatan.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                waliKelasCell.addElement(waliKelasJabatan);
                waliKelasCell.addElement(new com.itextpdf.text.Paragraph(" "));
                waliKelasCell.addElement(new com.itextpdf.text.Paragraph(" "));
                waliKelasCell.addElement(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph garisWaliKelas = new com.itextpdf.text.Paragraph("__________________________", normalFont);
                garisWaliKelas.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                waliKelasCell.addElement(garisWaliKelas);
                waliKelasCell.addElement(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph namaWaliKelasPar = new com.itextpdf.text.Paragraph(currentWaliKelas, normalFont);
                namaWaliKelasPar.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                waliKelasCell.addElement(namaWaliKelasPar);
                
                ttdTable.addCell(waliKelasCell);
                
                // KOLOM 2: ORANG TUA/WALI
                com.itextpdf.text.pdf.PdfPCell orangTuaCell = new com.itextpdf.text.pdf.PdfPCell();
                orangTuaCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                orangTuaCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                orangTuaCell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_TOP);
                orangTuaCell.setFixedHeight(120);
                orangTuaCell.setPadding(5);
                
                com.itextpdf.text.Paragraph orangTuaJabatan = new com.itextpdf.text.Paragraph("Orang Tua/Wali,", normalFont);
                orangTuaJabatan.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                orangTuaCell.addElement(orangTuaJabatan);
                orangTuaCell.addElement(new com.itextpdf.text.Paragraph(" "));
                orangTuaCell.addElement(new com.itextpdf.text.Paragraph(" "));
                orangTuaCell.addElement(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph garisOrangTua = new com.itextpdf.text.Paragraph("__________________________", normalFont);
                garisOrangTua.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                orangTuaCell.addElement(garisOrangTua);
                orangTuaCell.addElement(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph namaOrangTua = new com.itextpdf.text.Paragraph("( ________________ )", normalFont);
                namaOrangTua.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                orangTuaCell.addElement(namaOrangTua);
                
                ttdTable.addCell(orangTuaCell);
                
                // KOLOM 3: KEPALA SEKOLAH
                com.itextpdf.text.pdf.PdfPCell kepalaCell = new com.itextpdf.text.pdf.PdfPCell();
                kepalaCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                kepalaCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                kepalaCell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_TOP);
                kepalaCell.setFixedHeight(120);
                kepalaCell.setPadding(5);
                
                com.itextpdf.text.Paragraph kotaTanggalPar = new com.itextpdf.text.Paragraph(kotaTanggal, normalFont);
                kotaTanggalPar.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                kepalaCell.addElement(kotaTanggalPar);
                kepalaCell.addElement(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph kepalaJabatan = new com.itextpdf.text.Paragraph("Kepala Sekolah,", normalFont);
                kepalaJabatan.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                kepalaCell.addElement(kepalaJabatan);
                kepalaCell.addElement(new com.itextpdf.text.Paragraph(" "));
                kepalaCell.addElement(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph garisKepala = new com.itextpdf.text.Paragraph("__________________________", normalFont);
                garisKepala.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                kepalaCell.addElement(garisKepala);
                kepalaCell.addElement(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph namaKepala = new com.itextpdf.text.Paragraph("ADE SYAMSUDIN, M.Pd", boldFont);
                namaKepala.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                kepalaCell.addElement(namaKepala);
                
                ttdTable.addCell(kepalaCell);
                
                document.add(ttdTable);
                
                document.close();
                JOptionPane.showMessageDialog(this, "Raport PDF berhasil disimpan!\nLokasi: " + filePath);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addInfoRow(com.itextpdf.text.pdf.PdfPTable table, String label, String value, com.itextpdf.text.Font font) {
        com.itextpdf.text.pdf.PdfPCell labelCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(label + " :", font));
        labelCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
        labelCell.setPadding(3);
        com.itextpdf.text.pdf.PdfPCell valueCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(value, font));
        valueCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
        valueCell.setPadding(3);
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}