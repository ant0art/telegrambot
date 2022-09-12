package bot.dompp.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import bot.dompp.Utils;

public class UnknownCommand extends BaseCommand {
	private Logger logger = LoggerFactory.getLogger(UnknownCommand.class);

	public UnknownCommand() {/* Needed to init command method */}

	@Override
	public void runCommand(AbsSender absSender, Message message) {
		logger.info(String.format("Command %s starts", this.getClass().getName()));
		String userName = Utils.getUserName(message);
		SendMessage mess = SendMessage.builder().parseMode("MarkdownV2").chatId(message.getChatId())
				.text(String.format(
						"@%s, я подобной команды не знаю\\. Попробуй найти подходящую здесь /help",
						userName))
				.build();

		try {
			absSender.execute(mess);
			logger.info(String.format("Command %s ends", this.getClass().getName()));
		} catch (TelegramApiException e) {
			logger.error(String.format("ERROR in command: %s. User: %s", this.getClass().getName(),
					userName));
			e.printStackTrace();
		}
	}



}
