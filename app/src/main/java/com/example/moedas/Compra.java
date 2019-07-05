package com.example.moedas;

public class Compra {

    private String id;
    private double quantidade;
    private String moeda;

    public Compra() {
    }

    public Compra(String id, double quantidade, String moeda) {
        this.id = id;
        this.quantidade = quantidade;
        this.moeda = moeda;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public String getMoeda() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }
}
