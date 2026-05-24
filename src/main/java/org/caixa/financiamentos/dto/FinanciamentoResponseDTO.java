package org.caixa.financiamentos.dto;

import java.math.BigDecimal;
import java.util.List;

public record FinanciamentoResponseDTO(
    Long Id,
    BigDecimal valorTotalFinal,
    BigDecimal valorTotalJuros,
    List<parcelaDTO> listaMemoriaDeCalculo
) {
}
