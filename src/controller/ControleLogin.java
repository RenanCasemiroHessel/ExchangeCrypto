
package controller;
/**
 *
 * @author renan
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import DAO.Conexão;
import javax.swing.JOptionPane;


public class ControleLogin {
    private static String cpfUsuarioLogado;

    public static void setCpfUsuarioLogado(String cpf) {
        cpfUsuarioLogado = cpf;
    }

    public static String getCpfUsuarioLogado() {
        return cpfUsuarioLogado;
    }
    
    public boolean autenticar(String cpf, String senha) {
        if (senha.length() < 6) {
            JOptionPane.showMessageDialog(null, "A senha precisa ter mais de 6 dígitos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String sql = "SELECT * FROM esquematicos.pessoas WHERE cpf = ? AND senha = ?";
        try (Connection conn = Conexão.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar usuário: " + e.getMessage());
            return false;
        }
    }
}
