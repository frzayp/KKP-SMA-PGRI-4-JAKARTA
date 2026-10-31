import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

public class DataSiswaPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField nisField, namaField, tempatLahirField, alamatField, tahunMasukField;
    private JComboBox<String> jkCombo, agamaCombo, kelasCombo, statusCombo;
    private JSpinner tglLahirSpinner;
    private JButton tambahBtn, ubahBtn, hapusBtn, refreshBtn, backBtn;
    private JTextField searchField;
    private JPanel topStatsPanel;
    
    private JLabel totalSiswaLabel;
    private JLabel totalLakiLabel;
    private JLabel totalPerempuanLabel;
    private JLabel totalKelasLabel;
    
    public DataSiswaPanel(CardLayout cardLayout, JPanel mainPanel) {
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
        
        topStatsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        topStatsPanel.setOpaque(false);
        topStatsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JPanel card1 = createStatCard("👨‍🎓", "TOTAL SISWA", "0", "Siswa Aktif");
        totalSiswaLabel = (JLabel) card1.getClientProperty("valueLabel");
        topStatsPanel.add(card1);
        
        JPanel card2 = createStatCard("👨", "LAKI-LAKI", "0", "Orang");
        totalLakiLabel = (JLabel) card2.getClientProperty("valueLabel");
        topStatsPanel.add(card2);
        
        JPanel card3 = createStatCard("👩", "PEREMPUAN", "0", "Orang");
        totalPerempuanLabel = (JLabel) card3.getClientProperty("valueLabel");
        topStatsPanel.add(card3);
        
        JPanel card4 = createStatCard("🏫", "TOTAL KELAS", "0", "Kelas Aktif");
        totalKelasLabel = (JLabel) card4.getClientProperty("valueLabel");
        topStatsPanel.add(card4);
        
        add(topStatsPanel, BorderLayout.NORTH);
        
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplit.setResizeWeight(0.45);
        mainSplit.setDividerSize(8);
        mainSplit.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel formTitle = new JLabel("📝 FORM DATA SISWA (Kata kunci: NIS)");
        formTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        formTitle.setForeground(new Color(41, 128, 185));
        formContainer.add(formTitle, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        formPanel.add(new JLabel("NIS *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        nisField = new JTextField();
        formPanel.add(nisField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.15;
        formPanel.add(new JLabel("Nama Lengkap *:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.35;
        namaField = new JTextField();
        formPanel.add(namaField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Jenis Kelamin:"), gbc);
        gbc.gridx = 1;
        jkCombo = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"});
        formPanel.add(jkCombo, gbc);
        
        gbc.gridx = 2;
        formPanel.add(new JLabel("Tempat Lahir:"), gbc);
        gbc.gridx = 3;
        tempatLahirField = new JTextField();
        formPanel.add(tempatLahirField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tanggal Lahir:"), gbc);
        gbc.gridx = 1;
        tglLahirSpinner = new JSpinner(new SpinnerDateModel());
        tglLahirSpinner.setEditor(new JSpinner.DateEditor(tglLahirSpinner, "dd-MM-yyyy"));
        formPanel.add(tglLahirSpinner, gbc);
        
        gbc.gridx = 2;
        formPanel.add(new JLabel("Agama:"), gbc);
        gbc.gridx = 3;
        agamaCombo = new JComboBox<>(new String[]{"Islam", "Kristen", "Katolik", "Hindu", "Buddha", "Konghucu"});
        formPanel.add(agamaCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        alamatField = new JTextField();
        formPanel.add(alamatField, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Kelas:"), gbc);
        gbc.gridx = 1;
        kelasCombo = new JComboBox<>();
        formPanel.add(kelasCombo, gbc);
        
        gbc.gridx = 2;
        formPanel.add(new JLabel("Tahun Masuk:"), gbc);
        gbc.gridx = 3;
        tahunMasukField = new JTextField(6);
        tahunMasukField.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        formPanel.add(tahunMasukField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Aktif", "Lulus", "Pindah", "Keluar"});
        formPanel.add(statusCombo, gbc);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        
        tambahBtn = createStyledButton("➕ TAMBAH", new Color(46, 204, 113));
        ubahBtn = createStyledButton("✏️ UBAH", new Color(52, 152, 219));
        hapusBtn = createStyledButton("🗑️ HAPUS", new Color(231, 76, 60));
        refreshBtn = createStyledButton("🔄 REFRESH", new Color(52, 152, 219));
        backBtn = createStyledButton("◀ KEMBALI", new Color(52, 73, 94));
        
        btnPanel.add(tambahBtn);
        btnPanel.add(ubahBtn);
        btnPanel.add(hapusBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(backBtn);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 0, 5, 0);
        formPanel.add(btnPanel, gbc);
        
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(BorderFactory.createEmptyBorder());
        formContainer.add(formScrollPane, BorderLayout.CENTER);
        
        mainSplit.setTopComponent(formContainer);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        searchPanel.add(new JLabel("🔍 Cari (NIS/Nama):"));
        searchField = new JTextField(25);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cariData(searchField.getText().trim());
            }
        });
        searchPanel.add(searchField);
        
        JButton cariBtn = createSmallButton("CARI", new Color(52, 152, 219));
        searchPanel.add(cariBtn);
        
        JButton resetCariBtn = createSmallButton("RESET", new Color(108, 117, 125));
        resetCariBtn.addActionListener(e -> {
            searchField.setText("");
            loadData();
        });
        searchPanel.add(resetCariBtn);
        
        tableContainer.add(searchPanel, BorderLayout.NORTH);
        
        // KOLOM: TANPA EMAIL, NO HP, AYAH, IBU
        String[] columns = {
            "No", "NIS", "Nama", "JK", "Tempat Lahir", "Tanggal Lahir", 
            "Agama", "Alamat", "Kelas", "Tahun Masuk", "Status"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        int[] widths = {40, 100, 150, 60, 100, 90, 70, 180, 80, 80, 70};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(Color.WHITE);
        
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableContainer.add(tableScrollPane, BorderLayout.CENTER);
        
        mainSplit.setBottomComponent(tableContainer);
        
        add(mainSplit, BorderLayout.CENTER);
        
        tambahBtn.addActionListener(e -> tambahData());
        ubahBtn.addActionListener(e -> ubahData());
        hapusBtn.addActionListener(e -> hapusData());
        refreshBtn.addActionListener(e -> {
            loadData();
            loadKelas();
            updateTopStats();
            searchField.setText("");
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
        
        cariBtn.addActionListener(e -> cariData(searchField.getText().trim()));
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    // 0=No, 1=NIS, 2=Nama, 3=JK, 4=TempatLahir, 5=TglLahir,
                    // 6=Agama, 7=Alamat, 8=Kelas, 9=TahunMasuk, 10=Status
                    nisField.setText(getVal(row, 1));
                    namaField.setText(getVal(row, 2));
                    jkCombo.setSelectedItem(getVal(row, 3));
                    tempatLahirField.setText(getVal(row, 4));
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date tglDate = sdf.parse(getVal(row, 5));
                        tglLahirSpinner.setValue(tglDate);
                    } catch (Exception ex) {}
                    agamaCombo.setSelectedItem(getVal(row, 6));
                    alamatField.setText(getVal(row, 7));
                    kelasCombo.setSelectedItem(getVal(row, 8));
                    tahunMasukField.setText(getVal(row, 9));
                    statusCombo.setSelectedItem(getVal(row, 10));
                }
            }
        });
        
        updateTopStats();
    }
    
    private String getVal(int row, int col) {
        Object val = tableModel.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private JButton createSmallButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private JPanel createStatCard(String icon, String title, String defaultValue, String subtitle) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color startColor, endColor;
                if (title.contains("TOTAL SISWA")) {
                    startColor = new Color(52, 152, 219);
                    endColor = new Color(41, 128, 185);
                } else if (title.contains("LAKI-LAKI")) {
                    startColor = new Color(46, 204, 113);
                    endColor = new Color(39, 174, 96);
                } else if (title.contains("PEREMPUAN")) {
                    startColor = new Color(155, 89, 182);
                    endColor = new Color(142, 68, 173);
                } else {
                    startColor = new Color(241, 196, 15);
                    endColor = new Color(243, 156, 18);
                }
                GradientPaint gp = new GradientPaint(0, 0, startColor, w, h, endColor);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 15, 15);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(2, 4, w, h, 15, 15);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 15, 15);
            }
        };
        
        card.setLayout(new BorderLayout(8, 5));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        titleLabel.setForeground(new Color(255, 255, 255, 220));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(defaultValue);
        valueLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 9));
        subLabel.setForeground(new Color(255, 255, 255, 200));
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subLabel, BorderLayout.SOUTH);
        
        card.putClientProperty("valueLabel", valueLabel);
        
        return card;
    }
    
    private void updateTopStats() {
        try {
            List<String[]> siswaList = Database.getAllSiswa();
            int total = siswaList.size();
            int laki = 0, perempuan = 0;
            for (String[] s : siswaList) {
                if (s.length > 2 && s[2] != null) {
                    if (s[2].equalsIgnoreCase("Laki-laki")) laki++;
                    else if (s[2].equalsIgnoreCase("Perempuan")) perempuan++;
                }
            }
            int kelasCount = Database.getAllKelas().size();
            
            if (totalSiswaLabel != null) totalSiswaLabel.setText(String.valueOf(total));
            if (totalLakiLabel != null) totalLakiLabel.setText(String.valueOf(laki));
            if (totalPerempuanLabel != null) totalPerempuanLabel.setText(String.valueOf(perempuan));
            if (totalKelasLabel != null) totalKelasLabel.setText(String.valueOf(kelasCount));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void cariData(String keyword) {
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        tableModel.setRowCount(0);
        int no = 1;
        for (String[] siswa : Database.getAllSiswa()) {
            String nis = siswa.length > 0 ? siswa[0] : "";
            String nama = siswa.length > 1 ? siswa[1] : "";
            
            if (nis.toLowerCase().contains(keyword.toLowerCase()) || 
                nama.toLowerCase().contains(keyword.toLowerCase())) {
                
                tableModel.addRow(new Object[]{
                    no++, 
                    siswa[0], siswa[1], siswa[2], siswa[3], siswa[4],
                    siswa[5], siswa[6], siswa[7], siswa[8],
                    "Aktif"
                });
            }
        }
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Data tidak ditemukan!");
        }
    }
    
    private void loadKelas() {
        kelasCombo.removeAllItems();
        List<String[]> kelasList = Database.getAllKelas();
        if (kelasList.isEmpty()) {
            kelasCombo.addItem("-- Belum ada data kelas --");
        } else {
            for (String[] kelas : kelasList) {
                kelasCombo.addItem(kelas[1]);
            }
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        int no = 1;
        for (String[] siswa : Database.getAllSiswa()) {
            // siswa[0]=nis, siswa[1]=nama, siswa[2]=jk, siswa[3]=tempatLahir, siswa[4]=tglLahir,
            // siswa[5]=agama, siswa[6]=alamat, siswa[7]=kelas, siswa[8]=tahunMasuk
            tableModel.addRow(new Object[]{
                no++, 
                siswa[0], siswa[1], siswa[2], siswa[3], siswa[4],
                siswa[5], siswa[6], siswa[7], siswa[8],
                "Aktif"
            });
        }
        updateTopStats();
    }
    
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    
    private void tambahData() {
        String nis = nisField.getText().trim();
        String nama = namaField.getText().trim();
        String jk = (String) jkCombo.getSelectedItem();
        String tempatLahir = tempatLahirField.getText().trim();
        String tglLahir = formatDate((Date) tglLahirSpinner.getValue());
        String agama = (String) agamaCombo.getSelectedItem();
        String alamat = alamatField.getText().trim();
        String kelas = (String) kelasCombo.getSelectedItem();
        String tahunMasuk = tahunMasukField.getText().trim();
        
        if (nis.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIS dan Nama harus diisi!");
            return;
        }
        
        if (Database.getSiswaByNIS(nis) != null) {
            JOptionPane.showMessageDialog(this, "NIS sudah ada!");
            return;
        }
        
        if (Database.tambahSiswa(nis, nama, jk, tempatLahir, tglLahir, agama, alamat, kelas, tahunMasuk)) {
            loadData();
            resetForm();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data!");
        }
    }
    
    private void ubahData() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan diubah!");
            return;
        }
        
        String nisLama = getVal(row, 1);
        String nisBaru = nisField.getText().trim();
        String nama = namaField.getText().trim();
        String jk = (String) jkCombo.getSelectedItem();
        String tempatLahir = tempatLahirField.getText().trim();
        String tglLahir = formatDate((Date) tglLahirSpinner.getValue());
        String agama = (String) agamaCombo.getSelectedItem();
        String alamat = alamatField.getText().trim();
        String kelas = (String) kelasCombo.getSelectedItem();
        String tahunMasuk = tahunMasukField.getText().trim();
        
        if (nisBaru.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIS dan Nama harus diisi!");
            return;
        }
        
        if (Database.updateSiswa(nisLama, nisBaru, nama, jk, tempatLahir, tglLahir, agama, alamat, kelas, tahunMasuk)) {
            loadData();
            JOptionPane.showMessageDialog(this, "Data berhasil diubah!");
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengubah data!");
        }
    }
    
    private void hapusData() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?\nSemua data terkait (nilai, absensi, pembayaran) juga akan terhapus!", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String nis = getVal(row, 1);
            if (Database.hapusSiswa(nis)) {
                loadData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Data dihapus!");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data!");
            }
        }
    }
    
    private void resetForm() {
        nisField.setText("");
        namaField.setText("");
        jkCombo.setSelectedIndex(0);
        tempatLahirField.setText("");
        tglLahirSpinner.setValue(new Date());
        agamaCombo.setSelectedIndex(0);
        alamatField.setText("");
        tahunMasukField.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        statusCombo.setSelectedIndex(0);
        nisField.requestFocus();
    }
}