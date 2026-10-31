import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AbsensiPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> kelasCombo;
    private JTextField tanggalField;
    private JButton simpanBtn, refreshBtn, backBtn, tampilBtn;
    
    public AbsensiPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initComponents();
        loadKelas();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 242, 245));
        
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel filterTitle = new JLabel("📋 FORM ABSENSI SISWA");
        filterTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        filterTitle.setForeground(new Color(41, 128, 185));
        filterTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(filterTitle);
        filterPanel.add(Box.createVerticalStrut(15));
        
        JPanel filterInputPanel = new JPanel(new GridBagLayout());
        filterInputPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        filterInputPanel.add(new JLabel("Kelas:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.4;
        kelasCombo = new JComboBox<>();
        kelasCombo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        filterInputPanel.add(kelasCombo, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.1;
        filterInputPanel.add(new JLabel("Tanggal:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.4;
        tanggalField = new JTextField(15);
        tanggalField.setText(getCurrentDate());
        tanggalField.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        filterInputPanel.add(tanggalField, gbc);
        
        gbc.gridx = 4; gbc.weightx = 0.2;
        tampilBtn = new JButton("📋 TAMPILKAN SISWA");
        tampilBtn.setBackground(new Color(52, 152, 219));
        tampilBtn.setForeground(Color.WHITE);
        tampilBtn.setFocusPainted(false);
        tampilBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        tampilBtn.addActionListener(e -> loadSiswaByKelas());
        filterInputPanel.add(tampilBtn, gbc);
        
        filterPanel.add(filterInputPanel);
        filterPanel.add(Box.createVerticalStrut(10));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        simpanBtn = new JButton("💾 SIMPAN ABSENSI");
        simpanBtn.setBackground(new Color(46, 204, 113));
        simpanBtn.setForeground(Color.WHITE);
        simpanBtn.setFocusPainted(false);
        simpanBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        
        refreshBtn = new JButton("🔄 REFRESH");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        
        backBtn = new JButton("◀ KEMBALI");
        backBtn.setBackground(new Color(52, 73, 94));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        
        btnPanel.add(simpanBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(backBtn);
        
        filterPanel.add(btnPanel);
        
        add(filterPanel, BorderLayout.NORTH);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel tableTitle = new JLabel("📊 DAFTAR SISWA");
        tableTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        tableTitle.setForeground(new Color(41, 128, 185));
        tableContainer.add(tableTitle, BorderLayout.NORTH);
        
        String[] columns = {"No", "NIS", "Nama Siswa", "Kelas", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 4;
            }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        TableColumn statusColumn = table.getColumnModel().getColumn(4);
        // HAPUS "Tidak Hadir" agar tidak error di database ENUM / VARCHAR
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Hadir", "Sakit", "Izin", "Alpa"});
        statusColumn.setCellEditor(new DefaultCellEditor(statusCombo));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 35));
        
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        tableContainer.add(tableScrollPane, BorderLayout.CENTER);
        
        add(tableContainer, BorderLayout.CENTER);
        
        simpanBtn.addActionListener(e -> simpanAbsensi());
        refreshBtn.addActionListener(e -> {
            loadKelas();
            loadSiswaByKelas();
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
    
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
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
    
    private void loadSiswaByKelas() {
        if (kelasCombo.getSelectedIndex() == -1 || kelasCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Pilih kelas terlebih dahulu!");
            return;
        }
        
        String selectedKelas = (String) kelasCombo.getSelectedItem();
        if (selectedKelas.equals("-- Belum ada data kelas --")) {
            JOptionPane.showMessageDialog(this, "Silakan input data kelas terlebih dahulu!");
            return;
        }
        
        String tanggal = tanggalField.getText().trim();
        
        tableModel.setRowCount(0);
        int no = 1;
        
        List<String[]> siswaList = Database.getSiswaByKelas(selectedKelas);
        if (siswaList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada siswa di kelas " + selectedKelas);
            return;
        }
        
        for (String[] siswa : siswaList) {
            String nis = siswa[0];
            String nama = siswa[1];
            String kelas = siswa[7]; // Index kelas sekarang 7
            
            String status = "Hadir"; // Default hadir (bukan "-")
            
            for (Object[] absen : Database.getAllAbsensi()) {
                if (absen[0].toString().equals(tanggal) && 
                    absen[2].toString().equals(nis)) {
                    status = absen[4].toString();
                    break;
                }
            }
            
            tableModel.addRow(new Object[]{no++, nis, nama, kelas, status});
        }
    }
    
    private void simpanAbsensi() {
        // PERBAIKAN: Hentikan edit tabel dulu sebelum ambil data
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        
        String tanggal = tanggalField.getText().trim();
        String kelas = (String) kelasCombo.getSelectedItem();
        
        if (tanggal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan tanggal absensi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (kelas == null || kelas.equals("-- Belum ada data kelas --")) {
            JOptionPane.showMessageDialog(this, "Pilih kelas terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data siswa! Klik TAMPILKAN SISWA terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Database.hapusAbsensiByTanggalDanKelas(tanggal, kelas);
        
        int saved = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nis = tableModel.getValueAt(i, 1).toString();
            String nama = tableModel.getValueAt(i, 2).toString();
            String status = tableModel.getValueAt(i, 4).toString();
            
            if (status.isEmpty()) {
                status = "Alpa"; // Ganti dari "Tidak Hadir" ke "Alpa"
            }
            
            if (Database.simpanAbsensi(tanggal, kelas, nis, nama, status)) {
                saved++;
            }
        }
        
        JOptionPane.showMessageDialog(this, "✅ Absensi tanggal " + tanggal + " untuk kelas " + kelas + " berhasil disimpan!\nTotal " + saved + " siswa.");
    }
}