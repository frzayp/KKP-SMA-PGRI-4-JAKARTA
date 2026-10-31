import javax.swing.*;
import java.awt.*;

public class GantiPasswordPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPasswordField passwordLamaField;
    private JPasswordField passwordBaruField;
    private JPasswordField konfirmasiField;
    private JButton simpanBtn, backBtn;
    private JLabel statusLabel;
    
    public GantiPasswordPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setBackground(new Color(240, 242, 245));
        
        JPanel mainPanelForm = new JPanel(new BorderLayout());
        mainPanelForm.setBackground(Color.WHITE);
        mainPanelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        headerPanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel("🔐");
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
        
        JLabel titleLabel = new JLabel("GANTI PASSWORD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);
        mainPanelForm.add(headerPanel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        formPanel.add(new JLabel("Password Lama:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        passwordLamaField = new JPasswordField(20);
        passwordLamaField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(passwordLamaField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password Baru:"), gbc);
        gbc.gridx = 1;
        passwordBaruField = new JPasswordField(20);
        passwordBaruField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(passwordBaruField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Konfirmasi Password:"), gbc);
        gbc.gridx = 1;
        konfirmasiField = new JPasswordField(20);
        konfirmasiField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(konfirmasiField, gbc);
        
        JLabel infoLabel = new JLabel("💡 Password minimal 6 karakter");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(100, 100, 100));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(231, 76, 60));
        gbc.gridy = 4;
        formPanel.add(statusLabel, gbc);
        
        mainPanelForm.add(formPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        simpanBtn = new JButton("SIMPAN");
        simpanBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        simpanBtn.setBackground(new Color(46, 204, 113));
        simpanBtn.setForeground(Color.WHITE);
        simpanBtn.setFocusPainted(false);
        simpanBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        simpanBtn.setPreferredSize(new Dimension(120, 38));
        
        backBtn = new JButton("KEMBALI");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backBtn.setBackground(new Color(52, 73, 94));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setPreferredSize(new Dimension(120, 38));
        
        btnPanel.add(simpanBtn);
        btnPanel.add(backBtn);
        mainPanelForm.add(btnPanel, BorderLayout.SOUTH);
        
        add(mainPanelForm, BorderLayout.CENTER);
        
        simpanBtn.addActionListener(e -> gantiPassword());
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
    
    private void gantiPassword() {
        String passwordLama = new String(passwordLamaField.getPassword());
        String passwordBaru = new String(passwordBaruField.getPassword());
        String konfirmasi = new String(konfirmasiField.getPassword());
        
        if (passwordLama.isEmpty() || passwordBaru.isEmpty() || konfirmasi.isEmpty()) {
            statusLabel.setText("❌ Semua field harus diisi!");
            return;
        }
        
        if (passwordBaru.length() < 6) {
            statusLabel.setText("❌ Password baru minimal 6 karakter!");
            return;
        }
        
        if (!passwordBaru.equals(konfirmasi)) {
            statusLabel.setText("❌ Password baru dan konfirmasi tidak cocok!");
            return;
        }
        
        if (passwordLama.equals(passwordBaru)) {
            statusLabel.setText("❌ Password baru tidak boleh sama dengan password lama!");
            return;
        }
        
        boolean success = false;
        
        if (Session.isGuru()) {
            success = Database.gantiPasswordGuru(Session.getKodeGuru(), passwordLama, passwordBaru);
        } else if (Session.isAdmin() || Session.isKepsek()) {
            success = Database.gantiPasswordAdmin(Session.getUsername(), passwordLama, passwordBaru);
        }
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "✅ Password berhasil diubah!\n\n" +
                "Silakan login kembali dengan password baru Anda.", 
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
            
            Session.clear();
            cardLayout.show(mainPanel, "login");
        } else {
            statusLabel.setText("❌ Password lama salah!");
        }
    }
}