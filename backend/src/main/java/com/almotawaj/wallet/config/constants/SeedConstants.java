package com.almotawaj.wallet.config.constants;

import com.almotawaj.wallet.kyc.model.KycApplicationStatus;
import com.almotawaj.wallet.seed.model.SeedAccount;
import com.almotawaj.wallet.seed.model.SeedVoucher;
import com.almotawaj.wallet.user.model.Permission;
import com.almotawaj.wallet.user.model.UserRole;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public final class SeedConstants {
    public static final String SEED_TOKEN_PROPERTY = "${seed-token:}";
    public static final String SEED_PASSWORD_PROPERTY = "${seed-password:}";

    public static final String MERCHANT_EMAIL = "merchant@almotawaj.com";
    public static final List<SeedAccount> ACCOUNTS = List.of(
            new SeedAccount("admin@almotawaj.com", "30000001", UserRole.SUPER_ADMIN, Set.of(), null, null, null),
            new SeedAccount("reviewer@almotawaj.com", "30000004", UserRole.ADMIN,
                    Set.of(Permission.KYC_REVIEW, Permission.USER_MANAGE, Permission.STATISTICS_VIEW), null, null, null),
            new SeedAccount(MERCHANT_EMAIL, "30000003", UserRole.MERCHANT, Set.of(), null, null, null),
            new SeedAccount("client@almotawaj.com", "30000002", UserRole.CLIENT, Set.of(),
                    KycApplicationStatus.PENDING, "Ali Hasan", "900000001"),
            new SeedAccount("illia@almotawaj.com", "30000005", UserRole.CLIENT, Set.of(),
                    KycApplicationStatus.APPROVED, "Illia", "900000002"),
            new SeedAccount("salman@almotawaj.com", "30000006", UserRole.CLIENT, Set.of(),
                    KycApplicationStatus.REJECTED, "Salman Mohammed", "900000003"));

    public static final int VOUCHER_EXPIRY_DAYS = 30;
    public static final List<SeedVoucher> VOUCHERS = List.of(
            new SeedVoucher("Coffee Gift Card", "9.000", "10.000", 50, 2),
            new SeedVoucher("Dinner for Two", "25.000", "35.000", 10, 1),
            new SeedVoucher("Cinema Ticket", "4.000", "5.000", 100, 4),
            new SeedVoucher("Spa Day - Last One", "30.000", "45.000", 1, 1));
    public static final String REVIEWER_EMAIL = "admin@almotawaj.com";
    public static final String REJECTION_REASON = "The CPR copy is blurry. Please upload a clearer scan.";
    public static final LocalDate DATE_OF_BIRTH = LocalDate.of(1995, 1, 15);
    public static final String NATIONALITY = "BH";
    public static final String BLOCK = "338";
    public static final String ROAD = "3803";
    public static final String BUILDING = "1207";
    public static final String AREA = "Adliya";

    public static Set<String> accountEmails() {
        return ACCOUNTS.stream().map(SeedAccount::email).collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    private SeedConstants() {
    }
}
