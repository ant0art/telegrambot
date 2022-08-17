package bot.dompp;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class Utils {

	private Utils() {}

	/**
	 * Формирование обращения к пользователю
	 *
	 * @param inputMessage сообщение пользователя
	 */

	public static String getUserName(Message inputMessage) {
		String userFirstName = inputMessage.getFrom().getFirstName();
		String userLastName = inputMessage.getFrom().getLastName();
		String userName = inputMessage.getFrom().getUserName();
		String userAnswerName = "";

		// обработчик обращения к пользователю по имени
		if (userName == null) {
			if (userLastName == null) {
				userAnswerName = userFirstName;
			} else {
				userAnswerName = userFirstName + " " + userLastName;
			}
		} else {
			userAnswerName = userName;
		}
		return userAnswerName;
	}

	public static String getUserName(User user) {
        return (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());
    }

	public static long getUserId(Message inputMessage) {
		return inputMessage.getFrom().getId();
	}
}
