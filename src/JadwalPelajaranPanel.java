import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class JadwalPelajaranPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    
    private JComboBox<String> hariCombo;
    private JTextField jamMulaiField, jamSelesaiField;
    private JComboBox<String> kelasCombo, guruCombo;
    private JTextField mapelField;
    private JComboBox<String> tahunAjaranCombo, semesterCombo;
    private JButton tambahBtn, ubahBtn, hapusBtn, refreshBtn, backBtn, clearBtn;
    
    private int selectedId = -1;
    
    public JadwalPelajaranPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        
        if (!Session.isAdmin()) {
            JOptionPane.showMessageDialog(null, "⛔ Fitur JADWAL PELAJARAN hanya untuk Administrator!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            cardLayout.show(mainPanel, "menuUtama");
            return;
        }
        
        initComponents();
        loadKelas();
        loadGuru();
        loadData();
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
        
        JLabel formTitle = new JLabel("📅 FORM JADWAL PELAJARAN");
        formTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        formTitle.setForeground(new Color(41, 128, 185));
        formPanel.add(formTitle, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        gridPanel.add(new JLabel("Hari:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.2;
        hariCombo = new JComboBox<>(new String[]{"Senin", "Selasa", "Rabu", "Kamis", "Jumat"});
        gridPanel.add(hariCombo, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.1;
        gridPanel.add(new JLabel("Jam Mulai:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.2;
        jamMulaiField = new JTextField();
        jamMulaiField.setToolTipText("Format: HH:mm (contoh 07:30)");
        gridPanel.add(jamMulaiField, gbc);
        
        gbc.gridx = 4; gbc.weightx = 0.1;
        gridPanel.add(new JLabel("Jam Selesai:"), gbc);
        gbc.gridx = 5; gbc.weightx = 0.2;
        jamSelesaiField = new JTextField();
        jamSelesaiField.setToolTipText("Format: HH:mm (contoh 09:00)");
        gridPanel.add(jamSelesaiField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gridPanel.add(new JLabel("Kelas:"), gbc);
        gbc.gridx = 1;
        kelasCombo = new JComboBox<>();
        gridPanel.add(kelasCombo, gbc);
        
        gbc.gridx = 2;
        gridPanel.add(new JLabel("Guru:"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 3;
        guruCombo = new JComboBox<>();
        gridPanel.add(guruCombo, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 2;
        gridPanel.add(new JLabel("Mapel:"), gbc);
        gbc.gridx = 1;
        mapelField = new JTextField();
        gridPanel.add(mapelField, gbc);
        
        gbc.gridx = 2;
        gridPanel.add(new JLabel("Tahun Ajaran:"), gbc);
        gbc.gridx = 3;
        tahunAjaranCombo = new JComboBox<>();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int thnSkr = cal.get(java.util.Calendar.YEAR);
        int blnSkr = cal.get(java.util.Calendar.MONTH) + 1;
        int thnMulai = thnSkr;
        if (blnSkr < 7) thnMulai = thnSkr - 1;
        String ta0 = (thnMulai - 1) + "/" + thnMulai;
        String ta1 = thnMulai + "/" + (thnMulai + 1);
        String ta2 = (thnMulai + 1) + "/" + (thnMulai + 2);
        String ta3 = (thnMulai + 2) + "/" + (thnMulai + 3);
        tahunAjaranCombo.addItem(ta0);
        tahunAjaranCombo.addItem(ta1);
        tahunAjaranCombo.addItem(ta2);
        tahunAjaranCombo.addItem(ta3);
        tahunAjaranCombo.setSelectedItem(ta1);
        gridPanel.add(tahunAjaranCombo, gbc);
        
        gbc.gridx = 4;
        gridPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 5;
        semesterCombo = new JComboBox<>(new String[]{"Ganjil", "Genap"});
        gridPanel.add(semesterCombo, gbc);
        
        formPanel.add(gridPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        tambahBtn = createBtn("➕ TAMBAH", new Color(46, 204, 113));
        ubahBtn = createBtn("✏️ UBAH", new Color(52, 152, 219));
        hapusBtn = createBtn("🗑️ HAPUS", new Color(231, 76, 60));
        clearBtn = createBtn("🔄 CLEAR", new Color(108, 117, 125));
        refreshBtn = createBtn("🔄 REFRESH", new Color(52, 152, 219));
        backBtn = createBtn("◀ KEMBALI", new Color(52, 73, 94));
        
        btnPanel.add(tambahBtn);
        btnPanel.add(ubahBtn);
        btnPanel.add(hapusBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(backBtn);
        
        formPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(formPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel tableTitle = new JLabel("📋 DAFTAR JADWAL PELAJARAN");
        tableTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        tableTitle.setForeground(new Color(41, 128, 185));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        String[] cols = {"ID", "Hari", "Jam Mulai", "Jam Selesai", "Kelas", "Mapel", "NUPTK Guru", "Nama Guru", "Tahun Ajaran", "Semester"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        int[] widths = {50, 70, 80, 80, 100, 150, 100, 180, 100, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(Color.WHITE);
        
        JScrollPane scroll = new JScrollPane(table);
        tablePanel.add(scroll, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
        
        tambahBtn.addActionListener(e -> tambahData());
        ubahBtn.addActionListener(e -> ubahData());
        hapusBtn.addActionListener(e -> hapusData());
        clearBtn.addActionListener(e -> clearForm());
        refreshBtn.addActionListener(e -> { loadKelas(); loadGuru(); loadData(); });
        backBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "menuUtama");
            for (Component c : mainPanel.getComponents()) {
                if (c instanceof MenuUtamaPanel) {
                    ((MenuUtamaPanel) c).showDashboard();
                    break;
                }
            }
        });
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    hariCombo.setSelectedItem(tableModel.getValueAt(row, 1).toString());
                    jamMulaiField.setText(tableModel.getValueAt(row, 2).toString());
                    jamSelesaiField.setText(tableModel.getValueAt(row, 3).toString());
                    kelasCombo.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                    mapelField.setText(tableModel.getValueAt(row, 5).toString());
                    
                    String nuptk = tableModel.getValueAt(row, 6).toString();
                    for (int i = 0; i < guruCombo.getItemCount(); i++) {
                        if (guruCombo.getItemAt(i).startsWith(nuptk)) {
                            guruCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    tahunAjaranCombo.setSelectedItem(tableModel.getValueAt(row, 8).toString());
                    semesterCombo.setSelectedItem(tableModel.getValueAt(row, 9).toString());
                }
            }
        });
    }
    
    private JButton createBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
    
    private void loadKelas() {
        kelasCombo.removeAllItems();
        List<String[]> list = Database.getAllKelas();
        if (list.isEmpty()) {
            kelasCombo.addItem("-- Belum ada kelas --");
        } else {
            for (String[] k : list) kelasCombo.addItem(k[1]);
        }
    }
    
    private void loadGuru() {
        guruCombo.removeAllItems();
        List<String[]> list = Database.getAllGuru();
        if (list.isEmpty()) {
            guruCombo.addItem("-- Belum ada guru --");
        } else {
            for (String[] g : list) guruCombo.addItem(g[0] + " - " + g[2]);
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        for (Object[] j : Database.getAllJadwal()) {
            tableModel.addRow(j);
        }
    }
    
    private void clearForm() {
        selectedId = -1;
        hariCombo.setSelectedIndex(0);
        jamMulaiField.setText("");
        jamSelesaiField.setText("");
        if (kelasCombo.getItemCount() > 0) kelasCombo.setSelectedIndex(0);
        if (guruCombo.getItemCount() > 0) guruCombo.setSelectedIndex(0);
        mapelField.setText("");
        semesterCombo.setSelectedIndex(0);
        table.clearSelection();
    }
    
    private String[] extractGuru() {
        String sel = (String) guruCombo.getSelectedItem();
        if (sel == null || sel.startsWith("--")) return new String[]{"", ""};
        String[] parts = sel.split(" - ", 2);
        return new String[]{parts[0], parts.length > 1 ? parts[1] : ""};
    }
    
    private void tambahData() {
        String hari = (String) hariCombo.getSelectedItem();
        String jamMulai = jamMulaiField.getText().trim();
        String jamSelesai = jamSelesaiField.getText().trim();
        String kelas = (String) kelasCombo.getSelectedItem();
        String mapel = mapelField.getText().trim();
        String[] guru = extractGuru();
        
        if (jamMulai.isEmpty() || jamSelesai.isEmpty() || mapel.isEmpty() || guru[0].isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return;
        }
        
        if (!jamMulai.matches("\\d{2}:\\d{2}") || !jamSelesai.matches("\\d{2}:\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Format jam harus HH:mm (contoh 07:30)");
            return;
        }
        
        if (jamMulai.compareTo(jamSelesai) >= 0) {
            JOptionPane.showMessageDialog(this, "Jam selesai harus lebih besar dari jam mulai!");
            return;
        }
        
        if (Database.cekBentrokJadwal(hari, jamMulai, jamSelesai, kelas, guru[0], -1)) {
            JOptionPane.showMessageDialog(this, "⚠️ BENTROK! Kelas atau guru sudah punya jadwal di jam ini pada hari " + hari);
            return;
        }
        
        if (Database.tambahJadwal(hari, jamMulai, jamSelesai, kelas, mapel, guru[0], guru[1],
                (String) tahunAjaranCombo.getSelectedItem(), (String) semesterCombo.getSelectedItem())) {
            loadData();
            clearForm();
            JOptionPane.showMessageDialog(this, "✅ Jadwal berhasil ditambahkan!");
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal menambahkan jadwal!");
        }
    }
    
    private void ubahData() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih jadwal yang akan diubah!");
            return;
        }
        
        String hari = (String) hariCombo.getSelectedItem();
        String jamMulai = jamMulaiField.getText().trim();
        String jamSelesai = jamSelesaiField.getText().trim();
        String kelas = (String) kelasCombo.getSelectedItem();
        String mapel = mapelField.getText().trim();
        String[] guru = extractGuru();
        
        if (jamMulai.isEmpty() || jamSelesai.isEmpty() || mapel.isEmpty() || guru[0].isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return;
        }
        
        if (!jamMulai.matches("\\d{2}:\\d{2}") || !jamSelesai.matches("\\d{2}:\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Format jam harus HH:mm");
            return;
        }
        
        if (jamMulai.compareTo(jamSelesai) >= 0) {
            JOptionPane.showMessageDialog(this, "Jam selesai harus lebih besar dari jam mulai!");
            return;
        }
        
        if (Database.cekBentrokJadwal(hari, jamMulai, jamSelesai, kelas, guru[0], selectedId)) {
            JOptionPane.showMessageDialog(this, "⚠️ BENTROK! Kelas atau guru sudah punya jadwal di jam ini!");
            return;
        }
        
        if (Database.updateJadwal(selectedId, hari, jamMulai, jamSelesai, kelas, mapel, guru[0], guru[1],
                (String) tahunAjaranCombo.getSelectedItem(), (String) semesterCombo.getSelectedItem())) {
            loadData();
            clearForm();
            JOptionPane.showMessageDialog(this, "✅ Jadwal berhasil diubah!");
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal mengubah jadwal!");
        }
    }
    
    private void hapusData() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih jadwal yang akan dihapus!");
            return;
        }
        
        int c = JOptionPane.showConfirmDialog(this, "Yakin hapus jadwal ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            if (Database.hapusJadwal(selectedId)) {
                loadData();
                clearForm();
                JOptionPane.showMessageDialog(this, "✅ Jadwal dihapus!");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Gagal menghapus!");
            }
        }
    }
}