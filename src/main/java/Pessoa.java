public class Pessoa {
    private int id;
    private String nome, cpf, email;

    public Pessoa(int id, String nome, String cpf, String email){
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
    }

    public void exibirPessoa(){
        System.out.println("ID: "+ id);
        System.out.println("Cliente: "+ nome);
        System.out.println("CPF: "+ cpf);
        System.out.println("E-mail: "+ email);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
