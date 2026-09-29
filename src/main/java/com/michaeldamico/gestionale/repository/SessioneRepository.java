package com.michaeldamico.gestionale.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.michaeldamico.gestionale.entity.Sessione;

public interface SessioneRepository extends JpaRepository<Sessione, Long> {

    List<Sessione> findByDataBetween(LocalDate inizio, LocalDate fine);

    long countByDataBetween(LocalDate inizio, LocalDate fine);

    @Query("SELECT COUNT(DISTINCT s.data) FROM Sessione s WHERE s.data BETWEEN :inizio AND :fine")
    long contaGiorniDiAllenamento(LocalDate inizio, LocalDate fine);
}
