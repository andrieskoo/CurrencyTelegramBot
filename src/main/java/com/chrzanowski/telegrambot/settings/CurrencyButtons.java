package com.chrzanowski.telegrambot.settings;

import com.chrzanowski.telegrambot.data.currency.Currency;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

import static com.chrzanowski.telegrambot.settings.BankButtons.getInlineKeyboardButtons;


public class CurrencyButtons {
    private static final Set<Currency> selectedCurrency = new HashSet<>();

    public static InlineKeyboardMarkup setButtons(List<Currency> customerCurrencyList, List<Currency> currencyList, Locale locale) {
        List<List<InlineKeyboardButton>> keyboardList = new ArrayList<>();
        selectedCurrency.clear();
        selectedCurrency.addAll(customerCurrencyList);

        int buttonCounts = 0;
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        for (Currency currency : currencyList) {
            InlineKeyboardButton button = createCurrencyButton(currency);
            keyboardButtonsRow.add(button);
            buttonCounts++;

            if (buttonCounts == 3) {
                keyboardList.add(keyboardButtonsRow);
                keyboardButtonsRow = new ArrayList<>();
                buttonCounts = 0;
            }
        }
        if (!keyboardButtonsRow.isEmpty()){
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

    private static InlineKeyboardButton createCurrencyButton(Currency currency) {
        String buttonText = currency.getName();

        if (selectedCurrency.contains(currency)) {
            buttonText += " ✅";
        }

        return InlineKeyboardButton
                .builder()
                .text(buttonText)
                .callbackData("CURRENCY_" + currency.getIso4217Code())
                .build();
    }

}
