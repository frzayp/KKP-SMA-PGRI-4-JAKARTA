import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public class LaporanNilaiPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> kelasCombo;
    private JComboBox<String> mapelCombo;
    private JButton filterBtn, refreshBtn, cetakBtn, backBtn;
    private JTextField searchField;
    
    private Map<String, String> waliKelasCache = new HashMap<>();
    
    public LaporanNilaiPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initComponents();
        loadKelas();
        loadMataPelajaran();
        loadWaliKelasCache();
        loadDataNilai();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 242, 245));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("LAPORAN NILAI SISWA (Kata kunci: NIS)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(15));
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        filterPanel.setBackground(Color.WHITE);
        
        filterPanel.add(new JLabel("Kelas:"));
        kelasCombo = new JComboBox<>();
        kelasCombo.setPreferredSize(new Dimension(180, 30));
        filterPanel.add(kelasCombo);
        
        filterPanel.add(Box.createHorizontalStrut(15));
        
        filterPanel.add(new JLabel("Mata Pelajaran:"));
        mapelCombo = new JComboBox<>();
        mapelCombo.setPreferredSize(new Dimension(180, 30));
        filterPanel.add(mapelCombo);
        
        topPanel.add(filterPanel);
        topPanel.add(Box.createVerticalStrut(10));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        
        searchPanel.add(new JLabel("Cari (NIS/Nama):"));
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchPanel.add(searchField);
        
        JButton cariBtn = new JButton("CARI");
        cariBtn.setBackground(new Color(52, 152, 219));
        cariBtn.setForeground(Color.WHITE);
        cariBtn.setFocusPainted(false);
        cariBtn.addActionListener(e -> cariData());
        searchPanel.add(cariBtn);
        
        JButton resetCariBtn = new JButton("RESET");
        resetCariBtn.setBackground(new Color(108, 117, 125));
        resetCariBtn.setForeground(Color.WHITE);
        resetCariBtn.setFocusPainted(false);
        resetCariBtn.addActionListener(e -> {
            searchField.setText("");
            filterData();
        });
        searchPanel.add(resetCariBtn);
        
        topPanel.add(searchPanel);
        topPanel.add(Box.createVerticalStrut(10));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        filterBtn = new JButton("TAMPILKAN");
        filterBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterBtn.setBackground(new Color(52, 152, 219));
        filterBtn.setForeground(Color.WHITE);
        filterBtn.addActionListener(e -> filterData());
        
        refreshBtn = new JButton("REFRESH");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> {
            loadKelas();
            loadMataPelajaran();
            loadWaliKelasCache();
            loadDataNilai();
            searchField.setText("");
        });
        
        cetakBtn = new JButton("CETAK PDF");
        cetakBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cetakBtn.setBackground(new Color(46, 204, 113));
        cetakBtn.setForeground(Color.WHITE);
        cetakBtn.addActionListener(e -> cetakPDF());
        
        backBtn = new JButton("KEMBALI KE MENU");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backBtn.setBackground(new Color(231, 76, 60));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "menuUtama");
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof MenuUtamaPanel) {
                    ((MenuUtamaPanel) comp).showDashboard();
                    break;
                }
            }
        });
        
        btnPanel.add(filterBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(cetakBtn);
        btnPanel.add(backBtn);
        
        topPanel.add(btnPanel);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        String[] columns = {"No", "NIS", "Nama Siswa", "Kelas", "Wali Kelas", "Mata Pelajaran", "Guru", "Tugas", "UTS", "UAS", "Nilai Akhir"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        table.setRowHeight(30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        int[] widths = {40, 80, 130, 80, 120, 120, 120, 60, 60, 60, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 10));
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);
    }
    
    private void loadKelas() {
        String selected = (String) kelasCombo.getSelectedItem();
        kelasCombo.removeAllItems();
        kelasCombo.addItem("Semua Kelas");
        for (String[] k : Database.getAllKelas()) {
            kelasCombo.addItem(k[1]);
        }
        if (selected != null) kelasCombo.setSelectedItem(selected);
    }
    
    private void loadMataPelajaran() {
        String selected = (String) mapelCombo.getSelectedItem();
        mapelCombo.removeAllItems();
        mapelCombo.addItem("Semua Mata Pelajaran");
        
        Set<String> mapelSet = new HashSet<>();
        List<Object[]> nilaiList = Database.getAllNilai();
        for (Object[] nilai : nilaiList) {
            String mapel = nilai[2].toString();
            if (mapel != null && !mapel.isEmpty()) {
                mapelSet.add(mapel);
            }
        }
        
        List<String> mapelList = new ArrayList<>(mapelSet);
        Collections.sort(mapelList);
        for (String mapel : mapelList) {
            mapelCombo.addItem(mapel);
        }
        if (selected != null) mapelCombo.setSelectedItem(selected);
    }
    
    private void loadWaliKelasCache() {
        waliKelasCache.clear();
        List<String[]> kelasList = Database.getAllKelas();
        for (String[] k : kelasList) {
            if (k.length > 2) {
                waliKelasCache.put(k[1], k[2]);
            }
        }
    }
    
    private String getWaliKelasByKelas(String namaKelas) {
        if (namaKelas == null || namaKelas.isEmpty()) return "-";
        String wali = waliKelasCache.get(namaKelas);
        return wali != null ? wali : "-";
    }
    
    private void loadDataNilai() {
        tableModel.setRowCount(0);
        int no = 1;
        List<Object[]> nilaiList = Database.getAllNilai();
        
        for (Object[] nilai : nilaiList) {
            String nis = nilai[0].toString();
            String nama = nilai[1].toString();
            String mapel = nilai[2].toString();
            String guru = nilai[3].toString();
            double tugas = (double) nilai[4];
            double uts = (double) nilai[5];
            double uas = (double) nilai[6];
            double akhir = (double) nilai[7];
            
            // PERBAIKAN: Index kelas sekarang 7, bukan 9
            String kelas = "-";
            String[] siswa = Database.getSiswaByNIS(nis);
            if (siswa != null) {
                kelas = siswa[7];
            }
            
            String waliKelas = getWaliKelasByKelas(kelas);
            
            tableModel.addRow(new Object[]{no++, nis, nama, kelas, waliKelas, mapel, guru, tugas, uts, uas, akhir});
        }
    }
    
    private void cariData() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            filterData();
            return;
        }
        
        String selectedKelas = (String) kelasCombo.getSelectedItem();
        String selectedMapel = (String) mapelCombo.getSelectedItem();
        
        tableModel.setRowCount(0);
        int no = 1;
        List<Object[]> nilaiList = Database.getAllNilai();
        
        for (Object[] nilai : nilaiList) {
            String nis = nilai[0].toString();
            String nama = nilai[1].toString();
            String mapel = nilai[2].toString();
            String guru = nilai[3].toString();
            double tugas = (double) nilai[4];
            double uts = (double) nilai[5];
            double uas = (double) nilai[6];
            double akhir = (double) nilai[7];
            
            // PERBAIKAN: Index kelas sekarang 7, bukan 9
            String kelas = "-";
            String[] siswa = Database.getSiswaByNIS(nis);
            if (siswa != null) {
                kelas = siswa[7];
            }
            
            String waliKelas = getWaliKelasByKelas(kelas);
            
            boolean matchKelas = selectedKelas.equals("Semua Kelas") || kelas.equals(selectedKelas);
            boolean matchMapel = selectedMapel.equals("Semua Mata Pelajaran") || mapel.equals(selectedMapel);
            boolean matchCari = nis.toLowerCase().contains(keyword) || nama.toLowerCase().contains(keyword);
            
            if (matchKelas && matchMapel && matchCari) {
                tableModel.addRow(new Object[]{no++, nis, nama, kelas, waliKelas, mapel, guru, tugas, uts, uas, akhir});
            }
        }
    }
    
    private void filterData() {
        String selectedKelas = (String) kelasCombo.getSelectedItem();
        String selectedMapel = (String) mapelCombo.getSelectedItem();
        
        tableModel.setRowCount(0);
        int no = 1;
        List<Object[]> nilaiList = Database.getAllNilai();
        
        for (Object[] nilai : nilaiList) {
            String nis = nilai[0].toString();
            String nama = nilai[1].toString();
            String mapel = nilai[2].toString();
            String guru = nilai[3].toString();
            double tugas = (double) nilai[4];
            double uts = (double) nilai[5];
            double uas = (double) nilai[6];
            double akhir = (double) nilai[7];
            
            // PERBAIKAN: Index kelas sekarang 7, bukan 9
            String kelas = "-";
            String[] siswa = Database.getSiswaByNIS(nis);
            if (siswa != null) {
                kelas = siswa[7];
            }
            
            String waliKelas = getWaliKelasByKelas(kelas);
            
            boolean matchKelas = selectedKelas.equals("Semua Kelas") || kelas.equals(selectedKelas);
            boolean matchMapel = selectedMapel.equals("Semua Mata Pelajaran") || mapel.equals(selectedMapel);
            
            if (matchKelas && matchMapel) {
                tableModel.addRow(new Object[]{no++, nis, nama, kelas, waliKelas, mapel, guru, tugas, uts, uas, akhir});
            }
        }
    }
    
    private void cetakPDF() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk dicetak!");
            return;
        }
        
        try {
            String selectedKelas = (String) kelasCombo.getSelectedItem();
            String selectedMapel = (String) mapelCombo.getSelectedItem();
            
            String fileName = "Laporan_Nilai";
            if (!selectedKelas.equals("Semua Kelas")) fileName += "_" + selectedKelas;
            if (!selectedMapel.equals("Semua Mata Pelajaran")) fileName += "_" + selectedMapel;
            fileName += ".pdf";
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File(fileName));
            int result = fileChooser.showSaveDialog(this);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".pdf")) filePath += ".pdf";
                
                com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4.rotate());
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();
                
                com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 22, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font subTitleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 16, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10, com.itextpdf.text.Font.NORMAL);
                com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font boldFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11, com.itextpdf.text.Font.BOLD);
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
                
                String filterText = "";
                if (!selectedKelas.equals("Semua Kelas")) filterText += "Kelas: " + selectedKelas;
                if (!selectedMapel.equals("Semua Mata Pelajaran")) {
                    if (!filterText.isEmpty()) filterText += " | ";
                    filterText += "Mata Pelajaran: " + selectedMapel;
                }
                if (filterText.isEmpty()) filterText = "LAPORAN NILAI SISWA";
                
                com.itextpdf.text.Paragraph judulLaporan = new com.itextpdf.text.Paragraph(filterText, subTitleFont);
                judulLaporan.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                judulCell.addElement(judulLaporan);
                
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
                
                com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(11);
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(new float[]{0.3f, 0.8f, 1.3f, 0.8f, 1.2f, 1.2f, 1.2f, 0.6f, 0.6f, 0.6f, 0.8f});
                
                String[] headers = {"No", "NIS", "Nama", "Kelas", "Wali Kelas", "Mata Pelajaran", "Guru", "Tugas", "UTS", "UAS", "Nilai Akhir"};
                for (String header : headers) {
                    com.itextpdf.text.pdf.PdfPCell headerCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(header, headerFont));
                    headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(41, 128, 185));
                    headerCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    headerCell.setPadding(5);
                    pdfTable.addCell(headerCell);
                }
                
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < 11; j++) {
                        String value = tableModel.getValueAt(i, j) != null ? tableModel.getValueAt(i, j).toString() : "";
                        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(value, normalFont));
                        cell.setPadding(4);
                        pdfTable.addCell(cell);
                    }
                }
                
                document.add(pdfTable);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.pdf.PdfPTable ttdTable = new com.itextpdf.text.pdf.PdfPTable(2);
                ttdTable.setWidthPercentage(100);
                ttdTable.setWidths(new float[]{0.5f, 0.5f});
                
                com.itextpdf.text.pdf.PdfPCell kiriCell = new com.itextpdf.text.pdf.PdfPCell();
                kiriCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                kiriCell.setFixedHeight(100);
                ttdTable.addCell(kiriCell);
                
                com.itextpdf.text.pdf.PdfPCell kananCell = new com.itextpdf.text.pdf.PdfPCell();
                kananCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                kananCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                
                com.itextpdf.text.Paragraph tglPar = new com.itextpdf.text.Paragraph(kotaTanggal, normalFont);
                tglPar.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                kananCell.addElement(tglPar);
                kananCell.addElement(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph kepalaSekolahPar = new com.itextpdf.text.Paragraph("Kepala Sekolah,", normalFont);
                kepalaSekolahPar.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                kananCell.addElement(kepalaSekolahPar);
                kananCell.addElement(new com.itextpdf.text.Paragraph(" "));
                kananCell.addElement(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph namaKepsek = new com.itextpdf.text.Paragraph("ADE SYAMSUDIN, M.Pd", boldFont);
                namaKepsek.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                kananCell.addElement(namaKepsek);
                
                ttdTable.addCell(kananCell);
                document.add(ttdTable);
                
                document.close();
                JOptionPane.showMessageDialog(this, "PDF berhasil disimpan!\nLokasi: " + filePath);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}