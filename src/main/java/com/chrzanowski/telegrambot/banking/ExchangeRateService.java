package com.chrzanowski.telegrambot.banking;

import com.chrzanowski.telegrambot.banking.monobank.MonoBankService;
import com.chrzanowski.telegrambot.banking.nbp.NBPService;
import com.chrzanowski.telegrambot.banking.nbu.NBUService;
import com.chrzanowski.telegrambot.banking.privatbank.PrivatBankService;
import com.chrzanowski.telegrambot.data.currency.Currency;
import com.chrzanowski.telegrambot.data.customersettings.CustomerSettings;
import java.util.List;

public class ExchangeRateService {
    public String getMessageRate(CustomerSettings settings) {
        StringBuilder sb = new StringBuilder("Курс на сьогодні у *" + settings.getBank() + "* : \n\n");
        List<ExchangeRate> rates = getExchangeRates(settings);

        for (var rate : rates) {
            sb.append(rate.getBaseCurrency().getName()).append(" / ").append(rate.getRatedCurrency().getName());
            sb.append("\nКупівля: *").append(rate.getBuy()).append("* \nПродаж: *").append(rate.getSell()).append("*");
            sb.append("\n\n");
        }
        return sb.toString();
    }
    private List<ExchangeRate> getExchangeRates(CustomerSettings customerSettings){
        List<Currency> currencies = customerSettings.getCurrencies();
        BankService bankService;
        switch (customerSettings.getBank()){
            case PRIVATBANK -> bankService = new PrivatBankService();
            case NBU -> bankService =new NBUService();
            case MONOBANK -> bankService = new MonoBankService();
            case NBP -> bankService = new NBPService();
            default ->
                    throw new IllegalArgumentException("Unknown bank: " + customerSettings.getBank());
        }
        return bankService.getExchangeRates(currencies, customerSettings.getBaseCurrency());
    }
}
