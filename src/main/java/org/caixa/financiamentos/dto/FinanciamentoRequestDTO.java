package org.caixa.financiamentos.dto;

import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

public record FinanciamentoRequestDTO(
        @NotNull(message = "O valor inicial é obrigatório!")
        @DecimalMin(value = "0.00", inclusive = false, message = "O valor inicial deve ser maior que zero!")
        @Digits(integer = 10, fraction = 4, message = "O valor inicial deve ter no máximo 10 dígitos inteiros e 2 decimais")
        @Schema(description = "Valor inicial", example = "10000.99")
        BigDecimal valorInicial,
        @NotNull(message = "A taxa de juros mensal (%a.m.) é obrigatória!")
        @DecimalMin(value = "0.00", inclusive = false, message = "A taxa de juros mensal deve ser maior que zero!")
        @Digits(integer = 3, fraction = 4, message = "a taxa de juros deve ter no máximo 3 dígitos inteiros e 2 decimais")
        @Schema(description = "Taxa de juros mensal em porcentagem", example = "1.55")
        BigDecimal taxaJurosMensal,
        @NotNull(message = "O prazo em meses é obrigatório!")
        @Max(value = 999, message = "O prazo em meses deve ser no máximo 999 meses!")
        @Positive
        @Schema(description = "Prazo de duracao da simulação em meses", example = "36")
        Integer prazoMeses
) {
}
