package bot.dompp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage.SendMessageBuilder;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import bot.dompp.commands.BotCommandsConfig;
import bot.dompp.commands.service.*;
import bot.dompp.storage.HomeData;
import bot.dompp.storage.HomeData.HomeDataObj;

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
		 * Список доступных команд. Не содержит scope. Выводится
		 */
		register(new StartCommand("start", "Стартовая команда"));
		register(new SearchCommand("search", "Список ключевых слов"));
		register(new HelpCommand(this));
	}

	// обработчик входящего сообщения
	public String parseMessage(Message inputMessage) {
		String userName = Utils.getUserName(inputMessage);

		logger.info("FIRST VAR FOR RESPONSE");
		// Ответ, поступающий в любом случае, если не будет перезаписан
		String response = String.format(
				"@%s, очень неразборчиво написано\\! Я обязательно научусь выдавать больше информации\\! Но сперва всё же ознакомься со списком известных команд здесь /help",
				userName);

		// ответ на попытку пользователя использовать команду, которой нет
		if (inputMessage.isCommand()) {
			for (IBotCommand cmd : HelpCommand.getmCommandRegistry().getRegisteredCommands()) {
				if (!inputMessage.getText().equals(cmd.getCommandIdentifier()))
					response = String.format(
							"@%s, я подобной команды не знаю\\. Попробуй найти подходящую здесь /help",
							userName);
			}
		}

		logger.info("TRY TO COMPARE");

		HomeDataObj newObj = HomeData.hasMatch(inputMessage.getText());
		logger.info(String.format("Have any matches? - %s", newObj == null));
		if(newObj != null) {
			response = HomeData.matchAnswer(newObj);
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

	/*
	 * Метод обработки всех сообщений от пользователя, которые не являются командой
	 */
	@Override
	public void processNonCommandUpdate(Update update) {
		Message inMess = update.getMessage();
		Long chatId = inMess.getChatId();
		String response = parseMessage(inMess);
		setAnswer(chatId, Utils.getUserName(inMess), response);
	}

	/*
	 * Метод обработки ответа пользователю, на сообщение без команды
	 */
	private void setAnswer(Long chatId, String userName, String text) {
		//первым сообщением идёт изображение
		//или сообщение идёт как подпись к изображению
		//вторым идёт текст сообщения
		SendMessage message = new SendMessage();
		SendMessageBuilder messageBuilder = SendMessage.builder().text("Вау! Что-то новенькое!");
		SendMessage first = messageBuilder.chatId(chatId.toString()).build();
		message.enableMarkdownV2(true);
		message.setChatId(chatId.toString());
		message.setText(text);
		//третьим идёт геолокация при наличии

		//форматирование шаблона по наличию совпадения
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
