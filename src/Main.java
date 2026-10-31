import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatLightLaf;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    public Main() {
        setTitle("SIAKAD - SMA PGRI 4 Jakarta");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("ToolTip.font", new Font("Segoe UI", Font.PLAIN, 12));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Database.createTableEvent();
        
        if (!KoneksiDB.testConnection()) {
            JOptionPane.showMessageDialog(null, 
                "Gagal koneksi ke database!\nPastikan MySQL sedang berjalan.", 
                "Error Koneksi", JOptionPane.ERROR_MESSAGE);
        }
        
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/smapgri4.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {}
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(245, 245, 250));
        
        mainPanel.add(new LoginPanel(cardLayout, mainPanel), "login");
        mainPanel.add(new MenuUtamaPanel(cardLayout, mainPanel), "menuUtama");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            KoneksiDB.closeConnection();
        }));
    }
}



