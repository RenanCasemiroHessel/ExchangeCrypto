
package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import DAO.Conexão;


public class ControleLogin {
    private static String cpfUsuarioLogado;

    public static void setCpfUsuarioLogado(String cpf) {
        cpfUsuarioLogado = cpf;
    }

    public static String getCpfUsuarioLogado() {
        return cpfUsuarioLogado;
    }
    public boolean autenticar(String cpf, String senha) {
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
