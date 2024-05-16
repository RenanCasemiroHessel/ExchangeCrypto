/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import DAO.Conexão;
import java.util.Map;
import java.util.HashMap;
/**
 *
 * @author renan
 */
public class Controle {
    public static void depositar(double valor) {
    String cpf = ControleLogin.getCpfUsuarioLogado();
    try (Connection conn = Conexão.conectar()) {
        conn.setAutoCommit(false);
        try (PreparedStatement stmt = conn.prepareStatement("SELECT saldoreal FROM esquematicos.pessoas WHERE cpf = ?")) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double saldoAtual = rs.getDouble("saldoreal");
                saldoAtual += valor;

                try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE esquematicos.pessoas SET saldoreal = ? WHERE cpf = ?")) {
                    updateStmt.setDouble(1, saldoAtual);
                    updateStmt.setString(2, cpf);
                    updateStmt.executeUpdate();
                    conn.commit();
                    JOptionPane.showMessageDialog(null, "Depósito realizado com sucesso!");
                }
            }
        } catch (SQLException ex) {
            conn.rollback(); 
            throw ex;
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados: " + ex.getMessage());
    }
}
    
    
    public static Map<String, Double> obterSaldos(String cpf) {
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
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Erro ao acessar o banco de dados: " + ex.getMessage());
    }
    return saldos;
}
    public static String sacar(double valor, String cpf) {
    try (Connection conn = Conexão.conectar()) {
        conn.setAutoCommit(false);
        String sql = "SELECT saldoreal FROM esquematicos.pessoas WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double saldoAtual = rs.getDouble("saldoreal");
                    if (saldoAtual >= valor) {
                        saldoAtual -= valor;
                        sql = "UPDATE esquematicos.pessoas SET saldoreal = ? WHERE cpf = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(sql)) {
                            updateStmt.setDouble(1, saldoAtual);
                            updateStmt.setString(2, cpf);
                            updateStmt.executeUpdate();
                            conn.commit();
                            return "Saque realizado com sucesso!";
                        }
                    } else {
                        conn.rollback();
                        return "Saldo insuficiente.";
                    }
                }
            }
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        }
    } catch (SQLException ex) {
        return "Erro ao acessar o banco de dados: " + ex.getMessage();
    }
    return "Usuário não encontrado.";
}
}
