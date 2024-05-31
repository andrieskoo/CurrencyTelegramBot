package com.chrzanowski.telegrambot.data.customer;

import com.chrzanowski.telegrambot.banking.Bank;
import com.chrzanowski.telegrambot.data.currency.Currency;
import com.chrzanowski.telegrambot.data.currency.CurrencyService;
import com.chrzanowski.telegrambot.data.customersettings.CustomerSettings;
import com.chrzanowski.telegrambot.data.customersettings.CustomerSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerSettingsService customerSettingsService;
    private final CurrencyService currencyService;

    @Autowired
    public CustomerService(CustomerRepository userRepository, CustomerSettingsService customerSettingsService, CurrencyService currencyService) {
        this.customerRepository = userRepository;
        this.customerSettingsService = customerSettingsService;
        this.currencyService = currencyService;
    }

    public Customer getCustomerById(Long id){
        return customerRepository.findById(id).orElse(null);
    }

    public Customer getCustomerByTelegramId(Long id){
        return customerRepository.findCustomerByTelegramId(id);
    }


    public Customer saveCustomer(Customer user){
        Customer customer = customerRepository.save(user);
        CustomerSettings customerSettings = new CustomerSettings();
        customerSettings.setCustomerId(customer.getId());
        customerSettings.setCustomer(customer);
        customerSettings.setBaseCurrency(getDefaultCurrency(customer.getLanguageCode()));

        customerSettings.setCurrencies(List.of(currencyService.getCurrencyByIso4217Code(840),currencyService.getCurrencyByIso4217Code(978)));

        if (customer.getLanguageCode().equals("pl")){
            customerSettings.setBank(Bank.NBP);
        }
        else {
            customerSettings.setBank(Bank.PRIVATBANK);
        }
        customerSettingsService.saveCustomerSettings(customerSettings);

        return customer;
    }

    public Currency getBaseCurrency(Long id){
        return customerSettingsService.getBaseCurrency(id);
    }
    public void setBaseCurrency(Long id, Currency currency){
        customerSettingsService.setBaseCurrency(id, currency);
    }

    public void addCurrency(Currency currency, Long id){
        customerSettingsService.addCurrency(currency, id);
    }
    public void removeCurrency(Currency currency, Long id){
        customerSettingsService.removeCurrency(currency, id);
    }

    public List<Currency> getCustomerCurrency(Customer customer){
        return customerSettingsService.getCurrencies(customer.getId());
    }

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    public Bank getCustomerBank(Customer customer){
        return customerSettingsService.getCustomerBank(customer.getId());
    }
    public void setCustomerBank(Customer customer, Bank bank){
        customerSettingsService.setCustomerBank(customer.getId(), bank);
    }
    public CustomerSettings getCustomerSettings(Customer customer){
        return customerSettingsService.getCustomerSettingsById(customer.getId());
    }

    private Currency getDefaultCurrency(String locale){
        if (locale.equals("uk")){
            return currencyService.getCurrencyByIso4217Code(980);
        } else if (locale.equals("pl")) {
            return currencyService.getCurrencyByIso4217Code(985);
        }
        else {
            return currencyService.getCurrencyByIso4217Code(980);
        }
    }

    public Integer getCustomerNotificationHour(Customer customer) {
        return customerSettingsService.getCustomerNotificationHour(customer.getId());
    }
    public void setCustomerNotificationHour(Customer customer, Integer hour) {
        customerSettingsService.setCustomerNotificationHour(customer.getId(), hour);
    }
}
