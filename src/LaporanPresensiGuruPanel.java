import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LaporanPresensiGuruPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> guruCombo;
    private JTextField dariField, sampaiField;
    private JButton filterBtn, refreshBtn, backBtn;
    private JLabel infoLabel;
    
    public LaporanPresensiGuruPanel(CardLayout cardLayout, JPanel mainPanel) {
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
        
        JLabel title = new JLabel("📋 LAPORAN PRESENSI KEHADIRAN GURU");
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        title.setForeground(new Color(41, 128, 185));
        filterPanel.add(title, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        
        inputPanel.add(new JLabel("Guru:"));
        guruCombo = new JComboBox<>();
        guruCombo.setPreferredSize(new Dimension(200, 28));
        inputPanel.add(guruCombo);
        
        inputPanel.add(new JLabel("Dari:"));
        dariField = new JTextField(10);
        dariField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        inputPanel.add(dariField);
        
        inputPanel.add(new JLabel("Sampai:"));
        sampaiField = new JTextField(10);
        sampaiField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        inputPanel.add(sampaiField);
        
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
        
        String[] cols = {"ID", "Tanggal", "Kode Guru", "Nama Guru", "Status", "Keterangan", "Dicatat Oleh"};
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
            data = Database.getAllPresensiGuru();
        } else if (sel != null && !sel.startsWith("--")) {
            String kodeGuru = sel.split(" - ")[0];
            data = Database.getPresensiGuruByKode(kodeGuru);
        } else {
            data = Database.getAllPresensiGuru();
        }
        
        for (Object[] p : data) {
            tableModel.addRow(p);
        }
        
        infoLabel.setText("Total: " + tableModel.getRowCount() + " data presensi");
    }
    
    private void filterData() {
        tableModel.setRowCount(0);
        
        String sel = (String) guruCombo.getSelectedItem();
        String dari = dariField.getText().trim();
        String sampai = sampaiField.getText().trim();
        
        List<Object[]> data;
        if (sel != null && sel.startsWith("-- Semua")) {
            data = Database.getAllPresensiGuru();
        } else if (sel != null && !sel.startsWith("--")) {
            String kodeGuru = sel.split(" - ")[0];
            data = Database.getPresensiGuruByKode(kodeGuru);
        } else {
            data = Database.getAllPresensiGuru();
        }
        
        int count = 0;
        for (Object[] p : data) {
            String tgl = p[1].toString();
            if (tgl.compareTo(dari) >= 0 && tgl.compareTo(sampai) <= 0) {
                tableModel.addRow(p);
                count++;
            }
        }
        
        infoLabel.setText("Total: " + count + " data (Periode: " + dari + " s/d " + sampai + ")");
    }
}