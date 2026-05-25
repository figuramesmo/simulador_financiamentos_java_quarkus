package org.caixa.financiamentos.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

// Deserializer personalizado para garantir que os valores BigDecimal sejam interpretados com 4 casas decimais ao serem convertidos de JSON
public class EscalaFixaBigdecimalDTODeserializer extends JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return new BigDecimal(value).setScale(4, RoundingMode.HALF_EVEN);
    }
}
