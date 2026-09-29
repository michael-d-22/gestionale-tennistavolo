package com.michaeldamico.gestionale.dto;

import java.time.YearMonth;

public record RiepilogoMeseResponse(YearMonth mese,
                                    long totaleSessioni,
                                    long giorniAllenamento,
                                    long presenzeTotali,
                                    double presenzeMedieAlGiorno,
                                    double presenzeMediePerTurno) {
}
