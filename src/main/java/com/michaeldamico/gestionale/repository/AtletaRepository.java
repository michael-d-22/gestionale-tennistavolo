package com.michaeldamico.gestionale.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.michaeldamico.gestionale.entity.Atleta;

public interface AtletaRepository extends JpaRepository<Atleta, Long> {
}
