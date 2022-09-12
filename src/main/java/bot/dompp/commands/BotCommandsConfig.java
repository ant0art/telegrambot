package bot.dompp.commands;

import java.util.Arrays;
import java.util.List;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllChatAdministrators;

public class BotCommandsConfig {	private static final String BOT_NAME_DELIMITER = "@";

	// private SetMyCommands defaultCommands;
	// private SetMyCommands adminCommands;

	private BotCommandsConfig() {}

	public static SetMyCommands getDefaultCommands() {
		SetMyCommands setDefaultCommands = new SetMyCommands();
		List<BotCommand> defaultCommandsList =
				Arrays.asList(createCommand("search", "Список ключевых слов"),
						createCommand("help", "Список доступных команд"));
		setDefaultCommands.setCommands(defaultCommandsList);
		return setDefaultCommands;
	}


	// public final void setDefaultCommands(SetMyCommands defaultCommands) {
	// 	this.defaultCommands = defaultCommands;
	// }
	
	public static SetMyCommands getAdminCommands() {

		SetMyCommands.SetMyCommandsBuilder setAdminCommands =
				SetMyCommands.builder().scope(new BotCommandScopeAllChatAdministrators());
		List<BotCommand> adminCommandsList =
				Arrays.asList(createCommand("search", "Список ключевых слов"),
						createCommand("help", "Список доступных команд"),
						createCommand("delete", "Удалить сообщение"));
		
		setAdminCommands.commands(adminCommandsList);
		return setAdminCommands.build();
	}

	// public void setAdminCommands(SetMyCommands adminCommands) {
	// 	this.adminCommands = adminCommands;
	// }

	public static String getCommandWithoutBotName(String command) {
		return command.split(BOT_NAME_DELIMITER)[0].trim();
	}

	private static BotCommand createCommand(String commandName, String description) {
		return BotCommand.builder().command(commandName).description(description).build();
	}
}
