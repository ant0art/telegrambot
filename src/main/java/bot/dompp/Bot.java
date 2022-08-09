package bot.dompp;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Bot extends TelegramLongPollingBot {

	private static final String BOT_TOKEN = "5595912289:AAEZ8vLLBtdQPX2phjsdEhiup_H5l6Ar-h8";
	private static final String BOT_NAME = "@dom_pp_bot";

	public static void main(String[] args) {
		System.out.println("Hello world!");
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
		return BOT_NAME;
	}

	@Override
	public String getBotToken() {
		return BOT_TOKEN;
	}
}
