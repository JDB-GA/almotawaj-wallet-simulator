package com.almotawaj.wallet.repository.specification;

import com.almotawaj.wallet.config.constants.SortFields;
import com.almotawaj.wallet.model.WalletTransaction;
import com.almotawaj.wallet.model.request.TransactionSearchRequest;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

public final class WalletTransactionSpecifications {
    private static final char LIKE_ESCAPE = '\\';
    private static final List<SearchField> OWN_SEARCH_FIELDS = List.of(
            root -> root.get("counterpartyName"), root -> root.get("reference"), root -> root.get("description"));
    private static final List<SearchField> SYSTEM_SEARCH_FIELDS = List.of(
            root -> root.get("wallet").get("user").get("emailAddress"),
            root -> root.get("counterpartyName"), root -> root.get("reference"), root -> root.get("description"));

    private WalletTransactionSpecifications() {
    }

    public static Specification<WalletTransaction> forWallet(UUID walletId, TransactionSearchRequest filter, ZoneId zone) {
        Specification<WalletTransaction> ofWallet = (root, query, builder) -> builder.equal(root.get("wallet").get("id"), walletId);
        return Specification.allOf(ofWallet, filtered(filter, zone), searchMatches(filter.search(), OWN_SEARCH_FIELDS));
    }

    public static Specification<WalletTransaction> forUser(UUID userId, TransactionSearchRequest filter, ZoneId zone) {
        Specification<WalletTransaction> ofUser =
                (root, query, builder) -> builder.equal(root.get("wallet").get("user").get("id"), userId);
        return Specification.allOf(ofUser, filtered(filter, zone), searchMatches(filter.search(), OWN_SEARCH_FIELDS));
    }

    public static Specification<WalletTransaction> forSystem(TransactionSearchRequest filter, ZoneId zone) {
        return Specification.allOf(filtered(filter, zone), searchMatches(filter.search(), SYSTEM_SEARCH_FIELDS));
    }

    private static Specification<WalletTransaction> filtered(TransactionSearchRequest filter, ZoneId zone) {
        return Specification.allOf(
                equalTo("type", filter.type()),
                equalTo("direction", filter.direction()),
                (root, query, builder) -> filter.from() == null ? null
                        : builder.greaterThanOrEqualTo(root.get(SortFields.CREATED_AT), filter.from().atStartOfDay(zone).toInstant()),
                (root, query, builder) -> filter.to() == null ? null
                        : builder.lessThan(root.get(SortFields.CREATED_AT), filter.to().plusDays(1).atStartOfDay(zone).toInstant()),
                (root, query, builder) -> filter.minAmount() == null ? null
                        : builder.greaterThanOrEqualTo(root.get("amount"), filter.minAmount()),
                (root, query, builder) -> filter.maxAmount() == null ? null
                        : builder.lessThanOrEqualTo(root.get("amount"), filter.maxAmount()));
    }

    private static Specification<WalletTransaction> equalTo(String attribute, Object value) {
        return (root, query, builder) -> value == null ? null : builder.equal(root.get(attribute), value);
    }

    private static Specification<WalletTransaction> searchMatches(String search, List<SearchField> fields) {
        return (root, query, builder) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String pattern = "%" + escapeLike(search.strip().toLowerCase(Locale.ROOT)) + "%";
            return builder.or(fields.stream()
                    .map(field -> builder.like(builder.lower(field.apply(root)), pattern, LIKE_ESCAPE))
                    .toArray(Predicate[]::new));
        };
    }

    private interface SearchField extends Function<Root<WalletTransaction>, Expression<String>> {
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
