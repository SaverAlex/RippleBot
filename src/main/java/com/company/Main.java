package com.company;

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;


import java.io.IOException;
import java.util.*;

class SimpleBot extends TelegramLongPollingBot {

    public static Map<Long, Object> dictionaryThread = new HashMap<>();
    public static Map<String, Object> dictionaryUsers = new HashMap<>();

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new SimpleBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        Analysis_Alert analysis = findingTheObject(update);
        System.out.println(message);
        CallbackQuery callBack = update.getCallbackQuery();
        if ((callBack!= null) && update.hasCallbackQuery()) {
            // Работа с окном настроек
            switch (callBack.getData()){
                case "Цена продажи":
                    analysis.buyPrice = false;
                    answerCallbackQuery(callBack.getId(),"Сохранено" );

                    break;
                case "Цена покупки":
                    analysis.buyPrice = true;
                    answerCallbackQuery(callBack.getId(),"Сохранено" );
                    break;
            }
        }
        if (analysis.inputValue){
            try {
                analysis.inputValue = false;
                analysis.value = Double.parseDouble(message.getText());
                analysis.stoper = true;
                analysis.setMessage(message);
                Thread myThready = new Thread(analysis); //Создание потока "myThready"
                dictionaryThread.put(message.getChat().getId(), myThready);
                myThready.start();
                String settings;
                if (analysis.buyPrice) settings = "покупки";
                else settings = "продажи";
                sendMsg(message, "Запускаю анализ с следующими настройками: \n"
                        + "- Ждём цену " + settings + "валюты"  + "\n"
                        +" - Ожидаемая цена " + analysis.value, 0);
            }
            catch (Exception e){
                System.out.println(e);
                sendMsg(message, "Введено неверное значение, попробуй ещё раз",0);
            }
        }
        if (message != null && message.hasText()) {
            // Работа с сообщениями
            switch (message.getText()) {
                case "Привет":
                    sendMsg(message,"Привет, "+message.getChat().getFirstName(),0);
                     break;
                case "Остановка Анализа":
                    Thread thread = (Thread) dictionaryThread.get(message.getChat().getId());
                    thread.stop();
                    dictionaryThread.remove(message.getChat().getId());
                    sendMsg(message,"Процесс успешно остановлен",0);
                    break;
                case "Курс XRP":
                    try {
                        Reader.startRead2();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sendMsg(message, "Разница: "+ Reader.sign +Reader.xrpDif+"%",0);
                    sendMsg(message, Reader.xrpBuyS,0);
                    sendMsg(message, Reader.xrpSellS,0);
                    break;
                case "Что ты умеешь ?":
                    sendMsg(message, "Привет, меня зовут КьюБи, и я пока мало что умею, но я активно учусь, на данный момент я могу:"+
                            "\n"+ "1) Показать курс Riple",0);
                    break;
                case "Запуск Анализа":
                    if (dictionaryThread.containsKey(message.getChat().getId())){
                        sendMsg(message,"Ошибка, прежде чем начать новый процесс анализа, остановите старый",0);
                    }
                    else {
                        analysis.inputValue = true;
                        sendMsg(message, "Введите ожидаемую цену: ",0);

                    }
                   break;
                case "Настройки":
                    sendMsg(message,"На какую цену мы будем ориентироваться:",1);
                    break;
            }
        }
    }

    public void sendMsg(Message message, String text, int type) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        setButtons(sendMessage);
        switch (type){
            case 1:
                sendMessage.setReplyMarkup(setInLine());
                break;
            case 2:
        }

        sendMessage.setChatId(message.getChatId().toString());
        //sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public synchronized void answerCallbackQuery(String callbackId, String message) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackId);
        answer.setText(message);
        answer.setShowAlert(true);

        try {
            answerCallbackQuery(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add("Курс XRP");
        keyboardFirstRow.add("Что ты умеешь ?");

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add("Запуск Анализа");
        keyboardSecondRow.add("Остановка Анализа");

        KeyboardRow keyboardThirdRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardThirdRow.add("Настройки");

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private Analysis_Alert findingTheObject(Update update){
        Analysis_Alert analysis;
        Message message = update.getMessage();
        String id = "";
        if (message == null) {
            if (update.getCallbackQuery().getFrom().getId() != null)
                id = String.valueOf(update.getCallbackQuery().getFrom().getId());
        }
        else id = String.valueOf(message.getChat().getId());
        if (dictionaryUsers.containsKey(id)){
            analysis = (Analysis_Alert) dictionaryUsers.get(id);
            // Проверяем есть ли у этого пользователя свой объект
        }
        else {
            analysis = new Analysis_Alert();
            // Для каждого пользователя заводим свой объект с анализом
            dictionaryUsers.put(id, analysis);
        }
        return analysis;
    }
    private InlineKeyboardMarkup setInLine() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText("Цена продажи").setCallbackData("Цена продажи"));
        rowInline2.add(new InlineKeyboardButton().setText("Цена покупки").setCallbackData("Цена покупки"));

        rowsInline.add(rowInline);
        rowsInline.add(rowInline2);

        markupInline.setKeyboard(rowsInline);
        return markupInline;
        //message.setReplyMarkup(markupInline);
    }
}
