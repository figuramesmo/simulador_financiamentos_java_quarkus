package org.caixa.financiamentos.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "parcela")
public class Parcela {

    public Parcela(Integer mes, BigDecimal saldoInicial, BigDecimal juros, BigDecimal saldoFinal) {
        this.mes = mes;
        this.saldoInicial = saldoInicial;
        this.juros = juros;
        this.saldoFinal = saldoFinal;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer mes;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal saldoInicial;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal juros;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal saldoFinal;

    public Long getId() {
        return id;
    }

    public Integer getMes() {
        return mes;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }
}
