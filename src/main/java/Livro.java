public class Livro {
    private int id;
    private String titulo, autor;
    private boolean disponivel;


    public void alterarDisponibilidade(boolean status) {
        this.disponivel = status;
    }

    public Livro(int id, String titulo, String autor){
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.disponivel = true;
    }

    public void exibirLivros(){
        System.out.println("ID: "+ id);
        System.out.println("Título: "+ titulo);
        System.out.println("Autor: "+ autor);
        System.out.println("Disponível: "+ disponivel);
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public boolean isDisponivel() {
        return disponivel;
    }
}