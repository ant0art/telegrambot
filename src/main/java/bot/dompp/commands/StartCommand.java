package bot.dompp.commands;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import bot.dompp.Utils;

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

		SendMessage outMess = new SendMessage();

		// включаем поддержку режима разметки
		outMess.enableMarkdownV2(true);
		outMess.setChatId(message.getChatId());
		outMess.setText(response.toString());

		try {
			absSender.execute(outMess);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
