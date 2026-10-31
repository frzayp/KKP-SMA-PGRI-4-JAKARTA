import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SIAKAD extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    public SIAKAD() {
        setTitle("Sistem Informasi Akademik - SMA XXX");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // CardLayout untuk berpindah halaman
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Tambahkan semua panel
        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createMenuUtamaPanel(), "menuUtama");
        mainPanel.add(createDataSiswaPanel(), "dataSiswa");
        mainPanel.add(createDataGuruPanel(), "dataGuru");
        mainPanel.add(createDataKelasPanel(), "dataKelas");
        mainPanel.add(createInputNilaiPanel(), "inputNilai");
        mainPanel.add(createAbsensiPanel(), "absensi");
        mainPanel.add(createPembayaranSPPPanel(), "pembayaranSPP");
        mainPanel.add(createLaporanNilaiPanel(), "laporanNilai");
        mainPanel.add(createLaporanSiswaPanel(), "laporanSiswa");
        mainPanel.add(createLaporanGuruPanel(), "laporanGuru");
        mainPanel.add(createLaporanAbsensiPanel(), "laporanAbsensi");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }
    
    // ==================== 1. LOGIN PANEL ====================
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel title = new JLabel("SISTEM INFORMASI AKADEMIK");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        JTextField usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        // Role
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Hak Akses:"), gbc);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin TU", "Guru"});
        gbc.gridx = 1;
        panel.add(roleCombo, gbc);
        
        // Login Button
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(new Color(70, 130, 200));
        loginBtn.setForeground(Color.WHITE);
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);
        
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (username.equals("admin") || username.equals("guru")) {
                cardLayout.show(mainPanel, "menuUtama");
            } else {
                JOptionPane.showMessageDialog(panel, "Login gagal! Username: admin atau guru");
            }
        });
        
        return panel;
    }
    
    // ==================== 2. MENU UTAMA PANEL ====================
    private JPanel createMenuUtamaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        
        // Header
        JLabel header = new JLabel("SMA XXX - Sistem Informasi Akademik Terintegrasi", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setBackground(new Color(70, 130, 200));
        header.setOpaque(true);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 50));
        panel.add(header, BorderLayout.NORTH);
        
        // Menu buttons panel
        JPanel menuPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        menuPanel.setBackground(new Color(245, 245, 245));
        
        JButton btnMaster = createMenuButton("📁 MASTER DATA", null);
        JButton btnTransaksi = createMenuButton("📝 TRANSAKSI", null);
        JButton btnLaporan = createMenuButton("📊 LAPORAN", null);
        JButton btnLogout = createMenuButton("🚪 LOGOUT", "login");
        
        // Sub menu Master Data
        JPopupMenu masterMenu = new JPopupMenu();
        JMenuItem menuSiswa = new JMenuItem("Data Siswa");
        JMenuItem menuGuru = new JMenuItem("Data Guru");
        JMenuItem menuKelas = new JMenuItem("Data Kelas");
        menuSiswa.addActionListener(e -> cardLayout.show(mainPanel, "dataSiswa"));
        menuGuru.addActionListener(e -> cardLayout.show(mainPanel, "dataGuru"));
        menuKelas.addActionListener(e -> cardLayout.show(mainPanel, "dataKelas"));
        masterMenu.add(menuSiswa);
        masterMenu.add(menuGuru);
        masterMenu.add(menuKelas);
        btnMaster.addActionListener(e -> masterMenu.show(btnMaster, 0, btnMaster.getHeight()));
        
        // Sub menu Transaksi
        JPopupMenu transaksiMenu = new JPopupMenu();
        JMenuItem menuNilai = new JMenuItem("Input Nilai");
        JMenuItem menuAbsensi = new JMenuItem("Absensi Siswa");
        JMenuItem menuSPP = new JMenuItem("Pembayaran SPP");
        menuNilai.addActionListener(e -> cardLayout.show(mainPanel, "inputNilai"));
        menuAbsensi.addActionListener(e -> cardLayout.show(mainPanel, "absensi"));
        menuSPP.addActionListener(e -> cardLayout.show(mainPanel, "pembayaranSPP"));
        transaksiMenu.add(menuNilai);
        transaksiMenu.add(menuAbsensi);
        transaksiMenu.add(menuSPP);
        btnTransaksi.addActionListener(e -> transaksiMenu.show(btnTransaksi, 0, btnTransaksi.getHeight()));
        
        // Sub menu Laporan
        JPopupMenu laporanMenu = new JPopupMenu();
        JMenuItem menuLapSiswa = new JMenuItem("Laporan Data Siswa");
        JMenuItem menuLapGuru = new JMenuItem("Laporan Data Guru");
        JMenuItem menuLapNilai = new JMenuItem("Laporan Nilai");
        JMenuItem menuLapAbsensi = new JMenuItem("Laporan Absensi");
        menuLapSiswa.addActionListener(e -> cardLayout.show(mainPanel, "laporanSiswa"));
        menuLapGuru.addActionListener(e -> cardLayout.show(mainPanel, "laporanGuru"));
        menuLapNilai.addActionListener(e -> cardLayout.show(mainPanel, "laporanNilai"));
        menuLapAbsensi.addActionListener(e -> cardLayout.show(mainPanel, "laporanAbsensi"));
        laporanMenu.add(menuLapSiswa);
        laporanMenu.add(menuLapGuru);
        laporanMenu.add(menuLapNilai);
        laporanMenu.add(menuLapAbsensi);
        btnLaporan.addActionListener(e -> laporanMenu.show(btnLaporan, 0, btnLaporan.getHeight()));
        
        btnLogout.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        
        menuPanel.add(btnMaster);
        menuPanel.add(btnTransaksi);
        menuPanel.add(btnLaporan);
        menuPanel.add(btnLogout);
        
        panel.add(menuPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createMenuButton(String text, String targetPanel) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(new Color(100, 149, 237));
        btn.setForeground(Color.WHITE);
        if (targetPanel != null) {
            btn.addActionListener(e -> cardLayout.show(mainPanel, targetPanel));
        }
        return btn;
    }
    
    // ==================== 3. DATA SISWA PANEL (LENGKAP CRUD) ====================
    private JPanel createDataSiswaPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form input
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField nisnField = new JTextField();
        JTextField namaField = new JTextField();
        JTextField alamatField = new JTextField();
        JComboBox<String> kelasCombo = new JComboBox<>(new String[]{"X MIPA 1", "X MIPA 2", "XI MIPA 1", "XI IPS 1"});
        
        formPanel.add(new JLabel("NISN:"));
        formPanel.add(nisnField);
        formPanel.add(new JLabel("Nama:"));
        formPanel.add(namaField);
        formPanel.add(new JLabel("Alamat:"));
        formPanel.add(alamatField);
        formPanel.add(new JLabel("Kelas:"));
        formPanel.add(kelasCombo);
        
        // Table
        String[] columns = {"NISN", "Nama", "Alamat", "Kelas"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton tambahBtn = new JButton("TAMBAH");
        JButton ubahBtn = new JButton("UBAH");
        JButton hapusBtn = new JButton("HAPUS");
        JButton cariBtn = new JButton("CARI");
        JButton backBtn = new JButton("KEMBALI");
        
        btnPanel.add(tambahBtn);
        btnPanel.add(ubahBtn);
        btnPanel.add(hapusBtn);
        btnPanel.add(cariBtn);
        btnPanel.add(backBtn);
        
        // TAMBAH DATA
        tambahBtn.addActionListener(e -> {
            String nisn = nisnField.getText().trim();
            String nama = namaField.getText().trim();
            String alamat = alamatField.getText().trim();
            String kelas = (String) kelasCombo.getSelectedItem();
            
            if (nisn.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            tableModel.addRow(new Object[]{nisn, nama, alamat, kelas});
            nisnField.setText("");
            namaField.setText("");
            alamatField.setText("");
            kelasCombo.setSelectedIndex(0);
            JOptionPane.showMessageDialog(panel, "Data berhasil ditambahkan!");
        });
        
        // HAPUS DATA
        hapusBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(panel, "Yakin hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(panel, "Data dihapus!");
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Pilih data yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // UBAH DATA
        ubahBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String nisn = nisnField.getText().trim();
                String nama = namaField.getText().trim();
                String alamat = alamatField.getText().trim();
                String kelas = (String) kelasCombo.getSelectedItem();
                
                if (nisn.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                tableModel.setValueAt(nisn, selectedRow, 0);
                tableModel.setValueAt(nama, selectedRow, 1);
                tableModel.setValueAt(alamat, selectedRow, 2);
                tableModel.setValueAt(kelas, selectedRow, 3);
                
                nisnField.setText("");
                namaField.setText("");
                alamatField.setText("");
                JOptionPane.showMessageDialog(panel, "Data berhasil diubah!");
            } else {
                JOptionPane.showMessageDialog(panel, "Pilih data yang akan diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // CARI DATA
        cariBtn.addActionListener(e -> {
            String keyword = JOptionPane.showInputDialog(panel, "Masukkan NISN atau Nama yang dicari:");
            if (keyword != null && !keyword.trim().isEmpty()) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String nisn = tableModel.getValueAt(i, 0).toString();
                    String nama = tableModel.getValueAt(i, 1).toString();
                    if (nisn.toLowerCase().contains(keyword.toLowerCase()) || 
                        nama.toLowerCase().contains(keyword.toLowerCase())) {
                        table.setRowSelectionInterval(i, i);
                        table.scrollRectToVisible(table.getCellRect(i, 0, true));
                        JOptionPane.showMessageDialog(panel, "Data ditemukan!");
                        return;
                    }
                }
                JOptionPane.showMessageDialog(panel, "Data tidak ditemukan!");
            }
        });
        
        // KLIK TABEL ISI FORM
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    nisnField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    namaField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    alamatField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    String kelas = tableModel.getValueAt(selectedRow, 3).toString();
                    kelasCombo.setSelectedItem(kelas);
                }
            }
        });
        
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menuUtama"));
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(btnPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==================== 4. DATA GURU PANEL ====================
    private JPanel createDataGuruPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField nipField = new JTextField();
        JTextField namaField = new JTextField();
        JTextField mapelField = new JTextField();
        
        formPanel.add(new JLabel("NIP:"));
        formPanel.add(nipField);
        formPanel.add(new JLabel("Nama Guru:"));
        formPanel.add(namaField);
        formPanel.add(new JLabel("Mata Pelajaran:"));
        formPanel.add(mapelField);
        
        String[] columns = {"NIP", "Nama Guru", "Mata Pelajaran"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton tambahBtn = new JButton("TAMBAH");
        JButton ubahBtn = new JButton("UBAH");
        JButton hapusBtn = new JButton("HAPUS");
        JButton backBtn = new JButton("KEMBALI");
        
        btnPanel.add(tambahBtn);
        btnPanel.add(ubahBtn);
        btnPanel.add(hapusBtn);
        btnPanel.add(backBtn);
        
        tambahBtn.addActionListener(e -> {
            String nip = nipField.getText().trim();
            String nama = namaField.getText().trim();
            String mapel = mapelField.getText().trim();
            
            if (nip.isEmpty() || nama.isEmpty() || mapel.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Semua field harus diisi!");
                return;
            }
            tableModel.addRow(new Object[]{nip, nama, mapel});
            nipField.setText("");
            namaField.setText("");
            mapelField.setText("");
        });
        
        hapusBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                tableModel.removeRow(row);
            } else {
                JOptionPane.showMessageDialog(panel, "Pilih data yang akan dihapus!");
            }
        });
        
        ubahBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                tableModel.setValueAt(nipField.getText(), row, 0);
                tableModel.setValueAt(namaField.getText(), row, 1);
                tableModel.setValueAt(mapelField.getText(), row, 2);
            } else {
                JOptionPane.showMessageDialog(panel, "Pilih data yang akan diubah!");
            }
        });
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    nipField.setText(tableModel.getValueAt(row, 0).toString());
                    namaField.setText(tableModel.getValueAt(row, 1).toString());
                    mapelField.setText(tableModel.getValueAt(row, 2).toString());
                }
            }
        });
        
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menuUtama"));
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(btnPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==================== 5. DATA KELAS PANEL ====================
    private JPanel createDataKelasPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField idKelasField = new JTextField();
        JTextField namaKelasField = new JTextField();
        JTextField waliKelasField = new JTextField();
        
        formPanel.add(new JLabel("ID Kelas:"));
        formPanel.add(idKelasField);
        formPanel.add(new JLabel("Nama Kelas:"));
        formPanel.add(namaKelasField);
        formPanel.add(new JLabel("Wali Kelas:"));
        formPanel.add(waliKelasField);
        
        String[] columns = {"ID Kelas", "Nama Kelas", "Wali Kelas"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton tambahBtn = new JButton("TAMBAH");
        JButton ubahBtn = new JButton("UBAH");
        JButton hapusBtn = new JButton("HAPUS");
        JButton backBtn = new JButton("KEMBALI");
        
        btnPanel.add(tambahBtn);
        btnPanel.add(ubahBtn);
        btnPanel.add(hapusBtn);
        btnPanel.add(backBtn);
        
        tambahBtn.addActionListener(e -> {
            String id = idKelasField.getText().trim();
            String nama = namaKelasField.getText().trim();
            String wali = waliKelasField.getText().trim();
            
            if (id.isEmpty() || nama.isEmpty() || wali.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Semua field harus diisi!");
                return;
            }
            tableModel.addRow(new Object[]{id, nama, wali});
            idKelasField.setText("");
            namaKelasField.setText("");
            waliKelasField.setText("");
        });
        
        hapusBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                tableModel.removeRow(row);
            } else {
                JOptionPane.showMessageDialog(panel, "Pilih data yang akan dihapus!");
            }
        });
        
        ubahBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                tableModel.setValueAt(idKelasField.getText(), row, 0);
                tableModel.setValueAt(namaKelasField.getText(), row, 1);
                tableModel.setValueAt(waliKelasField.getText(), row, 2);
            } else {
                JOptionPane.showMessageDialog(panel, "Pilih data yang akan diubah!");
            }
        });
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    idKelasField.setText(tableModel.getValueAt(row, 0).toString());
                    namaKelasField.setText(tableModel.getValueAt(row, 1).toString());
                    waliKelasField.setText(tableModel.getValueAt(row, 2).toString());
                }
            }
        });
        
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menuUtama"));
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(btnPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==================== 6. INPUT NILAI PANEL ====================
    private JPanel createInputNilaiPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JComboBox<String> siswaCombo = new JComboBox<>(new String[]{"Ani (12345)", "Budi (12346)"});
        JTextField tugasField = new JTextField(10);
        JTextField utsField = new JTextField(10);
        JTextField uasField = new JTextField(10);
        JTextField akhirField = new JTextField(10);
        akhirField.setEditable(false);
        
        JButton hitungBtn = new JButton("HITUNG NILAI AKHIR");
        JButton simpanBtn = new JButton("SIMPAN");
        JButton resetBtn = new JButton("RESET");
        JButton backBtn = new JButton("KEMBALI");
        
        hitungBtn.addActionListener(e -> {
            try {
                double tugas = Double.parseDouble(tugasField.getText());
                double uts = Double.parseDouble(utsField.getText());
                double uas = Double.parseDouble(uasField.getText());
                double akhir = (tugas * 0.2) + (uts * 0.3) + (uas * 0.5);
                akhirField.setText(String.format("%.2f", akhir));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Masukkan angka yang valid!");
            }
        });
        
        simpanBtn.addActionListener(e -> {
            if (akhirField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Hitung nilai akhir terlebih dahulu!");
            } else {
                JOptionPane.showMessageDialog(panel, "Nilai berhasil disimpan!\nSiswa: " + siswaCombo.getSelectedItem() + "\nNilai Akhir: " + akhirField.getText());
            }
        });
        
        resetBtn.addActionListener(e -> {
            tugasField.setText("");
            utsField.setText("");
            uasField.setText("");
            akhirField.setText("");
        });
        
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menuUtama"));
        
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Pilih Siswa:"), gbc);
        gbc.gridx = 1; panel.add(siswaCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Nilai Tugas:"), gbc);
        gbc.gridx = 1; panel.add(tugasField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Nilai UTS:"), gbc);
        gbc.gridx = 1; panel.add(utsField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Nilai UAS:"), gbc);
        gbc.gridx = 1; panel.add(uasField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Nilai Akhir:"), gbc);
        gbc.gridx = 1; panel.add(akhirField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(hitungBtn);
        btnPanel.add(simpanBtn);
        btnPanel.add(resetBtn);
        btnPanel.add(backBtn);
        panel.add(btnPanel, gbc);
        
        return panel;
    }
    
    // ==================== 7. ABSENSI PANEL ====================
    private JPanel createAbsensiPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel filterPanel = new JPanel(new FlowLayout());
        JComboBox<String> kelasCombo = new JComboBox<>(new String[]{"X MIPA 1", "X MIPA 2"});
        JTextField tanggalField = new JTextField("12-04-2026", 10);
        filterPanel.add(new JLabel("Kelas:"));
        filterPanel.add(kelasCombo);
        filterPanel.add(new JLabel("Tanggal:"));
        filterPanel.add(tanggalField);
        
        String[] columns = {"No", "NISN", "Nama", "Hadir", "Sakit", "Izin", "Alpa"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Sample data
        tableModel.addRow(new Object[]{"1", "12345", "Ani", "✓", "", "", ""});
        tableModel.addRow(new Object[]{"2", "12346", "Budi", "", "✓", "", ""});
        
        JButton simpanBtn = new JButton("SIMPAN ABSENSI");
        JButton backBtn = new JButton("KEMBALI");
        JPanel btnPanel = new JPanel();
        btnPanel.add(simpanBtn);
        btnPanel.add(backBtn);
        
        simpanBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(panel, "Absensi tanggal " + tanggalField.getText() + " untuk kelas " + kelasCombo.getSelectedItem() + " berhasil disimpan!");
        });
        
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menuUtama"));
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==================== 8. PEMBAYARAN SPP PANEL ====================
    private JPanel createPembayaranSPPPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JComboBox<String> siswaCombo = new JComboBox<>(new String[]{"Ani (12345)", "Budi (12346)"});
        JComboBox<String> bulanCombo = new JComboBox<>(new String[]{"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"});
        JTextField nominalField = new JTextField("200000");
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Lunas", "Belum"});
        JTextField tglBayarField = new JTextField("12-04-2026");
        
        formPanel.add(new JLabel("Pilih Siswa:")); formPanel.add(siswaCombo);
        formPanel.add(new JLabel("Bulan:")); formPanel.add(bulanCombo);
        formPanel.add(new JLabel("Nominal SPP:")); formPanel.add(nominalField);
        formPanel.add(new JLabel("Status:")); formPanel.add(statusCombo);
        formPanel.add(new JLabel("Tanggal Bayar:")); formPanel.add(tglBayarField);
        
        String[] columns = {"Bulan", "Nominal", "Status", "Tanggal Bayar"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        tableModel.addRow(new Object[]{"Januari", "200000", "Lunas", "10-01-2026"});
        tableModel.addRow(new Object[]{"Februari", "200000", "Lunas", "10-02-2026"});
        
        JButton simpanBtn = new JButton("SIMPAN DATA");
        JButton backBtn = new JButton("KEMBALI");
        JPanel btnPanel = new JPanel();
        btnPanel.add(simpanBtn);
        btnPanel.add(backBtn);
        
        simpanBtn.addActionListener(e -> {
            String siswa = (String) siswaCombo.getSelectedItem();
            String bulan = (String) bulanCombo.getSelectedItem();
            String nominal = nominalField.getText();
            String status = (String) statusCombo.getSelectedItem();
            String tgl = tglBayarField.getText();
            
            tableModel.addRow(new Object[]{bulan, nominal, status, tgl});
            JOptionPane.showMessageDialog(panel, "Pembayaran SPP untuk " + siswa + " bulan " + bulan + " disimpan!");
        });
        
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menuUtama"));
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(btnPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==================== 9. LAPORAN NILAI PANEL ====================
    private JPanel createLaporanNilaiPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel filterPanel = new JPanel();
        JComboBox<String> kelasCombo = new JComboBox<>(new String[]{"X MIPA 1", "X MIPA 2", "XI MIPA 1", "XI IPS 1"});
        filterPanel.add(new JLabel("Kelas:"));
        filterPanel.add(kelasCombo);
        
        String[] columns = {"No", "NISN", "Nama", "Tugas", "UTS", "UAS", "Akhir"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        tableModel.addRow(new Object[]{"1", "12345", "Ani", "80", "75", "85", "81.5"});
        tableModel.addRow(new Object[]{"2", "12346", "Budi", "70", "65", "75", "70.0"});
        
        JButton cetakBtn = new JButton("CETAK PDF");
        JButton exportBtn = new JButton("EXPORT EXCEL");
        JButton backBtn = new JButton("KEMBALI");
        JPanel btnPanel = new JPanel();
        btnPanel.add(cetakBtn);
        btnPanel.add(exportBtn);
        btnPanel.add(backBtn);
        
        cetakBtn.addActionListener(e -> JOptionPane.showMessageDialog(panel, "Cetak Laporan Nilai Kelas " + kelasCombo.getSelectedItem()));
        exportBtn.addActionListener(e -> JOptionPane.showMessageDialog(panel, "Export Laporan Nilai ke Excel"));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menuUtama"));
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==================== 10. LAPORAN SISWA PANEL ====================
    private JPanel createLaporanSiswaPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] columns = {"NISN", "Nama", "Alamat", "Kelas"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        tableModel.addRow(new Object[]{"12345", "Ani", "Jl. Merdeka 1", "X MIPA 1"});
        tableModel.addRow(new Object[]{"12346", "Budi", "Jl. Merdeka 2", "X MIPA 2"});
        
        JButton cetakBtn = new JButton("CETAK PDF");
        JButton backBtn = new JButton("KEMBALI");
        JPanel btnPanel = new JPanel();
        btnPanel.add(cetakBtn);
        btnPanel.add(backBtn);
        
        cetakBtn.addActionListener(e -> JOptionPane.showMessageDialog(panel, "Cetak Laporan Data Siswa"));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menuUtama"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==================== 11. LAPORAN GURU PANEL ====================
    private JPanel createLaporanGuruPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] columns = {"NIP", "Nama Guru", "Mata Pelajaran"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        JButton cetakBtn = new JButton("CETAK PDF");
        JButton backBtn = new JButton("KEMBALI");
        JPanel btnPanel = new JPanel();
        btnPanel.add(cetakBtn);
        btnPanel.add(backBtn);
        
        cetakBtn.addActionListener(e -> JOptionPane.showMessageDialog(panel, "Cetak Laporan Data Guru"));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menuUtama"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==================== 12. LAPORAN ABSENSI PANEL ====================
    private JPanel createLaporanAbsensiPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel filterPanel = new JPanel();
        JComboBox<String> bulanCombo = new JComboBox<>(new String[]{"Januari", "Februari", "Maret", "April"});
        filterPanel.add(new JLabel("Bulan:"));
        filterPanel.add(bulanCombo);
        
        String[] columns = {"No", "NISN", "Nama", "Hadir", "Sakit", "Izin", "Alpa"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        tableModel.addRow(new Object[]{"1", "12345", "Ani", "22", "2", "1", "0"});
        tableModel.addRow(new Object[]{"2", "12346", "Budi", "20", "3", "1", "1"});
        
        JButton cetakBtn = new JButton("CETAK PDF");
        JButton backBtn = new JButton("KEMBALI");
        JPanel btnPanel = new JPanel();
        btnPanel.add(cetakBtn);
        btnPanel.add(backBtn);
        
        cetakBtn.addActionListener(e -> JOptionPane.showMessageDialog(panel, "Cetak Rekap Absensi Bulan " + bulanCombo.getSelectedItem()));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menuUtama"));
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SIAKAD().setVisible(true);
        });
    }
}