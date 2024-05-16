
package DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author renan
 */

public class InvesteDAO {
    public boolean verificarInvestimento(String cpf) {
        String sql = "SELECT * FROM Usuarios.Usuario WHERE cpf = ?";
        try (Connection conn = Conexão.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Retorna true se encontrou um investimento para o CPF
        } catch (SQLException e) {
            System.out.println("Erro ao verificar investimentos: " + e.getMessage());
            return false;
        }
    }

}


