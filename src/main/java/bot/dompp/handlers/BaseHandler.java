package bot.dompp.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class BaseHandler {

	protected static final String BASE_HANDLER = "base_handler";

	public void run() {}

	public void makeAnser(AbsSender absSender) {
		SendMessage message = new SendMessage();
		try {
			absSender.execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

}
