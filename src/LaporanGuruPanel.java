import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LaporanGuruPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton refreshBtn, cetakBtn, backBtn;
    
    public LaporanGuruPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        
        if (!Session.isAdmin() && !Session.isKepsek()) {
            JOptionPane.showMessageDialog(null, "⛔ Fitur LAPORAN GURU hanya dapat diakses oleh Administrator dan Kepala Sekolah!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            cardLayout.show(mainPanel, "menuUtama");
            return;
        }
        
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 242, 245));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("LAPORAN DATA GURU");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // HEADER TABEL: KODE GURU
        String[] columns = {"No", "Kode Guru", "Nama Guru", "Mata Pelajaran"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 35));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        refreshBtn = createStyledButton("REFRESH DATA", new Color(52, 152, 219));
        cetakBtn = createStyledButton("CETAK PDF", new Color(46, 204, 113));
        backBtn = createStyledButton("KEMBALI", new Color(231, 76, 60));
        
        btnPanel.add(refreshBtn);
        btnPanel.add(cetakBtn);
        btnPanel.add(backBtn);
        
        add(btnPanel, BorderLayout.SOUTH);
        
        refreshBtn.addActionListener(e -> loadData());
        cetakBtn.addActionListener(e -> cetakPDF());
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
        btn.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        int no = 1;
        for (String[] guru : Database.getAllGuru()) {
            tableModel.addRow(new Object[]{
                no++, guru[0], guru[2], guru[3]
            });
        }
    }
    
    private void cetakPDF() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk dicetak!");
            return;
        }
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("Laporan_Guru_SMA_PGRI_4.pdf"));
            int result = fileChooser.showSaveDialog(this);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".pdf")) filePath += ".pdf";
                
                com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4);
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();
                
                // FONT (DIPERBESAR)
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
                
                // KOP SURAT (REVISI)
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
                
                com.itextpdf.text.Paragraph judulLaporan = new com.itextpdf.text.Paragraph("LAPORAN DATA GURU", subTitleFont);
                judulLaporan.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                judulCell.addElement(judulLaporan);
                
                kopTable.addCell(judulCell);
                
                // LOGO KANAN PGRI
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
                
                com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(4);
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(new float[]{0.5f, 1.8f, 2.5f, 2.0f});
                
                // HEADER PDF: KODE GURU
                String[] headers = {"No", "Kode Guru", "Nama Guru", "Mata Pelajaran"};
                for (String header : headers) {
                    com.itextpdf.text.pdf.PdfPCell headerCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(header, headerFont));
                    headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(41, 128, 185));
                    headerCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    headerCell.setPadding(6);
                    pdfTable.addCell(headerCell);
                }
                
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < 4; j++) {
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
        }
    }
}