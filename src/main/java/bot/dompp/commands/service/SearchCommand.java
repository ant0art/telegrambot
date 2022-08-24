package bot.dompp.commands.service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import com.google.gson.JsonArray;
import bot.dompp.Utils;
import bot.dompp.storage.MyJsonParser;
import bot.dompp.storage.MyPath;

public class SearchCommand extends ServiceCommand {
	private Logger logger = LoggerFactory.getLogger(SearchCommand.class);

	/**
	 * @param identifier - name of command
	 * @param description - what command do
	 */
	public SearchCommand(String identifier, String description) {
		super(identifier, description);
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
		String userName = Utils.getUserName(user);

		StringBuilder response = new StringBuilder(String.format(
				"@%s, пока что я могу вывести только свой потенциал для поиска, но не сам результат. Нажмите на ключевое слово, и результат будет скопирован. Вставьте его в строку сообщения и осуществите поиск.%n*Вот предварительный список будущих ключевых слов:*%n%n",
				userName));


		logger.info("Begin injection of keys of json");
		Map<String, JsonArray> mp =
				new MyJsonParser(MyPath.DATA.getPath())
						.getPossibleRequests("keys");

		for (Map.Entry<String, JsonArray> entries : mp.entrySet()) {
			logger.info("Catch the pair key value");
			String request = entries.getKey();
			logger.info("Got the key");
			JsonArray values = entries.getValue().getAsJsonArray();
			logger.info("Got the value");

			response.append(String.format("*%s*%n", request)).append("Доступные: ");
			for (int i = 0; i < values.size(); i++) {
				response.append("\"").append(String.format("`%s`", values.get(i).getAsString()))
						.append("\"");
				if (i == values.size() - 1)
					response.append("\n\n");
				else
					response.append(", ");
			}

		}
		response.append("Чтобы узнать о доступных командах, жмите /help");
		logger.debug("Injection of keys of json ends");
		String templateAnswer = response.toString();
		templateAnswer.replaceAll("\"", "");

        String regexStr = "([-.+?^$(){}])";
        StringBuffer buffer = new StringBuffer();
        Pattern p = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(templateAnswer);
        while (m.find()) {
            m.appendReplacement(buffer, "\\\\$1");
        }
        m.appendTail(buffer);
        templateAnswer = buffer.toString();

		logger.debug(String.format("User: %s. Command starts: %s", userName,
				this.getCommandIdentifier()));
				logger.info(templateAnswer);
		sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
				templateAnswer);
		logger.debug(
				String.format("User %s. Command ends %s", userName, this.getCommandIdentifier()));
	}
}

