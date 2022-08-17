package bot.dompp.commands.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import bot.dompp.Utils;

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

		String response = String.format(
				"@%s, позднее по данной команде возможно будет получить полный перечень доступных к запросу слов.%nА пока жми /help, чтобы узнать какие еще команды имеются в моём распоряжении",
				userName);

		logger.debug(String.format("User: %s. Command starts: %s", userName,
				this.getCommandIdentifier()));
		sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, response);
		logger.debug(
				String.format("User %s. Command ends %s", userName, this.getCommandIdentifier()));
	}
}

