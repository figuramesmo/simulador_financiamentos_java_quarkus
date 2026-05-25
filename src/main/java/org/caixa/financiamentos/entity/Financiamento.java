package org.caixa.financiamentos.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "financiamento")
public class Financiamento extends PanacheEntityBase {

    public Financiamento(
            BigDecimal valorInicial,
            BigDecimal taxaJurosMensal,
            Integer prazoMeses
    ) {
        this.valorInicial = valorInicial;
        this.taxaJurosMensal = taxaJurosMensal;
        this.prazoMeses = prazoMeses;
        this.memoriaDeCalculo =new ArrayList<>();
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valorInicial;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal taxaJurosMensal;

    @Column(nullable = false)
    private Integer prazoMeses;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valorTotalFinal;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valorTotalJuros;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Parcela> memoriaDeCalculo;

    public Long getId() {
        return id;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public BigDecimal getTaxaJurosMensal() {
        return taxaJurosMensal;
    }

    public Integer getPrazoMeses() {
        return prazoMeses;
    }


    public BigDecimal getValorTotalFinal() {
        return valorTotalFinal;
    }

    public void setValorTotalFinal(BigDecimal valorTotalFinal) {
        this.valorTotalFinal = valorTotalFinal;
    }

    public BigDecimal getValorTotalJuros() {
        return valorTotalJuros;
    }

    public void setValorTotalJuros(BigDecimal valorTotalJuros) {
        this.valorTotalJuros = valorTotalJuros;
    }

    public List<Parcela> getMemoriaDeCalculo() {
        return memoriaDeCalculo;
    }

    public void addMemoriaDeCalculo(Parcela parcela) {
        this.memoriaDeCalculo.add(parcela);
    }
}
