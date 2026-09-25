package com.michaeldamico.gestionale.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.michaeldamico.gestionale.entity.Prenotazione;
import com.michaeldamico.gestionale.entity.StatoPrenotazione;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    List<Prenotazione> findBySessioneId(Long sessioneId);

    Optional<Prenotazione> findByAtletaIdAndSessioneId(Long atletaId, Long sessioneId);

    long countBySessioneIdAndStatoNot(Long sessioneId, StatoPrenotazione stato);
}
