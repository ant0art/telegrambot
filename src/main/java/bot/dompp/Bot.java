package bot.dompp;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage.SendMessageBuilder;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import bot.dompp.commands.BotCommandsConfig;
import bot.dompp.commands.service.*;

public final class Bot extends TelegramLongPollingCommandBot {
	private Logger logger = LoggerFactory.getLogger(Bot.class);

	private final String BOT_NAME;
	private final String BOT_TOKEN;

	public Bot(String botName, String botToken) {
		super();
		this.BOT_NAME = botName;
		this.BOT_TOKEN = botToken;
		logger.debug("Name & Token are set");

		/*
		 * Список доступных команд. Не содержит scope
		 */
		register(new StartCommand("start", "Начнем с начала. Сервисная команда")); // service
																					// command
		register(new SearchCommand("search", "Список ключевых слов")); // service command
		register(new HelpCommand(this)); // service command

	}

	// обработчик входящего сообщения
	public String parseMessage(Message inputMessage) {
		String userName = Utils.getUserName(inputMessage);
		String response = String.format(
				"%s, не спеши уходить! Я обязательно научусь выдавать больше информации!",
				userName);
		if (inputMessage.isCommand()) {
			for (IBotCommand cmd : HelpCommand.getmCommandRegistry().getRegisteredCommands()) {
				if (!inputMessage.getText().equals(cmd.getCommandIdentifier()))
					response = String.format(
							"%s, Я подобной команды не знаю. Попробуй найти подходящую здесь /help",
							userName);
			}
		}
		return response;
	}

	@Override
	public String getBotUsername() {
		return BOT_NAME;
	}

	@Override
	public String getBotToken() {
		return BOT_TOKEN;
	}

	@Override
	public void processNonCommandUpdate(Update update) {

		// получаем сообщение полшьзователя
		Message inMess = update.getMessage();
		Long chatId = inMess.getChatId();
		String response = parseMessage(inMess);

		// проверяем входящие сущности
		logger.warn("Here it is the log of all entities");
		List<MessageEntity> entities = inMess.getEntities();
		try {
			entities.forEach(messageEntity -> logger.info(messageEntity.toString()));
		} catch (NullPointerException e) {
			logger.info(
					String.format("This text is poor for any entity. Text: %s", inMess.getText()));
		}
		setAnswer(chatId, Utils.getUserName(inMess), response);
	}

	private void setAnswer(Long chatId, String userName, String text) {
		// формируем сообщение для ответа
		SendMessage message = new SendMessage();
		SendMessageBuilder messageBuilder = SendMessage.builder();
		SendMessage first =
				messageBuilder.text("Вау! Что-то новенькое!").chatId(chatId.toString()).build();
		message.enableMarkdown(true);
		message.setChatId(chatId.toString());
		message.setText(text);

		try {
			execute(new BotCommandsConfig().setBotCommands());
			execute(message);
			execute(first);
		} catch (TelegramApiException e) {
			logger.error(String.format("ERROR %s. Message doesn`t consist command. User: %s",
					e.getMessage(), userName));
			e.printStackTrace();
		}
	}
}
