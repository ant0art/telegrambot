package bot.dompp;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import bot.dompp.commands.BotCommandsConfig;
import bot.dompp.commands.service.HelpCommand;
import bot.dompp.commands.service.SearchCommand;
import bot.dompp.commands.service.StartCommand;
import bot.dompp.handlers.TelegramMessageParser;

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

	@Override
	public String getBotUsername() {
		return BOT_NAME;
	}

	@Override
	public String getBotToken() {
		return BOT_TOKEN;
	}

	@Override
	public void onRegister() {
		executeCommand(BotCommandsConfig.getDefaultCommands());
		executeCommand(BotCommandsConfig.getAdminCommands());
	}
	
	private void executeCommand(BotApiMethod<?> method) {
		try {
			execute(method);
		} catch (TelegramApiException e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
	}
	
	/*
	 * Метод обработки всех сообщений от пользователя, которые не являются командой
	 */
	@Override
	public void processNonCommandUpdate(Update update) {
		TelegramMessageParser parser = new TelegramMessageParser(this, update.getMessage());
		
		parser.parseMessage();
	}

	public class MyDeleteMessage implements Runnable {

		private Long chatId;
		private int messageId;
		private List<Integer> messageIds;


		/**
		 * @return the messageIds
		 */
		public List<Integer> getMessageIds() {
			return messageIds;
		}

		/**
		 * @param messageIds the messageIds to set
		 */
		public void setMessageIds(List<Integer> messageIds) {
			this.messageIds = messageIds;
		}

		/**
		 *
		 */
		public MyDeleteMessage() {}

		/**
		 * @return the chatId
		 */
		public Long getChatId() {
			return chatId;
		}

		/**
		 * @param chatId the chatId to set
		 */
		public void setChatId(Long chatId) {
			this.chatId = chatId;
		}

		/**
		 * @return the messageId
		 */
		public int getMessageId() {
			return messageId;
		}

		/**
		 * @param messageId the messageId to set
		 */
		public void setMessageId(int messageId) {
			this.messageId = messageId;
		}

		/**
		 * @param name
		 */
		public MyDeleteMessage(Long chatId, int messageId) {
			this.chatId = chatId;
			this.messageId = messageId;
		}

		@Override
		public void run() {
			try {
				synchronized (this) {
					while (true) {
						wait(5000);
						break;
					}
					deleteMessages(chatId, messageIds);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}

		public void deleteMessages(Long chatId, List<Integer> messageIds) {
			DeleteMessage messToDel = new DeleteMessage();
			messToDel.setChatId(chatId);

			try {
				for (Integer i : messageIds) {
					messToDel.setMessageId(i);
					execute(messToDel);
				}
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
	}
}
