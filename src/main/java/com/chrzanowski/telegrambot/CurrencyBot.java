package com.chrzanowski.telegrambot;

import com.chrzanowski.telegrambot.banking.Bank;
import com.chrzanowski.telegrambot.banking.ExchangeRateService;
import com.chrzanowski.telegrambot.botcommands.GetRateCommand;
import com.chrzanowski.telegrambot.botcommands.MenuCommand;
import com.chrzanowski.telegrambot.botcommands.StartCommand;
import com.chrzanowski.telegrambot.data.currency.Currency;
import com.chrzanowski.telegrambot.data.currency.CurrencyService;
import com.chrzanowski.telegrambot.data.customer.Customer;
import com.chrzanowski.telegrambot.data.customer.CustomerService;
import com.chrzanowski.telegrambot.menu.Menu;
import com.chrzanowski.telegrambot.menu.MenuButtons;
import com.chrzanowski.telegrambot.notification.NotificationSchedulerService;
import com.chrzanowski.telegrambot.settings.BankButtons;
import com.chrzanowski.telegrambot.settings.CurrencyButtons;
import com.chrzanowski.telegrambot.settings.NotificationButtons;
import com.chrzanowski.telegrambot.settings.SettingsButtons;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class CurrencyBot extends TelegramLongPollingCommandBot {

    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;

    private final SendMessage sendMessage = new SendMessage();
    private final EditMessageText editMessageText = new EditMessageText();
    private CurrencyService currencyService;
    private CustomerService customerService;
    private MessageSource messageSource;

    private ExchangeRateService exchangeRateService;
    private NotificationSchedulerService notificationSchedulerService;

    Locale languageLocale = Locale.getDefault();

    private Customer customer;


    @Autowired
    public CurrencyBot(CustomerService customerService,
                       CurrencyService currencyService,
                       MessageSource messageSource,
                       NotificationSchedulerService notificationSchedulerService,
                       ExchangeRateService exchangeRateService) {
        register(new GetRateCommand());
        register(new MenuCommand());
        register(new StartCommand(customerService, notificationSchedulerService, messageSource));

        this.messageSource = messageSource;
        this.currencyService = currencyService;
        this.customerService = customerService;
        this.notificationSchedulerService = notificationSchedulerService;
        this.exchangeRateService = exchangeRateService;
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }


    @Override
    public void processNonCommandUpdate(Update update) {
        Long chatId = getChatId(update);
        sendMessage.setChatId(chatId);
        languageLocale = Locale.forLanguageTag(getLocale(update));

        if (customer == null) {
            customer = getCustomer(chatId);
        }


        if (update.hasCallbackQuery()) {
            String callBackQuery = update.getCallbackQuery().getData();
            System.out.println("Button pressed = " + callBackQuery);
            System.out.println("chatId = " + chatId);
            String[] menu = callBackQuery.split("_");
            switch (Menu.valueOf(menu[0])) {
                case START -> performStart(update);
                case GETRATE -> performGetRate();
                case SETTINGS -> performSettings(update);
                case BANK -> performBank(update, menu);
                case CURRENCY -> performCallBackCurrency(update, menu);
                case NOTIFICATION -> performNotificationCallBack(update, menu);

            }


        }

        if (update.hasMessage()) {
            String responseText = messageSource.getMessage("incorrect.message", null, languageLocale);
            sendMessage.setText(responseText);
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage(sendMessage);
        }

    }

    private void performNotificationCallBack(Update update, String[] menu) {
        Integer customerNotificationHour = customerService.getCustomerNotificationHour(customer);
        Locale languageLocale = Locale.forLanguageTag(getLocale(update));

        if (menu.length > 1) {
            Integer newHour = Integer.parseInt(menu[1]);
            if (newHour == -1) {
                newHour = null;
            }
            customerService.setCustomerNotificationHour(customer, newHour);
            try {
                notificationSchedulerService.updateScheduledJob(customer.getTelegramId(), newHour);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
            customerNotificationHour = customerService.getCustomerNotificationHour(customer);

        }
        editMessageText.setChatId(getChatId(update));
        editMessageText.setMessageId(getMessageId(update));
        editMessageText.setText(messageSource.getMessage("chosetime.message", null, languageLocale));
        editMessageText.setReplyMarkup(NotificationButtons.setButtons(customerNotificationHour, languageLocale));
        editMessageText.setParseMode(ParseMode.HTML);
        sendMessage(editMessageText);
    }

    private void performBank(Update update, String[] menu) {
        Bank customerBank = customerService.getCustomerBank(customer);
        if (menu.length > 1) {
            Bank bank = Bank.valueOf(menu[1]);
            if (customerBank != bank) {
                customerService.setCustomerBank(customer, bank);
            }
            customerBank = customerService.getCustomerBank(customer);
        }
        String textMessage = messageSource.getMessage("chosebank.message", null, languageLocale);
        editMessageText.setChatId(getChatId(update));
        editMessageText.setMessageId(getMessageId(update));
        editMessageText.setText(textMessage);
        editMessageText.setReplyMarkup(BankButtons.setButtons(customerBank, languageLocale));
        editMessageText.setParseMode(ParseMode.HTML);
        sendMessage(editMessageText);
    }

    private void performSettings(Update update) {
        sendMessage.setText(messageSource.getMessage("chosesettings.message", null, languageLocale));
        sendMessage.setReplyMarkup(SettingsButtons.setButtons(languageLocale));
        sendMessage(sendMessage);
    }

    private void performStart(Update update) {
        sendMessage.setText(messageSource.getMessage("choseaction.message", null, Locale.forLanguageTag(getLocale(update))));
        sendMessage.setReplyMarkup(MenuButtons.setButtons(languageLocale));
        sendMessage(sendMessage);
    }

    private void performGetRate() {
        sendMessage.setText(exchangeRateService.getMessageRate(customerService.getCustomerSettings(customer), languageLocale));
        sendMessage(sendMessage);
    }


    private void performCallBackCurrency(Update update, String[] menu) {
        List<Currency> currencyList = currencyService.getAllCurrencies();
        List<Currency> customerCurrencyList = customerService.getCustomerCurrency(customer);

        if (menu.length > 1) {
            Currency currency = currencyService.getCurrencyByIso4217Code(Integer.valueOf(menu[1]));

            if (!customerCurrencyList.contains(currency)) {
                customerService.addCurrency(currency, customer.getId());
            } else {
                customerService.removeCurrency(currency, customer.getId());
            }
            customerCurrencyList = customerService.getCustomerCurrency(customer);
        }

        editMessageText.setChatId(getChatId(update));
        editMessageText.setMessageId(getMessageId(update));
        editMessageText.setText(
                messageSource.getMessage(
                        "choseexchange.message",
                        null,
                        Locale.forLanguageTag(getLocale(update)
                        ))
        );
        editMessageText.setReplyMarkup(CurrencyButtons.setButtons(customerCurrencyList, currencyList, languageLocale));
        editMessageText.setParseMode(ParseMode.HTML);
        sendMessage(editMessageText);
    }

    private Customer getCustomer(Long telegramId) {
        return customerService.getCustomerByTelegramId(telegramId);

    }

    private Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }
        return null;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }


    private Integer getMessageId(Update update) {
        if (update.hasCallbackQuery()) {

            return update.getCallbackQuery().getMessage().getMessageId();
        }
        return null;
    }

    public void sendMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessage(EditMessageText message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private String getLocale(Update update) {
        String defaultLocale = "uk";
        if (update.hasCallbackQuery()) {
            return (getAppropriateLocaleCod(update.getCallbackQuery().getFrom().getLanguageCode()));
        }
        if (update.hasMessage()) {
            return (getAppropriateLocaleCod(update.getMessage().getFrom().getLanguageCode()));
        }
        return defaultLocale;
    }

    public static String getAppropriateLocaleCod(String code) {
        String defaultLocale = "uk";
        switch (code) {
            case "uk" -> {
                return "uk";
            }
            case "pl" -> {
                return "pl";
            }
            case "en" -> {
                return "en";
            }
            default -> {
                return defaultLocale;
            }
        }
    }

}
