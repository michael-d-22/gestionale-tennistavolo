package com.michaeldamico.gestionale.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Sessione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime ora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSessione tipo;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Column(nullable = false)
    private Integer capienza;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allenatore_id")
    private Allenatore allenatore;

    protected Sessione() { }

    public Sessione(LocalDate data, LocalTime ora, TipoSessione tipo,
                    Categoria categoria, Integer capienza, Allenatore allenatore) {
        this.data = data;
        this.ora = ora;
        this.tipo = tipo;
        this.categoria = categoria;
        this.capienza = capienza;
        this.allenatore = allenatore;
    }

    public Long getId() { return id; }
    public LocalDate getData() { return data; }
    public LocalTime getOra() { return ora; }
    public TipoSessione getTipo() { return tipo; }
    public Categoria getCategoria() { return categoria; }
    public Integer getCapienza() { return capienza; }
    public Allenatore getAllenatore() { return allenatore; }

    public LocalDateTime getInizio() {
        return LocalDateTime.of(data, ora);
    }
}
