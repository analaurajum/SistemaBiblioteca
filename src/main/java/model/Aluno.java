package model;

import java.io.Serializable;

public class Aluno extends Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String curso;

    public Aluno(int id, String nome, String matricula, String curso, String email, String telefone) {
        super(id, nome, matricula, email, telefone);
        this.curso = curso;
    }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    @Override
    public String toString() {
        return "model.Aluno{" +
                "curso='" + curso + '\'' +
                '}';
    }
}
