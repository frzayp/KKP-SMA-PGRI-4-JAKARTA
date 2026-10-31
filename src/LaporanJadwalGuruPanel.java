import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class LaporanJadwalGuruPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> guruCombo, hariCombo;
    private JButton filterBtn, refreshBtn, backBtn;
    private JLabel infoLabel;
    
    public LaporanJadwalGuruPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        
        if (!Session.isAdmin() && !Session.isGuru() && !Session.isKepsek()) {
            JOptionPane.showMessageDialog(null, "⛔ Fitur ini hanya untuk Admin, Guru, dan Kepala Sekolah!", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
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
        
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel title = new JLabel("📅 LAPORAN JADWAL MENGAJAR GURU");
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        title.setForeground(new Color(41, 128, 185));
        filterPanel.add(title, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        inputPanel.setBackground(Color.WHITE);
        
        inputPanel.add(new JLabel("Guru:"));
        guruCombo = new JComboBox<>();
        guruCombo.setPreferredSize(new Dimension(250, 28));
        inputPanel.add(guruCombo);
        
        inputPanel.add(new JLabel("Hari:"));
        hariCombo = new JComboBox<>(new String[]{"Semua", "Senin", "Selasa", "Rabu", "Kamis", "Jumat"});
        hariCombo.setPreferredSize(new Dimension(120, 28));
        inputPanel.add(hariCombo);
        
        filterBtn = new JButton("🔍 TAMPILKAN");
        filterBtn.setBackground(new Color(52, 152, 219));
        filterBtn.setForeground(Color.WHITE);
        filterBtn.setFocusPainted(false);
        filterBtn.addActionListener(e -> filterData());
        inputPanel.add(filterBtn);
        
        refreshBtn = new JButton("🔄 REFRESH");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> { loadGuru(); loadData(); });
        inputPanel.add(refreshBtn);
        
        backBtn = new JButton("◀ KEMBALI");
        backBtn.setBackground(new Color(52, 73, 94));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "menuUtama");
            for (Component c : mainPanel.getComponents()) {
                if (c instanceof MenuUtamaPanel) {
                    ((MenuUtamaPanel) c).showDashboard();
                    break;
                }
            }
        });
        inputPanel.add(backBtn);
        
        filterPanel.add(inputPanel, BorderLayout.CENTER);
        
        infoLabel = new JLabel(" ");
        infoLabel.setFont(new Font("Segoe UI Emoji", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        filterPanel.add(infoLabel, BorderLayout.SOUTH);
        
        add(filterPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        String[] cols = {"ID", "Hari", "Jam Mulai", "Jam Selesai", "Kelas", "Mapel", "Kode Guru", "Nama Guru", "Tahun Ajaran", "Semester"};
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
    }
    
    private void loadGuru() {
        guruCombo.removeAllItems();
        guruCombo.addItem("-- Semua Guru --");
        
        if (Session.isGuru()) {
            String kodeGuru = Session.getKodeGuru();
            String nama = Session.getNama();
            guruCombo.addItem(kodeGuru + " - " + nama);
            guruCombo.setSelectedIndex(1);
            guruCombo.setEnabled(false);
        } else {
            List<String[]> list = Database.getAllGuru();
            for (String[] g : list) {
                guruCombo.addItem(g[0] + " - " + g[2]);
            }
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Object[]> data;
        
        String sel = (String) guruCombo.getSelectedItem();
        if (sel != null && sel.startsWith("-- Semua")) {
            data = Database.getAllJadwal();
        } else if (sel != null && !sel.startsWith("--")) {
            String kodeGuru = sel.split(" - ")[0];
            data = Database.getJadwalByGuru(kodeGuru);
        } else {
            data = Database.getAllJadwal();
        }
        
        for (Object[] j : data) {
            tableModel.addRow(j);
        }
        
        infoLabel.setText("Total: " + tableModel.getRowCount() + " jadwal");
    }
    
    private void filterData() {
        tableModel.setRowCount(0);
        
        String sel = (String) guruCombo.getSelectedItem();
        String hari = (String) hariCombo.getSelectedItem();
        List<Object[]> data;
        
        if (sel != null && sel.startsWith("-- Semua")) {
            data = Database.getAllJadwal();
        } else if (sel != null && !sel.startsWith("--")) {
            String kodeGuru = sel.split(" - ")[0];
            data = Database.getJadwalByGuru(kodeGuru);
        } else {
            data = Database.getAllJadwal();
        }
        
        for (Object[] j : data) {
            String hariJadwal = j[1].toString();
            if (hari.equals("Semua") || hariJadwal.equals(hari)) {
                tableModel.addRow(j);
            }
        }
        
        infoLabel.setText("Total: " + tableModel.getRowCount() + " jadwal (Filter: " + hari + ")");
    }
}