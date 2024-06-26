package com.chrzanowski.telegrambot.settings;


import com.chrzanowski.telegrambot.banking.Bank;
import com.chrzanowski.telegrambot.menu.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Component
public class BankButtons {

    private static MessageSource messageSource;

    @Autowired
    public BankButtons(MessageSource messageSource){
        BankButtons.messageSource = messageSource;
    }

    private static final Set<Bank> selectedBanks = new HashSet<>();

    public static InlineKeyboardMarkup setButtons(Bank selectedBank, Locale locale) {
        List<List<InlineKeyboardButton>> keyboardList = new ArrayList<>();
        selectedBanks.clear();
        selectedBanks.add(selectedBank);

        int buttonCounts = 0;
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        for (Bank bank : Bank.values()) {
            InlineKeyboardButton button = createBankButton(bank);
            keyboardButtonsRow.add(button);
            buttonCounts++;

            if (buttonCounts == 2) {
                keyboardList.add(keyboardButtonsRow);
                keyboardButtonsRow = new ArrayList<>();
                buttonCounts = 0;
            }
        }
        if (!keyboardButtonsRow.isEmpty()) {
            keyboardList.add(keyboardButtonsRow);
        }
        keyboardList.add(previousMenu(locale));

        return InlineKeyboardMarkup
                .builder()
                .keyboard(keyboardList)
                .build();
    }

    private static List<InlineKeyboardButton> previousMenu(Locale locale) {
        return getInlineKeyboardButtons(locale);
    }

    static List<InlineKeyboardButton> getInlineKeyboardButtons(Locale locale) {
        List<InlineKeyboardButton> keyboardPagination = new ArrayList<>();

        InlineKeyboardButton buttonPrevMenu = InlineKeyboardButton
                .builder()
                .text(messageSource.getMessage("previousmenu.buttontext", null, locale))
                .callbackData(Menu.SETTINGS.name())
                .build();
        InlineKeyboardButton buttonMainMenu = InlineKeyboardButton
                .builder()
                .text(messageSource.getMessage("mainmenu.buttontext", null, locale))
                .callbackData(Menu.START.name())
                .build();
        keyboardPagination.add(buttonPrevMenu);
        keyboardPagination.add(buttonMainMenu);
        return keyboardPagination;
    }

    private static InlineKeyboardButton createBankButton(Bank bank) {
        String buttonText = bank.name();
        if (selectedBanks.contains(bank)) {
            buttonText += " ✅";
        }
        return InlineKeyboardButton
                .builder()
                .text(buttonText)
                .callbackData("BANK_" + bank.name())
                .build();
    }
}
