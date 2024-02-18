package com.chrzanowski.telegrambot.settings;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.chrzanowski.telegrambot.settings.BankButtons.getInlineKeyboardButtons;

public class NotificationButtons {

    public static InlineKeyboardMarkup setButtons(Integer customerNotification) {
        List<List<InlineKeyboardButton>> keyboardList = new ArrayList<>();

        int buttonCounts = 0;
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        for (int i=6; i<=21;i++) {
            InlineKeyboardButton button = createHourButton(customerNotification, i);
            keyboardButtonsRow.add(button);
            buttonCounts++;

            if (buttonCounts == 4) {
                keyboardList.add(keyboardButtonsRow);
                keyboardButtonsRow = new ArrayList<>();
                buttonCounts = 0;
            }
        }
        if (!keyboardButtonsRow.isEmpty()){
            keyboardList.add(keyboardButtonsRow);
        }

        keyboardList.add(disableNotificationButtons(customerNotification));
        keyboardList.add(previousMenu());

        return InlineKeyboardMarkup
                .builder()
                .keyboard(keyboardList)
                .build();
    }

    private static List<InlineKeyboardButton> disableNotificationButtons(Integer customerNotification) {
        String buttonText = "Вимкнути сповіщення";

        if (customerNotification == null) {
            buttonText += " ✅";
        }
        List<InlineKeyboardButton> keyboardPagination = new ArrayList<>();
        InlineKeyboardButton buttonDisableNotification = InlineKeyboardButton
                .builder()
                .text(buttonText)
                .callbackData("NOTIFICATION_-1")
                .build();
        keyboardPagination.add(buttonDisableNotification);
        return keyboardPagination;
    }
    private static List<InlineKeyboardButton> previousMenu() {
        return getInlineKeyboardButtons();
    }

    private static InlineKeyboardButton createHourButton(Integer customerNotification, int hour) {

        String buttonText = hour+":00";

        if (customerNotification != null && customerNotification == hour) {
            buttonText += " ✅";
        }

        return InlineKeyboardButton
                .builder()
                .text(buttonText)
                .callbackData("NOTIFICATION_" + hour)
                .build();
    }

}
