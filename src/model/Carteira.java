/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
/**
 *
 * @author renan
 */
public class Carteira {
    private Bitcoin bitcoin;
    private Ethereum ethereum;
    private Ripple ripple;
    
    public Carteira() {
        this.bitcoin = new Bitcoin();
        this.ethereum = new Ethereum();
        this.ripple = new Ripple();
    }
    public Bitcoin getBitcoin() {
        return bitcoin;
    }

    public Ethereum getEthereum() {
        return ethereum;
    }

    public Ripple getRipple() {
        return ripple;
    }
}
