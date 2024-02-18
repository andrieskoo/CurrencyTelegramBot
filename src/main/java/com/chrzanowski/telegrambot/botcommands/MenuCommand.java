package com.chrzanowski.telegrambot.botcommands;

import com.chrzanowski.telegrambot.menu.MenuButtons;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MenuCommand extends BotCommand {
    public MenuCommand() {
        super("menu", "Main menu command");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        SendMessage menuMessage = new SendMessage();
        SendChatAction action = new SendChatAction();
        menuMessage.setText("Ви знаходитесь у головному меню! Оберіть, будь-ласка, дію ☺️");
        menuMessage.setChatId(chat.getId());
        menuMessage.setReplyMarkup(MenuButtons.setButtons());
        action.setChatId(chat.getId());
        action.setAction(ActionType.TYPING);

        try {
            absSender.execute(action);
            absSender.execute(menuMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
