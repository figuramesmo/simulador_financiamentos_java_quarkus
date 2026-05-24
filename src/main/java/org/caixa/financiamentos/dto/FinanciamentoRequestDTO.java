package org.caixa.financiamentos.dto;

import java.math.BigDecimal;

public record FinanciamentoRequestDTO(
        BigDecimal valorInicial,
        BigDecimal taxaJurosMensal,
        Integer prazoMeses
) {
}
