package com.chrzanowski.telegrambot.banking.privatbank;

import com.google.gson.annotations.SerializedName;

public class PrivatBankCurrency {
    @SerializedName("saleRateNB")
    private double saleRateNB;

    @SerializedName("purchaseRateNB")
    private double purchaseRateNB;

    @SerializedName("saleRate")
    private double saleRate;

    @SerializedName("purchaseRate")
    private double purchaseRate;

    @SerializedName("baseCurrency")
    private String baseCurrency;

    @SerializedName("currency")
    private String currency;


    public PrivatBankCurrency(String baseCurrency, String currency, double saleRate, double purchaseRate) {
        this.baseCurrency = baseCurrency;
        this.currency = currency;
        this.saleRate = saleRate;
        this.purchaseRate = purchaseRate;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getCurrency() {
        return currency;
    }

    public double getSaleRate() {
        return saleRate;
    }

    public double getPurchaseRate() {
        return purchaseRate;
    }


    @Override
    public String toString() {
        return "Currency{" +
                "baseCurrency='" + baseCurrency + '\'' +
                ", currency='" + currency + '\'' +
                ", saleRate=" + saleRate +
                ", purchaseRate=" + purchaseRate +
                '}';
    }
}
