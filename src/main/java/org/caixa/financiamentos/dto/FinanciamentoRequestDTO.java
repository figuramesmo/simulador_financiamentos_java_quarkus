package org.caixa.financiamentos.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record FinanciamentoRequestDTO(
        @NotBlank(message = "O valor inicial é obrigatório!")
        BigDecimal valorInicial,
        @NotBlank(message = "A taxa de juros mensal (%a.m.) é obrigatória!")
        BigDecimal taxaJurosMensal,
        @NotBlank(message = "O prazo em meses é obrigatório!")
        Integer prazoMeses
) {
}
