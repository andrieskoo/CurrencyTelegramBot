package com.chrzanowski.telegrambot.banking.nbp;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class NBPCurrency {

    @SerializedName("currency")
    private String currencyName;

    @Getter
    @SerializedName("code")
    private String currencyCode;

    @Getter
    @SerializedName("mid")
    private double rate;

}
