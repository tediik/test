package com.jm.online_store.bot.Telegram.handler;

import com.jm.online_store.bot.Telegram.service.TelegramBotService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Обработчик команд для телеграм бота
 */
@Component
@AllArgsConstructor
public class TelegramBotCommandHandler {

    private final TelegramBotService telegramBotService;

    /**
     * Метод для обработки команды, поступившей к нашему боту
     *
     * @param update входящее обновление, содержит в себе команду, которую надо обработать,
     *               id чата из которого поступила команда и множество другой полезной информации
     * @return SendMessage с текстовым ответом на команду
     */
    public SendMessage handleCommand(Update update) {
        String command = update.getMessage().getText();
        String message;
        String orderNumber = "";
        String quantity = "";

        if (command.contains("/checkrepair")) {
            LinkedList<String> s = new LinkedList<>(Arrays.asList(update.getMessage().getText().split(" ")));
            orderNumber = s.getLast();
            command = "/checkrepair";
        } else if (command.contains("/getNews")) {
            LinkedList<String> q = new LinkedList<>(Arrays.asList(update.getMessage().getText().split(" ")));
            quantity = q.getLast();
            command = "/getNews";
        }

        switch (command) {
            case "/start": {
                message = telegramBotService.getHelloMessage();
                break;
            }
            case "/help": {
                message = telegramBotService.getHelpMessage();
                break;
            }
            case "/getstocks": {
                message = telegramBotService.getActualStocks();
                break;
            }
            case "/checkrepair": {
                message = telegramBotService.getRepairOrder(orderNumber);
                break;
            }
            case "/getNews": {
                message = telegramBotService.getSomeQuantityOfNews();
                break;
            }

            default: {
                message = telegramBotService.getDefaultMessage();
                break;
            }
        }

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text(message)
                .build();
    }
}
