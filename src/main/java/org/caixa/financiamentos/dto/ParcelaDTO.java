package org.caixa.financiamentos.dto;

import java.math.BigDecimal;

public record ParcelaDTO(
    Integer mes,
    BigDecimal saldoInicial,
    BigDecimal juros,
    BigDecimal saldoFinal
) {
}
