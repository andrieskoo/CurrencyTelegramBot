package com.chrzanowski.telegrambot.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.chrzanowski.telegrambot.settings.BankButtons.getInlineKeyboardButtons;

@Component
public class NotificationButtons {

    private static MessageSource messageSource;

    @Autowired
    public NotificationButtons(MessageSource messageSource){
        NotificationButtons.messageSource = messageSource;
    }
    public static InlineKeyboardMarkup setButtons(Integer customerNotification, Locale locale) {
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

        keyboardList.add(disableNotificationButtons(
                customerNotification,
                messageSource.getMessage("diasablenotification.buttontext", null, locale)
        ));
        keyboardList.add(previousMenu(locale));

        return InlineKeyboardMarkup
                .builder()
                .keyboard(keyboardList)
                .build();
    }

    private static List<InlineKeyboardButton> disableNotificationButtons(Integer customerNotification, String textDisable) {
        String buttonText = textDisable;

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
    private static List<InlineKeyboardButton> previousMenu(Locale locale) {
        return getInlineKeyboardButtons(locale);
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
