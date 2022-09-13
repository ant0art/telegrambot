package bot.dompp.commands.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import bot.dompp.commands.BaseCommand;
import bot.dompp.commands.BotCommandsConfig;

public final class HelpCommand extends BaseCommand {
	private Logger logger = LoggerFactory.getLogger(HelpCommand.class);

	@Override
	public void runCommand(AbsSender absSender, Message message) {
		List<BotCommand> list = BotCommandsConfig.getDefaultCommands().getCommands();
		
		StringBuilder helpMessageBuilder = new StringBuilder("*Список доступных команд:*").append("\n");
		for (BotCommand botCommand : list) {
			helpMessageBuilder.append("/").append(botCommand.getCommand()).append(" \\- _")
					.append(botCommand.getDescription().toLowerCase()).append("_\n");
		}
		
		sendMessage(absSender, message, helpMessageBuilder.toString());
	}
}

