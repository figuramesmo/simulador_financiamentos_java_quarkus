package org.caixa.financiamentos.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.caixa.financiamentos.dto.FinanciamentoRequestDTO;
import org.caixa.financiamentos.dto.FinanciamentoResponseDTO;
import org.caixa.financiamentos.dto.ParcelaDTO;
import org.caixa.financiamentos.entity.Financiamento;
import org.caixa.financiamentos.entity.Parcela;
import org.caixa.financiamentos.repository.FinanciamentoRepository;
import org.caixa.financiamentos.repository.ParcelaRepository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@ApplicationScoped
public class SimulacaoService {

    private final FinanciamentoRepository financiamentoRepository;
    private final ParcelaRepository parcelaRepository;

    public SimulacaoService(FinanciamentoRepository financiamentoRepository, ParcelaRepository parcelaRepository) {
        this.financiamentoRepository = financiamentoRepository;
        this.parcelaRepository = parcelaRepository;
    }

    public static final MathContext MATH_CONTEXT = new MathContext(6, RoundingMode.HALF_UP);

    @Transactional
    public FinanciamentoResponseDTO criaFinanciamento(
            FinanciamentoRequestDTO financiamentoRequestDTO
    )
    {
        Financiamento financiamento = new Financiamento(
                financiamentoRequestDTO.valorInicial(),
                financiamentoRequestDTO.taxaJurosMensal(),
                financiamentoRequestDTO.prazoMeses()
                );
        BigDecimal valorTotalFinal = montantePeriodoTotal(
                financiamento.getValorInicial(),
                financiamento.getTaxaJurosMensal(),
                financiamento.getPrazoMeses()
        );

        financiamento.setValorTotalFinal(valorTotalFinal);

        BigDecimal valorTotalJuros = montanteJurosPeriodoTotal(
                financiamento.getValorInicial(),
                valorTotalFinal
        );

        financiamento.setValorTotalJuros(valorTotalJuros);

        financiamentoRepository.persist(financiamento);

        return new FinanciamentoResponseDTO(
            financiamento.getId(),
            financiamento.getValorTotalFinal().setScale(4, RoundingMode.HALF_UP),
            financiamento.getValorTotalJuros().setScale(4, RoundingMode.HALF_UP),
            parcelaToDTO(financiamento.getMemoriaDeCalculo())
        );
    }

    private BigDecimal montantePeriodoTotal(
            BigDecimal valorInicial,
            BigDecimal taxaJurosMensal,
            Integer prazoMeses
    ){
        BigDecimal taxaJurosDecimal = taxaJurosMensal.divide(BigDecimal.valueOf(100), MATH_CONTEXT);
        BigDecimal taxaCrescimento = BigDecimal.ONE.add(taxaJurosDecimal);
        BigDecimal taxaCrescimentoTemporal = taxaCrescimento.pow(prazoMeses, MATH_CONTEXT);
        return valorInicial.multiply(taxaCrescimentoTemporal);
    }

    private BigDecimal montanteJurosPeriodoTotal(
            BigDecimal valorInicial,
            BigDecimal valorTotalFinal
    ){
        return valorTotalFinal.subtract(valorInicial);
    }


    private List<ParcelaDTO> parcelaToDTO(List<Parcela> parcelas) {
        return parcelas.stream()
                .map(parcela -> new ParcelaDTO(
                        parcela.getMes(),
                        parcela.getSaldoInicial().setScale(4, RoundingMode.HALF_UP),
                        parcela.getJuros().setScale(4, RoundingMode.HALF_UP),
                        parcela.getSaldoFinal().setScale(4, RoundingMode.HALF_UP)
                ))
                .toList();
    }
}
