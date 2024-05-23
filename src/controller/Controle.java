
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
/**
 *
 * @author renan
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import DAO.Conexão;
import DAO.InvesteDAO;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import model.Carteira;
import model.Extrato;

public class Controle {
    private static Carteira carteira = new Carteira();
    
    private static String getDatabaseColumnForCurrency(String moeda) {
        switch (moeda.toLowerCase()) {
            case "bitcoin":
                return "saldobtc";
            case "ethereum":
                return "saldoeth";
            case "ripple":
                return "saldoxrp";
            default:
                throw new IllegalArgumentException("Moeda desconhecida: " + moeda);
        }
    }

    public static void depositar(double valor) {
        String cpf = ControleLogin.getCpfUsuarioLogado();
        try (Connection conn = Conexão.conectar()) {
            conn.setAutoCommit(false);
            InvesteDAO.depositar(cpf, valor, conn);
            conn.commit();
            JOptionPane.showMessageDialog(null, "Depósito realizado com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados: " + ex.getMessage());
        }
    }

    public static Map<String, Double> obterSaldos(String cpf) {
        try {
            return InvesteDAO.obterSaldos(cpf);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao acessar o banco de dados: " + ex.getMessage());
            return new HashMap<>();
        }
    }

    public static String sacar(double valor, String cpf) {
        try (Connection conn = Conexão.conectar()) {
            conn.setAutoCommit(false);
            InvesteDAO.sacar(cpf, valor, conn);
            conn.commit();
            return "Saque realizado com sucesso!";
        } catch (SQLException ex) {
            return "Erro ao acessar o banco de dados: " + ex.getMessage();
        }
    }

    public static String atualizarCotacoes() {
        StringBuilder resultado = new StringBuilder("Cotações atualizadas:\n");
        Random rand = new Random();
        Connection conn = null;

        try {
            conn = Conexão.conectar();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement("SELECT moedas, preco FROM esquematicos.valores");
                 ResultSet rs = stmt.executeQuery()) {

                try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE esquematicos.valores SET preco = ? WHERE moedas = ?")) {
                    while (rs.next()) {
                        String moeda = rs.getString("moedas");
                        double precoAntigo = rs.getDouble("preco");
                        double variacao = 1 + (rand.nextDouble() * 0.1 - 0.05);
                        double novoPreco = precoAntigo * variacao;
                        updateStmt.setDouble(1, Math.round(novoPreco * 100.0) / 100.0);
                        updateStmt.setString(2, moeda);
                        updateStmt.executeUpdate();

                        resultado.append(String.format("%s: R$ %.2f\n", moeda, Math.round(novoPreco * 100.0) / 100.0));
                    }
                    conn.commit();
                }
            }
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao tentar rollback: " + e.getMessage());
                }
            }
            JOptionPane.showMessageDialog(null, "Erro ao acessar o banco de dados: " + ex.getMessage());
            return "Falha ao atualizar cotações.";
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
                }
            }
        }
        return resultado.toString();
    }

    public static void validarSenha(String cpf, String senha, JTextArea textArea) {
        try (Connection conn = Conexão.conectar()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT nome, cpf, saldoreal, saldobtc, saldoeth, saldoxrp FROM esquematicos.pessoas WHERE cpf = ? AND senha = ?");
            stmt.setString(1, cpf);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String textoExibicao = String.format(
                        "Nome: %s\nCPF: %s\n\nReal: R$%.2f\nBitcoin: %.5f BTC\nEthereum: %.5f ETH\nRipple: %.5f XRP",
                        rs.getString("nome"), rs.getString("cpf"), rs.getDouble("saldoreal"), rs.getDouble("saldobtc"),
                        rs.getDouble("saldoeth"), rs.getDouble("saldoxrp")
                );
                textArea.setText(textoExibicao);
            } else {
                JOptionPane.showMessageDialog(null, "CPF ou senha incorretos!");
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao acessar o banco de dados!");
        }
    }

    public static boolean verificarSenhaCV(String cpf, String senha, JTextArea textArea) {
        try (Connection conn = Conexão.conectar()) {
            if (InvesteDAO.validarSenha(cpf, senha)) {
                exibirCotacoes(conn, textArea);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "CPF ou senha incorretos!");
                return false;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao acessar o banco de dados: " + ex.getMessage());
            return false;
        }
    }

    private static void exibirCotacoes(Connection conn, JTextArea textArea) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT moedas, preco FROM esquematicos.valores");
        ResultSet rs = stmt.executeQuery();
        StringBuilder cotacoes = new StringBuilder("Cotações Atuais:\n");

        while (rs.next()) {
            String moeda = rs.getString("moedas");
            double preco = rs.getDouble("preco");
            cotacoes.append(String.format("%s: R$ %.2f\n", moeda, preco));
        }
        textArea.setText(cotacoes.toString());
        rs.close();
        stmt.close();
    }

    public static void comprarCriptomoeda(String cpf, String tipoMoeda, double valorEmReais, Connection conn) {
        try {
            conn.setAutoCommit(false);
            String columnMoeda = getDatabaseColumnForCurrency(tipoMoeda);
            double precoMoeda = InvesteDAO.getCotacaoAtual(tipoMoeda, conn);

            if (precoMoeda <= 0) {
                JOptionPane.showMessageDialog(null, "Preço da moeda inválido. Verifique a cotação atual no banco de dados.", "Erro de Cotação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double taxaCompra = Controle.getTaxaCompra(tipoMoeda);
            double valorAposTaxa = valorEmReais * (1 - taxaCompra);
            double quantidadeMoeda = valorAposTaxa / precoMoeda;

            double saldoAtualReal = InvesteDAO.getSaldoUsuario(cpf, conn, "saldoreal");

            if (saldoAtualReal >= valorEmReais) {
                double novoSaldoReal = saldoAtualReal - valorEmReais;
                InvesteDAO.atualizarSaldoUsuario(cpf, novoSaldoReal, conn, "saldoreal");

                double novoSaldoCripto = InvesteDAO.getSaldoCriptomoeda(cpf, columnMoeda, conn) + quantidadeMoeda;
                InvesteDAO.atualizarSaldoCriptomoeda(cpf, columnMoeda, novoSaldoCripto, conn);

                double saldoAtualBtc = InvesteDAO.getSaldoUsuario(cpf, conn, "saldobtc");
                double saldoAtualEth = InvesteDAO.getSaldoUsuario(cpf, conn, "saldoeth");
                double saldoAtualXrp = InvesteDAO.getSaldoUsuario(cpf, conn, "saldoxrp");

                if (columnMoeda.equals("saldobtc")) {
                    saldoAtualBtc = novoSaldoCripto;
                } else if (columnMoeda.equals("saldoeth")) {
                    saldoAtualEth = novoSaldoCripto;
                } else if (columnMoeda.equals("saldoxrp")) {
                    saldoAtualXrp = novoSaldoCripto;
                }

                Extrato extrato = new Extrato(cpf, "+", valorEmReais, tipoMoeda, precoMoeda, taxaCompra, novoSaldoReal, saldoAtualBtc, saldoAtualEth, saldoAtualXrp);
                InvesteDAO.registrarExtrato(extrato, conn);

                conn.commit();
                JOptionPane.showMessageDialog(null, "Compra realizada com sucesso!");
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Saldo insuficiente.");
            }
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao tentar rollback: " + e.getMessage());
                }
            }
            JOptionPane.showMessageDialog(null, "Erro ao acessar o banco de dados: " + ex.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
                }
            }
        }
    }

    private static double getTaxaCompra(String tipoMoeda) {
        switch (tipoMoeda) {
            case "bitcoin":
                return carteira.getBitcoin().getTaxaCompra();
            case "ethereum":
                return carteira.getEthereum().getTaxaCompra();
            case "ripple":
                return carteira.getRipple().getTaxaCompra();
            default:
                return 0;
        }
    }

    public static void realizarCompra(JRadioButton bitcoinButton, JRadioButton ethereumButton, JRadioButton rippleButton, JTextField valorField, String cpf) {
        String tipoMoeda = null;
        if (bitcoinButton.isSelected()) tipoMoeda = "bitcoin";
        else if (ethereumButton.isSelected()) tipoMoeda = "ethereum";
        else if (rippleButton.isSelected()) tipoMoeda = "ripple";

        if (tipoMoeda != null) {
            try {
                double valorEmReais = Double.parseDouble(valorField.getText());
                Connection conn = Conexão.conectar();
                comprarCriptomoeda(cpf, tipoMoeda, valorEmReais, conn);
                conn.close();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Por favor, insira um valor válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao acessar o banco de dados: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, selecione uma criptomoeda.", "Seleção Necessária", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void venderCriptomoeda(String cpf, String tipoMoeda, double quantidade, Connection conn) throws SQLException {
        try {
            conn.setAutoCommit(false);
            String columnMoeda = getDatabaseColumnForCurrency(tipoMoeda);
            double precoMoeda = InvesteDAO.getCotacaoAtual(tipoMoeda, conn);
            double saldoMoeda = InvesteDAO.getSaldoCriptomoeda(cpf, columnMoeda, conn);
            double taxaVenda = getTaxaVenda(tipoMoeda);

            if (precoMoeda <= 0) {
                JOptionPane.showMessageDialog(null, "Preço da moeda inválido. Verifique a cotação atual.", "Erro de Cotação", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                return;
            }

            if (saldoMoeda < quantidade) {
                JOptionPane.showMessageDialog(null, "Saldo insuficiente de " + tipoMoeda, "Erro de Saldo", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                return;
            }

            double valorEmReais = quantidade * precoMoeda * (1 - taxaVenda);
            double novoSaldoMoeda = saldoMoeda - quantidade;
            double saldoRealAtual = InvesteDAO.getSaldoUsuario(cpf, conn, "saldoreal");

            InvesteDAO.atualizarSaldoCriptomoeda(cpf, columnMoeda, novoSaldoMoeda, conn);
            InvesteDAO.atualizarSaldoUsuario(cpf, saldoRealAtual + valorEmReais, conn, "saldoreal");

            double saldoAtualBtc = InvesteDAO.getSaldoUsuario(cpf, conn, "saldobtc");
            double saldoAtualEth = InvesteDAO.getSaldoUsuario(cpf, conn, "saldoeth");
            double saldoAtualXrp = InvesteDAO.getSaldoUsuario(cpf, conn, "saldoxrp");

            if (columnMoeda.equals("saldobtc")) {
                saldoAtualBtc = novoSaldoMoeda;
            } else if (columnMoeda.equals("saldoeth")) {
                saldoAtualEth = novoSaldoMoeda;
            } else if (columnMoeda.equals("saldoxrp")) {
                saldoAtualXrp = novoSaldoMoeda;
            }

            Extrato extrato = new Extrato(cpf, "-", quantidade, tipoMoeda, precoMoeda, taxaVenda, saldoRealAtual + valorEmReais, saldoAtualBtc, saldoAtualEth, saldoAtualXrp);
            InvesteDAO.registrarExtrato(extrato, conn);

            conn.commit();
            JOptionPane.showMessageDialog(null, "Venda realizada com sucesso!");
        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(null, "Erro ao tentar rollback: " + ex.getMessage(), "Erro de Rollback", JOptionPane.ERROR_MESSAGE);
                }
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, "Erro ao fechar a conexão com o banco de dados: " + e.getMessage(), "Erro ao Fechar Conexão", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private static double getTaxaVenda(String tipoMoeda) {
        return switch (tipoMoeda) {
            case "bitcoin" -> carteira.getBitcoin().getTaxaVenda();
            case "ethereum" -> carteira.getEthereum().getTaxaVenda();
            case "ripple" -> carteira.getRipple().getTaxaVenda();
            default -> throw new IllegalArgumentException("Moeda desconhecida: " + tipoMoeda);
        };
    }

    public static void realizarVenda(JRadioButton bitcoinButton, JRadioButton ethereumButton, JRadioButton rippleButton, JTextField quantidadeField, String cpf) {
        String tipoMoeda = null;
        if (bitcoinButton.isSelected()) tipoMoeda = "bitcoin";
        else if (ethereumButton.isSelected()) tipoMoeda = "ethereum";
        else if (rippleButton.isSelected()) tipoMoeda = "ripple";

        if (tipoMoeda != null) {
            try {
                double quantidade = Double.parseDouble(quantidadeField.getText());
                Connection conn = Conexão.conectar();
                venderCriptomoeda(cpf, tipoMoeda, quantidade, conn);
                conn.close();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Por favor, insira uma quantidade válida.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao acessar o banco de dados: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, selecione uma criptomoeda.", "Seleção Necessária", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void consultarExtrato(String cpf, String senha, JTextArea textArea) {
        textArea.setText(""); 
        try (Connection conn = Conexão.conectar()) {
            if (InvesteDAO.validarSenha(cpf, senha)) {
                String nome = InvesteDAO.getNomeUsuario(cpf);
                textArea.append("Nome: " + nome + "\nCPF: " + cpf + "\n\n");

                ResultSet rsExtrato = InvesteDAO.obterExtrato(cpf, conn);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                while (rsExtrato.next()) {
                    textArea.append(String.format("%s %s %.2f %s CT: %.2f TX: %.2f%% REAL: %.2f BTC: %.2f ETH: %.2f XRP: %.2f\n",
                            dateFormat.format(rsExtrato.getTimestamp("data")), rsExtrato.getString("tipo"), rsExtrato.getDouble("valor"), rsExtrato.getString("moeda"),
                            rsExtrato.getDouble("cotacao"), rsExtrato.getDouble("taxa") * 100, rsExtrato.getDouble("saldoreal"), rsExtrato.getDouble("saldobtc"),
                            rsExtrato.getDouble("saldoeth"), rsExtrato.getDouble("saldoxrp")));
                }
            } else {
                textArea.setText("Senha incorreta ou usuário não encontrado.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao acessar o banco de dados: " + ex.getMessage());
        }
    }
}