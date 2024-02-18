package com.chrzanowski.telegrambot.banking.privatbank;

import com.chrzanowski.telegrambot.banking.BankService;
import com.chrzanowski.telegrambot.banking.ExchangeRate;
import com.chrzanowski.telegrambot.data.currency.Currency;
import com.google.gson.Gson;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PrivatBankService implements BankService {
    private static final String URL = "https://api.privatbank.ua/p24api/exchange_rates?date=";
    private final HttpClient httpClient;
    private final Gson gson;
    private String date;

    public PrivatBankService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        LocalDateTime lcd = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.date = lcd.format(formatter);
    }

    private ExchangeRatePB getAllExchangeRate() {
        String url = URL + date;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            return gson.fromJson(responseBody, ExchangeRatePB.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BigDecimal getBuyRate(Currency currency, List<PrivatBankCurrency> ratedCurrencies){
        Optional<PrivatBankCurrency> privatBankCurrency = ratedCurrencies.stream()
                .filter(c -> c.getCurrency().equals(currency.getName())).findFirst();
        if (privatBankCurrency.isPresent()){
            return BigDecimal.valueOf(privatBankCurrency.get().getPurchaseRate());
        }
        return BigDecimal.valueOf(0);
    }
    private BigDecimal getSellRate(Currency currency, List<PrivatBankCurrency> ratedCurrencies){
        Optional<PrivatBankCurrency> privatBankCurrency = ratedCurrencies.stream()
                .filter(c -> c.getCurrency().equals(currency.getName())).findFirst();
        if (privatBankCurrency.isPresent()){
            return BigDecimal.valueOf(privatBankCurrency.get().getSaleRate());
        }
        return BigDecimal.valueOf(0);
    }

    @Override
    public List<ExchangeRate> getExchangeRates(List<Currency> currencies, Currency baseCurrency) {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        Optional<ExchangeRatePB> exchangeRate = Optional.ofNullable(getAllExchangeRate());
        if (exchangeRate.isPresent()) {
            if (baseCurrency.getIso4217Code().equals(exchangeRate.get().getBaseCurrency())) {
                List<PrivatBankCurrency> ratedCurrencies = exchangeRate.get().getCurrencies();
                for (Currency currency : currencies) {
                    exchangeRates.add(new ExchangeRate(baseCurrency, currency, getBuyRate(currency, ratedCurrencies), getSellRate(currency, ratedCurrencies)));
                }
            } else {
                return Collections.emptyList();
            }
        }
        return exchangeRates;
    }
}
