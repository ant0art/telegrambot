package bot.dompp;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import bot.dompp.commands.BotCommandsConfig;
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
	}

	@Override
	public String getBotUsername() {
		return BOT_NAME;
	}

	@Override
	public String getBotToken() {
		return BOT_TOKEN;
	}

	// @Override
	// public void onRegister() {
	// 	executeCommand(BotCommandsConfig.getDefaultCommands());
	// 	executeCommand(BotCommandsConfig.getAdminCommands());
	// }

	// private void executeCommand(BotApiMethod<?> method) {
	// 	try {
	// 		execute(method);
	// 	} catch (TelegramApiException e) {
	// 		e.printStackTrace();
	// 		logger.debug(e.getMessage());
	// 	}
	// }

	/*
	 * Метод обработки всех сообщений от пользователя, которые не являются
	 * командой
	 */
	@Override
	public void processNonCommandUpdate(Update update) {


		// if (update.hasMessage()) {
		// 	SendMessage message = SendMessage.builder().chatId(update.getMessage().getChatId())
		// 			.text("Этот текст возвращается пользователю в любом случае после получения от него любого запроса")
		// 			.build();

		// 	InlineKeyboardButton ikb1 = new InlineKeyboardButton();
		// 	ikb1.setText("Режим работы");
		// 	ikb1.setCallbackData("what to back?");
		// 	InlineKeyboardButton ikb2 = new InlineKeyboardButton();
		// 	ikb2.setText("Сотрудники");
		// 	ikb2.setCallbackData("what to back in second?");
		// 	List<InlineKeyboardButton> buttonsListRaw1 = new ArrayList<>();
		// 	buttonsListRaw1.add(ikb1);
		// 	buttonsListRaw1.add(ikb2);
		// 	// List<InlineKeyboardButton> buttonsListRaw2 = new ArrayList<>();
		// 	// buttonsListRaw2.add(ikb2);
		// 	List<List<InlineKeyboardButton>> buttonsList = new ArrayList<>();
		// 	buttonsList.add(buttonsListRaw1);
		// 	// InlineKeyboardMarkup ikbm = new InlineKeyboardMarkup(buttonsList);
		// 	InlineKeyboardMarkup ikbm = new InlineKeyboardMarkup();
		// 	ikbm.setKeyboard(buttonsList);
		// 	message.setReplyMarkup(ikbm);
		// 	try {
		// 		execute(message);
		// 	} catch (TelegramApiException e) {
		// 		e.printStackTrace();
		// 	}
		// }
		// if (update.hasCallbackQuery()) {
		// 	try {
		// 		String text = update.getCallbackQuery().getData();
		// 		text = update.getCallbackQuery().getMessage().getText() + "\n" + text;
		// 		Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
		// 		EditMessageText editMessageText = new EditMessageText();
		// 		editMessageText.setChatId(update.getCallbackQuery().getMessage().getChatId());
		// 		editMessageText.setMessageId(messageId);
		// 		editMessageText.setText(text);

		// 		// SendMessage outMess = new SendMessage();
		// 		// outMess.setChatId(update.getCallbackQuery().getMessage().getChatId());
		// 		// outMess.setText(text);
		// 		execute(editMessageText);
		// 	} catch (TelegramApiException e) {
		// 		e.printStackTrace();
		// 	}
		// }
		TelegramMessageParser parser = new TelegramMessageParser(this, update);
		
		if (update.hasCallbackQuery()) { /* Обработка обратного вызова */
			try {
				String text = update.getCallbackQuery().getData();
				text = update.getCallbackQuery().getMessage().getText() + "\n" + text;
				Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
				// EditMessageText editMessageText = new EditMessageText();
				// editMessageText.setChatId(update.getCallbackQuery().getMessage().getChatId());
				// editMessageText.setMessageId(messageId);
				// editMessageText.setText(text);

				SendMessage outMess = new SendMessage();
				outMess.setChatId(update.getCallbackQuery().getMessage().getChatId());
				outMess.setText(text);
				execute(outMess);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		} else if (update.hasMessage()) { /* Обработка только входящего сообщения */
			parser.parseMessage();
		}


		// TelegramMessageParser parser = new TelegramMessageParser(this,
		// update.getMessage());

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
