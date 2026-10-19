import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class DataStore {
    private static final ArrayList<Client> clients = new ArrayList<>();
    private static final ArrayList<Appointment> appointments = new ArrayList<>();

    public static final String[] DOCTORS = {
            "Dr. Santos - General Medicine",
            "Dr. Reyes - Pediatrics",
            "Dr. Cruz - Dentistry",
            "Dr. Garcia - Dermatology"
    };

    // Hard-coded admin account (fine for a school project)
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    // Who is currently logged in as client (null = nobody)
    private static Client currentClient = null;

    // ---------- Clients ----------

    public static boolean registerClient(String fullName, String email, String password) {
        for (Client c : clients) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return false;
            }
        }
        clients.add(new Client(fullName, email, password));
        return true;
    }
    public static Client loginClient(String email, String password) {
        for (Client c : clients) {
            if (c.getEmail().equalsIgnoreCase(email) && c.getPassword().equals(password)) {
                currentClient = c;
                return c;
            }
        }
        return null;
    }

    public static Client getCurrentClient() { return currentClient; }

    public static void logoutClient() { currentClient = null; }



    public static boolean loginAdmin(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }



    public static void addAppointment(Appointment a) {
        appointments.add(a);
    }

    public static void deleteAppointment(Appointment a) {
        appointments.remove(a);
    }


    public static boolean isSlotTaken(String doctor, LocalDate date, LocalTime time) {
        for (Appointment a : appointments) {
            if (a.getDoctor().equals(doctor)
                    && a.getDate().equals(date)
                    && a.getTime().equals(time)
                    && !a.getStatus().equals(Appointment.REJECTED)) {
                return true;
            }
        }
        return false;
    }


    public static ArrayList<Appointment> getAppointmentsFor(Client client) {
        ArrayList<Appointment> result = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getClient() == client) {
                result.add(a);
            }
        }
        return result;
    }

    public static ArrayList<Appointment> getAppointmentsByStatus(String status) {
        ArrayList<Appointment> result = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getStatus().equals(status)) {
                result.add(a);
            }
        }
        return result;
    }

    public static ArrayList<Appointment> getHistory() {
        ArrayList<Appointment> result = new ArrayList<>();
        for (Appointment a : appointments) {
            if (!a.getStatus().equals(Appointment.PENDING)) {
                result.add(a);
            }
        }
        return result;
    }
}
