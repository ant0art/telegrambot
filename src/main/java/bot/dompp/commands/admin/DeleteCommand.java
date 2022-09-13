package bot.dompp.commands.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import bot.dompp.commands.BaseCommand;

public class DeleteCommand extends BaseCommand {

	public DeleteCommand() { /* Needed to init command method */ }

	private Logger logger = LoggerFactory.getLogger(DeleteCommand.class);

	@Override
	public void runCommand(AbsSender absSender, Message message) {
		DeleteMessage messToDel = new DeleteMessage();
		messToDel.setChatId(message.getChatId());
		Message reply = message.getReplyToMessage();
		if (reply != null) {
			int i = message.getReplyToMessage().getMessageId();

			logger.info(String.format("HERE IS THE ID NUMBER OF MESSAGE TO DEL: %d", i));
			messToDel.setMessageId(i);
			try {
				absSender.execute(messToDel);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
	}



}
