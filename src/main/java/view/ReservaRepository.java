package view;

import dao.ConexaoDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Reserva;

public class ReservaRepository {

    public List<Reserva> listarTodos() {
        atualizarExpiradas();
        String sql = "SELECT id, aluno_id, livro_id, data_reserva, data_validade, status " +
                "FROM reserva ORDER BY id DESC";
        List<Reserva> lista = new ArrayList<>();
        try (Connection conn = ConexaoDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar reservas: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Reserva> buscarPorId(int id) {
        String sql = "SELECT id, aluno_id, livro_id, data_reserva, data_validade, status " +
                "FROM reserva WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar reserva por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Reserva> buscarAtivasPorLivro(int livroId) {
        atualizarExpiradas();
        String sql = "SELECT id, aluno_id, livro_id, data_reserva, data_validade, status " +
                "FROM reserva WHERE livro_id = ? AND status = 'ATIVA'";
        List<Reserva> resultado = new ArrayList<>();
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, livroId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar reservas ativas por livro: " + e.getMessage());
        }
        return resultado;
    }

    public Reserva salvar(Reserva reserva) {
        if (reserva.getId() == 0) {
            return inserir(reserva);
        } else {
            return atualizar(reserva);
        }
    }

    private Reserva inserir(Reserva reserva) {
        String sql = "INSERT INTO reserva (aluno_id, livro_id, data_reserva, data_validade, status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reserva.getAlunoId());
            ps.setInt(2, reserva.getLivroId());
            ps.setDate(3, Date.valueOf(reserva.getDataReserva()));
            ps.setDate(4, Date.valueOf(reserva.getDataValidade()));
            ps.setString(5, reserva.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    reserva.setId(keys.getInt(1));
                }
            }
            System.out.println("model.Reserva criada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao criar reserva: " + e.getMessage());
        }
        return reserva;
    }

    private Reserva atualizar(Reserva reserva) {
        String sql = "UPDATE reserva SET aluno_id = ?, livro_id = ?, data_reserva = ?, " +
                "data_validade = ?, status = ? WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reserva.getAlunoId());
            ps.setInt(2, reserva.getLivroId());
            ps.setDate(3, Date.valueOf(reserva.getDataReserva()));
            ps.setDate(4, Date.valueOf(reserva.getDataValidade()));
            ps.setString(5, reserva.getStatus());
            ps.setInt(6, reserva.getId());
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("model.Reserva atualizada com sucesso!");
            } else {
                System.out.println("model.Reserva não encontrada.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao atualizar reserva: " + e.getMessage());
        }
        return reserva;
    }

    public boolean remover(int id) {
        String sql = "DELETE FROM reserva WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("model.Reserva removida com sucesso!");
                return true;
            } else {
                System.out.println("model.Reserva não encontrada.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao remover reserva: " + e.getMessage());
        }
        return false;
    }

    private void atualizarExpiradas() {
        String sql = "UPDATE reserva SET status = 'EXPIRADA' " +
                "WHERE status = 'ATIVA' AND data_validade < CURDATE()";
        try (Connection conn = ConexaoDB.conectar();
             Statement st = conn.createStatement()) {
            int atualizadas = st.executeUpdate(sql);
            if (atualizadas > 0) {
                System.out.println(atualizadas + " reserva(s) marcada(s) como EXPIRADA.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao atualizar reservas expiradas: " + e.getMessage());
        }
    }

    private Reserva mapear(ResultSet rs) throws SQLException {
        return new Reserva(
                rs.getInt("id"),
                rs.getInt("aluno_id"),
                rs.getInt("livro_id"),
                rs.getDate("data_reserva").toLocalDate(),
                rs.getDate("data_validade").toLocalDate(),
                rs.getString("status")
        );
    }
}