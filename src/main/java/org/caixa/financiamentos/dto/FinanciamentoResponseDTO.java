package org.caixa.financiamentos.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.caixa.financiamentos.utils.FixedScaleDeserializer;
import org.caixa.financiamentos.utils.FixedScaleSerializer;

import java.math.BigDecimal;
import java.util.List;

public record FinanciamentoResponseDTO(
    Long id,
    @JsonSerialize(using = FixedScaleSerializer.class)
    @JsonDeserialize(using = FixedScaleDeserializer.class)
    BigDecimal valorTotalFinal,
    @JsonSerialize(using = FixedScaleSerializer.class)
    @JsonDeserialize(using = FixedScaleDeserializer.class)
    BigDecimal valorTotalJuros,
    List<ParcelaDTO> memoriaDeCalculo
) {
}
