import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;


public class AdminDashboardPanel extends JPanel {
    private static final String[] COLUMNS =
            {"Client", "Email", "Doctor", "Date", "Time", "Reason", "Status"};

    private final DefaultTableModel pendingModel = UIHelper.readOnlyModel(COLUMNS);
    private final DefaultTableModel historyModel = UIHelper.readOnlyModel(COLUMNS);
    private final JTable pendingTable = new JTable(pendingModel);

    // Same order as the rows in pendingTable, so row index -> Appointment object
    private ArrayList<Appointment> pendingShown = new ArrayList<>();

    public AdminDashboardPanel(MainFrame frame) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header with title + logout
        JPanel header = new JPanel(new BorderLayout());
        header.add(UIHelper.title("Admin Dashboard"), BorderLayout.WEST);
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> frame.showPanel(MainFrame.PORTAL));
        header.add(logout, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Two tables stacked
        JPanel tables = new JPanel(new GridLayout(2, 1, 0, 10));
        tables.add(buildPendingSection());

        JScrollPane historyScroll = new JScrollPane(new JTable(historyModel));
        historyScroll.setBorder(new TitledBorder("History"));
        tables.add(historyScroll);

        add(tables, BorderLayout.CENTER);
    }

    private JPanel buildPendingSection() {
        JPanel section = new JPanel(new BorderLayout(5, 5));
        section.setBorder(new TitledBorder("Appointments (Pending)"));
        section.add(new JScrollPane(pendingTable), BorderLayout.CENTER);

        JButton approve = new JButton("Approve");
        JButton reject = new JButton("Reject");
        JButton delete = new JButton("Delete");

        approve.addActionListener(e -> changeStatus(Appointment.APPROVED));
        reject.addActionListener(e -> changeStatus(Appointment.REJECTED));
        delete.addActionListener(e -> deleteSelected());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttons.add(approve);
        buttons.add(reject);
        buttons.add(delete);
        section.add(buttons, BorderLayout.SOUTH);
        return section;
    }
    private Appointment getSelected() {
        int row = pendingTable.getSelectedRow();
        if (row == -1) {
            UIHelper.error(this, "Please select an appointment in the Pending table first.");
            return null;
        }
        return pendingShown.get(row);
    }

    private void changeStatus(String newStatus) {
        Appointment a = getSelected();
        if (a == null) return;
        a.setStatus(newStatus);
        refresh();
    }

    private void deleteSelected() {
        Appointment a = getSelected();
        if (a == null) return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete this appointment permanently?", "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            DataStore.deleteAppointment(a);
            refresh();
        }
    }
    public void refresh() {
        pendingModel.setRowCount(0);
        historyModel.setRowCount(0);

        pendingShown = DataStore.getAppointmentsByStatus(Appointment.PENDING);
        for (Appointment a : pendingShown) {
            pendingModel.addRow(toRow(a));
        }
        for (Appointment a : DataStore.getHistory()) {
            historyModel.addRow(toRow(a));
        }
    }

    private Object[] toRow(Appointment a) {
        return new Object[]{
                a.getClient().getFullName(), a.getClient().getEmail(), a.getDoctor(),
                a.getDate(), a.getTime(), a.getReason(), a.getStatus()
        };
    }
}
