/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author renan
 */
public class Bitcoin {
    private double TaxaCompra;
    private double TaxaVenda;

    public Bitcoin() {
        this.TaxaCompra = 0.02;
        this.TaxaVenda = 0.03;
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
