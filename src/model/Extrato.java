/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
/**
 *
 * @author renan
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class Extrato {
    private String cpf;
    private Date data;
    private String tipo;
    private double valor;
    private String moeda;
    private double cotacao;
    private double taxa;
    private double saldoreal;
    private double saldobtc;
    private double saldoeth;
    private double saldoxrp;

    public Extrato(String cpf, String tipo, double valor, String moeda, double cotacao, double taxa, double saldoreal, double saldobtc, double saldoeth, double saldoxrp) {
        this.cpf = cpf;
        this.tipo = tipo;
        this.valor = valor;
        this.moeda = moeda;
        this.cotacao = cotacao;
        this.taxa = taxa;
        this.saldoreal = saldoreal;
        this.saldobtc = saldobtc;
        this.saldoeth = saldoeth;
        this.saldoxrp = saldoxrp;
        this.data = new Date();
    }

    public void registrar(Connection conn) throws SQLException {
        String sql = "INSERT INTO esquematicos.extrato (cpf, data, tipo, valor, moeda, cotacao, taxa, saldoreal, saldobtc, saldoeth, saldoxrp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.setTimestamp(2, new java.sql.Timestamp(data.getTime()));
            stmt.setString(3, tipo);
            stmt.setDouble(4, valor);
            stmt.setString(5, moeda);
            stmt.setDouble(6, cotacao);
            stmt.setDouble(7, taxa);
            stmt.setDouble(8, saldoreal);
            stmt.setDouble(9, saldobtc);
            stmt.setDouble(10, saldoeth);
            stmt.setDouble(11, saldoxrp);
            stmt.executeUpdate();
        }
    }
}
