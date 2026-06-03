package model;

import java.util.Date;

public class Reserva {
    private int id;
    private Date dataReserva;
    private String status;
    private Usuario usuario;
    private Livro livro;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getDataReserva() { return dataReserva; }
    public void setDataReserva(Date dataReserva) { this.dataReserva = dataReserva; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }
}
