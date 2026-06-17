package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int alunoId;
    private int livroId;
    private LocalDate dataReserva;
    private LocalDate dataValidade;
    private String status; // "ATIVA", "CANCELADA", "CONVERTIDA", "EXPIRADA"

    public Reserva(int id, int alunoId, int livroId, LocalDate dataReserva, LocalDate dataValidade, String status) {
        this.id = id;
        this.alunoId = alunoId;
        this.livroId = livroId;
        this.dataReserva = dataReserva;
        this.dataValidade = dataValidade;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAlunoId() { return alunoId; }
    public void setAlunoId(int alunoId) { this.alunoId = alunoId; }

    public int getLivroId() { return livroId; }
    public void setLivroId(int livroId) { this.livroId = livroId; }

    public LocalDate getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDate dataReserva) { this.dataReserva = dataReserva; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "model.Reserva{" + "id=" + id + ", alunoId=" + alunoId + ", livroId=" + livroId + ", status='" + status + "'}";
    }
}
