package com.michaeldamico.gestionale.entity;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"atleta_id", "sessione_id"}))
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id", nullable = false)
    private Atleta atleta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessione_id", nullable = false)
    private Sessione sessione;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoPrenotazione stato;

    protected Prenotazione() { }

    public Prenotazione(Atleta atleta, Sessione sessione) {
        this.atleta = atleta;
        this.sessione = sessione;
        this.stato = statoIniziale(sessione);
    }

    public Long getId() { return id; }
    public Atleta getAtleta() { return atleta; }
    public Sessione getSessione() { return sessione; }
    public StatoPrenotazione getStato() { return stato; }

    public void annulla() {
        this.stato = StatoPrenotazione.ANNULLATA;
    }

    public void riattiva() {
        this.stato = statoIniziale(sessione);
    }

    public void segnaPresente() {
        this.stato = StatoPrenotazione.PRESENTE;
    }

    public void segnaAssente() {
        this.stato = StatoPrenotazione.ASSENTE;
    }

    private static StatoPrenotazione statoIniziale(Sessione sessione) {
        return sessione.getTipo() == TipoSessione.INDIVIDUALE
                ? StatoPrenotazione.PRESENTE
                : StatoPrenotazione.PRENOTATA;
    }
}
