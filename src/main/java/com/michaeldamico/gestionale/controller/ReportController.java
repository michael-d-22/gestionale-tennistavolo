package com.michaeldamico.gestionale.controller;

import java.time.YearMonth;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.michaeldamico.gestionale.dto.PresenzeAtletaResponse;
import com.michaeldamico.gestionale.dto.RiepilogoMeseResponse;
import com.michaeldamico.gestionale.service.ReportService;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/presenze-atleti")
    public List<PresenzeAtletaResponse> presenzePerAtleta(@RequestParam YearMonth mese) {
        return reportService.presenzePerAtleta(mese);
    }

    @GetMapping("/riepilogo")
    public RiepilogoMeseResponse riepilogo(@RequestParam YearMonth mese) {
        return reportService.riepilogo(mese);
    }
}
