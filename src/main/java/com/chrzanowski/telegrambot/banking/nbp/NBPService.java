package com.chrzanowski.telegrambot.banking.nbp;

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

public class NBPService implements BankService {

    private static final String URL = "https://api.nbp.pl/api/exchangerates/tables/A/?format=json";
    private final HttpClient httpClient;
    private final Gson gson;

    public NBPService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    private List<ExchangeRateNBP> getAllExchangeRate(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            Type listType = new TypeToken<List<ExchangeRateNBP>>(){}.getType();
            List<ExchangeRateNBP> exchangeRates = gson.fromJson(responseBody, listType);
            return exchangeRates;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public BigDecimal getBuyRate(Currency currency, List<NBPCurrency> ratedCurrencies) {
        Optional<NBPCurrency> nbuCurrency = ratedCurrencies.stream()
                .filter(c-> c.getCurrencyCode().equals(currency.getName())).findFirst();
        if (nbuCurrency.isPresent()){
            return BigDecimal.valueOf(nbuCurrency.get().getRate());
        }
        return BigDecimal.valueOf(0);
    }

    public BigDecimal getSellRate(Currency currency, List<NBPCurrency> ratedCurrencies) {
        Optional<NBPCurrency> nbuCurrency = ratedCurrencies.stream()
                .filter(c-> c.getCurrencyCode().equals(currency.getName())).findFirst();
        if (nbuCurrency.isPresent()){
            return BigDecimal.valueOf(nbuCurrency.get().getRate());
        }
        return BigDecimal.valueOf(0);
    }

    @Override
    public List<ExchangeRate> getExchangeRates(List<Currency> currencies, Currency baseCurrency) {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        Optional<List<ExchangeRateNBP>> exchangeRate = Optional.ofNullable(getAllExchangeRate());
        if (exchangeRate.isPresent()) {
                List<NBPCurrency> ratedCurrencies = exchangeRate.get().get(0).getRates();
                for (Currency currency : currencies) {
                    exchangeRates.add(new ExchangeRate(baseCurrency, currency, getBuyRate(currency, ratedCurrencies), getSellRate(currency, ratedCurrencies)));
                }
        }
        return exchangeRates;
    }
}
