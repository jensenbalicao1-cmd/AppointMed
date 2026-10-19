import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ClientLoginPanel extends JPanel {
    private final MainFrame frame;

    private final JTextField loginEmail = new JTextField(15);
    private final JPasswordField loginPassword = new JPasswordField(15);

    private final JTextField regName = new JTextField(15);
    private final JTextField regEmail = new JTextField(15);
    private final JPasswordField regPassword = new JPasswordField(15);

    public ClientLoginPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        add(UIHelper.title("Client Portal"), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 20, 0));
        center.add(wrapTop(buildLoginForm()));
        center.add(wrapTop(buildRegisterForm()));
        add(center, BorderLayout.CENTER);

        JButton back = new JButton("Back");
        back.addActionListener(e -> frame.showPanel(MainFrame.PORTAL));
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.add(back);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel buildLoginForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Login"));

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> handleLogin());

        UIHelper.addRow(form, 0, "Email:", loginEmail);
        UIHelper.addRow(form, 1, "Password:", loginPassword);
        UIHelper.addFull(form, 2, loginBtn);
        return form;
    }

    private JPanel buildRegisterForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Create New Account"));

        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> handleRegister());

        UIHelper.addRow(form, 0, "Full Name:", regName);
        UIHelper.addRow(form, 1, "Email:", regEmail);
        UIHelper.addRow(form, 2, "Password:", regPassword);
        UIHelper.addFull(form, 3, registerBtn);
        return form;
    }
    private JPanel wrapTop(JPanel form) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(form, BorderLayout.NORTH);
        return wrapper;
    }

    private void handleLogin() {
        String email = loginEmail.getText().trim();
        String password = new String(loginPassword.getPassword());

        Client client = DataStore.loginClient(email, password);
        if (client == null) {
            UIHelper.error(this, "Wrong email or password.");
            return;
        }

        loginEmail.setText("");
        loginPassword.setText("");
        frame.showPanel(MainFrame.CLIENT_DASHBOARD);
    }

    private void handleRegister() {
        String name = regName.getText().trim();
        String email = regEmail.getText().trim();
        String password = new String(regPassword.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            UIHelper.error(this, "Please fill in all fields.");
            return;
        }
        if (!email.contains("@")) {
            UIHelper.error(this, "Please enter a valid email.");
            return;
        }
        if (password.length() < 4) {
            UIHelper.error(this, "Password must be at least 4 characters.");
            return;
        }
        if (!DataStore.registerClient(name, email, password)) {
            UIHelper.error(this, "That email is already registered.");
            return;
        }

        UIHelper.info(this, "Account created! You can log in now.");
        regName.setText("");
        regEmail.setText("");
        regPassword.setText("");
    }
}
