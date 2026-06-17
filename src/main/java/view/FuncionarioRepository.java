package view;

import dao.ConexaoDB;
import model.Funcionario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FuncionarioRepository {

    // ------------------------------------------------------------------
    // Listagem
    // ------------------------------------------------------------------

    public List<Funcionario> listarTodos() {
        String sql = "SELECT id, nome, cargo, matricula_funcional, email, telefone FROM funcionario ORDER BY nome";
        List<Funcionario> lista = new ArrayList<>();
        try (Connection conn = ConexaoDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar funcionários: " + e.getMessage());
        }
        return lista;
    }

    // ------------------------------------------------------------------
    // Busca por ID
    // ------------------------------------------------------------------

    public Optional<Funcionario> buscarPorId(int id) {
        String sql = "SELECT id, nome, cargo, matricula_funcional, email, telefone FROM funcionario WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar funcionário por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    // ------------------------------------------------------------------
    // Busca por nome ou matrícula funcional
    // ------------------------------------------------------------------

    public List<Funcionario> buscarPorNomeOuMatricula(String termo) {
        String sql = "SELECT id, nome, cargo, matricula_funcional, email, telefone FROM funcionario " +
                "WHERE LOWER(nome) LIKE ? OR LOWER(matricula_funcional) LIKE ? ORDER BY nome";
        List<Funcionario> resultado = new ArrayList<>();
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
            System.out.println("Erro ao buscar funcionários: " + e.getMessage());
        }
        return resultado;
    }

    // ------------------------------------------------------------------
    // Salvar (INSERT ou UPDATE)
    // ------------------------------------------------------------------

    public Funcionario salvar(Funcionario funcionario) {
        if (funcionario.getId() == 0) {
            return inserir(funcionario);
        } else {
            return atualizar(funcionario);
        }
    }

    private Funcionario inserir(Funcionario funcionario) {
        String sql = "INSERT INTO funcionario (nome, cargo, matricula_funcional, email, telefone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, funcionario.getNome());
            ps.setString(2, funcionario.getCargo());
            ps.setString(3, funcionario.getMatricula());
            ps.setString(4, funcionario.getEmail());
            ps.setString(5, funcionario.getTelefone());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    funcionario.setId(keys.getInt(1));
                }
            }
            System.out.println("Funcionário cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar funcionário: " + e.getMessage());
        }
        return funcionario;
    }

    private Funcionario atualizar(Funcionario funcionario) {
        String sql = "UPDATE funcionario SET nome = ?, cargo = ?, matricula_funcional = ?, email = ?, telefone = ? WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, funcionario.getNome());
            ps.setString(2, funcionario.getCargo());
            ps.setString(3, funcionario.getMatricula());
            ps.setString(4, funcionario.getEmail());
            ps.setString(5, funcionario.getTelefone());
            ps.setInt(6, funcionario.getId());
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("Funcionário atualizado com sucesso!");
            } else {
                System.out.println("Funcionário não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao atualizar funcionário: " + e.getMessage());
        }
        return funcionario;
    }

    // ------------------------------------------------------------------
    // Remover
    // ------------------------------------------------------------------

    public boolean remover(int id) {
        String sql = "DELETE FROM funcionario WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("Funcionário removido com sucesso!");
                return true;
            } else {
                System.out.println("Funcionário não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao remover funcionário: " + e.getMessage());
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Verificar matrícula funcional duplicada
    // ------------------------------------------------------------------

    public boolean existeMatricula(String matricula, int idIgnorar) {
        String sql = "SELECT COUNT(*) FROM funcionario WHERE LOWER(matricula_funcional) = LOWER(?) AND id <> ?";
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
            System.out.println("Erro ao verificar matrícula funcional: " + e.getMessage());
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Mapeamento ResultSet -> model.Funcionario
    // ------------------------------------------------------------------

    private Funcionario mapear(ResultSet rs) throws SQLException {
        return new Funcionario(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("cargo"),
                rs.getString("matricula_funcional"),
                rs.getString("email"),
                rs.getString("telefone")
        );
    }
}
