package bot.dompp;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public final class Bot extends TelegramLongPollingBot {

	private final String BOT_NAME;
	private final String BOT_TOKEN;

	public Bot(String botName, String botToken) {
		super();
		this.BOT_NAME = botName;
		this.BOT_TOKEN = botToken;

	}

	@Override
	public void onUpdateReceived(Update update) {

		if (update.hasMessage() && update.getMessage().hasText()) {
			// получаем сообщение полшьзователя
			Message inputMessage = update.getMessage();

			// формируем сообщение для ответа
			SendMessage message = new SendMessage();
			message.setChatId(inputMessage.getChatId());
			message.setText(parseMessage(inputMessage));

			try {
				execute(message); // Call method to send the message
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
	}

	// обработчик входящего сообщения
	public String parseMessage(Message inputMessage) {
		String userFirstName = inputMessage.getFrom().getFirstName();
		String userLastName = inputMessage.getFrom().getLastName();
		String userName = inputMessage.getFrom().getUserName();
		String userAnswerName = "";

		// обработчик обращения к пользователю по имени
		if (userName == null) {
			if (userLastName == null) {
				userAnswerName = userFirstName;
			} else
				userAnswerName = userFirstName + " " + userLastName;
		} else
			userAnswerName = userName;

		String response = String.format(
				"%s, не спеши уходить! Я обязательно научусь выдавать тебе больше информации!",
				userAnswerName);
		// обработчик команд бота
		if (inputMessage.isCommand()) {
			switch (inputMessage.getText()) {
				case "/start":
					response = String.format(
							"Приветствую тебя, %s! Я - бот сообщества ЖК \"Полюстрово Парк\". %n Я нахожусь в процессе обучения",
							userAnswerName);
					break;
				case "/help":
					response = String.format("%s! Раздел помощи пока в разработке", userAnswerName);
					break;
				default:
					response = String.format(
							"%s! Эта команда мне пока неизвестна, выбери другую, пожалуйста",
							userAnswerName);
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
}
