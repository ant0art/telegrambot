package bot.dompp.commands;

import java.util.Arrays;
import java.util.List;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllChatAdministrators;

public class BotCommandsConfig {
	private static final String BOT_NAME_DELIMITER = "@";

	public static final BotCommandsConfig INSTANCE = new BotCommandsConfig();

	private static SetMyCommands defaultCommands;
	private static SetMyCommands adminCommands;

	private BotCommandsConfig() {
		
		/* Set default commands */
		defaultCommands = new SetMyCommands();
		List<BotCommand> defaultCommandsList =
				Arrays.asList(createCommand("help", "Список доступных команд"),
						createCommand("search", "Список ключевых слов"));
		defaultCommands.setCommands(defaultCommandsList);

		/* Set admin commands */
		SetMyCommands.SetMyCommandsBuilder adminCommands =
				SetMyCommands.builder().scope(new BotCommandScopeAllChatAdministrators());
		List<BotCommand> adminCommandsList =
				Arrays.asList(createCommand("help", "Список доступных команд"),
						createCommand("search", "Список ключевых слов"),
						createCommand("delete", "Удалить сообщение"));

		adminCommands.commands(adminCommandsList).build();
	}

	public static SetMyCommands getDefaultCommands() {
		return defaultCommands;
	}

	public static SetMyCommands getAdminCommands() {
		return adminCommands;
	}

	public static String getCommandWithoutBotName(String command) {
		return command.split(BOT_NAME_DELIMITER)[0].trim();
	}

	private static BotCommand createCommand(String commandName, String description) {
		return BotCommand.builder().command(commandName).description(description).build();
	}
}
