package org.caixa.financiamentos.dto;

import java.math.BigDecimal;

public record parcelaDTO(
    Integer mes,
    BigDecimal saldoInicial,
    BigDecimal juros,
    BigDecimal saldoFinal
) {
}
