package org.caixa.financiamentos.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.caixa.financiamentos.dto.FinanciamentoRequestDTO;
import org.caixa.financiamentos.dto.FinanciamentoResponseDTO;
import org.caixa.financiamentos.repository.FinanciamentoRepository;
import org.caixa.financiamentos.repository.ParcelaRepository;

@ApplicationScoped
public class SimulacaoService {

    private final FinanciamentoRepository financiamentoRepository;
    private final ParcelaRepository parcelaRepository;

    public SimulacaoService(FinanciamentoRepository financiamentoRepository, ParcelaRepository parcelaRepository) {
        this.financiamentoRepository = financiamentoRepository;
        this.parcelaRepository = parcelaRepository;
    }

    @Transactional
    public FinanciamentoResponseDTO criaFinanciamento(
            FinanciamentoRequestDTO financiamentoRequestDTO
    )
    {
        return new FinanciamentoResponseDTO(

        )
    }


}
