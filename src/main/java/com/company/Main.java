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
import java.util.ArrayList;
import java.util.List;

class SimpleBot extends TelegramLongPollingBot {

    Analysis_Alert analysis = new Analysis_Alert();
    // Не работает на двух пользователей (ПОФИКСИТЬ)


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
        CallbackQuery callBack = update.getCallbackQuery();
        if ((callBack!= null) && update.hasCallbackQuery()) {
            switch (callBack.getData()){
                case "Ждём понижения":
                    answerCallbackQuery(callBack.getId(),"До какого момента ждём понижения ?" );
                    Analysis_Alert.upDown = 1;
                    break;
                case "Ждём повышения":
                    answerCallbackQuery(callBack.getId(),"До какого момента ждём повышения ?" );
                    Analysis_Alert.upDown = 0;
                    break;
            }
        }
        System.out.println(message);
        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "Остановка Анализа":
                    SendMessage sendMessage = new SendMessage();
                    // тут должен убиваться поток analysis
                    // P.S я не знаю как это делать
                    break;
                case "Курс XRP":
                    try {
                        Reader.startRead2();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sendMsg(message, "Разница: "+ Reader.sign +String.valueOf(Reader.xrpDif)+"%",0);
                    sendMsg(message, Reader.xrpBuyS,0);
                    sendMsg(message, Reader.xrpSellS,0);

                    break;
                case "Что ты умеешь ?":
                    sendMsg(message, "Привет, меня зовут КьюБи, и я пока мало что умею, но я активно учусь, на данный момент я могу:"+
                            "\n"+ "1) Показать курс Riple",0);
                    break;
                case "Запуск Анализа":
                    sendMsg(message, "Запускаю анализ ...",1);
                   break;
                default:
                    try {
                        Analysis_Alert.value = Double.parseDouble(message.getText());
                        System.out.println("NoExeption");
                        if (Analysis_Alert.upDown == 0 ){
                            sendMsg(message,"Ждём рост до " + Analysis_Alert.value ,0);
                        }
                        else {
                            sendMsg(message,"Ждём спад до " + Analysis_Alert.value,0);
                        }
                        sendMsg(message,"Веду анализ",0);
                        analysis.run();
                        Analysis_Alert.stoper = true;
                        sendMsg(message, "Разница: "+ Reader.sign +String.valueOf(Reader.xrpDif)+"%",0);
                        sendMsg(message, Reader.xrpBuyS,0);
                        sendMsg(message, Reader.xrpSellS,0);
                    }
                    catch (Exception e){
                        System.out.println(e);
                        sendMsg(message, "Введено неверное значение, попробуй ещё раз",0);
                    }
                    break;
            }
        }
        else {
            System.out.println("Error");
        }
    }

    private void sendMsg(Message message, String text, int type) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        setButtons(sendMessage);
        switch (type){
            case 1:
                sendMessage.setReplyMarkup(setInLine2());
                break;
            case 2:
                sendMessage.setReplyMarkup(setInLine3());
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

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
    }
    private void setInline() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        buttons1.add(new InlineKeyboardButton().setText("Кнопка").setCallbackData("Курс XRP"));
        buttons.add(buttons1);

        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);
    }
    private InlineKeyboardMarkup setInLine2() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText("Ждём повышения").setCallbackData("Ждём повышения"));
        rowInline2.add(new InlineKeyboardButton().setText("Ждём понижения").setCallbackData("Ждём понижения"));

        rowsInline.add(rowInline);
        rowsInline.add(rowInline2);

        markupInline.setKeyboard(rowsInline);
        return markupInline;
        //message.setReplyMarkup(markupInline);
    }
    private InlineKeyboardMarkup setInLine3() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline5 = new ArrayList<>();


        rowInline.add(new InlineKeyboardButton().setText("-11").setCallbackData("-11"));
        rowInline.add(new InlineKeyboardButton().setText("+11").setCallbackData("+11"));
        rowInline2.add(new InlineKeyboardButton().setText("-8").setCallbackData("-8"));
        rowInline2.add(new InlineKeyboardButton().setText("+8").setCallbackData("+8"));
        rowInline3.add(new InlineKeyboardButton().setText("-5").setCallbackData("-5"));
        rowInline3.add(new InlineKeyboardButton().setText("+5").setCallbackData("+5"));
        rowInline4.add(new InlineKeyboardButton().setText("-3").setCallbackData("-3"));
        rowInline4.add(new InlineKeyboardButton().setText("+3").setCallbackData("+3"));
        rowInline5.add(new InlineKeyboardButton().setText("-1").setCallbackData("-1"));
        rowInline5.add(new InlineKeyboardButton().setText("+1").setCallbackData("+1"));

        rowsInline.add(rowInline);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);
        rowsInline.add(rowInline4);
        rowsInline.add(rowInline5);

        markupInline.setKeyboard(rowsInline);
        return markupInline;
        //message.setReplyMarkup(markupInline);
    }
}
