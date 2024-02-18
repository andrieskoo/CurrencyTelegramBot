package com.chrzanowski.telegrambot.settings;

import com.chrzanowski.telegrambot.menu.Menu;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class SettingsButtons {
    public static InlineKeyboardMarkup setButtons() {
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();

        InlineKeyboardButton buttonBank = InlineKeyboardButton
                .builder()
                .text("Банк \uD83C\uDFDB")
                .callbackData(Menu.BANK.name())
                .build();

        InlineKeyboardButton buttonCurrency = InlineKeyboardButton
                .builder()
                .text("Валюта \uD83D\uDCB5\uD83D\uDCB6")
                .callbackData(Menu.CURRENCY.name())
                .build();

        InlineKeyboardButton buttonNotification = InlineKeyboardButton
                .builder()
                .text("Сповіщення ⏰")
                .callbackData(Menu.NOTIFICATION.name())
                .build();

        InlineKeyboardButton buttonPrevMenu = InlineKeyboardButton
                .builder()
                .text("Курс валют \uD83C\uDFDB")
                .callbackData(Menu.GETRATE.name())
                .build();
        InlineKeyboardButton buttonMainMenu = InlineKeyboardButton
                .builder()
                .text("Головне меню \uD83C\uDFE0")
                .callbackData(Menu.START.name())
                .build();

        keyboardButtonsRow1.add(buttonBank);
        keyboardButtonsRow1.add(buttonCurrency);
        keyboardButtonsRow2.add(buttonNotification);
        keyboardButtonsRow2.add(buttonPrevMenu);
        keyboardButtonsRow3.add(buttonMainMenu);
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(keyboardButtonsRow1)
                .keyboardRow(keyboardButtonsRow2)
                .keyboardRow(keyboardButtonsRow3)
                .build();
    }
}
