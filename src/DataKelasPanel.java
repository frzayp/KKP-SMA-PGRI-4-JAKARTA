import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class DataKelasPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField idField, namaField;
    private JComboBox<String> waliCombo, tingkatCombo;
    private JButton tambahBtn, ubahBtn, hapusBtn, refreshBtn, backBtn;
    private JTextField searchField;
    private JPanel topStatsPanel;
    
    private JLabel totalKelasLabel;
    private JLabel kelasXLabel;
    private JLabel kelasXILabel;
    private JLabel kelasXIILabel;
    
    public DataKelasPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        
        if (!Session.isAdmin()) {
            JOptionPane.showMessageDialog(null, "⛔ Fitur DATA KELAS hanya dapat diakses oleh Administrator!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            cardLayout.show(mainPanel, "menuUtama");
            return;
        }
        
        initComponents();
        loadData();
        loadWaliKelas();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 242, 245));
        
        topStatsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        topStatsPanel.setOpaque(false);
        topStatsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JPanel card1 = createStatCard("🏫", "TOTAL KELAS", "0", "Kelas Aktif");
        totalKelasLabel = (JLabel) card1.getClientProperty("valueLabel");
        topStatsPanel.add(card1);
        
        JPanel card2 = createStatCard("🔬", "KELAS X", "0", "Kelas");
        kelasXLabel = (JLabel) card2.getClientProperty("valueLabel");
        topStatsPanel.add(card2);
        
        JPanel card3 = createStatCard("🏛️", "KELAS XI", "0", "Kelas");
        kelasXILabel = (JLabel) card3.getClientProperty("valueLabel");
        topStatsPanel.add(card3);
        
        JPanel card4 = createStatCard("📖", "KELAS XII", "0", "Kelas");
        kelasXIILabel = (JLabel) card4.getClientProperty("valueLabel");
        topStatsPanel.add(card4);
        
        add(topStatsPanel, BorderLayout.NORTH);
        
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplit.setResizeWeight(0.35);
        mainSplit.setDividerSize(8);
        mainSplit.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel formTitle = new JLabel("FORM DATA KELAS");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(new Color(41, 128, 185));
        formContainer.add(formTitle, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        formPanel.add(new JLabel("ID Kelas:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        idField = new JTextField();
        formPanel.add(idField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Nama Kelas:"), gbc);
        gbc.gridx = 1;
        namaField = new JTextField();
        formPanel.add(namaField, gbc);
        
        // FIELD TINGKAT (Ganti Jurusan)
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tingkat:"), gbc);
        gbc.gridx = 1;
        tingkatCombo = new JComboBox<>(new String[]{"X", "XI", "XII"});
        tingkatCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(tingkatCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Wali Kelas:"), gbc);
        gbc.gridx = 1;
        waliCombo = new JComboBox<>();
        waliCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(waliCombo, gbc);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        
        tambahBtn = createStyledButton("TAMBAH", new Color(46, 204, 113));
        ubahBtn = createStyledButton("UBAH", new Color(52, 152, 219));
        hapusBtn = createStyledButton("HAPUS", new Color(231, 76, 60));
        refreshBtn = createStyledButton("REFRESH", new Color(52, 152, 219));
        backBtn = createStyledButton("KEMBALI", new Color(52, 73, 94));
        
        btnPanel.add(tambahBtn);
        btnPanel.add(ubahBtn);
        btnPanel.add(hapusBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(backBtn);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        
        JScrollPane formScrollPane = new JScrollPane(formPanel);
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
        
        searchPanel.add(new JLabel("Cari (ID Kelas/Nama Kelas):"));
        searchField = new JTextField(25);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cariData(searchField.getText().trim());
            }
        });
        searchPanel.add(searchField);
        
        JButton cariBtn = new JButton("CARI");
        cariBtn.setBackground(new Color(52, 152, 219));
        cariBtn.setForeground(Color.WHITE);
        cariBtn.addActionListener(e -> cariData(searchField.getText().trim()));
        searchPanel.add(cariBtn);
        
        JButton resetCariBtn = new JButton("RESET");
        resetCariBtn.setBackground(new Color(108, 117, 125));
        resetCariBtn.setForeground(Color.WHITE);
        resetCariBtn.addActionListener(e -> {
            searchField.setText("");
            loadData();
        });
        searchPanel.add(resetCariBtn);
        
        tableContainer.add(searchPanel, BorderLayout.NORTH);
        
        String[] columns = {"No", "ID Kelas", "Nama Kelas", "Tingkat", "Wali Kelas"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
            loadWaliKelas();
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
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    idField.setText(tableModel.getValueAt(row, 1).toString());
                    namaField.setText(tableModel.getValueAt(row, 2).toString());
                    tingkatCombo.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                    waliCombo.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                }
            }
        });
        
        updateTopStats();
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
                if (title.contains("TOTAL KELAS")) {
                    startColor = new Color(52, 152, 219);
                    endColor = new Color(41, 128, 185);
                } else if (title.contains("KELAS X")) {
                    startColor = new Color(46, 204, 113);
                    endColor = new Color(39, 174, 96);
                } else if (title.contains("KELAS XI")) {
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
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(new Color(255, 255, 255, 220));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(defaultValue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
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
            java.util.List<String[]> kelasList = Database.getAllKelas();
            int total = kelasList.size();
            int x = 0, xi = 0, xii = 0;
            
            for (String[] k : kelasList) {
                String nama = k[1].toUpperCase();
                if (nama.startsWith("X ") || nama.startsWith("X-") || nama.equals("X") || nama.contains(" X ")) x++;
                else if (nama.startsWith("XI ") || nama.startsWith("XI-") || nama.equals("XI")) xi++;
                else if (nama.startsWith("XII ") || nama.startsWith("XII-") || nama.equals("XII")) xii++;
            }
            
            if (totalKelasLabel != null) totalKelasLabel.setText(String.valueOf(total));
            if (kelasXLabel != null) kelasXLabel.setText(String.valueOf(x));
            if (kelasXILabel != null) kelasXILabel.setText(String.valueOf(xi));
            if (kelasXIILabel != null) kelasXIILabel.setText(String.valueOf(xii));
        } catch (Exception e) {}
    }
    
    private void loadWaliKelas() {
        waliCombo.removeAllItems();
        java.util.List<String[]> guruList = Database.getAllGuru();
        if (guruList.isEmpty()) {
            waliCombo.addItem("-- Belum ada data guru --");
        } else {
            for (String[] guru : guruList) {
                waliCombo.addItem(guru[2]);
            }
        }
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void cariData(String keyword) {
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        tableModel.setRowCount(0);
        int no = 1;
        for (String[] kelas : Database.getAllKelas()) {
            if (kelas[0].toLowerCase().contains(keyword.toLowerCase()) || 
                kelas[1].toLowerCase().contains(keyword.toLowerCase())) {
                
                // Ambil tingkat dari nama kelas (X, XI, XII)
                String nama = kelas[1].toUpperCase();
                String tingkat = "X";
                if (nama.startsWith("XII")) tingkat = "XII";
                else if (nama.startsWith("XI")) tingkat = "XI";
                else if (nama.startsWith("X")) tingkat = "X";
                
                tableModel.addRow(new Object[]{no++, kelas[0], kelas[1], tingkat, kelas[2]});
            }
        }
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Data tidak ditemukan!");
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        int no = 1;
        for (String[] kelas : Database.getAllKelas()) {
            // Ambil tingkat dari nama kelas (X, XI, XII)
            String nama = kelas[1].toUpperCase();
            String tingkat = "X";
            if (nama.startsWith("XII")) tingkat = "XII";
            else if (nama.startsWith("XI")) tingkat = "XI";
            else if (nama.startsWith("X")) tingkat = "X";
            
            tableModel.addRow(new Object[]{no++, kelas[0], kelas[1], tingkat, kelas[2]});
        }
        updateTopStats();
    }
    
    private void tambahData() {
        if (Database.getAllGuru().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Input data GURU terlebih dahulu!");
            return;
        }
        
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        String wali = (String) waliCombo.getSelectedItem();
        
        if (id.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Kelas dan Nama Kelas harus diisi!");
            return;
        }
        
        if (wali == null || wali.equals("-- Belum ada data guru --")) {
            JOptionPane.showMessageDialog(this, "Pilih Wali Kelas terlebih dahulu!");
            return;
        }
        
        for (String[] k : Database.getAllKelas()) {
            if (k[0].equals(id)) {
                JOptionPane.showMessageDialog(this, "ID Kelas sudah ada!");
                return;
            }
        }
        
        if (Database.tambahKelas(id, nama, wali)) {
            loadData();
            resetForm();
            JOptionPane.showMessageDialog(this, "Data kelas berhasil ditambahkan!");
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
        
        String idLama = tableModel.getValueAt(row, 1).toString();
        String idBaru = idField.getText().trim();
        String nama = namaField.getText().trim();
        String wali = (String) waliCombo.getSelectedItem();
        
        if (idBaru.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Kelas dan Nama Kelas harus diisi!");
            return;
        }
        
        if (Database.updateKelas(idLama, idBaru, nama, wali)) {
            loadData();
            JOptionPane.showMessageDialog(this, "Data kelas berhasil diubah!");
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
        
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data kelas ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String id = tableModel.getValueAt(row, 1).toString();
            if (Database.hapusKelas(id)) {
                loadData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Data kelas dihapus!");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data!");
            }
        }
    }
    
    private void resetForm() {
        idField.setText("");
        namaField.setText("");
        tingkatCombo.setSelectedIndex(0);
        if (waliCombo.getItemCount() > 0 && !waliCombo.getItemAt(0).equals("-- Belum ada data guru --")) {
            waliCombo.setSelectedIndex(0);
        }
        idField.requestFocus();
    }
}