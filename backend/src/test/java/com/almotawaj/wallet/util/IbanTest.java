package com.almotawaj.wallet.util;

import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.wallet.model.TopUpSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IbanTest {
    @Test
    void isValid_acceptsRealBahrainIbanWithSpaces() {
        assertThat(Iban.isValid("bh67 bmag 0000 1299 1234 56")).isTrue();
    }

    @Test
    void isValid_acceptsForeignIban() {
        assertThat(Iban.isValid("GB82WEST12345698765432")).isTrue();
    }

    @Test
    void isValid_rejectsWrongCheckDigits() {
        assertThat(Iban.isValid("BH68BMAG00001299123456")).isFalse();
    }

    @Test
    void isValid_rejectsMalformedValues() {
        assertThat(Iban.isValid("BH67")).isFalse();
        assertThat(Iban.isValid("1234BMAG00001299123456")).isFalse();
        assertThat(Iban.isValid(null)).isFalse();
    }

    @Test
    void topUpSources_haveValidIbans() {
        assertThat(TopUpSource.values()).allSatisfy(source -> assertThat(Iban.isValid(source.getIban())).isTrue());
    }

    @Test
    void of_buildsValidBahrainIban() {
        String iban = Iban.of(WalletConstants.COUNTRY_CODE, WalletConstants.BANK_CODE + "00000000001234");

        assertThat(iban).hasSize(WalletConstants.IBAN_LENGTH).startsWith("BH").contains("ALMT");
        assertThat(Iban.isValid(iban)).isTrue();
    }
}
