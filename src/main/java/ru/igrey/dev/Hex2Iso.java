package ru.igrey.dev;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.igrey.dev.service.Hex2IsoService;

/**
 * Created by sanasov on 01.04.2017.
 */
public class Hex2Iso extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(Hex2Iso.class);
    private Hex2IsoService hex2IsoService;

    public Hex2Iso(Hex2IsoService hex2IsoService) {
        this.hex2IsoService = hex2IsoService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.getText().equals("/start")){
                sendTextMessage(message.getChatId(), "Hi! You can send hex with or without header and received parsed iso message.");
                return;
            }
            String isoMessage = hex2IsoService.parse(message.getText());
            sendTextMessage(message.getChatId(), isoMessage);
        }
    }

    private void sendTextMessage(Long chatId, String responseMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId)
                .setText(responseMessage);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "hex2isoBot";
    }

    @Override
    public String getBotToken() {
        return "403047570:AAF62tRvXodwweGCfWTbQ8XsXAxXyZTFDP0";
    }

}

