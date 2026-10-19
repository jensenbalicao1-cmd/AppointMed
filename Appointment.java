import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    public static final String PENDING  = "Pending";
    public static final String APPROVED = "Approved";
    public static final String REJECTED = "Rejected";

    private final Client client;
    private final String doctor;
    private final LocalDate date;
    private final LocalTime time;
    private final String reason;
    private String status;

    public Appointment(Client client, String doctor, LocalDate date, LocalTime time, String reason) {
        this.client = client;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.status = PENDING; // every new appointment waits for the admin
    }

    public Client getClient()   { return client; }
    public String getDoctor()   { return doctor; }
    public LocalDate getDate()  { return date; }
    public LocalTime getTime()  { return time; }
    public String getReason()   { return reason; }
    public String getStatus()   { return status; }

    public void setStatus(String status) { this.status = status; }
}
