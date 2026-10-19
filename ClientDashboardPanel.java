import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;


public class ClientDashboardPanel extends JPanel {
    private final MainFrame frame;

    private final JComboBox<String> doctorBox = new JComboBox<>();
    private final JTextField dateField = new JTextField(10);
    private final JTextField timeField = new JTextField(10);
    private final JTextArea reasonArea = new JTextArea(4, 15);

    private final DefaultTableModel tableModel =
            UIHelper.readOnlyModel("Doctor", "Date", "Time", "Reason", "Status");

    public ClientDashboardPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header with title + logout
        JPanel header = new JPanel(new BorderLayout());
        header.add(UIHelper.title("Client Dashboard"), BorderLayout.WEST);
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            DataStore.logoutClient();
            frame.showPanel(MainFrame.PORTAL);
        });
        header.add(logout, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Doctor dropdown
        doctorBox.addItem("-- Select Doctor --");
        for (String doctor : DataStore.DOCTORS) {
            doctorBox.addItem(doctor);
        }

        // Left: booking form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Book an Appointment"));
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        JButton bookBtn = new JButton("Book Appointment");
        bookBtn.addActionListener(e -> handleBook());

        UIHelper.addRow(form, 0, "Doctor:", doctorBox);
        UIHelper.addRow(form, 1, "Date (yyyy-MM-dd):", dateField);
        UIHelper.addRow(form, 2, "Time (HH:mm, 24h):", timeField);
        UIHelper.addRow(form, 3, "Reason:", new JScrollPane(reasonArea));
        UIHelper.addFull(form, 4, bookBtn);

        JPanel left = new JPanel(new BorderLayout());
        left.add(form, BorderLayout.NORTH);
        add(left, BorderLayout.WEST);

        JScrollPane tableScroll = new JScrollPane(new JTable(tableModel));
        tableScroll.setBorder(new TitledBorder("My Appointments"));
        add(tableScroll, BorderLayout.CENTER);
    }

    private void handleBook() {
        if (doctorBox.getSelectedIndex() <= 0) {
            UIHelper.error(this, "Please select a doctor.");
            return;
        }
        String doctor = (String) doctorBox.getSelectedItem();
        String reason = reasonArea.getText().trim();
        if (reason.isEmpty()) {
            UIHelper.error(this, "Please enter a reason for the appointment.");
            return;
        }

        LocalDate date;
        LocalTime time;
        try {
            date = LocalDate.parse(dateField.getText().trim());
            time = LocalTime.parse(timeField.getText().trim());
        } catch (DateTimeParseException ex) {
            UIHelper.error(this, "Use the formats yyyy-MM-dd for date (e.g. 2026-11-05)\nand HH:mm for time (e.g. 14:30).");
            return;
        }

        if (date.isBefore(LocalDate.now())) {
            UIHelper.error(this, "The date can't be in the past.");
            return;
        }
        if (DataStore.isSlotTaken(doctor, date, time)) {
            UIHelper.error(this, "That doctor is already booked at that date and time.");
            return;
        }

        DataStore.addAppointment(new Appointment(DataStore.getCurrentClient(), doctor, date, time, reason));
        UIHelper.info(this, "Appointment booked! Waiting for admin approval.");

        doctorBox.setSelectedIndex(0);
        dateField.setText("");
        timeField.setText("");
        reasonArea.setText("");
        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        Client me = DataStore.getCurrentClient();
        if (me == null) return;

        for (Appointment a : DataStore.getAppointmentsFor(me)) {
            tableModel.addRow(new Object[]{
                    a.getDoctor(), a.getDate(), a.getTime(), a.getReason(), a.getStatus()
            });
        }
    }
}
