import javax.swing.*;
import java.awt.*;

public class ProfilSekolahPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public ProfilSekolahPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        // Panel utama dengan scroll
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 242, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // Wrapper untuk semua konten agar rata tengah
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerWrapper.setMaximumSize(new Dimension(1000, Integer.MAX_VALUE));

        // ==================== HEADER WITH ICON ====================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("PROFIL SEKOLAH");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(41, 128, 185));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        centerWrapper.add(headerPanel);

        // ==================== KARTU INFO UTAMA (HERO CARD) ====================
        JPanel heroCard = new JPanel();
        heroCard.setLayout(new BoxLayout(heroCard, BoxLayout.Y_AXIS));
        heroCard.setBackground(Color.WHITE);
        heroCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 240), 1),
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        ));
        heroCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroCard.setMaximumSize(new Dimension(1000, 250));

        // Logo dan Nama Sekolah
        JPanel logoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        logoWrapper.setOpaque(false);

        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/smapgri4.png"));
            Image scaledImage = logoIcon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            logoLabel.setText("🏫");
            logoLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 70));
            logoLabel.setForeground(new Color(41, 128, 185));
        }
        logoWrapper.add(logoLabel);

        JPanel textWrapper = new JPanel();
        textWrapper.setLayout(new BoxLayout(textWrapper, BoxLayout.Y_AXIS));
        textWrapper.setOpaque(false);

        JLabel schoolName = new JLabel("SMA PGRI 4 JAKARTA");
        schoolName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        schoolName.setForeground(new Color(41, 128, 185));
        schoolName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Sekolah Unggulan Terakreditasi A");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagline.setForeground(new Color(100, 100, 120));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel npsnLabel = new JLabel("NPSN: 20123456  |  Status: Swasta  |  Akreditasi: A");
        npsnLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        npsnLabel.setForeground(new Color(120, 120, 140));
        npsnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textWrapper.add(schoolName);
        textWrapper.add(Box.createVerticalStrut(8));
        textWrapper.add(tagline);
        textWrapper.add(Box.createVerticalStrut(8));
        textWrapper.add(npsnLabel);

        logoWrapper.add(textWrapper);
        heroCard.add(logoWrapper);

        centerWrapper.add(heroCard);
        centerWrapper.add(Box.createVerticalStrut(25));

        // ==================== GRID 2 KOLOM (VISI & MISI) ====================
        JPanel visiMisiGrid = new JPanel(new GridLayout(1, 2, 25, 0));
        visiMisiGrid.setOpaque(false);
        visiMisiGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
        visiMisiGrid.setMaximumSize(new Dimension(1000, 280));

        // VISI CARD
        JPanel visiCard = new JPanel();
        visiCard.setLayout(new BoxLayout(visiCard, BoxLayout.Y_AXIS));
        visiCard.setBackground(Color.WHITE);
        visiCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 240), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel visiIcon = new JLabel("🎯");
        visiIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 32));
        visiIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel visiTitle = new JLabel("VISI");
        visiTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        visiTitle.setForeground(new Color(41, 128, 185));
        visiTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea visiText = new JTextArea(
            "Menjadi sekolah unggul yang menghasilkan lulusan beriman, bertaqwa, berilmu, berakhlak mulia, dan berdaya saing global di era digital."
        );
        visiText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        visiText.setEditable(false);
        visiText.setLineWrap(true);
        visiText.setWrapStyleWord(true);
        visiText.setBackground(Color.WHITE);
        visiText.setBorder(BorderFactory.createEmptyBorder(12, 5, 5, 5));
        visiText.setForeground(new Color(70, 70, 90));

        visiCard.add(visiIcon);
        visiCard.add(Box.createVerticalStrut(5));
        visiCard.add(visiTitle);
        visiCard.add(Box.createVerticalStrut(10));
        visiCard.add(visiText);

        // MISI CARD
        JPanel misiCard = new JPanel();
        misiCard.setLayout(new BoxLayout(misiCard, BoxLayout.Y_AXIS));
        misiCard.setBackground(Color.WHITE);
        misiCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 240), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel misiIcon = new JLabel("📋");
        misiIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 32));
        misiIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel misiTitle = new JLabel("MISI");
        misiTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        misiTitle.setForeground(new Color(41, 128, 185));
        misiTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea misiText = new JTextArea(
            "1. Meningkatkan kualitas pembelajaran berbasis teknologi informasi.\n" +
            "2. Mengembangkan potensi siswa melalui kegiatan ekstrakurikuler.\n" +
            "3. Membangun karakter siswa yang berakhlak mulia.\n" +
            "4. Menjalin kerjasama dengan dunia usaha dan industri.\n" +
            "5. Meningkatkan profesionalisme tenaga pendidik dan kependidikan."
        );
        misiText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        misiText.setEditable(false);
        misiText.setLineWrap(true);
        misiText.setWrapStyleWord(true);
        misiText.setBackground(Color.WHITE);
        misiText.setBorder(BorderFactory.createEmptyBorder(12, 5, 5, 5));
        misiText.setForeground(new Color(70, 70, 90));

        misiCard.add(misiIcon);
        misiCard.add(Box.createVerticalStrut(5));
        misiCard.add(misiTitle);
        misiCard.add(Box.createVerticalStrut(10));
        misiCard.add(misiText);

        visiMisiGrid.add(visiCard);
        visiMisiGrid.add(misiCard);

        centerWrapper.add(visiMisiGrid);
        centerWrapper.add(Box.createVerticalStrut(25));

        // ==================== SEJARAH SINGKAT (DI TENGAH, RATA KIRI) ====================
        JPanel sejarahCard = new JPanel();
        sejarahCard.setLayout(new BoxLayout(sejarahCard, BoxLayout.Y_AXIS));
        sejarahCard.setBackground(Color.WHITE);
        sejarahCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 240), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        sejarahCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        sejarahCard.setMaximumSize(new Dimension(1000, 200));

        JPanel sejarahHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        sejarahHeader.setOpaque(false);
        sejarahHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sejarahIcon = new JLabel("📖");
        sejarahIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 22));
        JLabel sejarahTitle = new JLabel("SEJARAH SINGKAT");
        sejarahTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sejarahTitle.setForeground(new Color(41, 128, 185));
        sejarahHeader.add(sejarahIcon);
        sejarahHeader.add(sejarahTitle);

        JTextArea sejarahText = new JTextArea(
            "SMA PGRI 4 Jakarta berdiri sejak tahun 1981, memiliki luas tanah 1.150 m² merupakan sekolah yang memiliki reputasi baik dan diakui kualitasnya. " +
            "Terakreditasi A pada tanggal 22 Juni 2020 dengan No SK 458/BAN-SM/sk/2020. " +
            "Hal ini menandakan bahwa SMA PGRI 4 Jakarta berkomitmen untuk memberikan pendidikan berkualitas tinggi dan memenuhi standar nasional pendidikan."
        );
        sejarahText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sejarahText.setEditable(false);
        sejarahText.setLineWrap(true);
        sejarahText.setWrapStyleWord(true);
        sejarahText.setBackground(Color.WHITE);
        sejarahText.setBorder(BorderFactory.createEmptyBorder(12, 0, 5, 0));
        sejarahText.setForeground(new Color(70, 70, 90));

        sejarahCard.add(sejarahHeader);
        sejarahCard.add(Box.createVerticalStrut(10));
        sejarahCard.add(sejarahText);

        centerWrapper.add(sejarahCard);
        centerWrapper.add(Box.createVerticalStrut(25));

        // ==================== KONTAK & LOKASI (GRID 2 KOLOM) ====================
        JPanel contactGrid = new JPanel(new GridLayout(1, 2, 25, 0));
        contactGrid.setOpaque(false);
        contactGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
        contactGrid.setMaximumSize(new Dimension(1000, 200));

        // LOKASI CARD
        JPanel lokasiCard = new JPanel();
        lokasiCard.setLayout(new BoxLayout(lokasiCard, BoxLayout.Y_AXIS));
        lokasiCard.setBackground(Color.WHITE);
        lokasiCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 240), 1),
            BorderFactory.createEmptyBorder(18, 25, 18, 25)
        ));

        JLabel lokasiIcon = new JLabel("📍");
        lokasiIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
        lokasiIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lokasiTitle = new JLabel("LOKASI");
        lokasiTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lokasiTitle.setForeground(new Color(41, 128, 185));
        lokasiTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea lokasiText = new JTextArea(
            "Jl. Cipayung Raya, RT.1/RW.3\n" +
            "Kel. Cipayung, Kec. Cipayung\n" +
            "Jakarta Timur 13840"
        );
        lokasiText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lokasiText.setEditable(false);
        lokasiText.setLineWrap(true);
        lokasiText.setWrapStyleWord(true);
        lokasiText.setBackground(Color.WHITE);
        lokasiText.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        lokasiText.setForeground(new Color(70, 70, 90));
        lokasiText.setAlignmentX(Component.CENTER_ALIGNMENT);

        lokasiCard.add(lokasiIcon);
        lokasiCard.add(Box.createVerticalStrut(5));
        lokasiCard.add(lokasiTitle);
        lokasiCard.add(Box.createVerticalStrut(8));
        lokasiCard.add(lokasiText);

        // KONTAK CARD
        JPanel kontakCard = new JPanel();
        kontakCard.setLayout(new BoxLayout(kontakCard, BoxLayout.Y_AXIS));
        kontakCard.setBackground(Color.WHITE);
        kontakCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 240), 1),
            BorderFactory.createEmptyBorder(18, 25, 18, 25)
        ));

        JLabel kontakIcon = new JLabel("📞");
        kontakIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
        kontakIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel kontakTitle = new JLabel("KONTAK");
        kontakTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        kontakTitle.setForeground(new Color(41, 128, 185));
        kontakTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel kontakListPanel = new JPanel();
        kontakListPanel.setLayout(new BoxLayout(kontakListPanel, BoxLayout.Y_AXIS));
        kontakListPanel.setOpaque(false);
        kontakListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        kontakListPanel.add(createContactRow("📞", "Telepon", "(021) 2287 6501"));
        kontakListPanel.add(Box.createVerticalStrut(8));
        kontakListPanel.add(createContactRow("✉️", "Email", "info@smapgri4jakarta.sch.id"));
        kontakListPanel.add(Box.createVerticalStrut(8));
        kontakListPanel.add(createContactRow("🌐", "Website", "www.smapgri4jakarta.sch.id"));

        kontakCard.add(kontakIcon);
        kontakCard.add(Box.createVerticalStrut(5));
        kontakCard.add(kontakTitle);
        kontakCard.add(Box.createVerticalStrut(5));
        kontakCard.add(kontakListPanel);

        contactGrid.add(lokasiCard);
        contactGrid.add(kontakCard);

        centerWrapper.add(contactGrid);
        centerWrapper.add(Box.createVerticalStrut(30));

        // ==================== TOMBOL KEMBALI KE DASHBOARD ====================
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrapper.setOpaque(false);
        btnWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backBtn = new JButton("◀  KEMBALI KE DASHBOARD");
        backBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        backBtn.setBackground(new Color(41, 128, 185));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "menuUtama");
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof MenuUtamaPanel) {
                    ((MenuUtamaPanel) comp).showDashboard();
                    break;
                }
            }
        });

        btnWrapper.add(backBtn);
        centerWrapper.add(btnWrapper);
        centerWrapper.add(Box.createVerticalStrut(20));

        contentPanel.add(centerWrapper);

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(new Color(240, 242, 245));
        scrollPane.getViewport().setBackground(new Color(240, 242, 245));

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createContactRow(String icon, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(400, 30));
        row.setPreferredSize(new Dimension(400, 30));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));

        JLabel labelLabel = new JLabel(label + ":");
        labelLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelLabel.setForeground(new Color(70, 70, 90));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valueLabel.setForeground(new Color(100, 100, 120));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(iconLabel);
        leftPanel.add(labelLabel);

        row.add(leftPanel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);

        return row;
    }
}