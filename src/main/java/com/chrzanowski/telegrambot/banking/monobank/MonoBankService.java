package com.chrzanowski.telegrambot.banking.monobank;

import com.chrzanowski.telegrambot.banking.BankService;
import com.chrzanowski.telegrambot.banking.ExchangeRate;
import com.chrzanowski.telegrambot.data.currency.Currency;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MonoBankService implements BankService {



    private static final String URL = "https://api.monobank.ua/bank/currency";
    private final HttpClient httpClient;
    private final Gson gson;

    public MonoBankService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    private List<MonoBankCurrency> getAllExchangeRate(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            JsonArray jsonArray = JsonParser.parseString(responseBody).getAsJsonArray();

            List<MonoBankCurrency> currencies = new ArrayList<>();

            for (var jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                if (jsonObject.has("rateCross")) {
                    MonoBankCurrency currency = new MonoBankCurrency();
                    currency.setCurrencyCodeA(jsonObject.get("currencyCodeA").getAsInt());
                    currency.setCurrencyCodeB(jsonObject.get("currencyCodeB").getAsInt());
                    currency.setDate(jsonObject.get("date").getAsLong());
                    currency.setRateBuy(jsonObject.get("rateCross").getAsDouble());
                    currency.setRateSell(jsonObject.get("rateCross").getAsDouble());
                    currencies.add(currency);
                } else {
                    currencies.add(gson.fromJson(jsonObject, MonoBankCurrency.class));
                }
            }

            return currencies;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public BigDecimal getBuyRate(Currency currency, List<MonoBankCurrency> ratedCurrencies) {
        Optional<MonoBankCurrency> monoBankCurrency = ratedCurrencies.stream()
                .filter(c -> c.getCurrencyCodeA() == currency.getIso4217Code()).findFirst();
        if (monoBankCurrency.isPresent()){
            return BigDecimal.valueOf(monoBankCurrency.get().getRateBuy());
        }
        return BigDecimal.valueOf(0);
    }

    public BigDecimal getSellRate(Currency currency, List<MonoBankCurrency> ratedCurrencies) {
        Optional<MonoBankCurrency> monoBankCurrency = ratedCurrencies.stream()
                .filter(c -> c.getCurrencyCodeA() == currency.getIso4217Code()).findFirst();
        if (monoBankCurrency.isPresent()){
            return BigDecimal.valueOf(monoBankCurrency.get().getRateSell());
        }
        return BigDecimal.valueOf(0);
    }

    @Override
    public List<ExchangeRate> getExchangeRates(List<Currency> currencies, Currency baseCurrency) {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        Optional<List<MonoBankCurrency>> exchangeRate = Optional.ofNullable(getAllExchangeRate());
        if (exchangeRate.isPresent()) {
            List<MonoBankCurrency> ratedCurrencies = exchangeRate.get();
            for (Currency currency : currencies) {
                exchangeRates.add(new ExchangeRate(baseCurrency, currency, getBuyRate(currency, ratedCurrencies), getSellRate(currency, ratedCurrencies)));
            }
        }
        return exchangeRates;
    }
}
