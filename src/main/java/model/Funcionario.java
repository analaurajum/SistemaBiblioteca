package model;

public class Funcionario extends Pessoa {
    private String cargo;

    public void registrarEmprestimo() {
        System.out.println("Funcionário " + getNome() + " registrou um empréstimo.");
    }

    public void cadastrarLivro() {
        System.out.println("Funcionário " + getNome() + " cadastrou um livro.");
    }

    public void exibirFuncionario(){
        System.out.println("ID: "+ getId());
        System.out.println("Nome: "+ getNome());
        System.out.println("CPF: "+ getCpf());
        System.out.println("Nome: "+ getEmail());
        System.out.println("Cargo: "+ cargo);

    }



    public Funcionario(int id, String nome, String cpf, String email, String cargo) {
        super(id, nome, cpf, email);
        this.cargo = cargo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

}