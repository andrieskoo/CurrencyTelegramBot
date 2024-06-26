package com.chrzanowski.telegrambot.botcommands;


import com.chrzanowski.telegrambot.data.customer.CustomerService;
import com.chrzanowski.telegrambot.data.customer.Customer;
import com.chrzanowski.telegrambot.menu.MenuButtons;
import com.chrzanowski.telegrambot.notification.NotificationSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;

import static com.chrzanowski.telegrambot.CurrencyBot.getAppropriateLocaleCod;

@Slf4j
@Component
public class StartCommand extends BotCommand  {

    private final CustomerService customerService;

    private final NotificationSchedulerService notificationSchedulerService;

    private final MessageSource messageSource;

    @Autowired
    public StartCommand(CustomerService customerService, NotificationSchedulerService notificationSchedulerService, MessageSource messageSource) {
        super("start", "Start command");
        this.customerService = customerService;
        this.notificationSchedulerService = notificationSchedulerService;
        this.messageSource = messageSource;

    }


    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
//        System.out.println("chatId = " + chat.getId());
//        System.out.println("user = " + user);

        Customer customer = new Customer();
        customer.setName(user.getFirstName());
        customer.setTelegramId(user.getId());
        customer.setIsBot(user.getIsBot());
        customer.setLastname(user.getLastName());
        customer.setLanguageCode(user.getLanguageCode());
        customer.setIsPremium(user.getIsPremium());

        customerService.saveCustomer(customer);

        Locale locale = Locale.forLanguageTag(getAppropriateLocaleCod(user.getLanguageCode()));

        try {
            notificationSchedulerService.init();
        } catch (SchedulerException e) {
            log.error("Can't start notification scheduler {}", e.getMessage() );
            throw new RuntimeException(e);
        }

        SendMessage message = new SendMessage();
        message.setText(messageSource.getMessage("greeting.message", null, locale));
        message.setChatId(chat.getId());
        message.setReplyMarkup(MenuButtons.setButtons(locale));
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
