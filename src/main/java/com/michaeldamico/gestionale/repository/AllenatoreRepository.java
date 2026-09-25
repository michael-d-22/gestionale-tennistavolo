package com.michaeldamico.gestionale.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.michaeldamico.gestionale.entity.Allenatore;

public interface AllenatoreRepository extends JpaRepository<Allenatore, Long> {
}
