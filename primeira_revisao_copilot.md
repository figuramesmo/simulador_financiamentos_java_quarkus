Suggestion (brief):
- Move the arithmetic helpers to a dedicated utility (\`CalculadoraFinanciamento\`).
- Stop calling \`parcelaRepository.persist(...)\` in the service; instead attach child \`Parcela\` objects to the parent \`Financiamento\` and rely on JPA cascade persist.
- Ensure \`Financiamento\` / \`Parcela\` entities model the relationship (\`@OneToMany(cascade = CascadeType.PERSIST)\` / \`@ManyToOne\`) and that \`addMemoriaDeCalculo\` sets the back-reference.
- Keep Portuguese naming to match the existing codebase.

Explanation done. Updated/added classes below.

`src/main/java/org/caixa/financiamentos/util/CalculadoraFinanciamento.java`
```java
java
package org.caixa.financiamentos.util;

import org.caixa.financiamentos.entity.Financiamento;
import org.caixa.financiamentos.entity.Parcela;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class CalculadoraFinanciamento {

    // keep scale/precision decisions centralized
    public static final MathContext MATH_CONTEXT = new MathContext(8, RoundingMode.HALF_EVEN);
    public static final int OUTPUT_SCALE = 4;

    private CalculadoraFinanciamento() { }

    public static BigDecimal montantePeriodo(BigDecimal valorInicial, BigDecimal taxaJurosMensal, int prazoMeses) {
        BigDecimal taxaJurosDecimal = taxaJurosMensal.divide(BigDecimal.valueOf(100), MATH_CONTEXT);
        BigDecimal taxaCrescimento = BigDecimal.ONE.add(taxaJurosDecimal);
        BigDecimal taxaCrescimentoTemporal = taxaCrescimento.pow(prazoMeses, MATH_CONTEXT);
        return valorInicial.multiply(taxaCrescimentoTemporal, MATH_CONTEXT)
                .setScale(OUTPUT_SCALE, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal montanteJurosPeriodo(BigDecimal valorInicial, BigDecimal valorTotalFinal) {
        return valorTotalFinal.subtract(valorInicial, MATH_CONTEXT)
                .setScale(OUTPUT_SCALE, RoundingMode.HALF_EVEN);
    }

    // build the memory-of-calculation without persistence responsibilities
    public static List<Parcela> geraMemoriaDeCalculo(Financiamento financiamento) {
        List<Parcela> parcelas = new ArrayList<>();
        BigDecimal saldoInicial = financiamento.getValorInicial();

        for (int mes = 1; mes <= financiamento.getPrazoMeses(); mes++) {
            BigDecimal saldoFinal = montantePeriodo(saldoInicial, financiamento.getTaxaJurosMensal(), 1);
            BigDecimal juros = montanteJurosPeriodo(saldoInicial, saldoFinal);

            Parcela parcela = new Parcela(mes, saldoInicial, juros, saldoFinal);
            parcelas.add(parcela);

            saldoInicial = saldoFinal;
        }

        return parcelas;
    }
}
```

`src/main/java/org/caixa/financiamentos/entity/Financiamento.java`
```java
java
package org.caixa.financiamentos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "financiamento")
public class Financiamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal valorInicial;

    @Column(nullable = false)
    private BigDecimal taxaJurosMensal;

    @Column(nullable = false)
    private Integer prazoMeses;

    private BigDecimal valorTotalFinal;
    private BigDecimal valorTotalJuros;

    @OneToMany(mappedBy = "financiamento", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Parcela> memoriaDeCalculo = new ArrayList<>();

    // constructors, getters and setters

    public Financiamento() { }

    public Financiamento(BigDecimal valorInicial, BigDecimal taxaJurosMensal, Integer prazoMeses) {
        this.valorInicial = valorInicial;
        this.taxaJurosMensal = taxaJurosMensal;
        this.prazoMeses = prazoMeses;
    }

    public Long getId() { return id; }
    public BigDecimal getValorInicial() { return valorInicial; }
    public BigDecimal getTaxaJurosMensal() { return taxaJurosMensal; }
    public Integer getPrazoMeses() { return prazoMeses; }
    public BigDecimal getValorTotalFinal() { return valorTotalFinal; }
    public BigDecimal getValorTotalJuros() { return valorTotalJuros; }
    public List<Parcela> getMemoriaDeCalculo() { return memoriaDeCalculo; }

    public void setValorTotalFinal(BigDecimal valorTotalFinal) { this.valorTotalFinal = valorTotalFinal; }
    public void setValorTotalJuros(BigDecimal valorTotalJuros) { this.valorTotalJuros = valorTotalJuros; }

    // add child and set back-reference
    public void addMemoriaDeCalculo(Parcela parcela) {
        parcela.setFinanciamento(this);
        this.memoriaDeCalculo.add(parcela);
    }
}
```

`src/main/java/org/caixa/financiamentos/entity/Parcela.java`
```java
java
package org.caixa.financiamentos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "parcela")
public class Parcela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer mes;
    private BigDecimal saldoInicial;
    private BigDecimal juros;
    private BigDecimal saldoFinal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financiamento_id")
    private Financiamento financiamento;

    public Parcela() { }

    public Parcela(Integer mes, BigDecimal saldoInicial, BigDecimal juros, BigDecimal saldoFinal) {
        this.mes = mes;
        this.saldoInicial = saldoInicial;
        this.juros = juros;
        this.saldoFinal = saldoFinal;
    }

    public Long getId() { return id; }
    public Integer getMes() { return mes; }
    public BigDecimal getSaldoInicial() { return saldoInicial; }
    public BigDecimal getJuros() { return juros; }
    public BigDecimal getSaldoFinal() { return saldoFinal; }
    public Financiamento getFinanciamento() { return financiamento; }

    public void setFinanciamento(Financiamento financiamento) { this.financiamento = financiamento; }
}
```

`src/main/java/org/caixa/financiamentos/service/SimulacaoService.java`
```java
java
package org.caixa.financiamentos.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.caixa.financiamentos.dto.FinanciamentoRequestDTO;
import org.caixa.financiamentos.dto.FinanciamentoResponseDTO;
import org.caixa.financiamentos.dto.ParcelaDTO;
import org.caixa.financiamentos.entity.Financiamento;
import org.caixa.financiamentos.entity.Parcela;
import org.caixa.financiamentos.repository.FinanciamentoRepository;
import org.caixa.financiamentos.util.CalculadoraFinanciamento;

import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ApplicationScoped
public class SimulacaoService {

    private final FinanciamentoRepository financiamentoRepository;

    public SimulacaoService(FinanciamentoRepository financiamentoRepository) {
        this.financiamentoRepository = financiamentoRepository;
    }

    @Transactional
    public FinanciamentoResponseDTO criaFinanciamento(FinanciamentoRequestDTO dto) {
        Financiamento financiamento = new Financiamento(dto.valorInicial(), dto.taxaJurosMensal(), dto.prazoMeses());

        var valorTotalFinal = CalculadoraFinanciamento.montantePeriodo(
                financiamento.getValorInicial(),
                financiamento.getTaxaJurosMensal(),
                financiamento.getPrazoMeses()
        );
        financiamento.setValorTotalFinal(valorTotalFinal);

        var valorTotalJuros = CalculadoraFinanciamento.montanteJurosPeriodo(
                financiamento.getValorInicial(),
                financiamento.getValorTotalFinal()
        );
        financiamento.setValorTotalJuros(valorTotalJuros);

        // generate parcels (in-memory), attach to parent (cascade persists them)
        List<Parcela> parcelas = CalculadoraFinanciamento.geraMemoriaDeCalculo(financiamento);
        parcelas.forEach(financiamento::addMemoriaDeCalculo);

        // persist only the parent; children are persisted by cascade
        financiamentoRepository.persist(financiamento);

        return financiamentoToDTO(financiamento);
    }

    @Transactional
    public FinanciamentoResponseDTO getFinanciamentoById(Long id) {
        Financiamento financiamento = financiamentoRepository.findByIdOptional(id).orElseThrow(
                () -> new NoSuchElementException("O financiamento com a id " + id + " não foi encontrado.")
        );
        return financiamentoToDTO(financiamento);
    }

    private List<ParcelaDTO> parcelaToDTO(List<Parcela> parcelas) {
        return parcelas.stream()
                .map(p -> new ParcelaDTO(
                        p.getMes(),
                        p.getSaldoInicial().setScale(CalculadoraFinanciamento.OUTPUT_SCALE, RoundingMode.HALF_EVEN),
                        p.getJuros().setScale(CalculadoraFinanciamento.OUTPUT_SCALE, RoundingMode.HALF_EVEN),
                        p.getSaldoFinal().setScale(CalculadoraFinanciamento.OUTPUT_SCALE, RoundingMode.HALF_EVEN)
                ))
                .toList();
    }

    private FinanciamentoResponseDTO financiamentoToDTO(Financiamento financiamento) {
        return new FinanciamentoResponseDTO(
                financiamento.getId(),
                financiamento.getValorTotalFinal().setScale(CalculadoraFinanciamento.OUTPUT_SCALE, RoundingMode.HALF_EVEN),
                financiamento.getValorTotalJuros().setScale(CalculadoraFinanciamento.OUTPUT_SCALE, RoundingMode.HALF_EVEN),
                parcelaToDTO(financiamento.getMemoriaDeCalculo())
        );
    }
}
```

Notes:
- Removing direct calls to \`parcelaRepository.persist\` centralizes persistence to the parent and uses cascade persist.
- Calculation logic is centralized in \`CalculadoraFinanciamento\` for testability and single responsibility.
- Keep naming in Portuguese to match current code; if the team prefers English, rename consistently across the project.