package com.chrzanowski.telegrambot.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class MenuButtons {
    private static MessageSource messageSource;

    @Autowired
    public MenuButtons(MessageSource messageSource){
        MenuButtons.messageSource = messageSource;
    }
    public static InlineKeyboardMarkup setButtons(Locale locale) {
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();

        InlineKeyboardButton buttonGetRate = InlineKeyboardButton
                .builder()
                .text(messageSource.getMessage("exchancherate.buttontext", null, locale))
                .callbackData(Menu.GETRATE.name())
                .build();

        InlineKeyboardButton buttonSettings = InlineKeyboardButton
                .builder()
                .text(messageSource.getMessage("settings.buttontext", null, locale))
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
