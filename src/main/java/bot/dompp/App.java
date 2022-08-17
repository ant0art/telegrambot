package bot.dompp;

import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;

public class App {
	public static void main(String[] args) {


		final Map<String, String> getenv = System.getenv();
		PropertyConfigurator.configure("log4j.properties");


		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(new Bot(getenv.get("BOT_NAME"), getenv.get("BOT_TOKEN")));
		} catch (TelegramApiException e) {
			e.printStackTrace();

		}
	}
}
