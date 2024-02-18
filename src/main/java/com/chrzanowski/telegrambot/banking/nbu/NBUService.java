package com.chrzanowski.telegrambot.banking.nbu;

import com.chrzanowski.telegrambot.banking.BankService;
import com.chrzanowski.telegrambot.banking.ExchangeRate;
import com.chrzanowski.telegrambot.data.currency.Currency;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NBUService implements BankService {

    private static final String URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private final HttpClient httpClient;
    private final Gson gson;

    public NBUService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    private List<NBUCurrency> getAllExchangeRate(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            Type nbuListType = new TypeToken<List<NBUCurrency>>(){}.getType();
            return gson.fromJson(responseBody, nbuListType);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public BigDecimal getBuyRate(Currency currency, List<NBUCurrency> ratedCurrencies) {
        Optional<NBUCurrency> nbuCurrency = ratedCurrencies.stream()
                .filter(c -> c.getCc().equals(currency.getName())).findFirst();
        if (nbuCurrency.isPresent()){
            return BigDecimal.valueOf(nbuCurrency.get().getRate());
        }
        return BigDecimal.valueOf(0);
    }

    public BigDecimal getSellRate(Currency currency, List<NBUCurrency> ratedCurrencies) {
        Optional<NBUCurrency> nbuCurrency = ratedCurrencies.stream()
                .filter(c -> c.getCc().equals(currency.getName())).findFirst();
        if (nbuCurrency.isPresent()){
            return BigDecimal.valueOf(nbuCurrency.get().getRate());
        }
        return BigDecimal.valueOf(0);
    }

    @Override
    public List<ExchangeRate> getExchangeRates(List<Currency> currencies, Currency baseCurrency) {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        Optional<List<NBUCurrency>> exchangeRate = Optional.ofNullable(getAllExchangeRate());
        if (exchangeRate.isPresent()) {
                List<NBUCurrency> ratedCurrencies = exchangeRate.get();
                for (Currency currency : currencies) {
                    exchangeRates.add(new ExchangeRate(baseCurrency, currency, getBuyRate(currency, ratedCurrencies), getSellRate(currency, ratedCurrencies)));
                }
        }
        return exchangeRates;
    }
}
