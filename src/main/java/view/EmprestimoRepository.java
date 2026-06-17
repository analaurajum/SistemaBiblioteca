package view;

import dao.ConexaoDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Emprestimo;

public class EmprestimoRepository {

    public List<Emprestimo> listarTodos() {
        atualizarStatusAtrasados();
        String sql = "SELECT id, aluno_id, livro_id, funcionario_id, data_emprestimo, " +
                "data_prevista_devolucao, data_devolucao, status FROM emprestimo ORDER BY id DESC";
        List<Emprestimo> lista = new ArrayList<>();
        try (Connection conn = ConexaoDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar empréstimos: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Emprestimo> buscarPorId(int id) {
        String sql = "SELECT id, aluno_id, livro_id, funcionario_id, data_emprestimo, " +
                "data_prevista_devolucao, data_devolucao, status FROM emprestimo WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar empréstimo por ID: " + e.getMessage());
        }
        return Optional.empty();
    }


    public List<Emprestimo> buscarAtivosPorLivro(int livroId) {
        String sql = "SELECT id, aluno_id, livro_id, funcionario_id, data_emprestimo, " +
                "data_prevista_devolucao, data_devolucao, status FROM emprestimo " +
                "WHERE livro_id = ? AND data_devolucao IS NULL";
        List<Emprestimo> resultado = new ArrayList<>();
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, livroId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar empréstimos ativos por livro: " + e.getMessage());
        }
        return resultado;
    }

    public List<Emprestimo> buscarPorAluno(int alunoId) {
        String sql = "SELECT id, aluno_id, livro_id, funcionario_id, data_emprestimo, " +
                "data_prevista_devolucao, data_devolucao, status FROM emprestimo " +
                "WHERE aluno_id = ? ORDER BY id DESC";
        List<Emprestimo> resultado = new ArrayList<>();
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alunoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar empréstimos por aluno: " + e.getMessage());
        }
        return resultado;
    }

    public Emprestimo salvar(Emprestimo emprestimo) {
        if (emprestimo.getId() == 0) {
            return inserir(emprestimo);
        } else {
            return atualizar(emprestimo);
        }
    }

    private Emprestimo inserir(Emprestimo emprestimo) {
        String sql = "INSERT INTO emprestimo (aluno_id, livro_id, funcionario_id, data_emprestimo, " +
                "data_prevista_devolucao, data_devolucao, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, emprestimo.getAlunoId());
            ps.setInt(2, emprestimo.getLivroId());
            ps.setInt(3, emprestimo.getFuncionarioId());
            ps.setDate(4, Date.valueOf(emprestimo.getDataEmprestimo()));
            ps.setDate(5, Date.valueOf(emprestimo.getDataPrevistaDevolucao()));
            ps.setDate(6, emprestimo.getDataDevolucao() != null
                    ? Date.valueOf(emprestimo.getDataDevolucao()) : null);
            ps.setString(7, emprestimo.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    emprestimo.setId(keys.getInt(1));
                }
            }
            System.out.println("Empréstimo registrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao registrar empréstimo: " + e.getMessage());
        }
        return emprestimo;
    }

    private Emprestimo atualizar(Emprestimo emprestimo) {
        String sql = "UPDATE emprestimo SET aluno_id = ?, livro_id = ?, funcionario_id = ?, " +
                "data_emprestimo = ?, data_prevista_devolucao = ?, data_devolucao = ?, status = ? " +
                "WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, emprestimo.getAlunoId());
            ps.setInt(2, emprestimo.getLivroId());
            ps.setInt(3, emprestimo.getFuncionarioId());
            ps.setDate(4, Date.valueOf(emprestimo.getDataEmprestimo()));
            ps.setDate(5, Date.valueOf(emprestimo.getDataPrevistaDevolucao()));
            ps.setDate(6, emprestimo.getDataDevolucao() != null
                    ? Date.valueOf(emprestimo.getDataDevolucao()) : null);
            ps.setString(7, emprestimo.getStatus());
            ps.setInt(8, emprestimo.getId());
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("Empréstimo atualizado com sucesso!");
            } else {
                System.out.println("Empréstimo não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao atualizar empréstimo: " + e.getMessage());
        }
        return emprestimo;
    }

    public boolean remover(int id) {
        String sql = "DELETE FROM emprestimo WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("Empréstimo removido com sucesso!");
                return true;
            } else {
                System.out.println("Empréstimo não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao remover empréstimo: " + e.getMessage());
        }
        return false;
    }

    private void atualizarStatusAtrasados() {
        String sql = "UPDATE emprestimo SET status = 'ATRASADO' " +
                "WHERE data_devolucao IS NULL AND data_prevista_devolucao < CURDATE() AND status <> 'ATRASADO'";
        try (Connection conn = ConexaoDB.conectar();
             Statement st = conn.createStatement()) {
            int atualizados = st.executeUpdate(sql);
            if (atualizados > 0) {
                System.out.println(atualizados + " empréstimo(s) marcado(s) como ATRASADO.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao atualizar status de atrasos: " + e.getMessage());
        }
    }

    private Emprestimo mapear(ResultSet rs) throws SQLException {
        Date dataDev = rs.getDate("data_devolucao");
        return new Emprestimo(
                rs.getInt("id"),
                rs.getInt("aluno_id"),
                rs.getInt("livro_id"),
                rs.getInt("funcionario_id"),
                rs.getDate("data_emprestimo").toLocalDate(),
                rs.getDate("data_prevista_devolucao").toLocalDate(),
                dataDev != null ? dataDev.toLocalDate() : null,
                rs.getString("status")
        );
    }
}