package model;

import java.io.Serializable;

public class Funcionario extends Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String cargo;

    public Funcionario(int id, String nome, String cargo, String matricula, String email, String telefone) {
        super(id, nome, matricula, email, telefone);
        this.cargo = cargo;
    }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    @Override
    public String toString() {
        return "model.Funcionario{" +
                "cargo='" + cargo + '\'' +
                '}';
    }
}
