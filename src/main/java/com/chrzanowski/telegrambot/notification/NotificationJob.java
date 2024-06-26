package com.chrzanowski.telegrambot.notification;

import com.chrzanowski.telegrambot.CurrencyBot;
import com.chrzanowski.telegrambot.banking.ExchangeRateService;
import com.chrzanowski.telegrambot.data.customer.Customer;
import com.chrzanowski.telegrambot.data.customer.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;

@Slf4j
@Component
public class NotificationJob implements Job {
    @Autowired
    ExchangeRateService exchangeRateService;

    @Autowired
    CurrencyBot currencyBot;

    @Autowired
    CustomerService customerService;

    private final SendMessage sendMessage = new SendMessage();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        String chatId = (String) jobExecutionContext.getJobDetail().getJobDataMap().get("chatId");
        log.info("Executing job for chat id = {}", chatId);
        Customer customer = customerService.getCustomerByTelegramId(Long.valueOf(chatId));
        sendMessage.setChatId(chatId);
        Locale locale = Locale.forLanguageTag(customer.getLanguageCode());
        sendMessage.setText(exchangeRateService.getMessageRate(customer.getCustomerSettingsId(), locale));

        try {
            currencyBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }
}
