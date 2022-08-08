package bot.dompp;

import java.util.Map;
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
	private static final Map<String, String> getenv = System.getenv();

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
		return getenv.get("BOT_NAME");
    }

    @Override
    public String getBotToken() {
        return getenv.get("BOT_TOKEN");
    }
}
