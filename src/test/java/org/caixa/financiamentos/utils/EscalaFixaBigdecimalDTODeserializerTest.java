package org.caixa.financiamentos.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
class EscalaFixaBigdecimalDTODeserializerTest {

    private EscalaFixaBigdecimalDTODeserializer deserializer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        deserializer = new EscalaFixaBigdecimalDTODeserializer();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Deve desserializar BigDecimal com exatamente 4 casas decimais")
    void deserialize_ValorComQuatroCasas_DeveManterPrecisao() throws IOException {
        String json = "\"100.1234\"";
        JsonParser parser = objectMapper.getFactory().createParser(json);
        parser.nextToken();

        BigDecimal result = deserializer.deserialize(parser, null);

        assertEquals(new BigDecimal("100.1234"), result);
        assertEquals(4, result.scale());
    }

    @Test
    @DisplayName("Deve arredondar para 4 casas decimais quando houver mais")
    void deserialize_MaisQue4CasasDecimais_DeveArredondar() throws IOException {
        String json = "\"100.123456\"";
        JsonParser parser = objectMapper.getFactory().createParser(json);
        parser.nextToken();

        BigDecimal result = deserializer.deserialize(parser, null);

        assertEquals(new BigDecimal("100.1235"), result);
        assertEquals(4, result.scale());
    }

    @ParameterizedTest
    @ValueSource(strings = {"100.12", "100.1", "100"})
    @DisplayName("Deve preencher com zeros à direita até 4 casas decimais")
    void deserialize_MenosDe4CasasDecimais_DevePreencher(String valor) throws IOException {
        String json = "\"" + valor + "\"";
        JsonParser parser = objectMapper.getFactory().createParser(json);
        parser.nextToken();

        BigDecimal result = deserializer.deserialize(parser, null);

        assertEquals(4, result.scale());
        assertTrue(result.toPlainString().contains("."));
    }

    @Test
    @DisplayName("Deve retornar null quando string é nula")
    void deserialize_StringNula_DeveRetornarNull() throws IOException {
        String json = "null";
        JsonParser parser = objectMapper.getFactory().createParser(json);
        parser.nextToken();

        BigDecimal result = deserializer.deserialize(parser, null);

        assertNull(result);
    }

    @Test
    @DisplayName("Deve retornar null quando string está vazia")
    void deserialize_StringVazia_DeveRetornarNull() throws IOException {
        String json = "\"\"";
        JsonParser parser = objectMapper.getFactory().createParser(json);
        parser.nextToken();

        BigDecimal result = deserializer.deserialize(parser, null);

        assertNull(result);
    }

    @Test
    @DisplayName("Deve retornar null quando string contém apenas espaços")
    void deserialize_StringEspacos_DeveRetornarNull() throws IOException {
        String json = "\"   \"";
        JsonParser parser = objectMapper.getFactory().createParser(json);
        parser.nextToken();

        BigDecimal result = deserializer.deserialize(parser, null);

        assertNull(result);
    }

    @Test
    @DisplayName("Deve desserializar zero corretamente")
    void deserialize_Zero_DeveManterEscala() throws IOException {
        String json = "\"0\"";
        JsonParser parser = objectMapper.getFactory().createParser(json);
        parser.nextToken();

        BigDecimal result = deserializer.deserialize(parser, null);

        assertEquals(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_EVEN), result);
        assertEquals(4, result.scale());
    }

    @Test
    @DisplayName("Deve desserializar valores negativos")
    void deserialize_ValorNegativo_DeveManterPrecisao() throws IOException {
        String json = "\"-999.5678\"";
        JsonParser parser = objectMapper.getFactory().createParser(json);
        parser.nextToken();

        BigDecimal result = deserializer.deserialize(parser, null);

        assertEquals(new BigDecimal("-999.5678"), result);
        assertEquals(4, result.scale());
    }

    @Test
    @DisplayName("Deve desserializar valores muito pequenos")
    void deserialize_ValorMuitoPequeno_DeveArredondar() throws IOException {
        String json = "\"0.00001\"";
        JsonParser parser = objectMapper.getFactory().createParser(json);
        parser.nextToken();

        BigDecimal result = deserializer.deserialize(parser, null);

        assertEquals(new BigDecimal("0.0000"), result);
        assertEquals(4, result.scale());
    }

    @Test
    @DisplayName("Deve usar RoundingMode.HALF_EVEN para arredondamento")
    void deserialize_ArredondamentoHalfEven() throws IOException {
        String json = "\"100.12345\"";
        JsonParser parser = objectMapper.getFactory().createParser(json);
        parser.nextToken();

        BigDecimal result = deserializer.deserialize(parser, null);

        assertEquals(new BigDecimal("100.1234"), result);
    }

    @Test
    @DisplayName("Deve desserializar valores muito grandes")
    void deserialize_ValorMuitoGrande_DeveManterPrecisao() throws IOException {
        String json = "\"999999999.9999\"";
        JsonParser parser = objectMapper.getFactory().createParser(json);
        parser.nextToken();

        BigDecimal result = deserializer.deserialize(parser, null);

        assertEquals(new BigDecimal("999999999.9999"), result);
        assertEquals(4, result.scale());
    }

    @Test
    @DisplayName("Deve desserializar valor com notação científica")
    void deserialize_NotacaoCientifica_DeveConverterParaDecimal() throws IOException {
        String json = "\"1E+3\"";
        JsonParser parser = objectMapper.getFactory().createParser(json);
        parser.nextToken();

        BigDecimal result = deserializer.deserialize(parser, null);

        assertNotNull(result);
        assertEquals(4, result.scale());
    }
}
