package com.almotawaj.wallet.user.repository.specification;

import com.almotawaj.wallet.kyc.model.KycApplication;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.model.UserStatus;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class UserSpecifications {
    private static final char LIKE_ESCAPE = '\\';

    private UserSpecifications() {
    }

    public static Specification<User> matches(String search, UserRole role, UserStatus status) {
        return Specification.allOf(searchMatches(search), hasRole(role), hasStatus(status));
    }

    private static Specification<User> hasRole(UserRole role) {
        return (root, query, builder) -> role == null ? null : builder.equal(root.get("role"), role);
    }

    private static Specification<User> hasStatus(UserStatus status) {
        return (root, query, builder) -> status == null ? null : builder.equal(root.get("status"), status);
    }

    private static Specification<User> searchMatches(String search) {
        return (root, query, builder) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String pattern = "%" + escapeLike(search.strip().toLowerCase(Locale.ROOT)) + "%";

            Subquery<Integer> kycName = query.subquery(Integer.class);
            Root<KycApplication> application = kycName.from(KycApplication.class);
            kycName.select(builder.literal(1)).where(
                    builder.equal(application.get("user"), root),
                    builder.like(builder.lower(application.get("fullName")), pattern, LIKE_ESCAPE));

            return builder.or(
                    builder.like(builder.lower(root.get("emailAddress")), pattern, LIKE_ESCAPE),
                    builder.like(root.get("mobileNumber"), pattern, LIKE_ESCAPE),
                    builder.exists(kycName));
        };
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
