package com.company;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;

public class Analysis_Alert implements Runnable {

    public Double value; // сделать несколько кнопок -11, -8, -5, -3, -1, 1, 3, 5, 8, 11
    public boolean stoper = true;
    //public int upDown = 0; // Ждать повышения или понижения // 0 - ждём повышения // 1 - ждём понижения
    public boolean buyPrice = true; // Продажа или Покупка
    public boolean inputValue = false;
    private boolean upDown;
    private Message message;

    @Override
    public void run() {
        try {
            Reader.startRead2();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Double currentPrice;
        if (buyPrice)
            currentPrice = Double.valueOf(Reader.ParserEL(Reader.xrpBuy));
        else
            currentPrice = Double.valueOf(Reader.ParserEL(Reader.xrpSell));
        if (currentPrice > value){
            upDown = true;
        }
        else {
            upDown = false;
        }
        while (stoper) {
            alertAnalysis(currentPrice);
            System.out.println(Reader.xrpBuyS);
        }
        SimpleBot x = new SimpleBot();
        x.sendMsg(message, "Разница: "+ Reader.sign +Reader.xrpDif+"%",0);
        x.sendMsg(message, Reader.xrpBuyS,0);
        x.sendMsg(message, Reader.xrpSellS,0);
    }

    private void alertAnalysis(Double currentPrice) {
        try {
            Reader.startRead2();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (upDown){
            if (currentPrice <= value) stoper = false;
        }
        else if (currentPrice >= value) stoper = false;

    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
