package com.mrcsxsiq.appserver.domain;

public class Arquivo {

    private String tipo;
    private int id;
    private String nome;
    private String tamanho;

    public Arquivo() {
    }

    public Arquivo(String nome, String tamanho) {
        this.nome = nome;
        this.tamanho = tamanho;
    }

    public Arquivo(String nome, String tamanho, String tipo) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.tipo = tipo;
    }

    public Arquivo(int id, String nome, String tamanho) {
        this.id = id;
        this.nome = nome;
        this.tamanho = tamanho;
    }

    public Arquivo(int id, String nome, String tamanho, String tipo) {
        this.id = id;
        this.nome = nome;
        this.tamanho = tamanho;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
