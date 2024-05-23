package DAO;
/**
 *
 * @author renan
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexão {
    private static final String URL = "jdbc:postgresql://localhost:5432/ProjetoJava";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1213";

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            return null;
        }
    }
}
