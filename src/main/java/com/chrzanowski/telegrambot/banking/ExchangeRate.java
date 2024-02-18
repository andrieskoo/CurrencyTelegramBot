package com.chrzanowski.telegrambot.banking;


import com.chrzanowski.telegrambot.data.currency.Currency;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
public class ExchangeRate {

    private Currency baseCurrency;
    private Currency ratedCurrency;

    private BigDecimal buy;

    private BigDecimal sell;

    public ExchangeRate(Currency baseCurrency, Currency ratedCurrency, BigDecimal buy, BigDecimal sell) {
        this.baseCurrency = baseCurrency;
        this.ratedCurrency = ratedCurrency;
        this.buy = buy;
        this.sell = sell;
    }
}
