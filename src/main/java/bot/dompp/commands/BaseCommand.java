package bot.dompp.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class BaseCommand {
	
	protected static final String BASE_COMMAND = "base_command";

	public void runCommand(AbsSender absSender, Message message) {}

	public void sendMessage(AbsSender absSender, Message message, String response) {
		SendMessage outMess = SendMessage.builder().chatId(message.getChatId())
				.parseMode("MarkdownV2").text(response).build();
		try {
			absSender.execute(outMess);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

}
