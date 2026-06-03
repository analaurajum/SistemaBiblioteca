package model;

public class Usuario extends Pessoa {
    private String matricula;
    private String tipo;


    public void realizarReserva() {
        System.out.println("Reserva realizada pelo usuário " + getNome());
    }

    public void solicitarEmprestimo() {
        System.out.println("Empréstimo solicitado pelo usuário " + getNome());
    }

    public Usuario(int id, String nome, String cpf, String email, String matricula, String tipo) {
        super(id, nome, cpf, email);
        this.matricula = matricula;
        this.tipo = tipo;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}