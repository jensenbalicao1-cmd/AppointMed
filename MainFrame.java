import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {
    // Names used to switch between screens
    public static final String PORTAL           = "portal";
    public static final String CLIENT_LOGIN     = "clientLogin";
    public static final String CLIENT_DASHBOARD = "clientDashboard";
    public static final String ADMIN_LOGIN      = "adminLogin";
    public static final String ADMIN_DASHBOARD  = "adminDashboard";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);


    private final ClientDashboardPanel clientDashboard;
    private final AdminDashboardPanel adminDashboard;

    public MainFrame() {
        setTitle("appointMed");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        clientDashboard = new ClientDashboardPanel(this);
        adminDashboard = new AdminDashboardPanel(this);

        cards.add(new PortalPanel(this), PORTAL);
        cards.add(new ClientLoginPanel(this), CLIENT_LOGIN);
        cards.add(clientDashboard, CLIENT_DASHBOARD);
        cards.add(new AdminLoginPanel(this), ADMIN_LOGIN);
        cards.add(adminDashboard, ADMIN_DASHBOARD);

        add(cards);
        showPanel(PORTAL);
    }


    public void showPanel(String name) {
        if (name.equals(CLIENT_DASHBOARD)) {
            clientDashboard.refresh();
        } else if (name.equals(ADMIN_DASHBOARD)) {
            adminDashboard.refresh();
        }
        cardLayout.show(cards, name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
