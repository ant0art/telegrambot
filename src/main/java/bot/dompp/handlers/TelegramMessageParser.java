package bot.dompp.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class TelegramMessageParser {
	private Logger logger = LoggerFactory.getLogger(TelegramMessageParser.class);
	private Message message;
	private AbsSender absSender;

	public TelegramMessageParser(AbsSender absSender, Message message) {
		this.absSender = absSender;
		this.message = message;
	}

	public BaseHandler getHandler() {
		// в этом методе будет производиться обработка входящего и подбор
		// необходимого обработчика
		BaseHandler handler;

		handler = new NonCommandHandler(absSender, message);
		if (message.isCommand()) {
			logger.info("Command detected");
			handler = new CommandHandler(absSender, message);
		}
		return handler;
	}

	public void parseMessage() {
		// return
		getHandler().run();
	}


}


