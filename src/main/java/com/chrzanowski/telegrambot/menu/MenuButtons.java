package com.chrzanowski.telegrambot.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class MenuButtons {
    public static InlineKeyboardMarkup setButtons() {
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();

        InlineKeyboardButton buttonGetRate = InlineKeyboardButton
                .builder()
                .text("Курс валют \uD83C\uDFDB")
                .callbackData(Menu.GETRATE.name())
                .build();

        InlineKeyboardButton buttonSettings = InlineKeyboardButton
                .builder()
                .text("Налаштування")
                .callbackData(Menu.SETTINGS.name())
                .build();
        keyboardButtonsRow1.add(buttonGetRate);
        keyboardButtonsRow1.add(buttonSettings);
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(keyboardButtonsRow1)
                .build();
    }
}
