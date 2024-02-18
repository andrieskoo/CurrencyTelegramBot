package com.chrzanowski.telegrambot.data.customersettings;

import com.chrzanowski.telegrambot.banking.Bank;
import com.chrzanowski.telegrambot.data.currency.Currency;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CustomerSettingsService {
    private final CustomerSettingsRepository customerSettingsRepository;

    @Autowired
    public CustomerSettingsService(CustomerSettingsRepository customerSettingsRepository) {
        this.customerSettingsRepository = customerSettingsRepository;
    }

    public CustomerSettings saveCustomerSettings(CustomerSettings customerSettings){
        return customerSettingsRepository.save(customerSettings);
    }

    public Currency getBaseCurrency(Long id){
        return customerSettingsRepository.getReferenceById(id).getBaseCurrency();
    }

    public void setBaseCurrency(Long id, Currency currency){
        customerSettingsRepository.getReferenceById(id).setBaseCurrency(currency);
    }

    public void addCurrency(Currency currency, Long id){
        CustomerSettings customerSettings = getCustomerSettingsById(id);
        List<Currency> currencies = getCurrencies(id);
        currencies.add(currency);
        customerSettings.setCurrencies(currencies);
        customerSettingsRepository.save(customerSettings);
    }
    public void removeCurrency(Currency currency, Long id){
        CustomerSettings customerSettings = getCustomerSettingsById(id);
        List<Currency> currencies = getCurrencies(id);
        currencies.remove(currency);
        customerSettings.setCurrencies(currencies);
        customerSettingsRepository.save(customerSettings);
    }

    public CustomerSettings getCustomerSettingsById(Long id){
        return customerSettingsRepository.findById(id).orElse(null);
    }

    public List<Currency> getCurrencies(Long id){
        CustomerSettings customerSettings = getCustomerSettingsById(id);
        if (customerSettings != null){
            Hibernate.initialize(customerSettings.getCurrencies());
            return customerSettings.getCurrencies();
        }
        return Collections.emptyList();
    }

    public Bank getCustomerBank(Long id){
        return Objects.requireNonNull(customerSettingsRepository.findById(id).orElse(null)).getBank();
    }
    public void setCustomerBank(Long id, Bank bank){
        CustomerSettings customerSettings = getCustomerSettingsById(id);
        customerSettings.setBank(bank);
        customerSettingsRepository.save(customerSettings);
    }
    public Integer getCustomerNotificationHour(Long id) {
        return Objects.requireNonNull(customerSettingsRepository.findById(id).orElse(null)).getNotificationHour();
    }
    public void setCustomerNotificationHour(Long id, Integer hour) {
        CustomerSettings customerSettings = getCustomerSettingsById(id);
        customerSettings.setNotificationHour(hour);
        customerSettingsRepository.save(customerSettings);
    }
}
