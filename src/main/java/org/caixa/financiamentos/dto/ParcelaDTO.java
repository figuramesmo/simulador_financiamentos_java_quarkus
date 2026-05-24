package org.caixa.financiamentos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.caixa.financiamentos.utils.FixedScaleDeserializer;
import org.caixa.financiamentos.utils.FixedScaleSerializer;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

public record ParcelaDTO(
    Integer mes,
    @JsonSerialize(using = FixedScaleSerializer.class)
    @JsonDeserialize(using = FixedScaleDeserializer.class)
    @Schema(description = "Valor da parcela considerado para o cálculo do mês", example = "500.1111")
    BigDecimal saldoInicial,
    @JsonSerialize(using = FixedScaleSerializer.class)
    @JsonDeserialize(using = FixedScaleDeserializer.class)
    @Schema(description = "Valor do juros mensal calculado sobre o saldo inicial deste mês", example = "500.2222")
    BigDecimal juros,
    @JsonSerialize(using = FixedScaleSerializer.class)
    @JsonDeserialize(using = FixedScaleDeserializer.class)
    @Schema(description = "Valor do saldo final após a aplicação do juros mensal sobre o saldo inicial deste mês", example = "500.3333")
    BigDecimal saldoFinal
) {
}
