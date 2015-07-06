package br.edu.ifspsaocarlos.sdm.sdmmessenger.bean;

/**
 * Created by mateus on 26/06/15.
 */
public class Config {

    private String nome;
    private String valor;

    public Config() {
    }

    public Config(String nome, String valor) {
        this.nome = nome;
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
