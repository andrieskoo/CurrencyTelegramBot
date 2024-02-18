package com.chrzanowski.telegrambot.banking.nbp;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

public class ExchangeRateNBP {

    private String table;


    private String no;

    @SerializedName("effectiveDate")
    private String effectiveDate;

    @Getter
    @SerializedName("rates")
    private List<NBPCurrency> rates;


}
