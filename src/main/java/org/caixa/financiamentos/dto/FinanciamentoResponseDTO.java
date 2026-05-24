package org.caixa.financiamentos.dto;

import java.math.BigDecimal;
import java.util.List;

public record FinanciamentoResponseDTO(
    Long id,
    BigDecimal valorTotalFinal,
    BigDecimal valorTotalJuros,
    List<ParcelaDTO> listaMemoriaDeCalculo
) {
}
