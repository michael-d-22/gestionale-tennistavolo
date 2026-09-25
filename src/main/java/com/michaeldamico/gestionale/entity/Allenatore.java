package com.michaeldamico.gestionale.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Allenatore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    protected Allenatore() { }

    public Allenatore(String nome, String cognome) {
        this.nome = nome;
        this.cognome = cognome;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
}
