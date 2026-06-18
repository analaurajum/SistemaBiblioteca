package view;

import dao.ConexaoDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Aluno;

public class AlunoRepository {

    public List<Aluno> listarTodos() {
        String sql = "SELECT id, nome, matricula, curso, email, telefone FROM aluno ORDER BY nome";
        List<Aluno> alunos = new ArrayList<>();
        try (Connection conn = ConexaoDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                alunos.add(mapear(rs));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar alunos: " + e.getMessage());
        }
        return alunos;
    }

    public Optional<Aluno> buscarPorId(int id) {
        String sql = "SELECT id, nome, matricula, curso, email, telefone FROM aluno WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar aluno por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Aluno> buscarPorNomeOuMatricula(String termo) {
        String sql = "SELECT id, nome, matricula, curso, email, telefone FROM aluno " +
                "WHERE LOWER(nome) LIKE ? OR LOWER(matricula) LIKE ? ORDER BY nome";
        List<Aluno> resultado = new ArrayList<>();
        String filtro = "%" + termo.toLowerCase().trim() + "%";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar alunos: " + e.getMessage());
        }
        return resultado;
    }

    public Aluno salvar(Aluno aluno) {
        if (aluno.getId() == 0) {
            return inserir(aluno);
        } else {
            return atualizar(aluno);
        }
    }

    private Aluno inserir(Aluno aluno) {
        String sql = "INSERT INTO aluno (nome, matricula, curso, email, telefone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, aluno.getNome());
            ps.setString(2, aluno.getMatricula());
            ps.setString(3, aluno.getCurso());
            ps.setString(4, aluno.getEmail());
            ps.setString(5, aluno.getTelefone());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    aluno.setId(keys.getInt(1));
                }
            }
            System.out.println("Aluno cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar aluno: " + e.getMessage());
        }
        return aluno;
    }

    private Aluno atualizar(Aluno aluno) {
        String sql = "UPDATE aluno SET nome = ?, matricula = ?, curso = ?, email = ?, telefone = ? WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, aluno.getNome());
            ps.setString(2, aluno.getMatricula());
            ps.setString(3, aluno.getCurso());
            ps.setString(4, aluno.getEmail());
            ps.setString(5, aluno.getTelefone());
            ps.setInt(6, aluno.getId());
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("Aluno atualizado com sucesso!");
            } else {
                System.out.println("Aluno não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao atualizar aluno: " + e.getMessage());
        }
        return aluno;
    }

    public boolean remover(int id) {
        String sql = "DELETE FROM aluno WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("Aluno removido com sucesso!");
                return true;
            } else {
                System.out.println("Aluno não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao remover aluno: " + e.getMessage());
        }
        return false;
    }

    public boolean existeMatricula(String matricula, int idIgnorar) {
        String sql = "SELECT COUNT(*) FROM aluno WHERE LOWER(matricula) = LOWER(?) AND id <> ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricula);
            ps.setInt(2, idIgnorar);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao verificar matrícula: " + e.getMessage());
        }
        return false;
    }

    private Aluno mapear(ResultSet rs) throws SQLException {
        return new Aluno(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("matricula"),
                rs.getString("curso"),
                rs.getString("email"),
                rs.getString("telefone")
        );
    }

    public boolean existeCadastroAlunoEmReserva(int id) {
        String sql = "SELECT COUNT(*) FROM reserva WHERE aluno_id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao verificar matrícula: " + e.getMessage());
        }
        return false;
    }

    public boolean existeCadastroAlunoEmEmprestimo(int id) {
        String sql = "SELECT COUNT(*) FROM emprestimo WHERE aluno_id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao verificar matrícula: " + e.getMessage());
        }
        return false;
    }
}