/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author renan
 */
public class Ethereum {
    private double TaxaCompra;
    private double TaxaVenda;

    public Ethereum() {
        this.TaxaCompra = 0.01;
        this.TaxaVenda = 0.02;
    }

    public double getTaxaCompra() {
        return TaxaCompra;
    }

    public void setTaxaCompra(double TaxaCompra) {
        this.TaxaCompra = TaxaCompra;
    }

    public double getTaxaVenda() {
        return TaxaVenda;
    }

    public void setTaxaVenda(double TaxaVenda) {
        this.TaxaVenda = TaxaVenda;
    }
}
