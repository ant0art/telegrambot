package bot.dompp.commands.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import bot.dompp.Utils;
import bot.dompp.commands.BaseCommand;
import bot.dompp.commands.BotCommandsConfig;

public class StartCommand extends BaseCommand {
	private Logger logger = LoggerFactory.getLogger(StartCommand.class);

	@Override
	public void runCommand(AbsSender absSender, Message message) {
		String userName = Utils.getUserName(message.getFrom());
		
		StringBuilder response = new StringBuilder(String.format(
				"Приветствую тебя, @%s\\!%nЯ \\- бот сообщества [ЖК\"Полюстрово Парк\"](https://t.me/joinchat/D0gm5A0nY3WtypCH-L3Ziw)\\.%nЯ нахожусь в процессе обучения\\.%n%n",
				userName)).append("*Вот список доступных мне команд:*\n");

		List<BotCommand> list = BotCommandsConfig.getDefaultCommands().getCommands();

		for (BotCommand botCommand : list) {
			response.append("/").append(botCommand.getCommand()).append(" \\- _")
					.append(botCommand.getDescription().toLowerCase()).append("_\n");
		}

		sendMessage(absSender, message, response.toString());
	}
}
