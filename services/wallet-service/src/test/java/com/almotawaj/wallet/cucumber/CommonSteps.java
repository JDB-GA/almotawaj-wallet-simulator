package com.almotawaj.wallet.cucumber;

import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.account.model.AccountRole;
import com.almotawaj.wallet.account.model.AccountStatus;
import com.almotawaj.wallet.account.repository.AccountHolderRepository;
import com.almotawaj.wallet.paymentrequest.repository.PaymentRequestRepository;
import com.almotawaj.wallet.payout.repository.PayoutBatchRepository;
import com.almotawaj.wallet.payout.repository.PayoutItemRepository;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import com.almotawaj.wallet.wallet.repository.WalletTransactionRepository;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {
    @Autowired
    private ScenarioState state;
    @Autowired
    private AccountHolderRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletTransactionRepository transactionRepository;
    @Autowired
    private PaymentRequestRepository paymentRequestRepository;
    @Autowired
    private PayoutBatchRepository payoutBatchRepository;
    @Autowired
    private PayoutItemRepository payoutItemRepository;

    @Given("a client {string} with balance {bigdecimal}")
    public void aClientWithBalance(String email, BigDecimal balance) {
        createAccount(email, balance, AccountRole.CLIENT);
    }

    @Given("a merchant {string} with balance {bigdecimal}")
    public void aMerchantWithBalance(String email, BigDecimal balance) {
        createAccount(email, balance, AccountRole.MERCHANT);
    }

    private void createAccount(String email, BigDecimal balance, AccountRole role) {
        state.setCounter(state.getCounter() + 1);
        int counter = state.getCounter();

        AccountHolder user = new AccountHolder();
        user.setId(UUID.randomUUID());
        user.setEmailAddress(email);
        user.setMobileNumber("+9733330000" + counter);
        user.setRole(role);
        user.setStatus(AccountStatus.ACTIVE);
        user.setKycApproved(role == AccountRole.CLIENT);
        userRepository.saveAndFlush(user);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setIban("BH02ALMT0000000000090" + counter);
        wallet.setBalance(balance);
        walletRepository.saveAndFlush(wallet);
    }

    @Then("the response status is {int}")
    public void theResponseStatusIs(int status) {
        assertThat(state.getResponse().statusCode()).isEqualTo(status);
    }

    @Then("{string} has balance {bigdecimal}")
    public void hasBalance(String email, BigDecimal expected) {
        AccountHolder user = userRepository.findByEmailAddressIgnoreCase(email).orElseThrow();
        Wallet wallet = walletRepository.findByUserId(user.getId()).orElseThrow();
        assertThat(wallet.getBalance()).isEqualByComparingTo(expected);
    }

    @After
    public void cleanUp() {
        payoutItemRepository.deleteAll();
        payoutBatchRepository.deleteAll();
        paymentRequestRepository.deleteAll();
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
    }
}