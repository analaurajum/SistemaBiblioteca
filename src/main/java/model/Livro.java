package model;

import java.io.Serializable;

public class Livro implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String isbn;
    private String titulo;
    private String autor;
    private String anoPublicacao;
    private String capaUrl;
    private int quantidadeTotal;
    private int quantidadeDisponivel;

    public Livro(int id, String isbn, String titulo, String autor, String anoPublicacao, String capaUrl, int quantidadeTotal, int quantidadeDisponivel) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
        this.capaUrl = capaUrl;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(String anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public String getCapaUrl() { return capaUrl; }
    public void setCapaUrl(String capaUrl) { this.capaUrl = capaUrl; }

    public int getQuantidadeTotal() { return quantidadeTotal; }
    public void setQuantidadeTotal(int quantidadeTotal) { this.quantidadeTotal = quantidadeTotal; }

    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(int quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }

    @Override
    public String toString() {
        return "model.Livro{" + "id=" + id + ", titulo='" + titulo + "', isbn='" + isbn + "'}";
    }
}
