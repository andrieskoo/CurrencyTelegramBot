package com.chrzanowski.telegrambot.banking.privatbank;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

public class ExchangeRatePB {
    private String date;
    private String bank;
    @Getter
    @SerializedName("baseCurrency")
    private Integer baseCurrency;

    @SerializedName("baseCurrencyLit")
    private String baseCurrencyLitCode;

    @Getter
    @SerializedName("exchangeRate")
    private List<PrivatBankCurrency> currencies;

    @Override
    public String toString() {
        return "currencies=" + currencies;
    }

}
