package bot.dompp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import bot.dompp.commands.BotCommandsConfig;
import bot.dompp.commands.service.HelpCommand;
import bot.dompp.commands.service.SearchCommand;
import bot.dompp.commands.service.StartCommand;
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
	}
	
	private void executeCommand(BotApiMethod <?> method) {
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
		Message inMess = update.getMessage();
		Long chatId = inMess.getChatId();
		String chatType = update.getMessage().getChat().getType();


		String response = parseMessage(inMess);
		String[] photo = getPhotoForAnswer(inMess);
		Double[] lonlat = getLonlatForAnswer(inMess);

		setAnswer(chatId, Utils.getUserName(inMess), response, photo, lonlat);
	}

	public void setAnswer(Long chatId, String userName, String response, String[] photo, Double[] lonlat) {

		try {
			SendMessage mess = new SendMessage();
			SendPhoto photoMess = new SendPhoto();
			mess.enableMarkdownV2(true);
			mess.setChatId(chatId.toString());
			mess.setText(response);
			photoMess.setChatId(chatId);
			photoMess.setParseMode("MarkdownV2");

			switch (photo.length) {
				case 0:
					execute(mess);
					break;
				case 1:
					/* Alone Photo without caption */
					photoMess.setPhoto(new InputFile(photo[0]));
					if (response.length() > 1024) {
						execute(photoMess);
						execute(mess);

					} else {
						/* Alone Photo with caption */
						photoMess.setCaption(response);
						execute(photoMess);
					}
					break;
				default:
					SendMediaGroup mediaGroup = getMediaGroupForAnswer(chatId, response, photo);
					/* Group without caption */
					if (response.length() > 1024) {
						execute(mediaGroup);
						execute(mess);

					} else {
						/* Group with caption */
						execute(mediaGroup);
					}
			}

			if (lonlat.length != 0) {
				 execute(new SendLocation(chatId.toString(), lonlat[0], lonlat[1]));
			}
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public SendMediaGroup getMediaGroupForAnswer(Long chatId, String response, String[] photo) {
		SendMediaGroup mediaGroup = new SendMediaGroup();
		mediaGroup.setChatId(chatId);
		List<InputMedia> listMedia = new ArrayList<>();
		if (photo.length >= 2) {
			int n = photo.length > 10 ? 10 : photo.length;
			for (int i = 0; i < n; i++) {
				InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
				inputMediaPhoto.setMedia(photo[i]);
				listMedia.add(inputMediaPhoto);
			}
			mediaGroup.setMedias(listMedia);
		}
		if (response.length() <= 1024) {
			listMedia.get(0).setCaption(response);
			listMedia.get(0).setParseMode("MarkdownV2");
		}

		return mediaGroup;
	}

	public String[] getPhotoForAnswer(Message inMess) {
		String[] photo = new String[0];
		if (inMess.getText() != null) {
			HomeDataObj newObj = HomeData.hasMatch(inMess.getText());
			if (newObj != null) {
				for (Map.Entry<String, JsonElement> pair : newObj.getData().entrySet()) {
					if (pair.getKey().equals("photo") && !(pair.getValue() instanceof JsonNull)) {
						Gson gson = new Gson();
						String json = pair.getValue().toString();
						photo = gson.fromJson(json, String[].class);
					}
				}
			}
		}
		return photo;
	}

	public Double[] getLonlatForAnswer(Message inMess) {
		Double[] lonlat = new Double[0];
		if (inMess.getText() != null) {
			HomeDataObj newObj = HomeData.hasMatch(inMess.getText());
			if (newObj != null) {
				for (Map.Entry<String, JsonElement> pair : newObj.getData().entrySet()) {
					if (pair.getKey().equals("lonlat") && !(pair.getValue() instanceof JsonNull)) {
						Gson gson = new Gson();
						String json = pair.getValue().toString();
						lonlat = gson.fromJson(json, Double[].class);
					}
				}
			}
		}
		return lonlat;
	}

	public String parseMessage(Message inMess) {

		DeleteMessage messToDel = new DeleteMessage();
		if (inMess.isCommand()) {
			String s = BotCommandsConfig.getCommandWithoutBotName(inMess.getText());
			if (s.equals("/delete")) {
				int i = inMess.getReplyToMessage().getMessageId();
				messToDel.setChatId(inMess.getChatId());
			}
		}

		String userName = Utils.getUserName(inMess);
		/* Default answer */
		String response = String.format(
				"@%s, очень неразборчиво написано\\! Я обязательно научусь выдавать больше информации\\! Но сперва всё же ознакомься со списком известных команд здесь /help",
				userName);

		/* Answer to unknown command */
		if (inMess.isCommand()) {
			for (IBotCommand cmd : HelpCommand.getmCommandRegistry().getRegisteredCommands()) {
				if (!inMess.getText().equals(cmd.getCommandIdentifier()))
					response = String.format(
							"@%s, я подобной команды не знаю\\. Попробуй найти подходящую здесь /help",
							userName);
			}
		}

		/* Answer to match found mess by template */
		if (inMess.getText() != null) {
			HomeDataObj newObj = HomeData.hasMatch(inMess.getText());
			if (newObj != null) {
				response = HomeData.setMatchAnswer(newObj);
			}
		}
		return response;
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
					// deleteMessage(chatId, messageId);
					// SendMessage mess = new SendMessage();
					// execute(mess);
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

	public void deleteMessage(Update update) {
		String request = update.getMessage().getText();

	}
}
