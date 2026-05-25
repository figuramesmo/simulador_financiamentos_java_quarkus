package org.caixa.financiamentos.service;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.caixa.financiamentos.dto.FinanciamentoRequestDTO;
import org.caixa.financiamentos.dto.FinanciamentoResponseDTO;
import org.caixa.financiamentos.dto.SimulacaoResponseDTO;
import org.caixa.financiamentos.repository.FinanciamentoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class SimulacaoServiceTest {

    @Inject
    SimulacaoService simulacaoService;

    @Inject
    FinanciamentoRepository financiamentoRepository;

    private FinanciamentoRequestDTO validRequest;

    @BeforeEach
    void setUp() {
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

    @Test
    void criaFinanciamento_ComDadosValidos_DeveCriarFinanciamentoEParcelasComValoresCorretos() {
        FinanciamentoResponseDTO response = simulacaoService.criaFinanciamento(validRequest);

        assertNotNull(response.id());
        assertEquals(12, response.memoriaDeCalculo().size());
        assertEquals(0, response.valorTotalFinal().compareTo(BigDecimal.valueOf(11956.1817)));
        assertEquals(0, response.valorTotalJuros().compareTo(BigDecimal.valueOf(1956.1817)));
    }

    @Test
    void criaFinanciamento_ComDadosValidos_DeveCriarFinanciamentoEParcelas() {
        FinanciamentoResponseDTO response = simulacaoService.criaFinanciamento(validRequest);

        assertNotNull(response.id());
        assertEquals(12, response.memoriaDeCalculo().size());
        assertTrue(response.valorTotalFinal().compareTo(validRequest.valorInicial()) > 0);
        assertTrue(response.valorTotalJuros().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @Transactional
    void criaFinanciamento_DevePersistitNoDb() {
        FinanciamentoResponseDTO response = simulacaoService.criaFinanciamento(validRequest);

        var persisted = financiamentoRepository.findByIdOptional(response.id());
        assertTrue(persisted.isPresent());
        assertEquals(response.id(), persisted.get().getId());
        assertEquals(12, persisted.get().getMemoriaDeCalculo().size());
    }

    @Test
    void getFinanciamentoById_ComIdValido_DeveRetornarFinanciamento() {
        FinanciamentoResponseDTO created = simulacaoService.criaFinanciamento(validRequest);

        SimulacaoResponseDTO retrieved = simulacaoService.getFinanciamentoById(created.id());

        assertEquals(created.id(), retrieved.id());
        assertEquals(created.valorTotalFinal(), retrieved.valorTotalFinal());
        assertEquals(created.memoriaDeCalculo().size(), retrieved.memoriaDeCalculo().size());
    }

    @Test
    void getFinanciamentoById_ComIdInexistente_DeveLancarExcecao() {
        Long nonExistentId = 999999L;

        assertThrows(
                NoSuchElementException.class,
                () -> simulacaoService.getFinanciamentoById(nonExistentId)
        );
    }

    @Test
    void criaFinanciamento_MemoriaDeCalculo_DeveIncrementarSaldoMensalmente() {
        FinanciamentoResponseDTO response = simulacaoService.criaFinanciamento(validRequest);

        var parcelas = response.memoriaDeCalculo();
        for (int i = 0; i < parcelas.size() - 1; i++) {
            BigDecimal currentSaldoFinal = parcelas.get(i).saldoFinal();
            BigDecimal nextSaldoInicial = parcelas.get(i + 1).saldoInicial();

            assertEquals(currentSaldoFinal, nextSaldoInicial);
        }
    }

    @Test
    void criaFinanciamento_CadaParcela_DeveTemJurosPositivos() {
        FinanciamentoResponseDTO response = simulacaoService.criaFinanciamento(validRequest);

        response.memoriaDeCalculo().forEach(parcel ->
                assertTrue(parcel.juros().compareTo(BigDecimal.ZERO) > 0)
        );
    }

    @Test
    void criaFinanciamento_MultiplosFinanciamentos_DevepersistirIndependentemente() {
        FinanciamentoRequestDTO request2 = new FinanciamentoRequestDTO(
                new BigDecimal("5000.00"),
                new BigDecimal("2.00"),
                6
        );

        FinanciamentoResponseDTO resp1 = simulacaoService.criaFinanciamento(validRequest);
        FinanciamentoResponseDTO resp2 = simulacaoService.criaFinanciamento(request2);

        assertNotEquals(resp1.id(), resp2.id());
        assertEquals(12, resp1.memoriaDeCalculo().size());
        assertEquals(6, resp2.memoriaDeCalculo().size());
    }
}