package com.chrzanowski.telegrambot.banking;


import com.chrzanowski.telegrambot.data.currency.Currency;

import java.util.List;


public interface BankService {


    public List<ExchangeRate> getExchangeRates(List<Currency> currencies, Currency baseCurrency);
}

