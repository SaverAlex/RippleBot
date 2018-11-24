package com.company;

import java.io.IOException;

public class Analysis_Alert extends Thread {

    public static Double value; // сделать несколько кнопок -11, -8, -5, -3, -1, 1, 3, 5, 8, 11
    public static boolean stoper = true;
    public static int upDown = 0; // Ждать повышения или понижения // 0 - ждём повышения // 1 - ждём понижения

    // Принимать два значения upDown и value с помощью кнопок
    @Override
    public void run() {
        while (stoper) {
            alertAnalysis();
        }
    }

    private static void alertAnalysis() {
        try {
            Reader.startRead2();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String times = Reader.sign + String.valueOf(Reader.xrpDif);
        Double difference = Double.valueOf(times);
        System.out.println("||||||||||"+difference+"||"+value);
        switch (upDown){
            case 0: // Ждём повышения(Разница будет больше заданного значения)
                if (difference > value) {
                        stoper = false;
                }
                break;
            case 1: // Ждём понижения(Разница будет меньше заданного значения)
                if (difference < value){
                        stoper = false;
                }
                break;
        }


    }
}
