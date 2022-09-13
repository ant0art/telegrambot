package bot.dompp.commands.service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import com.google.gson.JsonArray;
import bot.dompp.Utils;
import bot.dompp.commands.BaseCommand;
import bot.dompp.storage.EnvVars;
import bot.dompp.storage.MyJsonParser;

public class SearchCommand extends BaseCommand {
	private Logger logger = LoggerFactory.getLogger(SearchCommand.class);

	@Override
	public void runCommand(AbsSender absSender, Message message) {
		String userName = Utils.getUserName(message.getFrom());

		StringBuilder response = new StringBuilder(String.format(
				"@%s, нажмите на ключевое слово, и результат будет скопирован. Вставьте его в строку сообщения и осуществите поиск.%n*Вот предварительный список ключевых слов:*%n%n",
				userName));

		Map<String, JsonArray> mp =
				new MyJsonParser(EnvVars.getVal("DATA")).getPossibleRequests("keys");

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
		Pattern.compile("\"").matcher(templateAnswer).replaceAll("");

		String regexStr = "([-.+?^$(){}])";
		StringBuffer buffer = new StringBuffer();
		Pattern p = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(templateAnswer);
		while (m.find()) {
			m.appendReplacement(buffer, "\\\\$1");
		}
		m.appendTail(buffer);
		templateAnswer = buffer.toString();
		logger.info(templateAnswer);

		sendMessage(absSender, message, templateAnswer);
	}
}

