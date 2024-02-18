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
import com.chrzanowski.telegrambot.settings.BankButtons;
import com.chrzanowski.telegrambot.settings.CurrencyButtons;
import com.chrzanowski.telegrambot.settings.NotificationButtons;
import com.chrzanowski.telegrambot.settings.SettingsButtons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;


@Component
public class CurrencyBot extends TelegramLongPollingCommandBot {

    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;

    private final SendMessage sendMessage = new SendMessage();
    private final EditMessageText editMessageText = new EditMessageText();
    private final CurrencyService currencyService;
    private final CustomerService customerService;
    private Customer customer;


    @Autowired
    public CurrencyBot(CustomerService customerService, CurrencyService currencyService) {
        register(new GetRateCommand());
        register(new MenuCommand());
        register(new StartCommand(customerService));

        this.currencyService = currencyService;
        this.customerService = customerService;
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }


    @Override
    public void processNonCommandUpdate(Update update) {
        Long chatId = getChatId(update);

        sendMessage.setChatId(chatId);

        if (customer == null) {
            customer = getCustomer(chatId);
        }


        if (update.hasCallbackQuery()) {
            String callBackQuery = update.getCallbackQuery().getData();

            System.out.println("Button pressed = " + callBackQuery);
            System.out.println("chatId = " + chatId);
            String[] menu = callBackQuery.split("_");
            switch (Menu.valueOf(menu[0])) {
                case START -> performStart();
                case GETRATE -> performGetRate();
                case SETTINGS -> performSettings();
                case BANK -> performBank(update, menu);
                case CURRENCY -> performCallBackCurrency(update, menu);
                case NOTIFICATION -> performNotificationCallBack(update, menu);

            }


        }

        if (update.hasMessage()) {
            String responseText = "На жаль, ви ввели невірну команду, будь-ласка, оберіть іншу \uD83D\uDE0A";
            sendMessage.setText(responseText);
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage(sendMessage);
        }

    }

    private void performNotificationCallBack(Update update, String[] menu) {
        Integer customerNotificationHour = customerService.getCustomerNotificationHour(customer);

        if (menu.length>1){
            Integer newHour = Integer.parseInt(menu[1]);
            if (newHour == -1){
                newHour = null;
            }
            customerService.setCustomerNotificationHour(customer, newHour);
            customerNotificationHour = customerService.getCustomerNotificationHour(customer);

        }
        editMessageText.setChatId(getChatId(update));
        editMessageText.setMessageId(getMessageId(update));
        editMessageText.setText("Виберіть, час коли Ви бажаєте отримувати сповіщення");
        editMessageText.setReplyMarkup(NotificationButtons.setButtons(customerNotificationHour));
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

        editMessageText.setChatId(getChatId(update));
        editMessageText.setMessageId(getMessageId(update));
        editMessageText.setText("Виберіть банк з якого отримувати курс валют ☺️");
        editMessageText.setReplyMarkup(BankButtons.setButtons(customerBank));
        editMessageText.setParseMode(ParseMode.HTML);
        sendMessage(editMessageText);
    }

    private void performSettings() {
        sendMessage.setText("Виберіть налаштування які хочете змінити ☺️");
        sendMessage.setReplyMarkup(SettingsButtons.setButtons());
        sendMessage(sendMessage);
    }

    private void performStart() {
        sendMessage.setText("Ви знаходитесь на головному меню! Оберіть, будь-ласка, дію ☺️");
        sendMessage.setReplyMarkup(MenuButtons.setButtons());
        sendMessage(sendMessage);
    }

    private void performGetRate() {
        sendMessage.setText(new ExchangeRateService().getMessageRate(customerService.getCustomerSettings(customer)));
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
        editMessageText.setText("Виберіть валюти, курс яких Ви бажаєте отримувати");
        editMessageText.setReplyMarkup(CurrencyButtons.setButtons(customerCurrencyList, currencyList));
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

    private void sendMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendMessage(EditMessageText message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
