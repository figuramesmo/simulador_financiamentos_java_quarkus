package org.caixa.financiamentos.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.caixa.financiamentos.dto.FinanciamentoRequestDTO;
import org.caixa.financiamentos.repository.FinanciamentoRepository;
import org.caixa.financiamentos.service.SimulacaoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@DisplayName("SimulacaoResource - Testes de Integração REST")
class SimulacaoResourceTest {

    @Inject
    FinanciamentoRepository financiamentoRepository;

    @Inject
    SimulacaoService simulacaoService;

    private FinanciamentoRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        RestAssured.basePath = "/financiamentos";
        validRequest = new FinanciamentoRequestDTO(
                new BigDecimal("10000.00"),
                new BigDecimal("1.50"),
                12
        );
    }

    @AfterEach
    @Transactional
    void tearDown() {
        financiamentoRepository.deleteAll();
    }

    // ========== POST /financiamentos ==========

    @Test
    @DisplayName("POST - Deve retornar 201 ao criar financiamento com dados válidos")
    void criaFinanciamento_DadosValidos_DeveCriar201() {
        given()
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post()
                .then()
                .statusCode(201)
                .header("Location", containsString("/financiamentos/"))
                .body("id", notNullValue())
                .body("valorTotalFinal", notNullValue())
                .body("valorTotalJuros", notNullValue())
                .body("memoriaDeCalculo", hasSize(12))
                .body("memoriaDeCalculo[0].mes", equalTo(1))
                .body("memoriaDeCalculo[11].mes", equalTo(12));
    }

    @Test
    @DisplayName("POST - Deve retornar 400 quando body é nulo")
    void criaFinanciamento_BodyNulo_DeveCriar400() {
        given()
                .contentType(ContentType.JSON)
                .body("")
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST - Deve retornar 400 quando valorInicial está ausente")
    void criaFinanciamento_SemValorInicial_DeveCriar400() {
        String invalidBody = """
                {
                    "taxaJurosMensal": 1.50,
                    "prazoMeses": 12
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidBody)
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST - Deve retornar 400 quando taxaJurosMensal está ausente")
    void criaFinanciamento_SemTaxaJuros_DeveCriar400() {
        String invalidBody = """
                {
                    "valorInicial": 10000.00,
                    "prazoMeses": 12
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidBody)
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST - Deve retornar 400 quando prazoMeses está ausente")
    void criaFinanciamento_SemPrazoMeses_DeveCriar400() {
        String invalidBody = """
                {
                    "valorInicial": 10000.00,
                    "taxaJurosMensal": 1.50
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidBody)
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST - Deve retornar 400 quando valorInicial é negativo")
    void criaFinanciamento_ValorNegativo_DeveCriar400() {
        FinanciamentoRequestDTO invalidRequest = new FinanciamentoRequestDTO(
                new BigDecimal("-5000.00"),
                new BigDecimal("1.50"),
                12
        );

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("mensagem", notNullValue());
    }

    @Test
    @DisplayName("POST - Deve retornar 400 quando prazoMeses é zero ou negativo")
    void criaFinanciamento_PrazoInvalido_DeveCriar400() {
        FinanciamentoRequestDTO invalidRequest = new FinanciamentoRequestDTO(
                new BigDecimal("10000.00"),
                new BigDecimal("1.50"),
                0
        );

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("mensagem", notNullValue());
    }

    @Test
    @DisplayName("POST - Deve persistir financiamento no banco de dados")
    @Transactional
    void criaFinanciamento_DadosValidos_DevePersistitNoDB() {
        Long idRetornado = given()
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        var persisted = financiamentoRepository.findByIdOptional(idRetornado);
        assert persisted.isPresent() : "Financiamento deve estar persistido no banco";
    }

    @Test
    @DisplayName("POST - Deve retornar Location header com URI do recurso criado")
    void criaFinanciamento_DadosValidos_DeveRetornarLocationHeader() {
        Long idRetornado = given()
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post()
                .then()
                .statusCode(201)
                .header("Location", containsString("/financiamentos/"))
                .extract()
                .jsonPath()
                .getLong("id");

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/" + idRetornado)
                .then()
                .statusCode(200);
    }

    // ========== GET /financiamentos/{id} ==========

    @Test
    @DisplayName("GET - Deve retornar 200 ao recuperar financiamento existente")
    void getFinanciamento_IdValido_DeveCriar200() {
        Long id = given()
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("valorTotalFinal", notNullValue())
                .body("valorTotalJuros", notNullValue())
                .body("memoriaDeCalculo", hasSize(12));
    }

    @Test
    @DisplayName("GET - Deve retornar 404 ao recuperar financiamento inexistente")
    void getFinanciamento_IdInexistente_DeveCriar404() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/999999")
                .then()
                .statusCode(404)
                .body("mensagem", notNullValue());
    }

    @Test
    @DisplayName("GET - Deve retornar todos os dados da memória de cálculo")
    void getFinanciamento_IdValido_DeveRetornarMemoriaCompleta() {
        Long id = given()
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/" + id)
                .then()
                .statusCode(200)
                .body("memoriaDeCalculo[0].mes", equalTo(1))
                .body("memoriaDeCalculo[0].saldoInicial", notNullValue())
                .body("memoriaDeCalculo[0].juros", notNullValue())
                .body("memoriaDeCalculo[0].saldoFinal", notNullValue());
    }

    @Test
    @DisplayName("GET - Deve manter dados consistentes entre POST e GET")
    void getFinanciamento_DadosConsistentes_PostEGet() {
        Long id = given()
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        BigDecimal valorTotalFinalPost = given()
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post()
                .then()
                .extract()
                .jsonPath()
                .getObject("valorTotalFinal", BigDecimal.class);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("valorTotalFinal", equalTo(valorTotalFinalPost.toString()));
    }

    // ========== Testes de Edge Cases ==========

    @Test
    @DisplayName("POST - Deve processar corretamente financiamento com 1 mês")
    void criaFinanciamento_1Mes_DeveProcessar() {
        FinanciamentoRequestDTO singleMonthRequest = new FinanciamentoRequestDTO(
                new BigDecimal("5000.00"),
                new BigDecimal("2.00"),
                1
        );

        given()
                .contentType(ContentType.JSON)
                .body(singleMonthRequest)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("memoriaDeCalculo", hasSize(1))
                .body("memoriaDeCalculo[0].mes", equalTo(1));
    }

    @Test
    @DisplayName("POST - Deve processar corretamente financiamento com 360 meses")
    void criaFinanciamento_360Meses_DeveProcessar() {
        FinanciamentoRequestDTO longTermRequest = new FinanciamentoRequestDTO(
                new BigDecimal("100000.00"),
                new BigDecimal("0.50"),
                360
        );

        given()
                .contentType(ContentType.JSON)
                .body(longTermRequest)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("memoriaDeCalculo", hasSize(360));
    }

    @Test
    @DisplayName("POST - Deve lidar com valores pequenos (centavos)")
    void criaFinanciamento_ValoresPequenos_DeveProcessar() {
        FinanciamentoRequestDTO smallRequest = new FinanciamentoRequestDTO(
                new BigDecimal("0.01"),
                new BigDecimal("0.01"),
                3
        );

        given()
                .contentType(ContentType.JSON)
                .body(smallRequest)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("valorTotalFinal", notNullValue());
    }

    @Test
    @DisplayName("POST - Deve retornar Content-Type JSON")
    void criaFinanciamento_ContentTypeJSON() {
        given()
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post()
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON);
    }

    @Test
    @DisplayName("GET - Deve retornar Content-Type JSON")
    void getFinanciamento_ContentTypeJSON() {
        Long id = given()
                .contentType(ContentType.JSON)
                .body(validRequest)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/" + id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}