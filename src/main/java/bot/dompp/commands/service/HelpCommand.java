package bot.dompp.commands.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import bot.dompp.Utils;

public final class HelpCommand extends ServiceCommand {
	private Logger logger = LoggerFactory.getLogger(HelpCommand.class);
	private static ICommandRegistry mCommandRegistry;

	/**
	 * @param identifier - name of command
	 * @param description - what command do
	 */
	public HelpCommand(ICommandRegistry commandRegistry) {
		super("help", "Список доступных команд");
		mCommandRegistry = commandRegistry;
	}

	/**
	 * @return the mCommandRegistry
	 */
	public static ICommandRegistry getmCommandRegistry() {
		return mCommandRegistry;
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
		String userName = Utils.getUserName(user);

		StringBuilder helpMessageBuilder =
				new StringBuilder("Список доступных команд:").append("\n");
		for (IBotCommand cmd : mCommandRegistry.getRegisteredCommands()) {
			if(cmd instanceof StartCommand) continue;
			helpMessageBuilder.append(cmd.toString());
		}
		logger.debug(String.format("User: %s. Command starts: %s", userName,
				this.getCommandIdentifier()));
		sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
				helpMessageBuilder.toString());
		logger.debug(String.format("User %s. Command ends %s", userName,
				this.getCommandIdentifier()));
	}
}

