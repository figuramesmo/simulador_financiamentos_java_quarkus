package org.caixa.financiamentos.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

// Serializer personalizado para garantir que os valores BigDecimal sejam formatados com 4 casas decimais ao serem convertidos para JSON
public class FixedScaleSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            BigDecimal formattedValue = value.setScale(4, RoundingMode.HALF_EVEN);
            gen.writeString(formattedValue.toPlainString());
        } else {
            gen.writeNull();
        }
    }
}

