package org.caixa.financiamentos.dto;

import java.math.BigDecimal;
import java.util.List;

public record SimulacaoResponseDTO(
        Long Id,
        BigDecimal valorInicial,
        BigDecimal taxaJurosMensal,
        Integer prazoMeses,
        BigDecimal valorTotalFinal,
        BigDecimal valorTotalJuros,
        List<parcelaDTO> memoriaDeCalculo
) {
}
