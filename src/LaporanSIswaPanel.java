import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LaporanSIswaPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton refreshBtn, cetakBtn, cetakPerKelasBtn, backBtn;
    private JComboBox<String> kelasCombo;
    private JTextField searchField;
    
    public LaporanSIswaPanel(CardLayout cardLayout, JPanel mainPanel) {
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
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("LAPORAN DATA SISWA (Kata kunci: NIS)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(Color.WHITE);
        
        filterPanel.add(new JLabel("Filter Kelas:"));
        kelasCombo = new JComboBox<>();
        kelasCombo.setPreferredSize(new Dimension(180, 30));
        kelasCombo.addItem("-- Semua Kelas --");
        kelasCombo.addActionListener(e -> filterByKelas());
        filterPanel.add(kelasCombo);
        
        filterPanel.add(Box.createHorizontalStrut(20));
        
        filterPanel.add(new JLabel("Cari (NIS/Nama):"));
        searchField = new JTextField(20);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterByKelas();
            }
        });
        filterPanel.add(searchField);
        
        JButton cariBtn = new JButton("CARI");
        cariBtn.setBackground(new Color(52, 152, 219));
        cariBtn.setForeground(Color.WHITE);
        cariBtn.addActionListener(e -> filterByKelas());
        filterPanel.add(cariBtn);
        
        JButton resetBtn = new JButton("RESET");
        resetBtn.setBackground(new Color(108, 117, 125));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            kelasCombo.setSelectedIndex(0);
            loadData();
        });
        filterPanel.add(resetBtn);
        
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        refreshBtn = createStyledButton("REFRESH DATA", new Color(52, 152, 219));
        cetakBtn = createStyledButton("CETAK PDF (SEMUA)", new Color(46, 204, 113));
        cetakPerKelasBtn = createStyledButton("CETAK PDF (PER KELAS)", new Color(243, 156, 18));
        backBtn = createStyledButton("KEMBALI KE DASHBOARD", new Color(231, 76, 60));
        
        btnPanel.add(refreshBtn);
        btnPanel.add(cetakBtn);
        btnPanel.add(cetakPerKelasBtn);
        btnPanel.add(backBtn);
        
        topPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // KOLOM: TANPA EMAIL, NO HP, AYAH, IBU
        String[] columns = {
            "No", "NIS", "Nama", "JK", "Tempat Lahir", "Tanggal Lahir",
            "Agama", "Alamat", "Kelas", "Tahun Masuk"
        };
        
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
        
        int[] widths = {40, 100, 150, 60, 100, 95, 70, 200, 80, 80};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 30));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
        
        refreshBtn.addActionListener(e -> {
            kelasCombo.setSelectedIndex(0);
            searchField.setText("");
            loadData();
        });
        cetakBtn.addActionListener(e -> cetakPDF("semua"));
        cetakPerKelasBtn.addActionListener(e -> {
            if (kelasCombo.getSelectedItem() == null || kelasCombo.getSelectedItem().toString().equals("-- Semua Kelas --")) {
                JOptionPane.showMessageDialog(this, "Pilih kelas terlebih dahulu!");
                return;
            }
            cetakPDF("perkelas");
        });
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
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void loadKelas() {
        kelasCombo.removeAllItems();
        kelasCombo.addItem("-- Semua Kelas --");
        List<String[]> kelasList = Database.getAllKelas();
        for (String[] k : kelasList) {
            if (k.length > 1 && k[1] != null && !k[1].isEmpty()) {
                kelasCombo.addItem(k[1]);
            }
        }
    }
    
    private void filterByKelas() {
        String selectedKelas = (String) kelasCombo.getSelectedItem();
        String keyword = searchField.getText().trim().toLowerCase();
        
        tableModel.setRowCount(0);
        int no = 1;
        
        for (String[] siswa : Database.getAllSiswa()) {
            String nis = siswa[0] != null ? siswa[0] : "";
            String nama = siswa[1] != null ? siswa[1] : "";
            String jk = siswa[2] != null ? siswa[2] : "";
            String tempatLahir = siswa[3] != null ? siswa[3] : "";
            String tglLahir = siswa[4] != null ? siswa[4] : "";
            String agama = siswa[5] != null ? siswa[5] : "";
            String alamat = siswa[6] != null ? siswa[6] : "";
            String kelas = siswa[7] != null ? siswa[7] : "";
            String thnMasuk = siswa[8] != null ? siswa[8] : "";
            
            boolean matchKelas = selectedKelas == null || selectedKelas.equals("-- Semua Kelas --") || kelas.equals(selectedKelas);
            boolean matchCari = keyword.isEmpty() || nis.toLowerCase().contains(keyword) || nama.toLowerCase().contains(keyword);
            
            if (matchKelas && matchCari) {
                tableModel.addRow(new Object[]{
                    no++, nis, nama, jk, tempatLahir, tglLahir,
                    agama, alamat, kelas, thnMasuk
                });
            }
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        int no = 1;
        for (String[] siswa : Database.getAllSiswa()) {
            String nis = siswa[0] != null ? siswa[0] : "";
            String nama = siswa[1] != null ? siswa[1] : "";
            String jk = siswa[2] != null ? siswa[2] : "";
            String tempatLahir = siswa[3] != null ? siswa[3] : "";
            String tglLahir = siswa[4] != null ? siswa[4] : "";
            String agama = siswa[5] != null ? siswa[5] : "";
            String alamat = siswa[6] != null ? siswa[6] : "";
            String kelas = siswa[7] != null ? siswa[7] : "";
            String thnMasuk = siswa[8] != null ? siswa[8] : "";
            
            tableModel.addRow(new Object[]{
                no++, nis, nama, jk, tempatLahir, tglLahir,
                agama, alamat, kelas, thnMasuk
            });
        }
    }
    
    private void cetakPDF(String tipe) {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk dicetak!");
            return;
        }
        
        try {
            String selectedKelas = (String) kelasCombo.getSelectedItem();
            String fileName;
            
            if (tipe.equals("perkelas") && selectedKelas != null && !selectedKelas.equals("-- Semua Kelas --")) {
                fileName = "Laporan_Siswa_Kelas_" + selectedKelas.replace("/", "_") + ".pdf";
            } else {
                fileName = "Laporan_Siswa_SMA_PGRI_4.pdf";
            }
            
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
                
                com.itextpdf.text.Paragraph judulLaporan = new com.itextpdf.text.Paragraph("LAPORAN DATA SISWA", subTitleFont);
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
                
                if (tipe.equals("perkelas") && selectedKelas != null && !selectedKelas.equals("-- Semua Kelas --")) {
                    com.itextpdf.text.Paragraph infoKelas = new com.itextpdf.text.Paragraph("Kelas: " + selectedKelas, headerFont);
                    infoKelas.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                    document.add(infoKelas);
                    document.add(new com.itextpdf.text.Paragraph(" "));
                }
                
                // PDF 10 KOLOM
                com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(10);
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(new float[]{
                    0.3f, 0.9f, 1.4f, 0.5f, 0.9f, 0.8f, 0.6f, 
                    1.7f, 0.7f, 0.7f
                });
                
                String[] headers = {
                    "No", "NIS", "Nama", "JK", "Tempat Lahir", "Tanggal Lahir", 
                    "Agama", "Alamat", "Kelas", "Tahun Masuk"
                };
                
                for (String header : headers) {
                    com.itextpdf.text.pdf.PdfPCell headerCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(header, headerFont));
                    headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(41, 128, 185));
                    headerCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    headerCell.setPadding(5);
                    pdfTable.addCell(headerCell);
                }
                
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < 10; j++) {
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
                kananCell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
                
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
            e.printStackTrace();
        }
    }
}