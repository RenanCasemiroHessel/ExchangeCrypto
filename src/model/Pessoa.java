/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author renan
 */
public class Pessoa {
        private String nome, cpf, senha;
        
        public Pessoa(){    
        }
        
        public Pessoa(String nome, String cpf, String senha){
            this.nome = nome;
            this.cpf = cpf;
            this.senha = senha;
        }
        
        public void setNome(String Nome){
            this.nome = Nome;
        }
        
        public void setCpf(String Cpf){
            this.cpf = Cpf;
        }
        
        public void setSenha(String Senha){
            this.senha = Senha;
        }
        
        public String getNome(){
            return nome;
        }
        
        public String getCpf(){
            return cpf;
        }
        
        public String getSenha(){
            return senha;
        }       
}
