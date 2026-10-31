import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

public class LaporanAbsensiPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> kelasCombo;
    private JComboBox<String> bulanCombo;
    private JComboBox<String> tahunCombo;
    private JTextField tanggalField;
    private JButton filterBtn, refreshBtn, cetakHariBtn, cetakBulanBtn, backBtn;
    private JLabel judulLaporan;
    private JLabel waliKelasLabel;
    
    public LaporanAbsensiPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initComponents();
        loadData();
        loadKelas();
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
        
        JLabel filterTitle = new JLabel("LAPORAN ABSENSI SISWA (Kata kunci: NIS)");
        filterTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        filterTitle.setForeground(new Color(41, 128, 185));
        filterPanel.add(filterTitle, BorderLayout.NORTH);
        
        JPanel filterInputPanel = new JPanel(new GridBagLayout());
        filterInputPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        filterInputPanel.add(new JLabel("Kelas:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.2;
        kelasCombo = new JComboBox<>();
        kelasCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        kelasCombo.addActionListener(e -> {
            if (kelasCombo.getSelectedIndex() != -1 && !kelasCombo.getSelectedItem().toString().equals("-- Pilih Kelas --")) {
                updateWaliKelas();
            }
        });
        filterInputPanel.add(kelasCombo, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.1;
        filterInputPanel.add(new JLabel("Wali Kelas:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.2;
        waliKelasLabel = new JLabel("-");
        waliKelasLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        waliKelasLabel.setForeground(new Color(41, 128, 185));
        filterInputPanel.add(waliKelasLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        filterInputPanel.add(new JLabel("Laporan Harian - Tanggal:"), gbc);
        gbc.gridx = 1;
        tanggalField = new JTextField(15);
        tanggalField.setText(getCurrentDate());
        tanggalField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterInputPanel.add(tanggalField, gbc);
        
        gbc.gridx = 2;
        filterBtn = new JButton("TAMPILKAN HARIAN");
        filterBtn.setBackground(new Color(52, 152, 219));
        filterBtn.setForeground(Color.WHITE);
        filterBtn.setFocusPainted(false);
        filterBtn.addActionListener(e -> tampilkanAbsensiHarian());
        filterInputPanel.add(filterBtn, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        filterInputPanel.add(new JLabel("Laporan Bulanan - Bulan:"), gbc);
        gbc.gridx = 1;
        bulanCombo = new JComboBox<>(new String[]{"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"});
        bulanCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterInputPanel.add(bulanCombo, gbc);
        
        gbc.gridx = 2;
        filterInputPanel.add(new JLabel("Tahun:"), gbc);
        gbc.gridx = 3;
        tahunCombo = new JComboBox<>();
        tahunCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        int tahunSekarang = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int i = tahunSekarang - 3; i <= tahunSekarang + 1; i++) {
            tahunCombo.addItem(String.valueOf(i));
        }
        filterInputPanel.add(tahunCombo, gbc);
        
        filterPanel.add(filterInputPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        cetakHariBtn = new JButton("CETAK PDF HARIAN");
        cetakHariBtn.setBackground(new Color(52, 152, 219));
        cetakHariBtn.setForeground(Color.WHITE);
        cetakHariBtn.setFocusPainted(false);
        cetakHariBtn.addActionListener(e -> cetakPDFHarian());
        
        cetakBulanBtn = new JButton("CETAK PDF REKAP BULANAN");
        cetakBulanBtn.setBackground(new Color(46, 204, 113));
        cetakBulanBtn.setForeground(Color.WHITE);
        cetakBulanBtn.setFocusPainted(false);
        cetakBulanBtn.addActionListener(e -> cetakPDFRekapBulanan());
        
        refreshBtn = new JButton("REFRESH");
        refreshBtn.setBackground(new Color(108, 117, 125));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> {
            loadKelas();
            loadData();
        });
        
        backBtn = new JButton("KEMBALI");
        backBtn.setBackground(new Color(231, 76, 60));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "menuUtama");
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof MenuUtamaPanel) {
                    ((MenuUtamaPanel) comp).showDashboard();
                    break;
                }
            }
        });
        
        btnPanel.add(cetakHariBtn);
        btnPanel.add(cetakBulanBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(backBtn);
        
        filterPanel.add(btnPanel, BorderLayout.SOUTH);
        add(filterPanel, BorderLayout.NORTH);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        judulLaporan = new JLabel("LAPORAN ABSENSI", SwingConstants.CENTER);
        judulLaporan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        judulLaporan.setForeground(new Color(41, 128, 185));
        tableContainer.add(judulLaporan, BorderLayout.NORTH);
        
        String[] columns = {"No", "NIS", "Nama Siswa", "Kelas", "Wali Kelas", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);
    }
    
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
    
    private void loadKelas() {
        kelasCombo.removeAllItems();
        kelasCombo.addItem("-- Pilih Kelas --");
        for (String[] kelas : Database.getAllKelas()) {
            kelasCombo.addItem(kelas[1]);
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        judulLaporan.setText("LAPORAN ABSENSI");
    }
    
    private String getWaliKelasByKelas(String namaKelas) {
        List<String[]> kelasList = Database.getAllKelas();
        for (String[] k : kelasList) {
            if (k.length > 2 && k[1].equalsIgnoreCase(namaKelas)) {
                return k[2];
            }
        }
        return "-";
    }
    
    private void updateWaliKelas() {
        if (kelasCombo.getSelectedIndex() > 0) {
            String selectedKelas = (String) kelasCombo.getSelectedItem();
            String wali = getWaliKelasByKelas(selectedKelas);
            waliKelasLabel.setText(wali);
        } else {
            waliKelasLabel.setText("-");
        }
    }
    
    private void tampilkanAbsensiHarian() {
        if (kelasCombo.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Pilih kelas terlebih dahulu!");
            return;
        }
        
        String selectedKelas = (String) kelasCombo.getSelectedItem();
        String tanggal = tanggalField.getText().trim();
        String waliKelas = getWaliKelasByKelas(selectedKelas);
        
        if (tanggal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan tanggal!");
            return;
        }
        
        tableModel.setRowCount(0);
        judulLaporan.setText("LAPORAN ABSENSI HARI " + tanggal + " - KELAS " + selectedKelas);
        
        int no = 1;
        List<Object[]> absensiList = Database.getAllAbsensi();
        
        for (String[] siswa : Database.getSiswaByKelas(selectedKelas)) {
            // PERBAIKAN: Index kelas sekarang 7
            String nis = siswa[0];
            String nama = siswa[1];
            String kelas = siswa[7];
            String status = "-";
            
            for (Object[] absen : absensiList) {
                String tglAbsen = absen[0].toString();
                String nisAbsen = absen[2].toString();
                if (tglAbsen.equals(tanggal) && nisAbsen.equals(nis)) {
                    status = absen[4].toString();
                    break;
                }
            }
            
            tableModel.addRow(new Object[]{no++, nis, nama, kelas, waliKelas, status});
        }
    }
    
    private void cetakPDFHarian() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk dicetak!\nKlik TAMPILKAN HARIAN terlebih dahulu.");
            return;
        }
        
        try {
            String selectedKelas = (String) kelasCombo.getSelectedItem();
            String tanggal = tanggalField.getText().trim();
            String waliKelas = getWaliKelasByKelas(selectedKelas);
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("Laporan_Absensi_Harian_" + selectedKelas + "_" + tanggal + ".pdf"));
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
                
                com.itextpdf.text.pdf.PdfPTable kopTable = new com.itextpdf.text.pdf.PdfPTable(3);
                kopTable.setWidthPercentage(100);
                kopTable.setWidths(new float[]{0.20f, 0.60f, 0.20f});
                
                com.itextpdf.text.pdf.PdfPCell logoCell = new com.itextpdf.text.pdf.PdfPCell();
                logoCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                logoCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
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
                
                com.itextpdf.text.Paragraph namaSekolah = new com.itextpdf.text.Paragraph("SMA PGRI 4 JAKARTA", titleFont);
                namaSekolah.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                judulCell.addElement(namaSekolah);
                
                com.itextpdf.text.Paragraph alamatSekolah = new com.itextpdf.text.Paragraph("Jl. Cipayung Raya, Jakarta Timur 13840", smallFont);
                alamatSekolah.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                judulCell.addElement(alamatSekolah);
                
                com.itextpdf.text.Paragraph judulLaporanPar = new com.itextpdf.text.Paragraph("LAPORAN ABSENSI HARIAN", subTitleFont);
                judulLaporanPar.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                judulCell.addElement(judulLaporanPar);
                
                kopTable.addCell(judulCell);
                
                com.itextpdf.text.pdf.PdfPCell logoKananCell = new com.itextpdf.text.pdf.PdfPCell();
                logoKananCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                logoKananCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
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
                
                com.itextpdf.text.Paragraph info = new com.itextpdf.text.Paragraph("Kelas: " + selectedKelas + " | Wali Kelas: " + waliKelas + " | Tanggal: " + tanggal, normalFont);
                info.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                document.add(info);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(6);
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(new float[]{0.3f, 0.8f, 1.5f, 0.8f, 1.2f, 0.8f});
                
                String[] headers = {"No", "NIS", "Nama Siswa", "Kelas", "Wali Kelas", "Status"};
                for (String header : headers) {
                    com.itextpdf.text.pdf.PdfPCell headerCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(header, headerFont));
                    headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(41, 128, 185));
                    headerCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    headerCell.setPadding(6);
                    pdfTable.addCell(headerCell);
                }
                
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < 6; j++) {
                        String value = tableModel.getValueAt(i, j) != null ? tableModel.getValueAt(i, j).toString() : "";
                        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(value, normalFont));
                        cell.setPadding(5);
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
                
                com.itextpdf.text.Paragraph waliKelasPar = new com.itextpdf.text.Paragraph("Wali Kelas,", normalFont);
                waliKelasPar.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                kananCell.addElement(waliKelasPar);
                kananCell.addElement(new com.itextpdf.text.Paragraph(" "));
                kananCell.addElement(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph namaWaliKelas = new com.itextpdf.text.Paragraph("( " + waliKelas + " )", boldFont);
                namaWaliKelas.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                kananCell.addElement(namaWaliKelas);
                
                ttdTable.addCell(kananCell);
                document.add(ttdTable);
                
                document.close();
                JOptionPane.showMessageDialog(this, "PDF berhasil disimpan!\nLokasi: " + filePath);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void cetakPDFRekapBulanan() {
        if (kelasCombo.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Pilih kelas terlebih dahulu!");
            return;
        }
        
        String selectedKelas = (String) kelasCombo.getSelectedItem();
        String selectedBulan = (String) bulanCombo.getSelectedItem();
        String selectedTahun = (String) tahunCombo.getSelectedItem();
        String waliKelas = getWaliKelasByKelas(selectedKelas);
        int bulanNumber = getBulanNumber(selectedBulan);
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("Rekap_Absensi_" + selectedKelas + "_" + selectedBulan + "_" + selectedTahun + ".pdf"));
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
                
                com.itextpdf.text.pdf.PdfPTable kopTable = new com.itextpdf.text.pdf.PdfPTable(3);
                kopTable.setWidthPercentage(100);
                kopTable.setWidths(new float[]{0.20f, 0.60f, 0.20f});
                
                com.itextpdf.text.pdf.PdfPCell logoCell = new com.itextpdf.text.pdf.PdfPCell();
                logoCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                logoCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
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
                
                com.itextpdf.text.Paragraph namaSekolah = new com.itextpdf.text.Paragraph("SMA PGRI 4 JAKARTA", titleFont);
                namaSekolah.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                judulCell.addElement(namaSekolah);
                
                com.itextpdf.text.Paragraph alamatSekolah = new com.itextpdf.text.Paragraph("Jl. Cipayung Raya, Jakarta Timur 13840", smallFont);
                alamatSekolah.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                judulCell.addElement(alamatSekolah);
                
                com.itextpdf.text.Paragraph judulLaporanPar = new com.itextpdf.text.Paragraph("REKAP ABSENSI BULANAN", subTitleFont);
                judulLaporanPar.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                judulCell.addElement(judulLaporanPar);
                
                kopTable.addCell(judulCell);
                
                com.itextpdf.text.pdf.PdfPCell logoKananCell = new com.itextpdf.text.pdf.PdfPCell();
                logoKananCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                logoKananCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
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
                
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Integer.parseInt(selectedTahun));
                cal.set(Calendar.MONTH, bulanNumber - 1);
                int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                
                String tglAwal = selectedTahun + "-" + String.format("%02d", bulanNumber) + "-01";
                String tglAkhir = selectedTahun + "-" + String.format("%02d", bulanNumber) + "-" + maxDay;
                
                com.itextpdf.text.Paragraph info = new com.itextpdf.text.Paragraph(
                    "Kelas: " + selectedKelas + " | Wali Kelas: " + waliKelas + 
                    " | Periode: " + selectedBulan + " " + selectedTahun + 
                    " (" + tglAwal + " s/d " + tglAkhir + ")", normalFont);
                info.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                document.add(info);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(9);
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(new float[]{0.3f, 0.8f, 1.5f, 0.8f, 1.2f, 0.7f, 0.7f, 0.7f, 0.7f});
                
                String[] headers = {"No", "NIS", "Nama Siswa", "Kelas", "Wali Kelas", "Hadir", "Sakit", "Izin", "Alpa"};
                for (String header : headers) {
                    com.itextpdf.text.pdf.PdfPCell headerCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(header, headerFont));
                    headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(41, 128, 185));
                    headerCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    headerCell.setPadding(6);
                    pdfTable.addCell(headerCell);
                }
                
                List<Object[]> absensiList = Database.getAllAbsensi();
                int no = 1;
                
                for (String[] siswa : Database.getSiswaByKelas(selectedKelas)) {
                    // PERBAIKAN: Index kelas sekarang 7
                    String nis = siswa[0];
                    String nama = siswa[1];
                    String kelas = siswa[7];
                    
                    int hadir = 0, sakit = 0, izin = 0, alpa = 0;
                    
                    for (Object[] absen : absensiList) {
                        String tgl = absen[0].toString();
                        String nisAbsen = absen[2].toString();
                        String status = absen[4].toString();
                        
                        if (nisAbsen.equals(nis) && tgl.compareTo(tglAwal) >= 0 && tgl.compareTo(tglAkhir) <= 0) {
                            switch (status) {
                                case "Hadir": hadir++; break;
                                case "Sakit": sakit++; break;
                                case "Izin": izin++; break;
                                case "Alpa": alpa++; break;
                            }
                        }
                    }
                    
                    pdfTable.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.valueOf(no++), normalFont)));
                    pdfTable.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(nis, normalFont)));
                    pdfTable.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(nama, normalFont)));
                    pdfTable.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(kelas, normalFont)));
                    pdfTable.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(waliKelas, normalFont)));
                    pdfTable.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.valueOf(hadir), normalFont)));
                    pdfTable.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.valueOf(sakit), normalFont)));
                    pdfTable.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.valueOf(izin), normalFont)));
                    pdfTable.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.valueOf(alpa), normalFont)));
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
                
                com.itextpdf.text.Paragraph waliKelasPar = new com.itextpdf.text.Paragraph("Wali Kelas,", normalFont);
                waliKelasPar.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                kananCell.addElement(waliKelasPar);
                kananCell.addElement(new com.itextpdf.text.Paragraph(" "));
                kananCell.addElement(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph namaWaliKelas = new com.itextpdf.text.Paragraph("( " + waliKelas + " )", boldFont);
                namaWaliKelas.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                kananCell.addElement(namaWaliKelas);
                
                ttdTable.addCell(kananCell);
                document.add(ttdTable);
                
                document.close();
                JOptionPane.showMessageDialog(this, "PDF Rekap Bulanan berhasil disimpan!\nLokasi: " + filePath);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private int getBulanNumber(String bulan) {
        switch (bulan) {
            case "Januari": return 1;
            case "Februari": return 2;
            case "Maret": return 3;
            case "April": return 4;
            case "Mei": return 5;
            case "Juni": return 6;
            case "Juli": return 7;
            case "Agustus": return 8;
            case "September": return 9;
            case "Oktober": return 10;
            case "November": return 11;
            case "Desember": return 12;
            default: return 0;
        }
    }
}