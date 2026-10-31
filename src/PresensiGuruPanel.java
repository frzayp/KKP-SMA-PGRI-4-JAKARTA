import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PresensiGuruPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    
    private JTextField tanggalField, keteranganField;
    private JComboBox<String> guruCombo, statusCombo;
    private JButton simpanBtn, hapusBtn, refreshBtn, backBtn, clearBtn;
    
    private int selectedId = -1;
    
    public PresensiGuruPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        
        if (!Session.isAdmin()) {
            JOptionPane.showMessageDialog(null, "⛔ Fitur PRESENSI GURU hanya untuk Administrator!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            cardLayout.show(mainPanel, "menuUtama");
            return;
        }
        
        initComponents();
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
        
        JLabel formTitle = new JLabel("📝 FORM PRESENSI GURU (Diisi oleh Admin)");
        formTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        formTitle.setForeground(new Color(41, 128, 185));
        formPanel.add(formTitle, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        gridPanel.add(new JLabel("Tanggal:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        tanggalField = new JTextField();
        tanggalField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        gridPanel.add(tanggalField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.15;
        gridPanel.add(new JLabel("Guru:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.35;
        guruCombo = new JComboBox<>();
        gridPanel.add(guruCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gridPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Hadir", "Sakit", "Izin", "Alpa", "Tugas Luar", "Cuti"});
        gridPanel.add(statusCombo, gbc);
        
        gbc.gridx = 2;
        gridPanel.add(new JLabel("Keterangan:"), gbc);
        gbc.gridx = 3;
        keteranganField = new JTextField();
        gridPanel.add(keteranganField, gbc);
        
        formPanel.add(gridPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        simpanBtn = createBtn("💾 SIMPAN", new Color(46, 204, 113));
        hapusBtn = createBtn("🗑️ HAPUS", new Color(231, 76, 60));
        clearBtn = createBtn("🔄 CLEAR", new Color(108, 117, 125));
        refreshBtn = createBtn("🔄 REFRESH", new Color(52, 152, 219));
        backBtn = createBtn("◀ KEMBALI", new Color(52, 73, 94));
        
        btnPanel.add(simpanBtn);
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
        
        JLabel tableTitle = new JLabel("📋 DAFTAR PRESENSI GURU");
        tableTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        tableTitle.setForeground(new Color(41, 128, 185));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        String[] cols = {"ID", "Tanggal", "NUPTK", "Nama Guru", "Status", "Keterangan", "Dicatat Oleh"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        int[] widths = {50, 100, 100, 200, 100, 200, 150};
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
        
        simpanBtn.addActionListener(e -> simpanData());
        hapusBtn.addActionListener(e -> hapusData());
        clearBtn.addActionListener(e -> clearForm());
        refreshBtn.addActionListener(e -> { loadGuru(); loadData(); });
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
                    tanggalField.setText(tableModel.getValueAt(row, 1).toString());
                    
                    String nuptk = tableModel.getValueAt(row, 2).toString();
                    for (int i = 0; i < guruCombo.getItemCount(); i++) {
                        if (guruCombo.getItemAt(i).startsWith(nuptk)) {
                            guruCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    statusCombo.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                    keteranganField.setText(tableModel.getValueAt(row, 5).toString());
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
        for (Object[] p : Database.getAllPresensiGuru()) {
            tableModel.addRow(p);
        }
    }
    
    private void clearForm() {
        selectedId = -1;
        tanggalField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        if (guruCombo.getItemCount() > 0) guruCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        keteranganField.setText("");
        table.clearSelection();
    }
    
    private void simpanData() {
        String tanggal = tanggalField.getText().trim();
        String sel = (String) guruCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();
        String keterangan = keteranganField.getText().trim();
        
        if (tanggal.isEmpty() || sel == null || sel.startsWith("--")) {
            JOptionPane.showMessageDialog(this, "Tanggal dan Guru harus dipilih!");
            return;
        }
        
        if (!tanggal.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Format tanggal harus yyyy-MM-dd (contoh 2026-10-05)");
            return;
        }
        
        String[] parts = sel.split(" - ", 2);
        String nuptk = parts[0];
        String nama = parts.length > 1 ? parts[1] : "";
        
        String dicatatOleh = Session.getNama() != null ? Session.getNama() : "Admin";
        
        if (Database.simpanPresensiGuru(tanggal, nuptk, nama, status, keterangan, dicatatOleh)) {
            loadData();
            clearForm();
            JOptionPane.showMessageDialog(this, "✅ Presensi berhasil disimpan!");
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal menyimpan presensi!");
        }
    }
    
    private void hapusData() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!");
            return;
        }
        
        int c = JOptionPane.showConfirmDialog(this, "Yakin hapus data presensi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            if (Database.hapusPresensiGuru(selectedId)) {
                loadData();
                clearForm();
                JOptionPane.showMessageDialog(this, "✅ Data dihapus!");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Gagal menghapus!");
            }
        }
    }
}