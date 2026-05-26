// Classe Reserva
import java.util.Date;

public class Reserva {
    private int id;
    private Date dataReserva;
    private String status;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getDataReserva() { return dataReserva; }
    public void setDataReserva(Date dataReserva) { this.dataReserva = dataReserva; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
