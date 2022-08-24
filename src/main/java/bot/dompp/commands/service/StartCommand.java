package bot.dompp.commands.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import bot.dompp.Utils;

public class StartCommand extends ServiceCommand {
	private Logger logger = LoggerFactory.getLogger(StartCommand.class);

	/**
	 * @param identifier - name of command
	 * @param description - what command do
	 */
	public StartCommand(String identifier, String description) {
		super(identifier, description);
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
		String userName = Utils.getUserName(user);

		StringBuilder response = new StringBuilder(String.format(
				"Приветствую тебя, @%s\\!%nЯ \\- бот сообщества [ЖК\"Полюстрово Парк\"](https://t.me/joinchat/D0gm5A0nY3WtypCH-L3Ziw)\\.%nЯ нахожусь в процессе обучения\\.%n%n",
				userName)).append("Вот список доступных мне команд\n");
		for (IBotCommand cmd : HelpCommand.getmCommandRegistry().getRegisteredCommands()) {
			if (cmd instanceof StartCommand)
				continue;
			response.append(cmd.toString());
		}

		logger.debug(String.format("User: %s. Command starts: %s", userName,
				this.getCommandIdentifier()));
		sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
				response.toString());
		logger.debug(
				String.format("User %s. Command ends %s", userName, this.getCommandIdentifier()));
	}
}
