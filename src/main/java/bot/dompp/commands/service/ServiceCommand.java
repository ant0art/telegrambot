package bot.dompp.commands.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class ServiceCommand extends BotCommand {
	private Logger logger = LoggerFactory.getLogger(ServiceCommand.class);


	ServiceCommand(String identifier, String description) {
		super(identifier, description);
	}


	@Override
	public String toString() {

		return "/" + this.getCommandIdentifier() + " - " + this.getDescription() + "\n";
	}

	/**
	 * Отправка ответа пользователю
	 */
	void sendAnswer(AbsSender absSender, Long chatId, String commandName, String userName,
			String text) {
		SendMessage message = new SendMessage();

		// включаем поддержку режима разметки
		message.enableMarkdown(true);
		message.setChatId(chatId.toString());
		message.setText(text);
		try {
			absSender.execute(message);
		} catch (TelegramApiException e) {
			logger.error(String.format("ERROR %s. Command: %s. User: %s", e.getMessage(),
					commandName, userName));
		}
	}
}
