package org.caixa.financiamentos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.caixa.financiamentos.utils.EscalaFixaBigdecimalDTODeserializer;
import org.caixa.financiamentos.utils.EscalaFixaBigdecimalDTOSerializer;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public record SimulacaoResponseDTO(
        Long id,
        @JsonSerialize(using = EscalaFixaBigdecimalDTOSerializer.class)
        @JsonDeserialize(using = EscalaFixaBigdecimalDTODeserializer.class)
        @Schema(description = "Valor inicial", example = "10000.1111")
        BigDecimal valorInicial,
        @JsonSerialize(using = EscalaFixaBigdecimalDTOSerializer.class)
        @JsonDeserialize(using = EscalaFixaBigdecimalDTODeserializer.class)
        @Schema(description = "Taxa de juros mensal em porcentagem", example = "1.5555")
        BigDecimal taxaJurosMensal,
        @Schema(description = "Prazo de duracao da simulação em meses", example = "36")
        Integer prazoMeses,
        @JsonSerialize(using = EscalaFixaBigdecimalDTOSerializer.class)
        @JsonDeserialize(using = EscalaFixaBigdecimalDTODeserializer.class)
        @Schema(description = "Valor total final após aplicação dos juros na quantidade de meses especificada", example = "10000.1111")
        BigDecimal valorTotalFinal,
        @JsonSerialize(using = EscalaFixaBigdecimalDTOSerializer.class)
        @JsonDeserialize(using = EscalaFixaBigdecimalDTODeserializer.class)
        @Schema(description = "Valor total dos juros após a quantidade de meses especificada", example = "1500.1111")
        BigDecimal valorTotalJuros,
        @Schema(description = "Lista de Parcelas (e suas informações), mês a mês", example = "[{\"mes\": 1, \"saldoInicial\": 10000.1234, \"juros\": 150.6666, \"saldoFinal\": 10150.1234}, {\"mes\": 2, \"saldoInicial\": 10150.1234, \"juros\": 152.9999, \"saldoFinal\": 10302.1234}]")
        List<ParcelaDTO> memoriaDeCalculo
) {
}
