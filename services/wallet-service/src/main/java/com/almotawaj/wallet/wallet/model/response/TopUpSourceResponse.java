package com.almotawaj.wallet.wallet.model.response;

import com.almotawaj.wallet.wallet.model.TopUpSource;

public record TopUpSourceResponse(TopUpSource id, String holderName, String iban, String bic, String bankName) {
    public static TopUpSourceResponse from(TopUpSource source) {
        return new TopUpSourceResponse(source, source.getHolderName(), source.getIban(), source.getBic(), source.getBankName());
    }
}
