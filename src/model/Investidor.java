/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author renan
 */
public class Investidor extends Pessoa {
    private double saldobtc, saldoeth, saldoxrp, saldoreais;
    private Carteira carteira;
    private Extrato extrato;

    public double getSaldobtc() {
        return saldobtc;
    }

    public double getSaldoeth() {
        return saldoeth;
    }

    public double getSaldoxrp() {
        return saldoxrp;
    }

    public double getSaldoreais() {
        return saldoreais;
    }

    public Carteira getCarteira() {
        return carteira;
    }

    public Extrato getExtrato() {
        return extrato;
    }

    public void setSaldobtc(double saldobtc) {
        this.saldobtc = saldobtc;
    }

    public void setSaldoeth(double saldoeth) {
        this.saldoeth = saldoeth;
    }

    public void setSaldoxrp(double saldoxrp) {
        this.saldoxrp = saldoxrp;
    }

    public void setSaldoreais(double saldoreais) {
        this.saldoreais = saldoreais;
    }

    public void setCarteira(Carteira carteira) {
        this.carteira = carteira;
    }

    public void setExtrato(Extrato extrato) {
        this.extrato = extrato;
    }
    
    
}
