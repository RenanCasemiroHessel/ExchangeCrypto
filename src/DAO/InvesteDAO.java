

package DAO;
/**
 *
 * @author renan
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import model.Extrato;
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
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Erro ao verificar investimentos: " + e.getMessage());
            return false;
        }
    }
    public static double getSaldoUsuario(String cpf, Connection conn, String coluna) throws SQLException {
        String sql = "SELECT " + coluna + " FROM esquematicos.pessoas WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(coluna);
                } else {
                    throw new SQLException("Usuário não encontrado.");
                }
            }
        }
    }

    public static void atualizarSaldoUsuario(String cpf, double novoSaldo, Connection conn, String coluna) throws SQLException {
        String sql = "UPDATE esquematicos.pessoas SET " + coluna + " = ? WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, novoSaldo);
            stmt.setString(2, cpf);
            stmt.executeUpdate();
        }
    }

    public static void atualizarSaldoCriptomoeda(String cpf, String coluna, double novoSaldo, Connection conn) throws SQLException {
        String sql = "UPDATE esquematicos.pessoas SET " + coluna + " = ? WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, novoSaldo);
            stmt.setString(2, cpf);
            stmt.executeUpdate();
        }
    }

    public static double getCotacaoAtual(String tipoMoeda, Connection conn) throws SQLException {
        String sql = "SELECT preco FROM esquematicos.valores WHERE moedas = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipoMoeda);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("preco");
                } else {
                    throw new SQLException("Não foi possível encontrar a cotação para a moeda: " + tipoMoeda);
                }
            }
        }
    }

    public static void registrarExtrato(Extrato extrato, Connection conn) throws SQLException {
        extrato.registrar(conn);
    }

    public static double getSaldoCriptomoeda(String cpf, String coluna, Connection conn) throws SQLException {
        String sql = "SELECT " + coluna + " FROM esquematicos.pessoas WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(coluna);
                } else {
                    throw new SQLException("Usuário não encontrado.");
                }
            }
        }
    }

    public static Map<String, Double> obterSaldos(String cpf) throws SQLException {
        Map<String, Double> saldos = new HashMap<>();
        try (Connection conn = Conexão.conectar()) {
            String sql = "SELECT saldoreal, saldobtc, saldoeth, saldoxrp FROM esquematicos.pessoas WHERE cpf = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, cpf);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    saldos.put("Reais", rs.getDouble("saldoreal"));
                    saldos.put("Bitcoin", rs.getDouble("saldobtc"));
                    saldos.put("Ethereum", rs.getDouble("saldoeth"));
                    saldos.put("Ripple", rs.getDouble("saldoxrp"));
                }
            }
        }
        return saldos;
    }

    public static void depositar(String cpf, double valor, Connection conn) throws SQLException {
        String sql = "SELECT saldoreal, saldobtc, saldoeth, saldoxrp FROM esquematicos.pessoas WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double saldoAtualReal = rs.getDouble("saldoreal");
                double saldoAtualBtc = rs.getDouble("saldobtc");
                double saldoAtualEth = rs.getDouble("saldoeth");
                double saldoAtualXrp = rs.getDouble("saldoxrp");

                double novoSaldoReal = saldoAtualReal + valor;

                try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE esquematicos.pessoas SET saldoreal = ? WHERE cpf = ?")) {
                    updateStmt.setDouble(1, novoSaldoReal);
                    updateStmt.setString(2, cpf);
                    updateStmt.executeUpdate();

                    Extrato extrato = new Extrato(cpf, "+", valor, "REAL", 0.0, 0, novoSaldoReal, saldoAtualBtc, saldoAtualEth, saldoAtualXrp);
                    extrato.registrar(conn);
                }
            }
        }
    }

    public static void sacar(String cpf, double valor, Connection conn) throws SQLException {
        String sql = "SELECT saldoreal, saldobtc, saldoeth, saldoxrp FROM esquematicos.pessoas WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double saldoAtual = rs.getDouble("saldoreal");
                double saldoBtc = rs.getDouble("saldobtc");
                double saldoEth = rs.getDouble("saldoeth");
                double saldoXrp = rs.getDouble("saldoxrp");

                if (saldoAtual >= valor) {
                    double novoSaldo = saldoAtual - valor;
                    sql = "UPDATE esquematicos.pessoas SET saldoreal = ? WHERE cpf = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(sql)) {
                        updateStmt.setDouble(1, novoSaldo);
                        updateStmt.setString(2, cpf);
                        updateStmt.executeUpdate();

                        Extrato extrato = new Extrato(cpf, "-", valor, "REAL", 0.0, 0, novoSaldo, saldoBtc, saldoEth, saldoXrp);
                        extrato.registrar(conn);
                    }
                } else {
                    throw new SQLException("Saldo insuficiente.");
                }
            } else {
                throw new SQLException("Usuário não encontrado.");
            }
        }
    }

    public static boolean validarSenha(String cpf, String senha) throws SQLException {
        try (Connection conn = Conexão.conectar();
             PreparedStatement stmt = conn.prepareStatement("SELECT cpf FROM esquematicos.pessoas WHERE cpf = ? AND senha = ?")) {
            stmt.setString(1, cpf);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public static String getNomeUsuario(String cpf) throws SQLException {
        try (Connection conn = Conexão.conectar();
             PreparedStatement stmt = conn.prepareStatement("SELECT nome FROM esquematicos.pessoas WHERE cpf = ?")) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nome");
            } else {
                throw new SQLException("Usuário não encontrado.");
            }
        }
    }

    public static ResultSet obterExtrato(String cpf, Connection conn) throws SQLException {
        String sqlExtrato = "SELECT data, tipo, valor, moeda, cotacao, taxa, saldoreal, saldobtc, saldoeth, saldoxrp FROM esquematicos.extrato WHERE cpf = ? ORDER BY data DESC";
        PreparedStatement stmtExtrato = conn.prepareStatement(sqlExtrato);
        stmtExtrato.setString(1, cpf);
        return stmtExtrato.executeQuery();
    }
}


