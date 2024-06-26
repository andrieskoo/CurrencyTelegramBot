package com.chrzanowski.telegrambot.settings;

import com.chrzanowski.telegrambot.menu.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class SettingsButtons {

    private static MessageSource messageSource;

    @Autowired
    public SettingsButtons(MessageSource messageSource){
        SettingsButtons.messageSource = messageSource;
    }
    public static InlineKeyboardMarkup setButtons(Locale locale) {
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();

        InlineKeyboardButton buttonBank = InlineKeyboardButton
                .builder()
                .text(messageSource.getMessage("bank.buttontext", null, locale))
                .callbackData(Menu.BANK.name())
                .build();

        InlineKeyboardButton buttonCurrency = InlineKeyboardButton
                .builder()
                .text(messageSource.getMessage("exchange.buttontext", null, locale))
                .callbackData(Menu.CURRENCY.name())
                .build();

        InlineKeyboardButton buttonNotification = InlineKeyboardButton
                .builder()
                .text(messageSource.getMessage("notification.buttontext", null, locale))
                .callbackData(Menu.NOTIFICATION.name())
                .build();

        InlineKeyboardButton buttonPrevMenu = InlineKeyboardButton
                .builder()
                .text(messageSource.getMessage("exchangerate.buttontext", null, locale))
                .callbackData(Menu.GETRATE.name())
                .build();
        InlineKeyboardButton buttonMainMenu = InlineKeyboardButton
                .builder()
                .text(messageSource.getMessage("mainmenu.buttontext", null, locale))
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
