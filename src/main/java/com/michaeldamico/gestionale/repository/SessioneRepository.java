package com.michaeldamico.gestionale.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.michaeldamico.gestionale.entity.Sessione;

public interface SessioneRepository extends JpaRepository<Sessione, Long> {

    List<Sessione> findByDataBetween(LocalDate inizio, LocalDate fine);
}
