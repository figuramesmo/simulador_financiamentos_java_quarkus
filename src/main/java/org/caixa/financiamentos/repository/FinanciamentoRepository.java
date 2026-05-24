package org.caixa.financiamentos.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.caixa.financiamentos.entity.Financiamento;

@ApplicationScoped
public class FinanciamentoRepository implements PanacheRepository<Financiamento> {
}
