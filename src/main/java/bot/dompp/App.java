package bot.dompp;

import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import bot.dompp.storage.EnvVars;

public class App {
	public static void main(String[] args) {

		PropertyConfigurator.configure("log4j.properties");

		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(new Bot(EnvVars.getVal("BOT_NAME"), EnvVars.getVal("BOT_TOKEN")));
		} catch (TelegramApiException e) {
			e.printStackTrace();

		}
	}
}
