package com.chrzanowski.telegrambot.banking.nbu;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class NBUCurrency {
    private int r030;
    private String txt;
    private double rate;
    private String cc;
    @SerializedName("exchangedate")
    private String exchangeDate;

    @Override
    public String toString() {
        return "NBUCurrency{" +
                "r030=" + r030 +
                ", txt='" + txt + '\'' +
                ", rate=" + rate +
                ", cc=" + cc +
                ", exchangeDate='" + exchangeDate + '\'' +
                '}';
    }
}
