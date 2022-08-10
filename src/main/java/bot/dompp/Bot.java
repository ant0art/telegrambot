package bot.dompp;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

	String userName;
	String token;

	public Bot(String string, String string2) {
		this.userName = string;
		this.token = string2;
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
		return userName;
	}

	@Override
	public String getBotToken() {
		return token;
	}
}
