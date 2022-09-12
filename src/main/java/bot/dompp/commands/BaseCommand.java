package bot.dompp.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public abstract class BaseCommand {
	
	protected static final String BASE_COMMAND = "base_command";

	public void runCommand(AbsSender absSender, Message message) {}

}
