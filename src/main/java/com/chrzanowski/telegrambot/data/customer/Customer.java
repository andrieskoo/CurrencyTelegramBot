package com.chrzanowski.telegrambot.data.customer;

import com.chrzanowski.telegrambot.data.customersettings.CustomerSettings;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long telegramId;
    private Boolean isBot;
    private String lastname;
    private String languageCode;
    private Boolean isPremium;
    @Column(columnDefinition = "TIMESTAMP")
    private Instant createdAt;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "customer", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private CustomerSettings customerSettingsId;
}
