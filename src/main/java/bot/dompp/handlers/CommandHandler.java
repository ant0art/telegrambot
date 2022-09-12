package bot.dompp.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import bot.dompp.commands.BaseCommand;
import bot.dompp.commands.DeleteCommand;
import bot.dompp.commands.UnknownCommand;

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
		BaseCommand command;
		CommandMessage commandMessage = new CommandMessage(message);

		logger.info(String.format("Command Message is %n%n %s %n ",
				commandMessage.getCommandMessage()));
		logger.info(String.format("Command Text is %n%n %s %n ", commandMessage.getCommandText()));
		logger.info(String.format("Command Message Text is %n%n %s %n ",
				commandMessage.getMessageText()));
		String comText = commandMessage.getCommandText();
		getCommandWithoutBotName(comText);
		switch (comText) {
			case "delete":
				command = new DeleteCommand();
				break;
			default:
				// для случаев незарегистрированных команд
				command = new UnknownCommand();
		}
		return command;
	}

	private static String getCommandWithoutBotName(String command) {
		return command.split("@")[0].trim();
	}

}
