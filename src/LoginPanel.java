import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import com.formdev.flatlaf.ui.FlatLineBorder;

public class LoginPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField userField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JLabel quoteLabel;
    private JButton showHideBtn;
    private String[] quotes = {
        "Pendidikan adalah senjata paling ampuh untuk mengubah dunia.",
        "Belajar tanpa berpikir tidak ada gunanya, berpikir tanpa belajar itu berbahaya.",
        "Ilmu tanpa agama buta, agama tanpa ilmu lumpuh.",
        "Hiduplah seolah engkau mati besok, belajarlah seolah engkau hidup selamanya.",
        "Pendidikan bukanlah persiapan untuk hidup, pendidikan adalah kehidupan itu sendiri.",
        "Jadilah manusia yang bermanfaat bagi orang lain.",
        "Tuntutlah ilmu dari buaian hingga liang lahat.",
        "Sebaik-baik manusia adalah yang paling bermanfaat bagi orang lain."
    };
    private int quoteIndex = 0;
    private Timer quoteTimer;
    
    private final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private final Color PRIMARY_HOVER = new Color(21, 101, 192);
    private final Color BG_COLOR = new Color(245, 247, 250);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color BORDER_COLOR = new Color(224, 224, 224);
    private final Color CARD_COLOR = new Color(255, 255, 255);
    
    public LoginPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initComponents();
        startQuoteRotation();
    }
    
    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(BG_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        
        JPanel mainCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int shadowSize = 20;
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(shadowSize/2, shadowSize/2, getWidth() - shadowSize, getHeight() - shadowSize, 20, 20);
                g2d.dispose();
            }
        };
        mainCard.setBackground(CARD_COLOR);
        mainCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        mainCard.setPreferredSize(new Dimension(1000, 620));
        
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.fill = GridBagConstraints.BOTH;
        cardGbc.weightx = 1;
        cardGbc.weighty = 1;
        
        // LEFT PANEL (FORM LOGIN)
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(CARD_COLOR);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        leftGbc.weightx = 1.0;
        
        // ============ LOGO (CENTER) ============
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/smapgri4.png"));
            Image scaledImage = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            logoLabel.setText("🏫");
            logoLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 60));
            logoLabel.setForeground(PRIMARY_COLOR);
        }
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Panel wrapper untuk logo (biar benar-benar center)
        JPanel logoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoWrapper.setOpaque(false);
        logoWrapper.add(logoLabel);
        
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.insets = new Insets(0, 0, 5, 0);
        leftGbc.anchor = GridBagConstraints.CENTER;
        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(logoWrapper, leftGbc);
        
        // ============ JUDUL SEKOLAH (CENTER) ============
        JLabel schoolName = new JLabel("SMA PGRI 4 JAKARTA");
        schoolName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        schoolName.setForeground(TEXT_COLOR);
        schoolName.setHorizontalAlignment(SwingConstants.CENTER);
        leftGbc.gridy = 1;
        leftGbc.insets = new Insets(4, 0, 2, 0);
        leftPanel.add(schoolName, leftGbc);
        
        // ============ SUB JUDUL (CENTER) ============
        JLabel systemName = new JLabel("SISTEM INFORMASI AKADEMIK");
        systemName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        systemName.setForeground(new Color(150, 150, 170));
        systemName.setHorizontalAlignment(SwingConstants.CENTER);
        leftGbc.gridy = 2;
        leftGbc.insets = new Insets(0, 0, 20, 0);
        leftPanel.add(systemName, leftGbc);
        
        // ============ FORM INPUT ============
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);
        formContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        formContainer.setMaximumSize(new Dimension(340, 320));
        formContainer.setPreferredSize(new Dimension(340, 320));
        
        // 1. Login Sebagai
        JPanel roleContainer = new JPanel(new BorderLayout(0, 6));
        roleContainer.setOpaque(false);
        roleContainer.setMaximumSize(new Dimension(340, 60));
        roleContainer.setPreferredSize(new Dimension(340, 60));
        
        JLabel roleLabel = new JLabel("Login Sebagai");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(TEXT_COLOR);
        roleContainer.add(roleLabel, BorderLayout.NORTH);
        
        roleCombo = new JComboBox<>(new String[]{"Administrator", "Guru"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleCombo.setBackground(CARD_COLOR);
        roleCombo.setBorder(createRoundedBorder());
        roleCombo.setMaximumSize(new Dimension(340, 38));
        roleCombo.setPreferredSize(new Dimension(340, 38));
        roleCombo.addActionListener(e -> updatePlaceholder());
        roleContainer.add(roleCombo, BorderLayout.CENTER);
        
        formContainer.add(roleContainer);
        formContainer.add(Box.createVerticalStrut(12));
        
        // 2. Username / Kode Guru
        JPanel userContainer = new JPanel(new BorderLayout(10, 6));
        userContainer.setOpaque(false);
        userContainer.setMaximumSize(new Dimension(340, 65));
        userContainer.setPreferredSize(new Dimension(340, 65));
        
        JLabel userLabel = new JLabel("Username / Kode Guru");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(TEXT_COLOR);
        userContainer.add(userLabel, BorderLayout.NORTH);
        
        JPanel userFieldPanel = new JPanel(new BorderLayout(8, 0));
        userFieldPanel.setOpaque(false);
        userFieldPanel.setBorder(createRoundedBorder());
        userFieldPanel.setMaximumSize(new Dimension(340, 38));
        userFieldPanel.setPreferredSize(new Dimension(340, 38));
        
        JLabel userIcon = new JLabel(" ");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userFieldPanel.add(userIcon, BorderLayout.WEST);
        
        userField = new JTextField();
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userField.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 0));
        userField.setOpaque(false);
        userFieldPanel.add(userField, BorderLayout.CENTER);
        
        userContainer.add(userFieldPanel, BorderLayout.CENTER);
        
        formContainer.add(userContainer);
        formContainer.add(Box.createVerticalStrut(12));
        
        // 3. Password
        JPanel passContainer = new JPanel(new BorderLayout(10, 6));
        passContainer.setOpaque(false);
        passContainer.setMaximumSize(new Dimension(340, 65));
        passContainer.setPreferredSize(new Dimension(340, 65));
        
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passLabel.setForeground(TEXT_COLOR);
        passContainer.add(passLabel, BorderLayout.NORTH);
        
        JPanel passFieldPanel = new JPanel(new BorderLayout(8, 0));
        passFieldPanel.setOpaque(false);
        passFieldPanel.setBorder(createRoundedBorder());
        passFieldPanel.setMaximumSize(new Dimension(340, 38));
        passFieldPanel.setPreferredSize(new Dimension(340, 38));
        
        JLabel passIcon = new JLabel(" ");
        passIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passFieldPanel.add(passIcon, BorderLayout.WEST);
        
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 0));
        passwordField.setOpaque(false);
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doLogin();
                }
            }
        });
        passFieldPanel.add(passwordField, BorderLayout.CENTER);
        
        showHideBtn = new JButton("👁");
        showHideBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        showHideBtn.setBorderPainted(false);
        showHideBtn.setContentAreaFilled(false);
        showHideBtn.setFocusPainted(false);
        showHideBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showHideBtn.setPreferredSize(new Dimension(30, 30));
        showHideBtn.addActionListener(e -> togglePasswordVisibility());
        passFieldPanel.add(showHideBtn, BorderLayout.EAST);
        
        passContainer.add(passFieldPanel, BorderLayout.CENTER);
        
        formContainer.add(passContainer);
        
        leftGbc.gridy = 3;
        leftGbc.insets = new Insets(0, 0, 20, 0);
        leftGbc.anchor = GridBagConstraints.CENTER;
        leftPanel.add(formContainer, leftGbc);
        
        // ============ LOGIN BUTTON ============
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBackground(PRIMARY_COLOR);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        loginBtn.setMaximumSize(new Dimension(340, 44));
        loginBtn.setPreferredSize(new Dimension(340, 44));
        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(PRIMARY_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(PRIMARY_COLOR);
            }
        });
        loginBtn.addActionListener(e -> doLogin());
        
        leftGbc.gridy = 4;
        leftGbc.insets = new Insets(5, 0, 0, 0);
        leftPanel.add(loginBtn, leftGbc);
        
        // ============ RIGHT PANEL (QUOTES) ============
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(PRIMARY_COLOR);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));
        
        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.insets = new Insets(8, 8, 8, 8);
        rightGbc.fill = GridBagConstraints.HORIZONTAL;
        rightGbc.weightx = 1.0;
        
        JLabel illustrasiLabel = new JLabel("🎓");
        illustrasiLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 80));
        illustrasiLabel.setForeground(new Color(255, 255, 255, 180));
        illustrasiLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightGbc.gridx = 0;
        rightGbc.gridy = 0;
        rightPanel.add(illustrasiLabel, rightGbc);
        
        JLabel quoteIconTop = new JLabel("\"");
        quoteIconTop.setFont(new Font("Times New Roman", Font.BOLD, 60));
        quoteIconTop.setForeground(new Color(255, 255, 255, 60));
        rightGbc.gridy = 1;
        rightGbc.insets = new Insets(0, 0, 0, 0);
        rightPanel.add(quoteIconTop, rightGbc);
        
        quoteLabel = new JLabel();
        quoteLabel.setFont(new Font("Georgia", Font.ITALIC, 17));
        quoteLabel.setForeground(Color.WHITE);
        quoteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightGbc.gridy = 2;
        rightGbc.insets = new Insets(10, 20, 10, 20);
        rightPanel.add(quoteLabel, rightGbc);
        
        JLabel quoteIconBottom = new JLabel("\"");
        quoteIconBottom.setFont(new Font("Times New Roman", Font.BOLD, 60));
        quoteIconBottom.setForeground(new Color(255, 255, 255, 60));
        rightGbc.gridy = 3;
        rightGbc.insets = new Insets(0, 0, 10, 0);
        rightPanel.add(quoteIconBottom, rightGbc);
        
        JSeparator lineSep = new JSeparator();
        lineSep.setForeground(new Color(255, 255, 255, 80));
        lineSep.setPreferredSize(new Dimension(200, 2));
        rightGbc.gridy = 4;
        rightGbc.insets = new Insets(15, 40, 10, 40);
        rightPanel.add(lineSep, rightGbc);
        
        JLabel subText = new JLabel("— Inspirasi Pendidikan —");
        subText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subText.setForeground(new Color(255, 255, 255, 160));
        rightGbc.gridy = 5;
        rightGbc.insets = new Insets(0, 30, 0, 30);
        rightPanel.add(subText, rightGbc);
        
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.weightx = 0.42;
        mainCard.add(leftPanel, cardGbc);
        
        cardGbc.gridx = 1;
        cardGbc.weightx = 0.58;
        mainCard.add(rightPanel, cardGbc);
        
        gbc.anchor = GridBagConstraints.CENTER;
        add(mainCard, gbc);
        
        updatePlaceholder();
    }
    
    private Border createRoundedBorder() {
        return BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER_COLOR, 1, 15),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        );
    }
    
    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;
        
        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x + thickness/2, y + thickness/2, width - thickness, height - thickness, radius, radius);
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }
    }
    
    private void togglePasswordVisibility() {
        if (passwordField.getEchoChar() == (char) 0) {
            passwordField.setEchoChar('•');
            showHideBtn.setText("👁");
        } else {
            passwordField.setEchoChar((char) 0);
            showHideBtn.setText("👁‍🗨");
        }
    }
    
    private void updatePlaceholder() {
        String role = (String) roleCombo.getSelectedItem();
        if (role.equals("Administrator")) {
            userField.setToolTipText("Masukkan username admin");
        } else {
            userField.setToolTipText("Masukkan kode guru");
        }
        userField.requestFocus();
    }
    
    private void doLogin() {
        String role = (String) roleCombo.getSelectedItem();
        String username = userField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username/Kode Guru dan Password harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Object[] loginResult = null;
        
        if (role.equals("Guru")) {
            loginResult = Database.loginGuru(username, password);
            if (loginResult != null) {
                Session.setLoginGuru((String) loginResult[0], (String) loginResult[1], (String) loginResult[2]);
            }
        } else if (role.equals("Administrator")) {
            loginResult = Database.loginAdmin(username, password, "admin");
            if (loginResult != null) {
                String roleDb = (String) loginResult[2];
                Session.setLoginAdmin((String) loginResult[0], (String) loginResult[1], roleDb);
            }
        }
        
        if (loginResult != null) {
            JOptionPane.showMessageDialog(this, "Selamat datang, " + Session.getNama() + "!\nAnda login sebagai " + role, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof MenuUtamaPanel) {
                    MenuUtamaPanel menuPanel = (MenuUtamaPanel) comp;
                    menuPanel.refreshMenu();
                    menuPanel.refreshContentPanel();
                    break;
                }
            }
            
            cardLayout.show(mainPanel, "menuUtama");
        } else {
            JOptionPane.showMessageDialog(this, "Login gagal!\nPeriksa Username/Kode Guru dan Password Anda.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void startQuoteRotation() {
        quoteLabel.setText("<html><div style='text-align: center; width: 350px;'>" + quotes[0] + "</div></html>");
        quoteTimer = new Timer(7000, e -> {
            quoteIndex = (quoteIndex + 1) % quotes.length;
            quoteLabel.setText("<html><div style='text-align: center; width: 350px;'>" + quotes[quoteIndex] + "</div></html>");
        });
        quoteTimer.start();
    }
}



