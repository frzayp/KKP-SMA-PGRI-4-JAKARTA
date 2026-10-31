import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PembayaranSPPPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField nisField, namaField, totalTagihanField, dibayarField, sisaTagihanField, tunggakanField;
    private JTextField searchField;
    private JComboBox<String> jenisTagihanCombo, bulanCombo, statusCombo, filterJenisCombo;
    private JComboBox<String> metodeCombo;
    private JButton cariBtn, simpanBtn, cetakNotaBtn, refreshBtn, backBtn, transaksiBaruBtn;
    private JLabel kelasLabel;
    private String currentNIS = "";
    private String currentNama = "";
    private String currentKelas = "";
    private List<Object[]> allPembayaranCache;
    private NumberFormat rupiahFormat;
    
    public PembayaranSPPPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        
        if (!Session.isAdmin()) {
            JOptionPane.showMessageDialog(null, "⛔ Fitur PEMBAYARAN SPP hanya dapat diakses oleh Administrator!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            cardLayout.show(mainPanel, "menuUtama");
            return;
        }
        
        rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        initComponents();
        loadDataPembayaran();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 242, 245));
        
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // ============ HEADER PANEL ============
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel formTitle = new JLabel("FORM PEMBAYARAN SPP (Kata kunci: NIS)");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(new Color(41, 128, 185));
        headerPanel.add(formTitle, BorderLayout.WEST);
        
        transaksiBaruBtn = new JButton("➕ TRANSAKSI BARU");
        transaksiBaruBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        transaksiBaruBtn.setBackground(new Color(52, 152, 219));
        transaksiBaruBtn.setForeground(Color.WHITE);
        transaksiBaruBtn.setFocusPainted(false);
        transaksiBaruBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        transaksiBaruBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        transaksiBaruBtn.addActionListener(e -> resetForm());
        headerPanel.add(transaksiBaruBtn, BorderLayout.EAST);
        
        formPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Baris 1 - NIS & CARI
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        gridPanel.add(new JLabel("NIS:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        nisField = new JTextField();
        nisField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(nisField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.15;
        cariBtn = new JButton("CARI SISWA");
        cariBtn.setBackground(new Color(52, 152, 219));
        cariBtn.setForeground(Color.WHITE);
        cariBtn.setFocusPainted(false);
        gridPanel.add(cariBtn, gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.15;
        gridPanel.add(new JLabel("Total Tunggakan:"), gbc);
        gbc.gridx = 4; gbc.weightx = 0.25;
        tunggakanField = new JTextField();
        tunggakanField.setEditable(false);
        tunggakanField.setBackground(new Color(255, 255, 200));
        tunggakanField.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gridPanel.add(tunggakanField, gbc);
        
        // Baris 2 - Nama Siswa & Kelas
        gbc.gridx = 0; gbc.gridy = 1;
        gridPanel.add(new JLabel("Nama Siswa:"), gbc);
        gbc.gridx = 1;
        namaField = new JTextField();
        namaField.setEditable(false);
        namaField.setBackground(new Color(240, 240, 240));
        namaField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(namaField, gbc);
        
        gbc.gridx = 2;
        gridPanel.add(new JLabel("Kelas:"), gbc);
        gbc.gridx = 3;
        kelasLabel = new JLabel("-");
        kelasLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        kelasLabel.setForeground(new Color(41, 128, 185));
        gridPanel.add(kelasLabel, gbc);
        
        // Baris 3 - Jenis Tagihan & Bulan
        gbc.gridx = 0; gbc.gridy = 2;
        gridPanel.add(new JLabel("Jenis Tagihan:"), gbc);
        gbc.gridx = 1;
        jenisTagihanCombo = new JComboBox<>(new String[]{"SPP", "DAFTAR_ULANG", "UANG_GEDUNG"});
        jenisTagihanCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jenisTagihanCombo.addActionListener(e -> setDefaultTagihan());
        gridPanel.add(jenisTagihanCombo, gbc);
        
        gbc.gridx = 2;
        gridPanel.add(new JLabel("Bulan (untuk SPP):"), gbc);
        gbc.gridx = 3;
        bulanCombo = new JComboBox<>(new String[]{
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        });
        bulanCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(bulanCombo, gbc);
        
        // Baris 4 - Total Tagihan & Jumlah Dibayar
        gbc.gridx = 0; gbc.gridy = 3;
        gridPanel.add(new JLabel("Total Tagihan:"), gbc);
        gbc.gridx = 1;
        totalTagihanField = new JTextField();
        totalTagihanField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        totalTagihanField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { hitungSisa(); }
        });
        gridPanel.add(totalTagihanField, gbc);
        
        gbc.gridx = 2;
        gridPanel.add(new JLabel("Jumlah Dibayar:"), gbc);
        gbc.gridx = 3;
        dibayarField = new JTextField();
        dibayarField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dibayarField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { hitungSisa(); }
        });
        gridPanel.add(dibayarField, gbc);
        
        // Baris 5 - Sisa Tagihan & Metode Pembayaran
        gbc.gridx = 0; gbc.gridy = 4;
        gridPanel.add(new JLabel("Sisa Tagihan:"), gbc);
        gbc.gridx = 1;
        sisaTagihanField = new JTextField();
        sisaTagihanField.setEditable(false);
        sisaTagihanField.setBackground(new Color(240, 240, 240));
        sisaTagihanField.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gridPanel.add(sisaTagihanField, gbc);
        
        gbc.gridx = 2;
        gridPanel.add(new JLabel("Metode Pembayaran:"), gbc);
        gbc.gridx = 3;
        metodeCombo = new JComboBox<>(new String[]{"Tunai", "Transfer"});
        metodeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(metodeCombo, gbc);
        
        // Baris 6 - Status
        gbc.gridx = 0; gbc.gridy = 5;
        gridPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Belum Bayar", "Cicilan", "Lunas"});
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(statusCombo, gbc);
        
        formPanel.add(gridPanel, BorderLayout.CENTER);
        
        // BUTTON PANEL
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        
        simpanBtn = new JButton("💾 SIMPAN PEMBAYARAN");
        simpanBtn.setBackground(new Color(46, 204, 113));
        simpanBtn.setForeground(Color.WHITE);
        simpanBtn.setFocusPainted(false);
        simpanBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        
        cetakNotaBtn = new JButton("🧾 CETAK NOTA");
        cetakNotaBtn.setBackground(new Color(52, 152, 219));
        cetakNotaBtn.setForeground(Color.WHITE);
        cetakNotaBtn.setFocusPainted(false);
        cetakNotaBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        
        refreshBtn = new JButton("🔄 REFRESH");
        refreshBtn.setBackground(new Color(108, 117, 125));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        
        backBtn = new JButton("◀ KEMBALI");
        backBtn.setBackground(new Color(231, 76, 60));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        
        btnPanel.add(simpanBtn);
        btnPanel.add(cetakNotaBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(backBtn);
        
        formPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(formPanel, BorderLayout.NORTH);
        
        // TABEL RIWAYAT PEMBAYARAN
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel tableTitle = new JLabel("RIWAYAT PEMBAYARAN");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setForeground(new Color(41, 128, 185));
        tableContainer.add(tableTitle, BorderLayout.NORTH);
        
        // Filter Panel
        JPanel filterTablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterTablePanel.setBackground(Color.WHITE);
        filterTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        filterTablePanel.add(new JLabel("🔍 Cari (NIS/Nama):"));
        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterTableData();
            }
        });
        filterTablePanel.add(searchField);
        
        filterTablePanel.add(new JLabel("Jenis:"));
        filterJenisCombo = new JComboBox<>(new String[]{"Semua", "SPP", "DAFTAR_ULANG", "UANG_GEDUNG"});
        filterJenisCombo.addActionListener(e -> filterTableData());
        filterTablePanel.add(filterJenisCombo);
        
        JButton resetFilterBtn = new JButton("RESET FILTER");
        resetFilterBtn.setBackground(new Color(108, 117, 125));
        resetFilterBtn.setForeground(Color.WHITE);
        resetFilterBtn.addActionListener(e -> {
            searchField.setText("");
            filterJenisCombo.setSelectedIndex(0);
            loadDataPembayaran();
        });
        filterTablePanel.add(resetFilterBtn);
        
        tableContainer.add(filterTablePanel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "NIS", "Nama", "Jenis", "Bulan", "Total", "Dibayar", "Sisa", "Status", "Metode", "Tanggal"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(28);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(100);
        table.getColumnModel().getColumn(8).setPreferredWidth(80);
        table.getColumnModel().getColumn(9).setPreferredWidth(80);
        table.getColumnModel().getColumn(10).setPreferredWidth(100);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tableContainer);
        splitPane.setResizeWeight(0.45);
        splitPane.setDividerSize(8);
        
        add(splitPane, BorderLayout.CENTER);
        
        // EVENT HANDLERS
        cariBtn.addActionListener(e -> cariSiswa());
        nisField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) cariSiswa();
            }
        });
        
        simpanBtn.addActionListener(e -> simpanPembayaran());
        cetakNotaBtn.addActionListener(e -> cetakNota());
        refreshBtn.addActionListener(e -> {
            loadDataPembayaran();
            resetForm();
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
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String nis = tableModel.getValueAt(row, 1).toString();
                    String nama = tableModel.getValueAt(row, 2).toString();
                    String jenis = tableModel.getValueAt(row, 3).toString();
                    String bulan = tableModel.getValueAt(row, 4).toString();
                    String totalStr = tableModel.getValueAt(row, 5).toString();
                    String dibayarStr = tableModel.getValueAt(row, 6).toString();
                    String sisaStr = tableModel.getValueAt(row, 7).toString();
                    String metode = tableModel.getValueAt(row, 9).toString();
                    
                    nisField.setText(nis);
                    namaField.setText(nama);
                    jenisTagihanCombo.setSelectedItem(jenis);
                    if (!bulan.equals("-") && !bulan.isEmpty()) {
                        bulanCombo.setSelectedItem(bulan);
                    }
                    totalTagihanField.setText(parseRupiahToFormatted(parseRupiahToDouble(totalStr)));
                    dibayarField.setText(parseRupiahToFormatted(parseRupiahToDouble(dibayarStr)));
                    sisaTagihanField.setText(parseRupiahToFormatted(parseRupiahToDouble(sisaStr)));
                    statusCombo.setSelectedItem(tableModel.getValueAt(row, 8).toString());
                    metodeCombo.setSelectedItem(metode);
                    currentNIS = nis;
                    currentNama = nama;
                    
                    String[] siswa = Database.getSiswaByNIS(nis);
                    if (siswa != null && siswa.length > 9) {
                        currentKelas = siswa[9];
                        if (kelasLabel != null) kelasLabel.setText(currentKelas);
                    }
                }
            }
        });
    }
    
    private double parseRupiahToDouble(String rupiahStr) {
        try {
            if (rupiahStr == null || rupiahStr.isEmpty()) return 0;
            String clean = rupiahStr.replace("Rp", "").replace("Rp ", "").replace(".", "").replace(",", "").trim();
            clean = clean.replaceAll("[^0-9]", "");
            if (clean.isEmpty()) return 0;
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private String parseRupiahToFormatted(double amount) {
        return String.format("%,.0f", amount).replace(",", ".");
    }
    
    private String formatRupiah(double amount) {
        return "Rp " + String.format("%,.0f", amount).replace(",", ".");
    }
    
    private void filterTableData() {
        String keyword = searchField.getText().trim().toLowerCase();
        String selectedJenis = (String) filterJenisCombo.getSelectedItem();
        
        tableModel.setRowCount(0);
        
        for (Object[] p : allPembayaranCache) {
            String nis = p[1].toString().toLowerCase();
            String nama = p[2].toString().toLowerCase();
            String jenis = p[3].toString();
            
            boolean matchKeyword = keyword.isEmpty() || nis.contains(keyword) || nama.contains(keyword);
            boolean matchJenis = selectedJenis.equals("Semua") || jenis.equals(selectedJenis);
            
            if (matchKeyword && matchJenis) {
                double total = (double) p[5];
                double dibayar = (double) p[6];
                double sisa = (double) p[7];
                String metode = p.length > 10 ? p[10].toString() : "-";
                
                tableModel.addRow(new Object[]{
                    p[0], p[1], p[2], p[3], 
                    p[4] != null && !p[4].toString().isEmpty() ? p[4] : "-",
                    formatRupiah(total), formatRupiah(dibayar), formatRupiah(sisa),
                    p[8], metode, p[9]
                });
            }
        }
    }
    
    private void setDefaultTagihan() {
        String jenis = (String) jenisTagihanCombo.getSelectedItem();
        switch (jenis) {
            case "SPP":
                totalTagihanField.setText("250.000");
                bulanCombo.setEnabled(true);
                break;
            case "DAFTAR_ULANG":
                totalTagihanField.setText("300.000");
                bulanCombo.setEnabled(false);
                bulanCombo.setSelectedIndex(0);
                break;
            case "UANG_GEDUNG":
                totalTagihanField.setText("1.500.000");
                bulanCombo.setEnabled(false);
                bulanCombo.setSelectedIndex(0);
                break;
        }
        hitungSisa();
    }
    
    private void hitungSisa() {
        try {
            String totalText = totalTagihanField.getText().trim().replace(".", "");
            String dibayarText = dibayarField.getText().trim().replace(".", "");
            
            double total = totalText.isEmpty() ? 0 : Double.parseDouble(totalText);
            double dibayar = dibayarText.isEmpty() ? 0 : Double.parseDouble(dibayarText);
            double sisa = total - dibayar;
            
            if (sisa < 0) sisa = 0;
            sisaTagihanField.setText(String.format("%,.0f", sisa).replace(",", "."));
            
            if (sisa <= 0) {
                statusCombo.setSelectedItem("Lunas");
            } else if (dibayar > 0 && sisa > 0) {
                statusCombo.setSelectedItem("Cicilan");
            } else {
                statusCombo.setSelectedItem("Belum Bayar");
            }
        } catch (NumberFormatException e) {
            sisaTagihanField.setText("0");
        }
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
            
            namaField.setText(currentNama);
            if (kelasLabel != null) {
                kelasLabel.setText(currentKelas);
            }
            
            double totalTunggakan = 0;
            for (Object[] p : allPembayaranCache) {
                if (p[1].toString().equals(nis)) {
                    double sisaVal = (double) p[7];
                    String statusBayar = p[8].toString();
                    if (!statusBayar.equals("Lunas") && sisaVal > 0) {
                        totalTunggakan += sisaVal;
                    }
                }
            }
            tunggakanField.setText(formatRupiah(totalTunggakan));
            setDefaultTagihan();
            
            JOptionPane.showMessageDialog(this, "Siswa ditemukan:\nNama: " + currentNama + "\nKelas: " + currentKelas);
        } else {
            JOptionPane.showMessageDialog(this, "Siswa dengan NIS " + nis + " tidak ditemukan!");
            resetForm();
        }
    }
    
    private void simpanPembayaran() {
        if (currentNIS.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cari siswa terlebih dahulu!");
            return;
        }
        
        String jenis = (String) jenisTagihanCombo.getSelectedItem();
        String bulan = (String) bulanCombo.getSelectedItem();
        String metode = (String) metodeCombo.getSelectedItem();
        String totalText = totalTagihanField.getText().trim().replace(".", "");
        String dibayarText = dibayarField.getText().trim().replace(".", "");
        
        double totalTagihan;
        double dibayar;
        
        try {
            totalTagihan = Double.parseDouble(totalText);
            dibayar = Double.parseDouble(dibayarText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Masukkan nominal yang valid!");
            return;
        }
        
        if (totalTagihan <= 0) {
            JOptionPane.showMessageDialog(this, "Total tagihan harus lebih dari 0!");
            return;
        }
        
        if (dibayar <= 0) {
            JOptionPane.showMessageDialog(this, "Jumlah dibayar harus lebih dari 0!");
            return;
        }
        
        if (dibayar > totalTagihan) {
            JOptionPane.showMessageDialog(this, "Jumlah dibayar tidak boleh melebihi total tagihan!");
            return;
        }
        
        double sisa = totalTagihan - dibayar;
        String status = (sisa <= 0) ? "Lunas" : (dibayar > 0 ? "Cicilan" : "Belum Bayar");
        String tglBayar = getCurrentDate();
        String bulanText = jenis.equals("SPP") ? bulan : "-";
        
        boolean tagihanExists = false;
        String existingId = null;
        double existingDibayar = 0;
        
        for (Object[] p : allPembayaranCache) {
            if (p[1].toString().equals(currentNIS) && p[3].toString().equals(jenis)) {
                String existingBulan = p[4] != null ? p[4].toString() : "";
                if (jenis.equals("SPP") && existingBulan.equals(bulan)) {
                    tagihanExists = true;
                    existingId = p[0].toString();
                    existingDibayar = (double) p[6];
                    break;
                } else if (!jenis.equals("SPP") && existingBulan.equals("-")) {
                    tagihanExists = true;
                    existingId = p[0].toString();
                    existingDibayar = (double) p[6];
                    break;
                }
            }
        }
        
        boolean success;
        
        if (tagihanExists) {
            double totalDibayarBaru = existingDibayar + dibayar;
            double sisaBaru = totalTagihan - totalDibayarBaru;
            if (sisaBaru < 0) sisaBaru = 0;
            String statusBaru = (sisaBaru <= 0) ? "Lunas" : "Cicilan";
            
            success = updatePembayaranById(existingId, totalDibayarBaru, sisaBaru, statusBaru, tglBayar, metode);
        } else {
            String query = "INSERT INTO tbl_pembayaran (nisn, nama_siswa, jenis_tagihan, bulan, total_tagihan, dibayar, sisa_tagihan, status, tgl_bayar, metode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
                pstmt.setString(1, currentNIS);
                pstmt.setString(2, currentNama);
                pstmt.setString(3, jenis);
                pstmt.setString(4, bulanText);
                pstmt.setDouble(5, totalTagihan);
                pstmt.setDouble(6, dibayar);
                pstmt.setDouble(7, sisa);
                pstmt.setString(8, status);
                pstmt.setString(9, tglBayar);
                pstmt.setString(10, metode);
                success = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                success = false;
            }
        }
        
        if (success) {
            loadDataPembayaran();
            
            double totalTunggakan = 0;
            for (Object[] p : allPembayaranCache) {
                if (p[1].toString().equals(currentNIS)) {
                    double sisaVal = (double) p[7];
                    String statusTagihan = p[8].toString();
                    if (!statusTagihan.equals("Lunas") && sisaVal > 0) {
                        totalTunggakan += sisaVal;
                    }
                }
            }
            tunggakanField.setText(formatRupiah(totalTunggakan));
            sisaTagihanField.setText(parseRupiahToFormatted(sisa));
            
            JOptionPane.showMessageDialog(this, 
                "PEMBAYARAN BERHASIL!\n\n" +
                "Jumlah dibayar: " + formatRupiah(dibayar) + "\n" +
                "Sisa tagihan: " + formatRupiah(sisa) + "\n" +
                "Status: " + status + "\n" +
                "Metode: " + metode);
            
            int cetak = JOptionPane.showConfirmDialog(this, 
                "Apakah ingin mencetak nota pembayaran?", 
                "Cetak Nota", JOptionPane.YES_NO_OPTION);
            if (cetak == JOptionPane.YES_OPTION) {
                cetakNotaDenganData(totalTagihan, dibayar, sisa, status, tglBayar, dibayar, metode);
            }
            
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan pembayaran!");
        }
    }
    
    private boolean updatePembayaranById(String id, double totalDibayar, double sisa, String status, String tglBayar, String metode) {
        String query = "UPDATE tbl_pembayaran SET dibayar = ?, sisa_tagihan = ?, status = ?, tgl_bayar = ?, metode = ? WHERE id_pembayaran = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(query)) {
            pstmt.setDouble(1, totalDibayar);
            pstmt.setDouble(2, sisa);
            pstmt.setString(3, status);
            pstmt.setString(4, tglBayar);
            pstmt.setString(5, metode);
            pstmt.setString(6, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void cetakNotaDenganData(double totalTagihan, double dibayar, double sisa, String status, String tgl, double pembayaranIni, String metode) {
        try {
            String nis = currentNIS;
            String nama = currentNama;
            String jenis = (String) jenisTagihanCombo.getSelectedItem();
            String bulan = (String) bulanCombo.getSelectedItem();
            
            SimpleDateFormat sdfTgl = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("id", "ID"));
            SimpleDateFormat sdfTglOnly = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
            SimpleDateFormat sdfHari = new SimpleDateFormat("EEEE", new Locale("id", "ID"));
            String tanggalLengkap = sdfTgl.format(new Date());
            String tanggalOnly = sdfTglOnly.format(new Date());
            String hari = sdfHari.format(new Date());
            String hariTanggal = hari + ", " + tanggalOnly;
            
            String noTransaksi = "TRX/" + jenis + "/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + (System.currentTimeMillis() % 10000);
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("Kwitansi_" + nis + "_" + System.currentTimeMillis() + ".pdf"));
            int result = fileChooser.showSaveDialog(this);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".pdf")) filePath += ".pdf";
                
                com.itextpdf.text.Rectangle kwitansiSize = new com.itextpdf.text.Rectangle(550, 480);
                com.itextpdf.text.Document document = new com.itextpdf.text.Document(kwitansiSize, 15, 15, 15, 15);
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();
                
                com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font subTitleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.NORMAL);
                com.itextpdf.text.Font boldFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font smallFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.NORMAL);
                
                // KOP SURAT
                com.itextpdf.text.pdf.PdfPTable kopTable = new com.itextpdf.text.pdf.PdfPTable(2);
                kopTable.setWidthPercentage(100);
                kopTable.setWidths(new float[]{0.15f, 0.85f});
                
                com.itextpdf.text.pdf.PdfPCell logoCell = new com.itextpdf.text.pdf.PdfPCell();
                logoCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                logoCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                try {
                    java.net.URL imgUrl = getClass().getResource("/images/smapgri4.png");
                    if (imgUrl != null) {
                        com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(imgUrl);
                        logo.scaleToFit(45, 45);
                        logoCell.addElement(logo);
                    }
                } catch (Exception e) {}
                
                com.itextpdf.text.pdf.PdfPCell namaCell = new com.itextpdf.text.pdf.PdfPCell();
                namaCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                namaCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                com.itextpdf.text.Paragraph namaSekolah = new com.itextpdf.text.Paragraph("SMA PGRI 4 JAKARTA", titleFont);
                namaSekolah.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                namaCell.addElement(namaSekolah);
                com.itextpdf.text.Paragraph alamatSekolah = new com.itextpdf.text.Paragraph("Jl. Cipayung Raya, Jakarta Timur 13840", smallFont);
                alamatSekolah.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                namaCell.addElement(alamatSekolah);
                com.itextpdf.text.Paragraph judulKwitansi = new com.itextpdf.text.Paragraph("BUKTI PEMBAYARAN", subTitleFont);
                judulKwitansi.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                namaCell.addElement(judulKwitansi);
                
                kopTable.addCell(logoCell);
                kopTable.addCell(namaCell);
                document.add(kopTable);
                
                document.add(new com.itextpdf.text.Paragraph(" "));
                com.itextpdf.text.Paragraph garis = new com.itextpdf.text.Paragraph("────────────────────────────────────────────────────────────");
                garis.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(garis);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                // INFO TRANSAKSI
                com.itextpdf.text.pdf.PdfPTable infoTable = new com.itextpdf.text.pdf.PdfPTable(2);
                infoTable.setWidthPercentage(100);
                infoTable.setWidths(new float[]{0.25f, 0.75f});
                
                addInfoRow(infoTable, "No Transaksi", noTransaksi, normalFont);
                addInfoRow(infoTable, "Tanggal", tanggalLengkap, normalFont);
                addInfoRow(infoTable, "NIS", nis, normalFont);
                addInfoRow(infoTable, "Kelas", currentKelas, normalFont);
                addInfoRow(infoTable, "Nama", nama, normalFont);
                addInfoRow(infoTable, "Metode Pembayaran", metode, normalFont);
                
                document.add(infoTable);
                document.add(new com.itextpdf.text.Paragraph(" "));
                document.add(garis);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                // TABEL TAGIHAN
                com.itextpdf.text.pdf.PdfPTable tagihanTable = new com.itextpdf.text.pdf.PdfPTable(4);
                tagihanTable.setWidthPercentage(100);
                tagihanTable.setWidths(new float[]{0.5f, 2.5f, 1.0f, 1.2f});
                
                String[] tagihanHeaders = {"No", "Nama Pembayaran", "Bulan", "Nominal"};
                for (String header : tagihanHeaders) {
                    com.itextpdf.text.pdf.PdfPCell headerCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(header, boldFont));
                    headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                    headerCell.setPadding(4);
                    tagihanTable.addCell(headerCell);
                }
                
                String namaPembayaran = "";
                if (jenis.equals("SPP")) namaPembayaran = "BIAYA SPP";
                else if (jenis.equals("DAFTAR_ULANG")) namaPembayaran = "BIAYA DAFTAR ULANG";
                else namaPembayaran = "BIAYA UANG GEDUNG";
                
                tagihanTable.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("1", normalFont)));
                tagihanTable.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(namaPembayaran, normalFont)));
                String bulanText = jenis.equals("SPP") ? bulan : "-";
                tagihanTable.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(bulanText, normalFont)));
                tagihanTable.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(parseRupiahToFormatted(pembayaranIni), normalFont)));
                
                document.add(tagihanTable);
                document.add(new com.itextpdf.text.Paragraph(" "));
                document.add(garis);
                document.add(new com.itextpdf.text.Paragraph(" "));
                
                // BAGIAN BAWAH
                com.itextpdf.text.pdf.PdfPTable bottomTable = new com.itextpdf.text.pdf.PdfPTable(2);
                bottomTable.setWidthPercentage(100);
                bottomTable.setWidths(new float[]{0.50f, 0.50f});
                bottomTable.setSpacingBefore(10);
                bottomTable.setSpacingAfter(10);
                
                com.itextpdf.text.pdf.PdfPCell leftCell = new com.itextpdf.text.pdf.PdfPCell();
                leftCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                leftCell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
                leftCell.setFixedHeight(100);
                
                com.itextpdf.text.pdf.PdfPTable totalLeftTable = new com.itextpdf.text.pdf.PdfPTable(2);
                totalLeftTable.setWidthPercentage(80);
                totalLeftTable.setWidths(new float[]{0.40f, 0.60f});
                totalLeftTable.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                
                addTotalRowLeft(totalLeftTable, "Total Tagihan", parseRupiahToFormatted(totalTagihan), boldFont, normalFont);
                addTotalRowLeft(totalLeftTable, "Dibayar", parseRupiahToFormatted(dibayar), normalFont, normalFont);
                addTotalRowLeft(totalLeftTable, "Sisa Tagihan", parseRupiahToFormatted(sisa), normalFont, normalFont);
                
                leftCell.addElement(totalLeftTable);
                bottomTable.addCell(leftCell);
                
                com.itextpdf.text.pdf.PdfPCell rightCell = new com.itextpdf.text.pdf.PdfPCell();
                rightCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                rightCell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
                rightCell.setFixedHeight(100);
                rightCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                
                com.itextpdf.text.Paragraph tglFooter = new com.itextpdf.text.Paragraph("Jakarta, " + hariTanggal, normalFont);
                tglFooter.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                rightCell.addElement(tglFooter);
                rightCell.addElement(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph bendahara = new com.itextpdf.text.Paragraph("Bendahara,", normalFont);
                bendahara.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                rightCell.addElement(bendahara);
                rightCell.addElement(new com.itextpdf.text.Paragraph(" "));
                rightCell.addElement(new com.itextpdf.text.Paragraph(" "));
                
                com.itextpdf.text.Paragraph namaBendahara = new com.itextpdf.text.Paragraph("NIA", boldFont);
                namaBendahara.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                rightCell.addElement(namaBendahara);
                
                bottomTable.addCell(rightCell);
                
                document.add(bottomTable);
                
                document.close();
                JOptionPane.showMessageDialog(this, "Kwitansi berhasil disimpan!\nLokasi: " + filePath);
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
    
    private void addTotalRowLeft(com.itextpdf.text.pdf.PdfPTable table, String label, String value, com.itextpdf.text.Font labelFont, com.itextpdf.text.Font valueFont) {
        com.itextpdf.text.pdf.PdfPCell labelCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(label + " :", labelFont));
        labelCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
        labelCell.setPadding(4);
        labelCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
        com.itextpdf.text.pdf.PdfPCell valueCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(value, valueFont));
        valueCell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
        valueCell.setPadding(4);
        valueCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    private void cetakNota() {
        if (currentNIS.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data pembayaran untuk dicetak!");
            return;
        }
        
        String totalText = totalTagihanField.getText().trim().replace(".", "");
        String dibayarText = dibayarField.getText().trim().replace(".", "");
        String metode = (String) metodeCombo.getSelectedItem();
        
        double totalTagihan = Double.parseDouble(totalText);
        double dibayar = Double.parseDouble(dibayarText);
        double sisa = totalTagihan - dibayar;
        String status = (sisa <= 0) ? "Lunas" : "Cicilan";
        String tgl = getCurrentDate();
        
        cetakNotaDenganData(totalTagihan, dibayar, sisa, status, tgl, dibayar, metode);
    }
    
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
    
    private void resetForm() {
        nisField.setText("");
        namaField.setText("");
        tunggakanField.setText("");
        kelasLabel.setText("-");
        jenisTagihanCombo.setSelectedIndex(0);
        bulanCombo.setSelectedIndex(0);
        totalTagihanField.setText("");
        dibayarField.setText("");
        sisaTagihanField.setText("");
        statusCombo.setSelectedIndex(0);
        metodeCombo.setSelectedIndex(0);
        currentNIS = "";
        currentNama = "";
        currentKelas = "";
        nisField.requestFocus();
    }
    
    private void loadDataPembayaran() {
        tableModel.setRowCount(0);
        allPembayaranCache = Database.getAllPembayaran();
        
        for (Object[] p : allPembayaranCache) {
            if (p.length >= 10) {
                double total = (double) p[5];
                double dibayar = (double) p[6];
                double sisa = (double) p[7];
                String metode = p.length > 10 ? p[10].toString() : "-";
                
                tableModel.addRow(new Object[]{
                    p[0], p[1], p[2], p[3], 
                    p[4] != null && !p[4].toString().isEmpty() ? p[4] : "-",
                    formatRupiah(total), formatRupiah(dibayar), formatRupiah(sisa),
                    p[8], metode, p[9]
                });
            }
        }
        
        if (!currentNIS.isEmpty()) {
            double totalTunggakan = 0;
            for (Object[] p : allPembayaranCache) {
                if (p[1].toString().equals(currentNIS)) {
                    double sisa = (double) p[7];
                    String status = p[8].toString();
                    if (!status.equals("Lunas") && sisa > 0) {
                        totalTunggakan += sisa;
                    }
                }
            }
            tunggakanField.setText(formatRupiah(totalTunggakan));
        }
    }
}