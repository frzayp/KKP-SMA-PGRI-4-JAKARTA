import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

public class KalenderAkademikPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JCalendar calendar;
    private JTextArea eventDetailArea;
    private DefaultListModel<String> eventListModel;
    private JList<String> eventList;
    private JButton tambahBtn, editBtn, hapusBtn, refreshBtn, backBtn;
    private String userRole = "Admin";
    
    private String[] eventTypes = {"Ujian", "Libur", "Keuangan", "Akademik", "Ekstrakurikuler"};
    private Color[] eventColors = {
        new Color(46, 204, 113),
        new Color(231, 76, 60),
        new Color(241, 196, 15),
        new Color(230, 126, 34),
        new Color(52, 152, 219)
    };
    
    public KalenderAkademikPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initComponents();
        loadEvents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 242, 245));
        
        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel("KALENDER AKADEMIK");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // MAIN CONTENT
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 15, 0));
        mainContent.setOpaque(false);
        
        // LEFT PANEL - KALENDER
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        calendar = new JCalendar();
        calendar.getDayChooser().setDayBordersVisible(true);
        calendar.getDayChooser().setAlwaysFireDayProperty(true);
        calendar.addPropertyChangeListener("calendar", evt -> {
            loadEvents();
            showEventDetail();
        });
        calendar.addPropertyChangeListener("day", evt -> showEventDetail());
        
        leftPanel.add(calendar, BorderLayout.CENTER);
        
        // LEGEND PANEL
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        legendPanel.setBorder(BorderFactory.createTitledBorder("Legenda"));
        legendPanel.setBackground(Color.WHITE);
        
        String[] legendItems = {"[U] Ujian", "[L] Libur", "[K] Keuangan", "[A] Akademik", "[E] Ekstrakurikuler"};
        for (String item : legendItems) {
            JLabel legendLabel = new JLabel(item);
            legendLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            legendPanel.add(legendLabel);
        }
        
        leftPanel.add(legendPanel, BorderLayout.SOUTH);
        
        // RIGHT PANEL - EVENT DETAIL
        JPanel rightPanelMain = new JPanel(new BorderLayout());
        rightPanelMain.setBackground(Color.WHITE);
        rightPanelMain.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel eventTitle = new JLabel("EVENT & KEGIATAN");
        eventTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        eventTitle.setForeground(new Color(41, 128, 185));
        rightPanelMain.add(eventTitle, BorderLayout.NORTH);
        
        eventListModel = new DefaultListModel<>();
        eventList = new JList<>(eventListModel);
        eventList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedEventDetail();
            }
        });
        
        JScrollPane listScroll = new JScrollPane(eventList);
        listScroll.setPreferredSize(new Dimension(0, 150));
        listScroll.setBorder(BorderFactory.createTitledBorder("Daftar Event"));
        
        eventDetailArea = new JTextArea();
        eventDetailArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        eventDetailArea.setEditable(false);
        eventDetailArea.setLineWrap(true);
        eventDetailArea.setWrapStyleWord(true);
        eventDetailArea.setBackground(new Color(248, 249, 250));
        eventDetailArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Detail Event"),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        JScrollPane detailScroll = new JScrollPane(eventDetailArea);
        detailScroll.setPreferredSize(new Dimension(0, 120));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        
        tambahBtn = createIconButton("TAMBAH", new Color(46, 204, 113));
        editBtn = createIconButton("EDIT", new Color(52, 152, 219));
        hapusBtn = createIconButton("HAPUS", new Color(231, 76, 60));
        refreshBtn = createIconButton("REFRESH", new Color(108, 117, 125));
        backBtn = createIconButton("KEMBALI", new Color(52, 73, 94));
        
        btnPanel.add(tambahBtn);
        btnPanel.add(editBtn);
        btnPanel.add(hapusBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(backBtn);
        
        rightPanelMain.add(listScroll, BorderLayout.NORTH);
        rightPanelMain.add(detailScroll, BorderLayout.CENTER);
        rightPanelMain.add(btnPanel, BorderLayout.SOUTH);
        
        mainContent.add(leftPanel);
        mainContent.add(rightPanelMain);
        
        add(mainContent, BorderLayout.CENTER);
        
        // EVENT HANDLERS
        tambahBtn.addActionListener(e -> showEventDialog(null));
        editBtn.addActionListener(e -> {
            int index = eventList.getSelectedIndex();
            if (index != -1) {
                String selected = eventListModel.get(index);
                int id = extractIdFromString(selected);
                if (id != -1) {
                    showEventDialog(getEventById(id));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih event yang akan diedit!");
            }
        });
        hapusBtn.addActionListener(e -> {
            int index = eventList.getSelectedIndex();
            if (index != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus event ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String selected = eventListModel.get(index);
                    int id = extractIdFromString(selected);
                    if (id != -1) {
                        deleteEvent(id);
                        loadEvents();
                        showEventDetail();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih event yang akan dihapus!");
            }
        });
        refreshBtn.addActionListener(e -> {
            loadEvents();
            showEventDetail();
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
    
    private JButton createIconButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void loadEvents() {
        eventListModel.clear();
        Date selectedDate = calendar.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String selectedDateStr = sdf.format(selectedDate);
        
        String sql = "SELECT id_event, judul, tipe_event FROM tbl_event " +
                     "WHERE tgl_mulai <= ? AND (tgl_selesai >= ? OR tgl_selesai IS NULL) " +
                     "ORDER BY tgl_mulai";
        
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, selectedDateStr);
            pstmt.setString(2, selectedDateStr);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id_event");
                String judul = rs.getString("judul");
                String tipe = rs.getString("tipe_event");
                String prefix = getPrefixByTipe(tipe);
                eventListModel.addElement(prefix + " [" + id + "] " + judul);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private String getPrefixByTipe(String tipe) {
        switch (tipe) {
            case "Ujian": return "[U]";
            case "Libur": return "[L]";
            case "Keuangan": return "[K]";
            case "Akademik": return "[A]";
            case "Ekstrakurikuler": return "[E]";
            default: return "[?]";
        }
    }
    
    private void showEventDetail() {
        if (eventListModel.isEmpty()) {
            eventDetailArea.setText("Tidak ada event pada tanggal ini.");
            return;
        }
        
        Date selectedDate = calendar.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        String tanggal = sdf.format(selectedDate);
        
        StringBuilder sb = new StringBuilder();
        sb.append("TANGGAL: ").append(tanggal).append("\n");
        sb.append("----------------------------------------\n\n");
        
        String sql = "SELECT judul, deskripsi, tipe_event, kelas_target FROM tbl_event " +
                     "WHERE tgl_mulai <= ? AND (tgl_selesai >= ? OR tgl_selesai IS NULL)";
        
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(sql)) {
            String selectedDateStr = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
            pstmt.setString(1, selectedDateStr);
            pstmt.setString(2, selectedDateStr);
            ResultSet rs = pstmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                String judul = rs.getString("judul");
                String deskripsi = rs.getString("deskripsi");
                String tipe = rs.getString("tipe_event");
                String kelasTarget = rs.getString("kelas_target");
                String prefix = getPrefixByTipe(tipe);
                
                sb.append(prefix).append(" ").append(judul).append("\n");
                sb.append("   Tipe: ").append(tipe).append("\n");
                sb.append("   Target: ").append(kelasTarget).append("\n");
                if (deskripsi != null && !deskripsi.isEmpty()) {
                    sb.append("   Deskripsi: ").append(deskripsi).append("\n");
                }
                sb.append("\n");
            }
            if (count == 0) {
                sb.append("Tidak ada event pada tanggal ini.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sb.append("Error loading event detail.\n");
        }
        
        eventDetailArea.setText(sb.toString());
        eventDetailArea.setCaretPosition(0);
    }
    
    private void showSelectedEventDetail() {
        int index = eventList.getSelectedIndex();
        if (index == -1) return;
        
        String selected = eventListModel.get(index);
        int id = extractIdFromString(selected);
        if (id == -1) return;
        
        String sql = "SELECT judul, deskripsi, tgl_mulai, tgl_selesai, tipe_event, kelas_target " +
                     "FROM tbl_event WHERE id_event = ?";
        
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                String judul = rs.getString("judul");
                String deskripsi = rs.getString("deskripsi");
                Date tglMulai = rs.getDate("tgl_mulai");
                Date tglSelesai = rs.getDate("tgl_selesai");
                String tipe = rs.getString("tipe_event");
                String kelasTarget = rs.getString("kelas_target");
                String prefix = getPrefixByTipe(tipe);
                
                StringBuilder sb = new StringBuilder();
                sb.append(prefix).append(" ").append(judul).append("\n");
                sb.append("----------------------------------------\n\n");
                sb.append("Tanggal: ").append(sdf.format(tglMulai));
                if (tglSelesai != null && !tglMulai.equals(tglSelesai)) {
                    sb.append(" - ").append(sdf.format(tglSelesai));
                }
                sb.append("\n");
                sb.append("Tipe Event: ").append(tipe).append("\n");
                sb.append("Target Kelas: ").append(kelasTarget).append("\n");
                if (deskripsi != null && !deskripsi.isEmpty()) {
                    sb.append("\nDeskripsi:\n").append(deskripsi).append("\n");
                }
                eventDetailArea.setText(sb.toString());
                eventDetailArea.setCaretPosition(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private int extractIdFromString(String text) {
        try {
            if (text.contains("[")) {
                int startIdx = text.indexOf("[");
                int endIdx = text.indexOf("]");
                return Integer.parseInt(text.substring(startIdx + 1, endIdx));
            }
        } catch (Exception e) {}
        return -1;
    }
    
    private Object[] getEventById(int id) {
        String sql = "SELECT id_event, judul, deskripsi, tgl_mulai, tgl_selesai, tipe_event, kelas_target " +
                     "FROM tbl_event WHERE id_event = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Object[]{
                    rs.getInt("id_event"),
                    rs.getString("judul"),
                    rs.getString("deskripsi"),
                    rs.getDate("tgl_mulai"),
                    rs.getDate("tgl_selesai"),
                    rs.getString("tipe_event"),
                    rs.getString("kelas_target")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void deleteEvent(int id) {
        String sql = "DELETE FROM tbl_event WHERE id_event = ?";
        try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Event berhasil dihapus!");
            loadEvents();
            showEventDetail();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menghapus event: " + e.getMessage());
        }
    }
    
    private void showEventDialog(Object[] eventData) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), eventData == null ? "Tambah Event" : "Edit Event", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Judul Event:"), gbc);
        gbc.gridx = 1;
        JTextField judulField = new JTextField(25);
        formPanel.add(judulField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tipe Event:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> tipeCombo = new JComboBox<>(eventTypes);
        formPanel.add(tipeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Target Kelas:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> kelasTargetCombo = new JComboBox<>(getKelasList());
        formPanel.add(kelasTargetCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Tanggal Mulai:"), gbc);
        gbc.gridx = 1;
        JDateChooser tglMulai = new JDateChooser();
        tglMulai.setDateFormatString("dd-MM-yyyy");
        formPanel.add(tglMulai, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Tanggal Selesai:"), gbc);
        gbc.gridx = 1;
        JDateChooser tglSelesai = new JDateChooser();
        tglSelesai.setDateFormatString("dd-MM-yyyy");
        formPanel.add(tglSelesai, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Deskripsi:"), gbc);
        gbc.gridx = 1;
        JTextArea deskripsiArea = new JTextArea(4, 25);
        deskripsiArea.setLineWrap(true);
        deskripsiArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(deskripsiArea);
        formPanel.add(descScroll, gbc);
        
        if (eventData != null) {
            judulField.setText(eventData[1].toString());
            String tipe = eventData[5].toString();
            for (int i = 0; i < eventTypes.length; i++) {
                if (eventTypes[i].equals(tipe)) {
                    tipeCombo.setSelectedIndex(i);
                    break;
                }
            }
            String kelasTarget = eventData[6].toString();
            for (int i = 0; i < kelasTargetCombo.getItemCount(); i++) {
                if (kelasTargetCombo.getItemAt(i).equals(kelasTarget)) {
                    kelasTargetCombo.setSelectedIndex(i);
                    break;
                }
            }
            tglMulai.setDate((Date) eventData[3]);
            tglSelesai.setDate((Date) eventData[4]);
            if (eventData[2] != null) deskripsiArea.setText(eventData[2].toString());
        } else {
            tglMulai.setDate(new Date());
            tglSelesai.setDate(new Date());
        }
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton simpanBtn = new JButton("SIMPAN");
        simpanBtn.setBackground(new Color(46, 204, 113));
        simpanBtn.setForeground(Color.WHITE);
        simpanBtn.setFocusPainted(false);
        
        JButton batalBtn = new JButton("BATAL");
        batalBtn.setBackground(new Color(108, 117, 125));
        batalBtn.setForeground(Color.WHITE);
        batalBtn.setFocusPainted(false);
        
        btnPanel.add(simpanBtn);
        btnPanel.add(batalBtn);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        simpanBtn.addActionListener(e -> {
            String judul = judulField.getText().trim();
            if (judul.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Judul event harus diisi!");
                return;
            }
            
            int tipeIdx = tipeCombo.getSelectedIndex();
            String tipe = eventTypes[tipeIdx];
            String kelasTarget = (String) kelasTargetCombo.getSelectedItem();
            Date tglMulaiDate = tglMulai.getDate();
            Date tglSelesaiDate = tglSelesai.getDate();
            String deskripsi = deskripsiArea.getText().trim();
            
            if (tglMulaiDate == null) {
                JOptionPane.showMessageDialog(dialog, "Tanggal mulai harus diisi!");
                return;
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tglMulaiStr = sdf.format(tglMulaiDate);
            String tglSelesaiStr = tglSelesaiDate != null ? sdf.format(tglSelesaiDate) : tglMulaiStr;
            
            if (eventData == null) {
                String sql = "INSERT INTO tbl_event (judul, deskripsi, tgl_mulai, tgl_selesai, tipe_event, kelas_target, dibuat_oleh) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(sql)) {
                    pstmt.setString(1, judul);
                    pstmt.setString(2, deskripsi);
                    pstmt.setString(3, tglMulaiStr);
                    pstmt.setString(4, tglSelesaiStr);
                    pstmt.setString(5, tipe);
                    pstmt.setString(6, kelasTarget);
                    pstmt.setString(7, userRole);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Event berhasil ditambahkan!");
                    dialog.dispose();
                    loadEvents();
                    showEventDetail();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "Gagal menambahkan event: " + ex.getMessage());
                }
            } else {
                int id = (int) eventData[0];
                String sql = "UPDATE tbl_event SET judul=?, deskripsi=?, tgl_mulai=?, tgl_selesai=?, tipe_event=?, kelas_target=? WHERE id_event=?";
                try (PreparedStatement pstmt = KoneksiDB.getConnection().prepareStatement(sql)) {
                    pstmt.setString(1, judul);
                    pstmt.setString(2, deskripsi);
                    pstmt.setString(3, tglMulaiStr);
                    pstmt.setString(4, tglSelesaiStr);
                    pstmt.setString(5, tipe);
                    pstmt.setString(6, kelasTarget);
                    pstmt.setInt(7, id);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Event berhasil diubah!");
                    dialog.dispose();
                    loadEvents();
                    showEventDetail();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "Gagal mengubah event: " + ex.getMessage());
                }
            }
        });
        
        batalBtn.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }
    
    private String[] getKelasList() {
        java.util.List<String> kelasList = new ArrayList<>();
        kelasList.add("Semua Kelas");
        java.util.List<String[]> kelasData = Database.getAllKelas();
        for (String[] k : kelasData) {
            if (k.length > 1) {
                kelasList.add(k[1]);
            }
        }
        return kelasList.toArray(new String[0]);
    }
}