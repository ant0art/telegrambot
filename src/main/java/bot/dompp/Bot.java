package bot.dompp;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Hello world!
 *
 */
public class Bot extends TelegramLongPollingBot {

    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new Bot());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message incomMess = update.getMessage();
        String incomMessChatId = incomMess.getChatId().toString();
        String outMeString = incomMess.getText();
        SendMessage outMess = new SendMessage();

        outMess.setChatId(incomMessChatId);
        outMess.setText(outMeString);
        try {
            execute(outMess);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return Properties.BOT_NAME.getValue();
    }

    @Override
    public String getBotToken() {
        return Properties.BOT_TOKEN.getValue();
    }
}
