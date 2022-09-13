package bot.dompp.commands.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import bot.dompp.Utils;
import bot.dompp.commands.BaseCommand;

public class UnknownCommand extends BaseCommand {
	private Logger logger = LoggerFactory.getLogger(UnknownCommand.class);

	public UnknownCommand() {/* Needed to init command method */}

	@Override
	public void runCommand(AbsSender absSender, Message message) {
		logger.info(String.format("Command %s starts", this.getClass().getName()));
		String userName = Utils.getUserName(message);
		String response = String.format("@%s, я подобной команды не знаю\\. Попробуй найти подходящую здесь /help", userName);

		sendMessage(absSender, message, response);
	}



}
