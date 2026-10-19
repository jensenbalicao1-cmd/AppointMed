
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class ClientDashboardPanel extends JPanel {
    private final MainFrame frame;

    private final JComboBox<String> doctorBox = new JComboBox<>();
    private final JTextField dateField = new JTextField(10);
    private final JComboBox<String> timeBox = new JComboBox<>();
    private final JTextArea reasonArea = new JTextArea(4, 15);

    private final DefaultTableModel tableModel =
            UIHelper.readOnlyModel("Doctor", "Date", "Time", "Reason", "Status");

    public ClientDashboardPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel header = new JPanel(new BorderLayout());
        header.add(UIHelper.title("Client Dashboard"), BorderLayout.WEST);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            DataStore.logoutClient();
            frame.showPanel(MainFrame.PORTAL);
        });
        header.add(logout, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        doctorBox.addItem("-- Select Doctor --");
        for (String doctor : DataStore.DOCTORS) {
            doctorBox.addItem(doctor);
        }

        // Date field and calendar button
        dateField.setEditable(false);
        JButton calendarButton = new JButton("📅");
        calendarButton.setToolTipText("Choose appointment date");

        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.add(dateField, BorderLayout.CENTER);
        datePanel.add(calendarButton, BorderLayout.EAST);

        calendarButton.addActionListener(e -> showCalendar());
        dateField.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showCalendar();
            }
        });

        // Appointment time dropdown
        timeBox.addItem("-- Select Time --");
        for (int hour = 8; hour <= 17; hour++) {
            timeBox.addItem(String.format("%02d:00", hour));
            if (hour < 17) {
                timeBox.addItem(String.format("%02d:30", hour));
            }
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Book an Appointment"));

        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        JButton bookBtn = new JButton("Book Appointment");
        bookBtn.addActionListener(e -> handleBook());

        UIHelper.addRow(form, 0, "Doctor:", doctorBox);
        UIHelper.addRow(form, 1, "Date:", datePanel);
        UIHelper.addRow(form, 2, "Time:", timeBox);
        UIHelper.addRow(form, 3, "Reason:", new JScrollPane(reasonArea));
        UIHelper.addFull(form, 4, bookBtn);

        JPanel left = new JPanel(new BorderLayout());
        left.add(form, BorderLayout.NORTH);
        add(left, BorderLayout.WEST);

        JScrollPane tableScroll = new JScrollPane(new JTable(tableModel));
        tableScroll.setBorder(new TitledBorder("My Appointments"));
        add(tableScroll, BorderLayout.CENTER);
    }

    private void showCalendar() {
        final JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Choose Appointment Date",
                true
        );

        JPanel calendar = new JPanel(new BorderLayout(8, 8));
        calendar.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel monthPanel = new JPanel(new BorderLayout());
        JButton previous = new JButton("<");
        JButton next = new JButton(">");
        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);

        monthPanel.add(previous, BorderLayout.WEST);
        monthPanel.add(monthLabel, BorderLayout.CENTER);
        monthPanel.add(next, BorderLayout.EAST);

        JPanel daysPanel = new JPanel(new GridLayout(0, 7, 4, 4));

        calendar.add(monthPanel, BorderLayout.NORTH);
        calendar.add(daysPanel, BorderLayout.CENTER);

        LocalDate initialDate;
        try {
            initialDate = dateField.getText().isEmpty()
                    ? LocalDate.now()
                    : LocalDate.parse(dateField.getText());
        } catch (DateTimeParseException ex) {
            initialDate = LocalDate.now();
        }

        YearMonth[] displayedMonth = {
                YearMonth.from(initialDate)
        };

        Runnable renderCalendar = () -> {
            monthLabel.setText(displayedMonth[0].getMonth()
                    + " " + displayedMonth[0].getYear());

            daysPanel.removeAll();

            String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (String weekday : weekdays) {
                daysPanel.add(new JLabel(weekday, SwingConstants.CENTER));
            }

            LocalDate firstDay = displayedMonth[0].atDay(1);
            int offset = firstDay.getDayOfWeek().getValue() - 1;

            for (int i = 0; i < offset; i++) {
                daysPanel.add(new JLabel(""));
            }

            for (int day = 1; day <= displayedMonth[0].lengthOfMonth(); day++) {
                final LocalDate selectedDate = displayedMonth[0].atDay(day);
                JButton dayButton = new JButton(String.valueOf(day));

                if (selectedDate.isBefore(LocalDate.now())) {
                    dayButton.setEnabled(false);
                }

                dayButton.addActionListener(e -> {
                    dateField.setText(selectedDate.toString());
                    dialog.dispose();
                });

                daysPanel.add(dayButton);
            }

            daysPanel.revalidate();
            daysPanel.repaint();
            dialog.pack();
        };

        previous.addActionListener(e -> {
            displayedMonth[0] = displayedMonth[0].minusMonths(1);
            renderCalendar.run();
        });

        next.addActionListener(e -> {
            displayedMonth[0] = displayedMonth[0].plusMonths(1);
            renderCalendar.run();
        });

        renderCalendar.run();
        dialog.setContentPane(calendar);
        dialog.setMinimumSize(new Dimension(320, 250));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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

        if (dateField.getText().trim().isEmpty()) {
            UIHelper.error(this, "Please choose an appointment date.");
            return;
        }

        if (timeBox.getSelectedIndex() <= 0) {
            UIHelper.error(this, "Please select an appointment time.");
            return;
        }

        LocalDate date;
        LocalTime time;

        try {
            date = LocalDate.parse(dateField.getText().trim());
            time = LocalTime.parse((String) timeBox.getSelectedItem());
        } catch (DateTimeParseException ex) {
            UIHelper.error(this, "Please select a valid date and time.");
            return;
        }

        if (date.isBefore(LocalDate.now())) {
            UIHelper.error(this, "The date can't be in the past.");
            return;
        }

        if (date.equals(LocalDate.now()) && !time.isAfter(LocalTime.now())) {
            UIHelper.error(this, "Please choose a future appointment time.");
            return;
        }

        if (DataStore.isSlotTaken(doctor, date, time)) {
            UIHelper.error(this,
                    "That doctor is already booked at that date and time.");
            return;
        }

        DataStore.addAppointment(new Appointment(
                DataStore.getCurrentClient(), doctor, date, time, reason));

        UIHelper.info(this, "Appointment booked! Waiting for admin approval.");

        doctorBox.setSelectedIndex(0);
        dateField.setText("");
        timeBox.setSelectedIndex(0);
        reasonArea.setText("");
        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        Client me = DataStore.getCurrentClient();
        if (me == null) return;

        for (Appointment a : DataStore.getAppointmentsFor(me)) {
            tableModel.addRow(new Object[]{
                    a.getDoctor(), a.getDate(), a.getTime(),
                    a.getReason(), a.getStatus()
            });
        }
    }
}
