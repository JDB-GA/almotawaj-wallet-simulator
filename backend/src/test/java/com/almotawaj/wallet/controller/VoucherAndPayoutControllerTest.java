package com.almotawaj.wallet.controller;

import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.config.security.JwtUtils;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.model.KycStatus;
import com.almotawaj.wallet.model.User;
import com.almotawaj.wallet.model.UserRole;
import com.almotawaj.wallet.model.Voucher;
import com.almotawaj.wallet.model.Wallet;
import com.almotawaj.wallet.repository.UserRepository;
import com.almotawaj.wallet.repository.VoucherRepository;
import com.almotawaj.wallet.repository.WalletRepository;
import com.jayway.jsonpath.JsonPath;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VoucherAndPayoutControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private VoucherRepository voucherRepository;

    private User merchant;
    private User client;

    @BeforeEach
    void setUp() {
        merchant = user("shop@example.com", "+97333100001", UserRole.MERCHANT);
        client = user("sara@example.com", "+97333100002", UserRole.CLIENT);
        wallet(merchant, "BH02ALMT00000000000101", "100.000");
        wallet(client, "BH02ALMT00000000000102", "100.000");
    }

    @Test
    void client_buysAVoucher_andRepeatingTheKeyReturnsTheSamePurchase() throws Exception {
        Voucher voucher = voucher(3);
        String first = mvc.perform(post("/api/vouchers/{id}/purchase", voucher.getId()).cookie(cookie(client))
                        .header(WalletConstants.IDEMPOTENCY_HEADER, "key-1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        mvc.perform(post("/api/vouchers/{id}/purchase", voucher.getId()).cookie(cookie(client))
                        .header(WalletConstants.IDEMPOTENCY_HEADER, "key-1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(JsonPath.<String>read(first, "$.id")));
    }

    @Test
    void soldOutVoucher_returns409() throws Exception {
        Voucher voucher = voucher(0);

        mvc.perform(post("/api/vouchers/{id}/purchase", voucher.getId()).cookie(cookie(client)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("VOUCHER_SOLD_OUT"));
    }

    @Test
    void merchant_cannotBuyVouchers() throws Exception {
        mvc.perform(post("/api/vouchers/{id}/purchase", voucher(3).getId()).cookie(cookie(merchant)))
                .andExpect(status().isForbidden());
    }

    @Test
    void tooLongIdempotencyKey_failsValidation() throws Exception {
        mvc.perform(post("/api/vouchers/{id}/purchase", voucher(3).getId()).cookie(cookie(client))
                        .header(WalletConstants.IDEMPOTENCY_HEADER, "k".repeat(65)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    void client_seesVouchersOnSale() throws Exception {
        voucher(3);

        mvc.perform(get("/api/vouchers").cookie(cookie(client)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Gift card"));
    }

    @Test
    void merchant_createsAVoucher_butNotAbovePriceFaceValue() throws Exception {
        mvc.perform(post("/api/merchant/vouchers").cookie(cookie(merchant)).contentType(MediaType.APPLICATION_JSON)
                        .content(voucherJson("45.000", "50.000")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.remaining").value(10))
                .andExpect(jsonPath("$.version").value(0));

        mvc.perform(post("/api/merchant/vouchers").cookie(cookie(merchant)).contentType(MediaType.APPLICATION_JSON)
                        .content(voucherJson("60.000", "50.000")))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.code").value("VOUCHER_PRICE_ABOVE_FACE_VALUE"));
    }

    @Test
    void staleVoucherEdit_returns409() throws Exception {
        Voucher voucher = voucher(3);
        String body = """
                {"title":"Renamed","price":"40.000","status":"ACTIVE","saleEndsAt":"%s","version":%d}"""
                .formatted(Instant.now().plus(Duration.ofDays(3)), voucher.getVersion() + 5);

        mvc.perform(put("/api/merchant/vouchers/{id}", voucher.getId()).cookie(cookie(merchant))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("VOUCHER_EDIT_CONFLICT"));
    }

    @Test
    void merchant_startsAPayout_andGets202() throws Exception {
        mvc.perform(post("/api/merchant/payouts").cookie(cookie(merchant)).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items":[{"recipient":"sara@example.com","amount":"15.000"}]}"""))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andExpect(jsonPath("$.items[0].status").value("PENDING"));
    }

    @Test
    void client_cannotStartPayouts() throws Exception {
        mvc.perform(post("/api/merchant/payouts").cookie(cookie(client)).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items":[{"recipient":"shop@example.com","amount":"15.000"}]}"""))
                .andExpect(status().isForbidden());
    }

    @Test
    void swagger_documentsTheNewEndpoints() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/vouchers/{voucherId}/purchase'].post.parameters[?(@.name == 'Idempotency-Key')]").exists())
                .andExpect(jsonPath("$.paths['/api/merchant/payouts'].post.responses['202']").exists())
                .andExpect(jsonPath("$.paths['/api/merchant/vouchers/{voucherId}'].put.responses['409']").exists());
    }

    private String voucherJson(String price, String faceValue) {
        return """
                {"title":"Gift card","price":"%s","faceValue":"%s","totalQuantity":10,"perClientLimit":2,"saleEndsAt":"%s"}"""
                .formatted(price, faceValue, Instant.now().plus(Duration.ofDays(3)));
    }

    private Voucher voucher(int remaining) {
        Voucher voucher = new Voucher();
        voucher.setMerchant(merchant);
        voucher.setTitle("Gift card");
        voucher.setPrice(new BigDecimal("45.000"));
        voucher.setFaceValue(new BigDecimal("50.000"));
        voucher.setTotalQuantity(Math.max(remaining, 1));
        voucher.setRemaining(remaining);
        voucher.setPerClientLimit(5);
        voucher.setSaleEndsAt(Instant.now().plus(Duration.ofDays(1)));
        return voucherRepository.saveAndFlush(voucher);
    }

    private Cookie cookie(User user) {
        return new Cookie(SecurityConstants.AUTH_COOKIE_NAME, jwtUtils.generateToken(new MyUserDetails(user)));
    }

    private User user(String email, String mobile, UserRole role) {
        User user = new User();
        user.setEmailAddress(email);
        user.setMobileNumber(mobile);
        user.setPassword("hashed");
        user.setRole(role);
        user.setEmailVerified(true);
        user.setKycStatus(role == UserRole.CLIENT ? KycStatus.APPROVED : KycStatus.NOT_SUBMITTED);
        return userRepository.saveAndFlush(user);
    }

    private void wallet(User owner, String iban, String balance) {
        Wallet wallet = new Wallet();
        wallet.setUser(owner);
        wallet.setIban(iban);
        wallet.setBalance(new BigDecimal(balance));
        walletRepository.saveAndFlush(wallet);
    }
}
