package bot.dompp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage.SendMessageBuilder;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageAutoDeleteTimerChanged;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import bot.dompp.commands.BotCommandsConfig;
import bot.dompp.commands.service.*;
import bot.dompp.storage.HomeData;
import bot.dompp.storage.HomeData.HomeDataObj;

public final class Bot extends TelegramLongPollingCommandBot {
	private Logger logger = LoggerFactory.getLogger(Bot.class);
	private static Editor editor = new Editor();

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

	/*
	 * Метод обработки всех сообщений от пользователя, которые не являются командой
	 */
	@Override
	public void processNonCommandUpdate(Update update) {
		Message inMess = update.getMessage();
		Long chatId = inMess.getChatId();


		logger.info(update.getMessage().getMessageId().toString());

		//1. В начале обрабатываем сообщение, которое поступило: отделяем картинки, локации, текст, видео, аудио
		//2. определяем текст, который к нам поступил на предмет наличия его в базе методом hasMatch, выводящим объект, если совпадение есть
		//3. Создать метод, который принимает в себя объект HomeDataObj и на основе имующихся данных выводящий ответ
		//4. Удаление сообщения через отрезок времени
		String response = parseMessage(inMess);
		if (inMess.getText() != null) {
			HomeDataObj newObj = HomeData.hasMatch(inMess.getText());
			logger.info(String.format("Have any matches? - %s", newObj != null));
			if (newObj != null) {
				logger.info(String.format("We are here? - %s", newObj == null));
				String[] photo = null;
				Double[] lonlat = null;
				for (Map.Entry<String, JsonElement> pair : newObj.getData().entrySet()) {
					if (pair.getKey().equals("lonlat") && !(pair.getValue() instanceof JsonNull)) {
						Gson gson = new Gson();
						String json = pair.getValue().toString();
						lonlat = gson.fromJson(json, Double[].class);
					} else if (pair.getKey().equals("photo")
							&& !(pair.getValue() instanceof JsonNull)) {
						Gson gson = new Gson();
						String json = pair.getValue().toString();
						photo = gson.fromJson(json, String[].class);
					}
				}
				response = HomeData.matchAnswer(newObj);

				try {
					if (photo != null && lonlat != null) {
						//метод вывоящий всё
						setAnswerWithPicAndLoc(chatId, Utils.getUserName(inMess), response, photo,
								lonlat);
					} else if (photo != null) {
						setAnswerWithPic(chatId, Utils.getUserName(inMess), response, photo);
					} else if (lonlat != null) {
						setAnswerWithLoc(chatId, Utils.getUserName(inMess), response, lonlat);
					} else {
						setAnswer(chatId, Utils.getUserName(inMess), response);
					}
				} catch (NullPointerException ex) {
					ex.printStackTrace();
				}
			} else {
				setAnswer(chatId, Utils.getUserName(inMess), response);
			}
		} else {
			logger.info(response);
			setAnswer(chatId, Utils.getUserName(inMess), response);
		}

	}

	// обработчик входящего сообщения
	public String parseMessage(Message inputMessage) {
		String userName = Utils.getUserName(inputMessage);

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

		// logger.info("TRY TO COMPARE");

		// HomeDataObj newObj = HomeData.hasMatch(inputMessage.getText());
		// logger.info(String.format("Have any matches? - %s", newObj == null));
		// if (newObj != null) {
		// 	response = HomeData.matchAnswer(newObj);
		// }
		return response;
	}

	/*
	 * Метод обработки ответа пользователю, на сообщение без команды
	 */
	private void setAnswer(Long chatId, String userName, String text) {
		// первым сообщением идёт изображение
		// или сообщение идёт как подпись к изображению
		// вторым идёт текст сообщения
		// MessageAutoDeleteTimerChanged messToDel =
		// 		new MessageAutoDeleteTimerChanged();
		// messToDel.setMessageAutoDeleteTime(5000);
		// messToDel.

		SendMessage message = new SendMessage();
		// SendMessageBuilder messageBuilder = SendMessage.builder().text("Вау! Что-то новенькое!");

		// SendMessage first = messageBuilder.chatId(chatId.toString()).build();
		message.enableMarkdownV2(true);
		message.setChatId(chatId.toString());
		message.setText(text);

		// третьим идёт геолокация при наличии

		// форматирование шаблона по наличию совпадения
		try {
			execute(new BotCommandsConfig().setBotCommands());
			int messId = execute(message).getMessageId();
			logger.error(String.format("MessId: %d", messId));

			DeleteMessage messToDel = new DeleteMessage();
			messToDel.setChatId(chatId);
			messToDel.setMessageId(messId);
			editor.run();

			// execute(first);
		} catch (TelegramApiException e) {
			logger.error(String.format("ERROR %s. Message doesn`t consist command. User: %s",
					e.getMessage(), userName));
			e.printStackTrace();
		}
	}

	private void setAnswerWithPicAndLoc(Long chatId, String userName, String text, String[] photos,
			Double[] lonlat) {
		// первым сообщением идёт изображение
		// или сообщение идёт как подпись к изображению
		// вторым идёт текст сообщения
		SendMessage message = new SendMessage();
		SendPhoto photo = new SendPhoto();
		SendLocation location = new SendLocation();

		List<Integer> messIds = new ArrayList<>();

		message.enableMarkdownV2(true);
		message.setChatId(chatId.toString());
		message.setText(text);
		photo.setChatId(chatId.toString());
		location.setChatId(chatId.toString());
		location.setLatitude(lonlat[0]);
		location.setLongitude(lonlat[1]);
		// третьим идёт геолокация при наличии

		// форматирование шаблона по наличию совпадения
		try {
			execute(new BotCommandsConfig().setBotCommands());
			for (int i = 0; i < photos.length; i++) {
				photo.setPhoto(new InputFile(photos[i]));
				messIds.add(execute(photo).getMessageId());
			}
			messIds.add(execute(message).getMessageId());
			// execute(message);
			messIds.add(execute(location).getMessageId());
			// logger.error(String.format("MessId: %d", messId));
			new Editor().run();
			for (Integer i : messIds) {
				deleteMessage(chatId, i);
			}

		} catch (TelegramApiException e) {
			logger.error(String.format("ERROR %s. Message doesn`t consist command. User: %s",
					e.getMessage(), userName));
			e.printStackTrace();
		}
	}

	private void setAnswerWithPic(Long chatId, String userName, String text, String[] photos) {
		// первым сообщением идёт изображение
		// или сообщение идёт как подпись к изображению
		// вторым идёт текст сообщения
		SendMessage message = new SendMessage();
		SendPhoto photo = new SendPhoto();
		message.enableMarkdownV2(true);
		message.setChatId(chatId.toString());
		message.setText(text);
		photo.setChatId(chatId.toString());
		// третьим идёт геолокация при наличии

		// форматирование шаблона по наличию совпадения
		try {
			execute(new BotCommandsConfig().setBotCommands());
			for (int i = 0; i < photos.length; i++) {
				photo.setPhoto(new InputFile(photos[i]));
				execute(photo);
			}
			execute(message);
		} catch (TelegramApiException e) {
			logger.error(String.format("ERROR %s. Message doesn`t consist command. User: %s",
					e.getMessage(), userName));
			e.printStackTrace();
		}
	}

	private void setAnswerWithLoc(Long chatId, String userName, String text, Double[] lonlat) {
		// первым сообщением идёт изображение
		// или сообщение идёт как подпись к изображению
		// вторым идёт текст сообщения
		SendMessage message = new SendMessage();
		SendLocation location = new SendLocation();
		message.enableMarkdownV2(true);
		message.setChatId(chatId.toString());
		message.setText(text);
		location.setChatId(chatId.toString());

		location.setLongitude(lonlat[1]);
		location.setLatitude(lonlat[0]);
		// третьим идёт геолокация при наличии

		// форматирование шаблона по наличию совпадения
		try {
			execute(message);
			execute(location);
		} catch (TelegramApiException e) {
			logger.error(String.format("ERROR %s. Message doesn`t consist command. User: %s",
					e.getMessage(), userName));
			e.printStackTrace();
		}
	}

	private void deleteMessage(Long chatId, int messageId) {
		DeleteMessage messToDel = new DeleteMessage();
		messToDel.setChatId(chatId);
		messToDel.setMessageId(messageId);
		// editor.run();
		try {
			execute(messToDel);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
