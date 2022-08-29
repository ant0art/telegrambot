package bot.dompp;

import org.slf4j.Logger;
import org.telegram.telegrambots.meta.bots.AbsSender.*;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public class Editor implements Runnable {

	private Logger logger = LoggerFactory.getLogger(Editor.class);

	private long chatId;
	private int messageId;

	/**
	 * @param messageId
	 * @param chatId
	 */
	public Editor(long chatId, int messageId) {
		this.messageId = messageId;
		this.chatId = chatId;
	}

	/**
	 *
	 */
	public Editor() {}

	@Override
	public void run() {
		try {
			logger.info("We are in Editor");
			for (int i = 0; i < 6; i++) {
				Thread.sleep(1000);
				logger.info(String.format("Прошло %d секунд", i));
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the chatId
	 */
	public long getChatId() {
		return chatId;
	}

	/**
	 * @param chatId the chatId to set
	 */
	public void setChatId(long chatId) {
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



}
