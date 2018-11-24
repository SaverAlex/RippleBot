package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Reader {

    public static Elements xrpBuy;
    public static Elements xrpSell;
    public static String xrpBuyS;
    public static String xrpSellS;
    public static Double xrpDif;
    public static String sign;

    public static void startRead2 () throws IOException {

            Document document = Jsoup.connect("https://exmo.me/ru/trade").get();
            Elements elements = document.getElementsByAttributeValue("pair", "XRP_USD"); // Решить проблему меняющегося знака down or up (решено)
            for (Element el : elements){
                String xrpDifLocalVarible;
                String times = String.valueOf(el.select("div[class=chprice arrow_up_before]").indexOf("up"));
                int checkUp = times.indexOf("up");
                String times2 = String.valueOf(el.select("div[class=chprice arrow_down_before]"));
                int checkDown = times2.indexOf("down");
                if ((checkUp == -1) && (checkDown != -1)) {
                    xrpDifLocalVarible = ParserEL(el.select("div[class=chprice arrow_down_before]"));
                    xrpDifLocalVarible = xrpDifLocalVarible.trim();
                    sign = "-";
                    xrpDif = Double.valueOf(xrpDifLocalVarible.substring(0,xrpDifLocalVarible.length()-1));
                }
                else {
                    xrpDifLocalVarible = ParserEL(el.select("div[class=chprice arrow_up_before]"));
                    xrpDifLocalVarible = xrpDifLocalVarible.trim();
                    sign = "+";
                    xrpDif = Double.valueOf(xrpDifLocalVarible.substring(0,xrpDifLocalVarible.length()-1));
                }
            }
            for (Element element : elements) {
                xrpBuy = element.select("td[class=scol_3 buy_price]");
                xrpBuyS = "Покупка: " + ParserEL(xrpBuy);
                xrpSell = element.select("td[class=scol_4 sell_price]");
                xrpSellS = "Продажа: " + ParserEL(xrpSell);
            }

    }
    private static String ParserEL (Elements element){
        String result;
        int ind = element.toString().indexOf(">");
        result = element.toString().substring(ind);
        ind = result.indexOf("<");
        result = result.substring(1,ind);
        return result;
    }
 }
