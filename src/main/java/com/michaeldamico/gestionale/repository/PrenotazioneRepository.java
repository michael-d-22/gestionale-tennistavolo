package com.michaeldamico.gestionale.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.michaeldamico.gestionale.dto.PresenzeAtletaResponse;
import com.michaeldamico.gestionale.entity.Prenotazione;
import com.michaeldamico.gestionale.entity.StatoPrenotazione;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    List<Prenotazione> findBySessioneId(Long sessioneId);

    Optional<Prenotazione> findByAtletaIdAndSessioneId(Long atletaId, Long sessioneId);

    long countBySessioneIdAndStatoNot(Long sessioneId, StatoPrenotazione stato);

    long countByStatoAndSessioneDataBetween(StatoPrenotazione stato, LocalDate inizio, LocalDate fine);

    @Query("""
            SELECT new com.michaeldamico.gestionale.dto.PresenzeAtletaResponse(a.id, a.nome, a.cognome, COUNT(p))
            FROM Prenotazione p JOIN p.atleta a JOIN p.sessione s
            WHERE p.stato = :stato AND s.data BETWEEN :inizio AND :fine
            GROUP BY a.id, a.nome, a.cognome
            ORDER BY a.cognome, a.nome
            """)
    List<PresenzeAtletaResponse> contaPresenzePerAtleta(StatoPrenotazione stato, LocalDate inizio, LocalDate fine);
}
