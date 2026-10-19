import javax.swing.*;
import java.awt.*;


public class PortalPanel extends JPanel {

    public PortalPanel(MainFrame frame) {
        setLayout(new GridBagLayout());

        JPanel box = new JPanel(new GridLayout(4, 1, 10, 15));
        JLabel welcome = UIHelper.title("Welcome to appointMed");
        welcome.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel choose = new JLabel("Choose Portal", SwingConstants.CENTER);

        JButton clientBtn = new JButton("Go to Client Portal");
        JButton adminBtn = new JButton("Go to Admin Portal");

        clientBtn.addActionListener(e -> frame.showPanel(MainFrame.CLIENT_LOGIN));
        adminBtn.addActionListener(e -> frame.showPanel(MainFrame.ADMIN_LOGIN));

        box.add(welcome);
        box.add(choose);
        box.add(clientBtn);
        box.add(adminBtn);
        add(box);
    }
}
