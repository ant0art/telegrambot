package bot.dompp;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class App {
	public static void main(String[] args) {
        Bot telegramBot = new Bot("dom_pp_bot", "5595912289:AAEZ8vLLBtdQPX2phjsdEhiup_H5l6Ar-h8");

		
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(telegramBot);

		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
    }
}
