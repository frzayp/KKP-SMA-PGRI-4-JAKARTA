import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DialogResetPassword extends JDialog {
    private JTextField kodeGuruField;
    private JButton resetBtn, batalBtn;
    private JLabel statusLabel;
    
    public DialogResetPassword(JFrame parent) {
        super(parent, "Reset Password Guru", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        headerPanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel("🔐");
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
        
        JLabel titleLabel = new JLabel("RESET PASSWORD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        JLabel infoLabel = new JLabel("Masukkan Kode Guru untuk mereset password ke default:");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Kode Guru:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        kodeGuruField = new JTextField(15);
        kodeGuruField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(kodeGuruField, gbc);
        
        JLabel warningLabel = new JLabel("⚠️ Password akan direset menjadi: guru123");
        warningLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        warningLabel.setForeground(new Color(231, 76, 60));
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        formPanel.add(warningLabel, gbc);
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(46, 204, 113));
        gbc.gridy = 3;
        formPanel.add(statusLabel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(Color.WHITE);
        
        resetBtn = new JButton("RESET PASSWORD");
        resetBtn.setBackground(new Color(46, 204, 113));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setFocusPainted(false);
        resetBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        batalBtn = new JButton("BATAL");
        batalBtn.setBackground(new Color(108, 117, 125));
        batalBtn.setForeground(Color.WHITE);
        batalBtn.setFocusPainted(false);
        batalBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        batalBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnPanel.add(resetBtn);
        btnPanel.add(batalBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        resetBtn.addActionListener(e -> resetPassword());
        batalBtn.addActionListener(e -> dispose());
        kodeGuruField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    resetPassword();
                }
            }
        });
    }
    
    private void resetPassword() {
        String kodeGuru = kodeGuruField.getText().trim();
        
        if (kodeGuru.isEmpty()) {
            statusLabel.setText("❌ Masukkan Kode Guru terlebih dahulu!");
            statusLabel.setForeground(new Color(231, 76, 60));
            return;
        }
        
        if (!Database.cekKodeGuru(kodeGuru)) {
            statusLabel.setText("❌ Kode Guru tidak ditemukan atau guru tidak aktif!");
            statusLabel.setForeground(new Color(231, 76, 60));
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Yakin akan mereset password guru dengan Kode Guru: " + kodeGuru + "?\n\nPassword akan menjadi: guru123", 
            "Konfirmasi Reset", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (Database.resetPasswordGuru(kodeGuru)) {
                String namaGuru = Database.getNamaGuruByKode(kodeGuru);
                JOptionPane.showMessageDialog(this, 
                    "✅ Password berhasil direset!\n\n" +
                    "Kode Guru: " + kodeGuru + "\n" +
                    "Nama: " + (namaGuru != null ? namaGuru : "-") + "\n\n" +
                    "Password baru: guru123\n\n" +
                    "Silakan login dengan password baru.", 
                    "Reset Berhasil", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                statusLabel.setText("❌ Gagal mereset password!");
                statusLabel.setForeground(new Color(231, 76, 60));
            }
        }
    }
}