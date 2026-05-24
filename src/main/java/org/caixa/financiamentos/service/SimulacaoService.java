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
import java.util.NoSuchElementException;

@ApplicationScoped
public class SimulacaoService {

    private final FinanciamentoRepository financiamentoRepository;
    private final ParcelaRepository parcelaRepository;

    public SimulacaoService(FinanciamentoRepository financiamentoRepository, ParcelaRepository parcelaRepository) {
        this.financiamentoRepository = financiamentoRepository;
        this.parcelaRepository = parcelaRepository;
    }

    public static final MathContext MATH_CONTEXT = new MathContext(4, RoundingMode.HALF_EVEN);

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

        BigDecimal valorTotalFinal = montantePeriodo(
                financiamento.getValorInicial(),
                financiamento.getTaxaJurosMensal(),
                financiamento.getPrazoMeses()
        );

        financiamento.setValorTotalFinal(valorTotalFinal);

        BigDecimal valorTotalJuros = montanteJurosPeriodo(
                financiamento.getValorInicial(),
                financiamento.getValorTotalFinal()
        );

        financiamento.setValorTotalJuros(valorTotalJuros);

        geraMemoriaDeCalculo(financiamento);

        financiamentoRepository.persist(financiamento);

        return financiamentoToDTO(financiamento);
    }

    @Transactional
    public FinanciamentoResponseDTO getFinanciamentoById(
            Long id
    ){
        Financiamento financiamento = financiamentoRepository.findByIdOptional(id).orElseThrow(
                () -> new NoSuchElementException("O financiamento com a id " + id + " não foi encontrado.")
        );
        return financiamentoToDTO(financiamento);
    }

    private BigDecimal montantePeriodo(
            BigDecimal valorInicial,
            BigDecimal taxaJurosMensal,
            Integer prazoMeses
    ){
        BigDecimal taxaJurosDecimal = taxaJurosMensal.divide(BigDecimal.valueOf(100), MATH_CONTEXT);
        BigDecimal taxaCrescimento = BigDecimal.ONE.add(taxaJurosDecimal);
        BigDecimal taxaCrescimentoTemporal = taxaCrescimento.pow(prazoMeses);
        return valorInicial.multiply(taxaCrescimentoTemporal);
    }

    private BigDecimal montanteJurosPeriodo(
            BigDecimal valorInicial,
            BigDecimal valorTotalFinal
    ){
        return valorTotalFinal.subtract(valorInicial);
    }


    private void geraMemoriaDeCalculo(
            Financiamento financiamento
    ){
        BigDecimal saldoInicial = financiamento.getValorInicial();

        // Calcula juros e saldo final mês a mês
        for (int mes = 1; mes <= financiamento.getPrazoMeses(); mes++) {
            // aplica a taxa de juros mensal sobre o saldo inicial do mês
            BigDecimal saldoFinal = montantePeriodo(saldoInicial, financiamento.getTaxaJurosMensal(), 1);
            // calcula o valor do juros mensal
            BigDecimal juros = montanteJurosPeriodo(saldoInicial, saldoFinal);

            // cria a parcela correspondente a este mês e persiste
            Parcela parcela = new Parcela(mes, saldoInicial, juros, saldoFinal);
            parcelaRepository.persist(parcela);

            // adiciona a parcela à memória de cálculo do financiamento
            financiamento.addMemoriaDeCalculo(parcela);

            // o saldo final deste mês se torna o saldo inicial do próximo mês
            saldoInicial = saldoFinal;
        }
    }


    private List<ParcelaDTO> parcelaToDTO(List<Parcela> parcelas) {
        return parcelas.stream()
                .map(parcela -> new ParcelaDTO(
                        parcela.getMes(),
                        parcela.getSaldoInicial().setScale(4, RoundingMode.HALF_EVEN),
                        parcela.getJuros().setScale(4, RoundingMode.HALF_EVEN),
                        parcela.getSaldoFinal().setScale(4, RoundingMode.HALF_EVEN)
                ))
                .toList();
    }

    private FinanciamentoResponseDTO financiamentoToDTO(Financiamento financiamento) {
        return new FinanciamentoResponseDTO(
                financiamento.getId(),
                financiamento.getValorTotalFinal().setScale(4, RoundingMode.HALF_EVEN),
                financiamento.getValorTotalJuros().setScale(4, RoundingMode.HALF_EVEN),
                parcelaToDTO(financiamento.getMemoriaDeCalculo())
        );
    }
}
