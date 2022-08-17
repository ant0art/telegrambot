package bot.dompp.commands;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public class BotCommandsConfig {

	public BotCommandsConfig() {
		// команды бота должны формироваться за счет контекста и прав доступа тех пользователей, у
		// которых он вызывается

		/*
		 * Общие команды для всех пользователей
		 */


		/*
		 * Команды для автора бота
		 */


		 /*
		  * Команды для администратора
		  */
	}


	public SetMyCommands setBotCommands(){
		//добавить условие по добавлению команд согласно прав доступа пользователя
		/*
		 * Строковое меню бота. Содержит scope, зависит от контекста
		 */
		SetMyCommands setMyCommands = new SetMyCommands();
		List<org.telegram.telegrambots.meta.api.objects.commands.BotCommand> commandsForBot = new ArrayList<>();
        commandsForBot.add(new BotCommand("search", "Список ключевых слов"));
        commandsForBot.add(new BotCommand("help", "Список доступных команд"));
        setMyCommands.setCommands(commandsForBot);

		return setMyCommands;
	}

}
