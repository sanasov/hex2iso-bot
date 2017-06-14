package ru.igrey.dev;


import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by sanasov on 01.04.2017.
 */
public class Hex2IsoStart {

    public static void main(String[] args) {

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(createHex2IsoBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static Hex2Iso createHex2IsoBot() {
        return new Hex2Iso(
                new MoasHex2IsoService()
        );
    }

}
