package org.caixa.financiamentos.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.caixa.financiamentos.entity.Parcela;

@ApplicationScoped
public class ParcelaRepository implements PanacheRepository<Parcela> {
}
