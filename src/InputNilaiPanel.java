import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class InputNilaiPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField nisField, namaField, kelasField, tugasField, utsField, uasField, akhirField;
    private JComboBox<String> mapelCombo;
    private JButton cariBtn, hitungBtn, simpanBtn, resetBtn, refreshBtn, backBtn;
    private DefaultTableModel tableModel;
    private JTable table;
    private String currentNIS = "";
    private String currentGuruPengajar = "";
    
    public InputNilaiPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initComponents();
        loadDataNilai();
        loadMataPelajaranFromGuru();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 242, 245));
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel inputTitle = new JLabel("FORM INPUT NILAI (Cari pakai NIS)");
        inputTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        inputTitle.setForeground(new Color(41, 128, 185));
        inputPanel.add(inputTitle, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        gridPanel.add(new JLabel("NIS:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        nisField = new JTextField();
        nisField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(nisField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.2;
        cariBtn = new JButton("CARI SISWA");
        cariBtn.setBackground(new Color(52, 152, 219));
        cariBtn.setForeground(Color.WHITE);
        cariBtn.setFocusPainted(false);
        gridPanel.add(cariBtn, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gridPanel.add(new JLabel("Nama Siswa:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        namaField = new JTextField();
        namaField.setEditable(false);
        namaField.setBackground(new Color(240, 240, 240));
        namaField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(namaField, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 2;
        gridPanel.add(new JLabel("Kelas:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        kelasField = new JTextField();
        kelasField.setEditable(false);
        kelasField.setBackground(new Color(240, 240, 240));
        kelasField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(kelasField, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 3;
        gridPanel.add(new JLabel("Mata Pelajaran:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        mapelCombo = new JComboBox<>();
        mapelCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mapelCombo.addActionListener(e -> {
            if (mapelCombo.getSelectedIndex() != -1 && mapelCombo.getItemCount() > 0) {
                String selected = (String) mapelCombo.getSelectedItem();
                if (selected != null && selected.contains(" - ")) {
                    currentGuruPengajar = selected.split(" - ")[0];
                }
            }
        });
        gridPanel.add(mapelCombo, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 4;
        gridPanel.add(new JLabel("Nilai Tugas (50%):"), gbc);
        gbc.gridx = 1;
        tugasField = new JTextField();
        tugasField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(tugasField, gbc);
        gbc.gridx = 2;
        gridPanel.add(new JLabel("Nilai UTS (20%):"), gbc);
        gbc.gridx = 3;
        utsField = new JTextField();
        utsField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(utsField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gridPanel.add(new JLabel("Nilai UAS (30%):"), gbc);
        gbc.gridx = 1;
        uasField = new JTextField();
        uasField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(uasField, gbc);
        gbc.gridx = 2;
        gridPanel.add(new JLabel("Nilai Akhir:"), gbc);
        gbc.gridx = 3;
        akhirField = new JTextField();
        akhirField.setEditable(false);
        akhirField.setBackground(new Color(240, 240, 240));
        akhirField.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gridPanel.add(akhirField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(Color.WHITE);
        
        hitungBtn = createStyledButton("HITUNG NILAI", new Color(52, 152, 219));
        simpanBtn = createStyledButton("SIMPAN NILAI", new Color(46, 204, 113));
        resetBtn = createStyledButton("RESET", new Color(231, 76, 60));
        refreshBtn = createStyledButton("REFRESH", new Color(52, 152, 219));
        backBtn = createStyledButton("KEMBALI", new Color(52, 73, 94));
        
        btnPanel.add(hitungBtn);
        btnPanel.add(simpanBtn);
        btnPanel.add(resetBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(backBtn);
        
        gridPanel.add(btnPanel, gbc);
        
        inputPanel.add(gridPanel, BorderLayout.CENTER);
        
        add(inputPanel, BorderLayout.NORTH);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel tableTitle = new JLabel("RIWAYAT NILAI SISWA");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setForeground(new Color(41, 128, 185));
        tableContainer.add(tableTitle, BorderLayout.NORTH);
        
        String[] columns = {"NIS", "Nama Siswa", "Kelas", "Mata Pelajaran", "Guru", "Tugas", "UTS", "UAS", "Nilai Akhir"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(60);
        table.getColumnModel().getColumn(6).setPreferredWidth(60);
        table.getColumnModel().getColumn(7).setPreferredWidth(60);
        table.getColumnModel().getColumn(8).setPreferredWidth(80);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        add(tableContainer, BorderLayout.CENTER);
        
        nisField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) cariSiswa();
            }
        });
        
        cariBtn.addActionListener(e -> cariSiswa());
        hitungBtn.addActionListener(e -> hitungNilai());
        simpanBtn.addActionListener(e -> simpanNilai());
        resetBtn.addActionListener(e -> resetForm());
        refreshBtn.addActionListener(e -> {
            loadDataNilai();
            loadMataPelajaranFromGuru();
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
                    nisField.setText(tableModel.getValueAt(row, 0).toString());
                    namaField.setText(tableModel.getValueAt(row, 1).toString());
                    kelasField.setText(tableModel.getValueAt(row, 2).toString());
                    String mapel = tableModel.getValueAt(row, 3).toString();
                    String guru = tableModel.getValueAt(row, 4).toString();
                    if (!mapel.isEmpty() && !guru.isEmpty()) {
                        mapelCombo.setSelectedItem(guru + " - " + mapel);
                    }
                    tugasField.setText(tableModel.getValueAt(row, 5).toString());
                    utsField.setText(tableModel.getValueAt(row, 6).toString());
                    uasField.setText(tableModel.getValueAt(row, 7).toString());
                    akhirField.setText(tableModel.getValueAt(row, 8).toString());
                    currentNIS = tableModel.getValueAt(row, 0).toString();
                    currentGuruPengajar = guru;
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
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void loadMataPelajaranFromGuru() {
        mapelCombo.removeAllItems();
        List<String[]> guruList = Database.getAllMataPelajaranFromGuru();
        if (guruList.isEmpty()) {
            mapelCombo.addItem("Belum ada data guru - Silakan input guru dulu");
        } else {
            for (String[] guru : guruList) {
                mapelCombo.addItem(guru[0] + " - " + guru[1]);
            }
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
            // siswa[0]=nis, siswa[1]=nama, siswa[2]=jk, siswa[3]=tempatLahir,
            // siswa[4]=tglLahir, siswa[5]=agama, siswa[6]=alamat, siswa[7]=kelas, siswa[8]=tahunMasuk
            namaField.setText(siswa[1]);
            kelasField.setText(siswa[7]);  // <-- INDEX KELAS: 7
            currentNIS = nis;
            
            List<Object[]> nilaiList = Database.getAllNilai();
            boolean found = false;
            for (Object[] nilai : nilaiList) {
                if (nilai[0].toString().equals(nis)) {
                    tugasField.setText(nilai[4].toString());
                    utsField.setText(nilai[5].toString());
                    uasField.setText(nilai[6].toString());
                    akhirField.setText(nilai[7].toString());
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                tugasField.setText("");
                utsField.setText("");
                uasField.setText("");
                akhirField.setText("");
            }
            
            JOptionPane.showMessageDialog(this, "Siswa ditemukan: " + siswa[1] + " | Kelas: " + siswa[7]);
        } else {
            JOptionPane.showMessageDialog(this, "Siswa dengan NIS " + nis + " tidak ditemukan!");
            namaField.setText("");
            kelasField.setText("");
            currentNIS = "";
        }
    }
    
    private void hitungNilai() {
        if (currentNIS.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cari siswa terlebih dahulu!");
            return;
        }
        
        try {
            double tugas = tugasField.getText().isEmpty() ? 0 : Double.parseDouble(tugasField.getText());
            double uts = utsField.getText().isEmpty() ? 0 : Double.parseDouble(utsField.getText());
            double uas = uasField.getText().isEmpty() ? 0 : Double.parseDouble(uasField.getText());
            
            if (tugas < 0 || tugas > 100 || uts < 0 || uts > 100 || uas < 0 || uas > 100) {
                JOptionPane.showMessageDialog(this, "Nilai harus antara 0-100!");
                return;
            }
            
            // RUMUS BARU: Tugas 50%, UTS 20%, UAS 30%
            double akhir = (tugas * 0.5) + (uts * 0.2) + (uas * 0.3);
            akhirField.setText(String.format("%.2f", akhir));
            
            JOptionPane.showMessageDialog(this, "Nilai Akhir Dihitung!\n\nNilai Akhir = (Tugas × 50%) + (UTS × 20%) + (UAS × 30%)");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Masukkan angka yang valid untuk nilai!");
        }
    }
    
    private void simpanNilai() {
        if (currentNIS.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cari siswa terlebih dahulu!");
            return;
        }
        
        if (mapelCombo.getSelectedIndex() == -1 || mapelCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Pilih Mata Pelajaran terlebih dahulu!");
            return;
        }
        
        String selectedMapel = (String) mapelCombo.getSelectedItem();
        if (selectedMapel.equals("Belum ada data guru - Silakan input guru dulu")) {
            JOptionPane.showMessageDialog(this, "Silakan input data GURU terlebih dahulu!");
            return;
        }
        
        if (tugasField.getText().isEmpty() || utsField.getText().isEmpty() || uasField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan semua nilai (Tugas, UTS, UAS)!");
            return;
        }
        
        if (akhirField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hitung nilai akhir terlebih dahulu!");
            return;
        }
        
        String nis = currentNIS;
        String nama = namaField.getText();
        String mataPelajaran = selectedMapel.split(" - ")[1];
        String guruPengajar = selectedMapel.split(" - ")[0];
        double tugas = Double.parseDouble(tugasField.getText());
        double uts = Double.parseDouble(utsField.getText());
        double uas = Double.parseDouble(uasField.getText());
        double akhir = Double.parseDouble(akhirField.getText());
        
        if (Database.simpanNilai(nis, nama, mataPelajaran, guruPengajar, tugas, uts, uas, akhir)) {
            loadDataNilai();
            JOptionPane.showMessageDialog(this, "Nilai berhasil disimpan untuk " + nama + "\nMata Pelajaran: " + mataPelajaran + "\nGuru: " + guruPengajar);
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan nilai!");
        }
    }
    
    private void resetForm() {
        nisField.setText("");
        namaField.setText("");
        kelasField.setText("");
        tugasField.setText("");
        utsField.setText("");
        uasField.setText("");
        akhirField.setText("");
        currentNIS = "";
        currentGuruPengajar = "";
        nisField.requestFocus();
        if (mapelCombo.getItemCount() > 0) {
            mapelCombo.setSelectedIndex(0);
        }
    }
    
    private void loadDataNilai() {
        tableModel.setRowCount(0);
        for (Object[] nilai : Database.getAllNilai()) {
            String nis = nilai[0].toString();
            String nama = nilai[1].toString();
            String mapel = nilai[2].toString();
            String guru = nilai[3].toString();
            double tugas = (double) nilai[4];
            double uts = (double) nilai[5];
            double uas = (double) nilai[6];
            double akhir = (double) nilai[7];
            
            String kelas = "-";
            String[] siswa = Database.getSiswaByNIS(nis);
            if (siswa != null) {
                kelas = siswa[7];  // <-- INDEX KELAS: 7
            }
            
            tableModel.addRow(new Object[]{nis, nama, kelas, mapel, guru, tugas, uts, uas, akhir});
        }
    }
}