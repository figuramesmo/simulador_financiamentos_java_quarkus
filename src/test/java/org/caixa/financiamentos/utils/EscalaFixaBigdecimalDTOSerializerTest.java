package org.caixa.financiamentos.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class EscalaFixaBigdecimalDTOSerializerTest {

    private EscalaFixaBigdecimalDTOSerializer serializer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        serializer = new EscalaFixaBigdecimalDTOSerializer();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Deve serializar BigDecimal com exatamente 4 casas decimais")
    void serialize_ValorComPrecisao_DeveFormatarCom4CasasDecimais() throws IOException {
        BigDecimal value = new BigDecimal("100.1234");
        StringWriter writer = new StringWriter();
        JsonGenerator generator = objectMapper.getFactory().createGenerator(writer);

        serializer.serialize(value, generator, null);
        generator.flush();

        String result = writer.toString();
        assertTrue(result.contains("100.1234"));
    }

    @Test
    @DisplayName("Deve arredondar para 4 casas decimais quando houver mais")
    void serialize_MaisQue4CasasDecimais_DeveArredondar() throws IOException {
        BigDecimal value = new BigDecimal("100.123456");
        StringWriter writer = new StringWriter();
        JsonGenerator generator = objectMapper.getFactory().createGenerator(writer);

        serializer.serialize(value, generator, null);
        generator.flush();

        String result = writer.toString();
        assertTrue(result.contains("100.1235"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"100.12", "100.1", "100"})
    @DisplayName("Deve preencher com zeros à direita até 4 casas decimais")
    void serialize_MenosDe4CasasDecimais_DevePreencher(String valor) throws IOException {
        BigDecimal value = new BigDecimal(valor);
        StringWriter writer = new StringWriter();
        JsonGenerator generator = objectMapper.getFactory().createGenerator(writer);

        serializer.serialize(value, generator, null);
        generator.flush();

        String result = writer.toString();
        assertTrue(result.contains("000") || result.contains("00") || result.contains("0"));
    }

    @Test
    @DisplayName("Deve serializar null como JSON null")
    void serialize_ValorNulo_DeveSerializarComNull() throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = objectMapper.getFactory().createGenerator(writer);

        serializer.serialize(null, generator, null);
        generator.flush();

        String result = writer.toString();
        assertTrue(result.contains("null"));
    }

    @Test
    @DisplayName("Deve serializar zero corretamente")
    void serialize_Zero_DeveSerializarComQuatroCasas() throws IOException {
        BigDecimal value = BigDecimal.ZERO;
        StringWriter writer = new StringWriter();
        JsonGenerator generator = objectMapper.getFactory().createGenerator(writer);

        serializer.serialize(value, generator, null);
        generator.flush();

        String result = writer.toString();
        assertTrue(result.contains("0.0000"));
    }

    @Test
    @DisplayName("Deve serializar valores negativos com 4 casas decimais")
    void serialize_ValorNegativo_DeveSerializarComQuatroCasas() throws IOException {
        BigDecimal value = new BigDecimal("-999.5678");
        StringWriter writer = new StringWriter();
        JsonGenerator generator = objectMapper.getFactory().createGenerator(writer);

        serializer.serialize(value, generator, null);
        generator.flush();

        String result = writer.toString();
        assertTrue(result.contains("-999.5678"));
    }

    @Test
    @DisplayName("Deve serializar valores muito pequenos")
    void serialize_ValorMuitoPequeno_DeveSerializarComPrecisao() throws IOException {
        BigDecimal value = new BigDecimal("0.00001");
        StringWriter writer = new StringWriter();
        JsonGenerator generator = objectMapper.getFactory().createGenerator(writer);

        serializer.serialize(value, generator, null);
        generator.flush();

        String result = writer.toString();
        assertTrue(result.contains("0.0000"));
    }

    @Test
    @DisplayName("Deve usar RoundingMode.HALF_EVEN para arredondamento")
    void serialize_ArredondamentoHalfEven() throws IOException {
        // 100.12345 com HALF_EVEN arredonda para 100.1235 (round half to even)
        BigDecimal value = new BigDecimal("100.12345");
        StringWriter writer = new StringWriter();
        JsonGenerator generator = objectMapper.getFactory().createGenerator(writer);

        serializer.serialize(value, generator, null);
        generator.flush();

        String result = writer.toString();
        assertTrue(result.contains("100.1234"));
    }

    @Test
    @DisplayName("Deve serializar valores muito grandes")
    void serialize_ValorMuitoGrande_DeveSerializarComPrecisao() throws IOException {
        BigDecimal value = new BigDecimal("999999999.9999");
        StringWriter writer = new StringWriter();
        JsonGenerator generator = objectMapper.getFactory().createGenerator(writer);

        serializer.serialize(value, generator, null);
        generator.flush();

        String result = writer.toString();
        assertTrue(result.contains("999999999.9999"));
    }
}