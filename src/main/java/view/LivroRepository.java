package view;

import dao.ConexaoDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Livro;

public class LivroRepository {

    public List<Livro> listarTodos() {
        String sql = "SELECT id, isbn, titulo, autor, ano_publicacao, capa_url, " +
                "quantidade_total, quantidade_disponivel FROM livro ORDER BY titulo";
        List<Livro> livros = new ArrayList<>();
        try (Connection conn = ConexaoDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                livros.add(mapear(rs));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar livros: " + e.getMessage());
        }
        return livros;
    }

    public Optional<Livro> buscarPorId(int id) {
        String sql = "SELECT id, isbn, titulo, autor, ano_publicacao, capa_url, " +
                "quantidade_total, quantidade_disponivel FROM livro WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar livro por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Livro> buscarPorIsbn(String isbn) {
        String sql = "SELECT id, isbn, titulo, autor, ano_publicacao, capa_url, " +
                "quantidade_total, quantidade_disponivel FROM livro WHERE LOWER(isbn) = LOWER(?)";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar livro por ISBN: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Livro> buscarPorTituloOuAutorOuIsbn(String termo) {
        String sql = "SELECT id, isbn, titulo, autor, ano_publicacao, capa_url, " +
                "quantidade_total, quantidade_disponivel FROM livro " +
                "WHERE LOWER(titulo) LIKE ? OR LOWER(autor) LIKE ? OR LOWER(isbn) LIKE ? " +
                "ORDER BY titulo";
        List<Livro> resultado = new ArrayList<>();
        String filtro = "%" + termo.toLowerCase().trim() + "%";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar livros: " + e.getMessage());
        }
        return resultado;
    }


    public Livro salvar(Livro livro) {
        if (livro.getId() == 0) {
            return inserir(livro);
        } else {
            return atualizar(livro);
        }
    }

    private Livro inserir(Livro livro) {
        String sql = "INSERT INTO livro (isbn, titulo, autor, ano_publicacao, capa_url, " +
                "quantidade_total, quantidade_disponivel) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, livro.getIsbn());
            ps.setString(2, livro.getTitulo());
            ps.setString(3, livro.getAutor());
            ps.setString(4, livro.getAnoPublicacao());
            ps.setString(5, livro.getCapaUrl());
            ps.setInt(6, livro.getQuantidadeTotal());
            ps.setInt(7, livro.getQuantidadeDisponivel());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    livro.setId(keys.getInt(1));
                }
            }
            System.out.println("model.Livro cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar livro: " + e.getMessage());
        }
        return livro;
    }

    private Livro atualizar(Livro livro) {
        String sql = "UPDATE livro SET isbn = ?, titulo = ?, autor = ?, ano_publicacao = ?, " +
                "capa_url = ?, quantidade_total = ?, quantidade_disponivel = ? WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, livro.getIsbn());
            ps.setString(2, livro.getTitulo());
            ps.setString(3, livro.getAutor());
            ps.setString(4, livro.getAnoPublicacao());
            ps.setString(5, livro.getCapaUrl());
            ps.setInt(6, livro.getQuantidadeTotal());
            ps.setInt(7, livro.getQuantidadeDisponivel());
            ps.setInt(8, livro.getId());
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("model.Livro atualizado com sucesso!");
            } else {
                System.out.println("model.Livro não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao atualizar livro: " + e.getMessage());
        }
        return livro;
    }


    public boolean remover(int id) {
        String sql = "DELETE FROM livro WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("model.Livro removido com sucesso!");
                return true;
            } else {
                System.out.println("model.Livro não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao remover livro: " + e.getMessage());
        }
        return false;
    }

    private Livro mapear(ResultSet rs) throws SQLException {
        return new Livro(
                rs.getInt("id"),
                rs.getString("isbn"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("ano_publicacao"),
                rs.getString("capa_url"),
                rs.getInt("quantidade_total"),
                rs.getInt("quantidade_disponivel")
        );
    }
}