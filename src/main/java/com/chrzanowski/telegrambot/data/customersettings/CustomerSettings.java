package com.chrzanowski.telegrambot.data.customersettings;

import com.chrzanowski.telegrambot.banking.Bank;
import com.chrzanowski.telegrambot.data.currency.Currency;
import com.chrzanowski.telegrambot.data.customer.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class CustomerSettings {
    @Id
    private Long customerId;

    @Min(value = 0, message = "Value must be between 0 and 23")
    @Max(value = 23, message = "Value must be between 0 and 23")
    private Integer notificationHour;
    @Enumerated(EnumType.STRING)
    private Bank bank;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Currency baseCurrency;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @MapsId
    private Customer customer;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "customer_settings_currency",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "iso4217Code"))
    private List<Currency> currencies;

}
