package com.chrzanowski.telegrambot.settings;


import com.chrzanowski.telegrambot.banking.Bank;
import com.chrzanowski.telegrambot.menu.Menu;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.*;

public class BankButtons {

    private static final Set<Bank> selectedBanks = new HashSet<>();

    public static InlineKeyboardMarkup setButtons(Bank selectedBank) {
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
        if (!keyboardButtonsRow.isEmpty()){
            keyboardList.add(keyboardButtonsRow);
        }
        keyboardList.add(previousMenu());

        return InlineKeyboardMarkup
                .builder()
                .keyboard(keyboardList)
                .build();
    }

    private static List<InlineKeyboardButton> previousMenu() {
        return getInlineKeyboardButtons();
    }

    static List<InlineKeyboardButton> getInlineKeyboardButtons() {
        List<InlineKeyboardButton> keyboardPagination = new ArrayList<>();
        InlineKeyboardButton buttonPrevMenu = InlineKeyboardButton
                .builder()
                .text("Попереднє меню ↪\uFE0F")
                .callbackData(Menu.SETTINGS.name())
                .build();
        InlineKeyboardButton buttonMainMenu = InlineKeyboardButton
                .builder()
                .text("Головне меню \uD83C\uDFE0")
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
