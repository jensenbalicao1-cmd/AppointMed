import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class AdminLoginPanel extends JPanel {
    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);

    public AdminLoginPanel(MainFrame frame) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        add(UIHelper.title("Admin Portal"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Admin Login"));

        JButton loginBtn = new JButton("Admin Login");
        loginBtn.addActionListener(e -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword());

            if (!DataStore.loginAdmin(user, pass)) {
                UIHelper.error(this, "Wrong admin username or password.");
                return;
            }
            usernameField.setText("");
            passwordField.setText("");
            frame.showPanel(MainFrame.ADMIN_DASHBOARD);
        });

        UIHelper.addRow(form, 0, "Username:", usernameField);
        UIHelper.addRow(form, 1, "Password:", passwordField);
        UIHelper.addFull(form, 2, loginBtn);

        JPanel wrapper = new JPanel(new GridBagLayout()); // centers the form
        wrapper.add(form);
        add(wrapper, BorderLayout.CENTER);

        JButton back = new JButton("Back");
        back.addActionListener(e -> frame.showPanel(MainFrame.PORTAL));
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.add(back);
        add(south, BorderLayout.SOUTH);
    }
}
