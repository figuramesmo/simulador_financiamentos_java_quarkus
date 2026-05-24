package org.caixa.financiamentos.dto;

import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

public record FinanciamentoRequestDTO(
        @NotNull(message = "O valor inicial é obrigatório!")
        @DecimalMin(value = "0.00", inclusive = false, message = "O valor inicial deve ser maior que zero!")
        @Digits(integer = 10, fraction = 2, message = "O valor inicial deve ter no máximo 10 dígitos inteiros e 2 decimais")
        @Schema()
        BigDecimal valorInicial,
        @NotNull(message = "A taxa de juros mensal (%a.m.) é obrigatória!")
        @DecimalMin(value = "0.00", inclusive = false, message = "A taxa de juros mensal deve ser maior que zero!")
        @Digits(integer = 3, fraction = 2, message = "a taxa de juros deve ter no máximo 3 dígitos inteiros e 2 decimais")
        BigDecimal taxaJurosMensal,
        @NotNull(message = "O prazo em meses é obrigatório!")
        @Max(value = 999, message = "O prazo em meses deve ser no máximo 999 meses!")
        @Positive
        Integer prazoMeses
) {
}
