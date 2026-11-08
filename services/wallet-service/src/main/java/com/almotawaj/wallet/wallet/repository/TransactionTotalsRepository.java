package com.almotawaj.wallet.wallet.repository;

import com.almotawaj.wallet.wallet.model.DirectionTotal;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionTotalsRepository {
    private final EntityManager entityManager;

    public List<DirectionTotal> totalsByDirection(Specification<WalletTransaction> specification) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<DirectionTotal> query = builder.createQuery(DirectionTotal.class);
        Root<WalletTransaction> root = query.from(WalletTransaction.class);
        query.select(builder.construct(DirectionTotal.class, root.get("direction"), builder.count(root),
                builder.sum(root.<BigDecimal>get("amount"))));
        Predicate predicate = specification.toPredicate(root, query, builder);
        if (predicate != null) {
            query.where(predicate);
        }
        query.groupBy(root.get("direction"));
        return entityManager.createQuery(query).getResultList();
    }
}
