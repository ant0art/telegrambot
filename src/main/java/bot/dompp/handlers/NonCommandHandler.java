package bot.dompp.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import bot.dompp.Utils;
import bot.dompp.storage.HomeData;
import bot.dompp.storage.HomeData.HomeDataObj;

public class NonCommandHandler extends BaseHandler {
	private static final String MARKDOWN_V2 = "MarkdownV2";
	private AbsSender absSender;
	private Message message;

	/**
	 * 
	 */
	public NonCommandHandler(AbsSender absSender, Message message) {
		this.absSender = absSender;
		this.message = message;
	}

	@Override
	public void run() {
		String response = parseMessage(message);

		try {
			SendMessage mess = SendMessage.builder().chatId(message.getChatId())
					.parseMode(MARKDOWN_V2).text(response).build();
			SendPhoto photoMess = new SendPhoto();
			photoMess.setChatId(message.getChatId());
			photoMess.setParseMode(MARKDOWN_V2);

			String[] photo = getPhotoForAnswer(message);
			Double[] lonlat = getLonlatForAnswer(message);

			switch (photo.length) {
				case 0:
					absSender.execute(mess);
					break;
				case 1:
					/* Alone Photo without caption */
					photoMess.setPhoto(new InputFile(photo[0]));
					if (response.length() > 1024) {
						absSender.execute(photoMess);
						absSender.execute(mess);

					} else {
						/* Alone Photo with caption */
						photoMess.setCaption(response);
						absSender.execute(photoMess);
					}
					break;
				default:
					SendMediaGroup mediaGroup =
							getMediaGroupForAnswer(message.getChatId(), response, photo);
					/* Group without caption */
					if (response.length() > 1024) {
						absSender.execute(mediaGroup);
						absSender.execute(mess);

					} else {
						/* Group with caption */
						absSender.execute(mediaGroup);
					}
			}

			if (lonlat.length != 0) {
				absSender.execute(
						new SendLocation(message.getChatId().toString(), lonlat[0], lonlat[1]));
			}
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public String parseMessage(Message inMess) {
		String userName = Utils.getUserName(inMess);
		/* Default answer */
		String response = String.format(
				"@%s, ?????????? ???????????????????????? ????????????????\\! ?? ?????????????????????? ?????????????? ???????????????? ???????????? ????????????????????\\! ???? ???????????? ?????? ???? ???????????????????? ???? ?????????????? ?????????????????? ???????????? ?????????? /help",
				userName);

		/* Answer to match found mess by template */
		if (inMess.getText() != null) {
			HomeDataObj newObj = HomeData.hasMatch(inMess.getText());
			if (newObj != null) {
				response = HomeData.setMatchAnswer(newObj);
			}
		}
		return response;
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
			listMedia.get(0).setParseMode(MARKDOWN_V2);
		}

		return mediaGroup;
	}
}
