package bot.dompp.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import bot.dompp.commands.BaseCommand;
import bot.dompp.commands.admin.DeleteCommand;
import bot.dompp.commands.service.HelpCommand;
import bot.dompp.commands.service.SearchCommand;
import bot.dompp.commands.service.StartCommand;
import bot.dompp.commands.service.UnknownCommand;

public class CommandHandler extends BaseHandler {
	private AbsSender absSender;
	private Message message;
	private Logger logger = LoggerFactory.getLogger(CommandHandler.class);


	/**
	 * 
	 */
	public CommandHandler(AbsSender absSender, Message message) {
		this.absSender = absSender;
		this.message = message;
	}

	@Override
	public void run() {
		BaseCommand command = getCommandHandler(message);
		command.runCommand(absSender, message);
	}

	public BaseCommand getCommandHandler(Message message) {
		CommandMessage commandMessage = new CommandMessage(message);

		logger.info(String.format("Command Message is %n%n %s %n ",
				commandMessage.getCommandMessage()));
		// logger.info(String.format("Command Text is %n%n %s %n ",
		// commandMessage.getCommandText()));
		logger.info(String.format("Command Message Text is %n%n %s %n ",
				commandMessage.getMessageText()));
		String comText = commandMessage.getMessageText();
		comText = getCommandWithoutBotName(comText);
		logger.info(String.format("ComText is %s", comText));

		switch (comText) {
			case "/start":
				return new StartCommand();
			case "/delete":
				return new DeleteCommand();
			case "/search":
				return new SearchCommand();
			case "/help":
				return new HelpCommand();
			default:
				// для случаев незарегистрированных команд
				return new UnknownCommand();
		}
	}

	private static String getCommandWithoutBotName(String command) {
		return command.split("@")[0].trim();
	}

}
