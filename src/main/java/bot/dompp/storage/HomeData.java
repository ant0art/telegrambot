package bot.dompp.storage;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import bot.dompp.storage.HomeData.HomeDataObj.Links;
import bot.dompp.storage.HomeData.HomeDataObj.Shedule;


public class HomeData extends BotLibrary {
	private static Logger logger = LoggerFactory.getLogger(HomeData.class);
	static final Map<String, String> getenv = System.getenv();

	private static String path = EnvVars.getVal("DATA");
	
	public class HomeDataObj {
		private String[] keys;
		private String[] regex;
		private String title;
		private JsonObject data;
		private String[] tpl;

		public HomeDataObj() {
			this.keys = new String[] {};
			this.regex = new String[] {};
			this.title = "";
			this.data = new JsonObject();
			this.tpl = new String[] {};
		}

		/**
		 * @return the data
		 */
		public JsonObject getData() {
			return data;
		}

		public class Shedule {
			List<DayOfWork> daysOfWork;
			private static Logger logger = LoggerFactory.getLogger(Shedule.class);

			public Shedule() {
				this.daysOfWork = new ArrayList<>();
			}

			public class DayOfWork extends Shedule {
				private String day;
				private String start;
				private String end;
				private String workbreak;

				public DayOfWork() {
					this.day = "";
					this.start = "";
					this.end = "";
					this.workbreak = "";
				}
			}


			/**
			 * @return the daysOfWork
			 */
			public static List<DayOfWork> getDaysOfWork(JsonElement jsonElement) {
				List<DayOfWork> daysOfWork = new ArrayList<>();
				logger.info("we get list");
				try {
					Gson gson = new Gson();
					String json = gson.toJson(jsonElement);
					DayOfWork[] temp = gson.fromJson(json, DayOfWork[].class);
					logger.info("Make an jArray from jElement");
					daysOfWork = Arrays.asList(temp);
					logger.info(String.format("daysOfWork is this - %s", daysOfWork));
				} catch (NullPointerException ex) {
					ex.printStackTrace();
				}

				return daysOfWork;
			}

			public static String makeString(JsonElement jsonElement) {
				logger.info("Start command MAKESTRING");

				List<DayOfWork> dayOfWorks = getDaysOfWork(jsonElement);
				Map<String, String> result = new LinkedHashMap<>();
				List<String> workTime = new ArrayList<>();
				List<String> dayList = new ArrayList<>();

				for (DayOfWork dayOfWork : dayOfWorks) {
					workTime.add(getWorkTime(dayOfWork));
					dayList.add(dayOfWork.day);
				}
				Set<String> workTimeSet = new LinkedHashSet<>(workTime);
				for (String s : workTimeSet) {
					int temp = -1;
					int count = 0;
					for (int j = 0; j < workTime.size(); j++) {
						if (workTime.get(j).equals(s)) {
							count++;
							String currenString = dayList.get(j);
							if (temp == -1) {
								temp = j;
								result.put(s, currenString);
							}
							String tempString = result.get(s);
							if (j > temp + 1 && count == 1) { // можно поставить запятую
								result.put(s, tempString + ", " + currenString);
								temp = j;
							} else if (j == temp + 1 && count == 2) {// можно поставить дефис
								result.put(s, tempString + "-" + currenString);
								temp = j;
							} else if (j >= temp + 1 && count > 2) {// можно поставить дефис и
																	// заменить последние две буквы
																	// строки
								String changeString =
										tempString.substring(0, tempString.length() - 2);
								result.put(s, changeString + currenString);
								temp = j;
							}
						} else {
							count = 0;
						}
					}
				}
				StringBuilder sBuilder = new StringBuilder("\n");
				for (Map.Entry<String, String> pair : result.entrySet()) {
					sBuilder.append(pair.getValue()).append(": ").append(pair.getKey())
							.append("\n");
					logger.info(sBuilder.toString());
				}
				return sBuilder.toString();
			}

			public static String getWorkTime(DayOfWork dayOfWork) {
				String workTime = "";
				if (dayOfWork.start == null) {
					workTime = "выходной";
				} else {
					if (dayOfWork.workbreak != null) {
						workTime = dayOfWork.start + "-" + dayOfWork.end + " обед: "
								+ dayOfWork.workbreak;
					} else {
						workTime = dayOfWork.start + "-" + dayOfWork.end;
					}
				}
				return workTime;
			}
		}

		public class Links {
			List<LinkObj> linksList;

			public Links(List<LinkObj> linksList) {
				this.linksList = linksList;
			}

			public static List<LinkObj> getListOfLinks(JsonElement jsonElement) {
				List<LinkObj> listOfLinks = new ArrayList<>();
				logger.info("we get list");
				try {
					Gson gson = new Gson();
					String json = gson.toJson(jsonElement);
					LinkObj[] temp = gson.fromJson(json, LinkObj[].class);
					logger.info("Make an jArray from jElement");
					listOfLinks = Arrays.asList(temp);
					logger.info(String.format("LinkObjList is this - %s", listOfLinks));
				} catch (NullPointerException ex) {
					ex.printStackTrace();
				}

				return listOfLinks;
			}

			public static String makeString(JsonElement jsonElement) {
				StringBuilder sBuilder = new StringBuilder("\n");
				List<LinkObj> listOfLinks = getListOfLinks(jsonElement);
				String title;
				String url;

				for (int i = 0; i < listOfLinks.size(); i++) {
					title = listOfLinks.get(i).title;
					url = listOfLinks.get(i).url;
					sBuilder.append(title).append(": ").append(url).append("\n");
				}

				return sBuilder.toString();
			}


			public class LinkObj {
				private String type;
				private String title;
				private String url;

				/**
				 * @param type
				 * @param title
				 * @param url
				 */
				public LinkObj() {
					this.type = "";
					this.title = "";
					this.url = "";
				}

			}
		}

	}

	public static HomeDataObj hasMatch(String userMessage) {
		HomeDataObj homeDataObj = null;
		for (HomeDataObj obj : HomeData.getHomeDataObjsList()) {
			for (int i = 0; i < obj.regex.length; i++) {
				Pattern pattern = Pattern.compile(obj.regex[i], Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(userMessage.toLowerCase());
				if (matcher.find()) {
					homeDataObj = obj;
				}
			}
		}
		return homeDataObj;
	}

	public static String getStringFromJson(JsonElement json) {
		String result = "";

		if (json.isJsonArray()) {
			JsonArray jArray = json.getAsJsonArray();
			result = jArray.toString();
		} else if (json.isJsonPrimitive()) {
			JsonPrimitive jPrimitive = json.getAsJsonPrimitive();
			result = jPrimitive.toString();

		} else if (json.isJsonNull()) {
			JsonNull jNull = json.getAsJsonNull();
			result = jNull.toString();
		} else {
			JsonObject jObject = json.getAsJsonObject();
			result = jObject.toString();
		}
		return result;
	}

	public static String setMatchAnswer(HomeDataObj homeDataObj) {
		StringBuilder sBuilder = new StringBuilder("*").append(homeDataObj.title).append("*\n");
		JsonObject dataObj = homeDataObj.data;
		for (int i = 0; i < homeDataObj.tpl.length; i++) {
			String s = homeDataObj.tpl[i];
			String tmp = "";
			String regexStr = "\\{\\{.+?}}";
			Pattern p = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(s);
			while (m.find()) {
				// возвращаем во временной строке результат совпадения, чтобы
				// сравнить с ключами data
				int start = m.start();
				int end = m.end();
				tmp = s.substring(start + 2, end - 2);
				for (Map.Entry<String, JsonElement> dEntry2 : dataObj.entrySet()) {
					// если в шаблоне regex выражение совпадет с ключем data, едем
					// дальше
					if (tmp.equals(dEntry2.getKey())) {
						sBuilder.append(getDataString(dEntry2.getValue(), s, start, end))
								.toString();
					}
				}
			}

		}

		return replaceWithSlash(sBuilder.toString());
	}

	/**
	 * @param sElement takes JsonElement from data field
	 * @param regTmp contains data option in braces
	 * @param start takes index of first brace
	 * @param end takes index of last brace
	 * @return String for template request
	 */
	public static String getDataString(JsonElement sElement, String regTmp, int start, int end) {
		String result = regTmp.substring(start + 2, end - 2);
		logger.info("start metod getDataString");
		switch (result) {
			case "lonlat": {
				result = regTmp.replace(regTmp.substring(start, end), "\n" + sElement.toString());
				break;
			}
			case "photo": {
				result = regTmp.replace(regTmp.substring(start, end), "\n" + sElement.toString());
				break;
			}
			case "phones": {
				result = regTmp.replace(regTmp.substring(start, end), "\n" + sElement.toString());
				break;
			}
			case "schedule": {
				result = regTmp.replace(regTmp.substring(start, end), Shedule.makeString(sElement));
				break;
			}
			case "links": {
				result = regTmp.replace(regTmp.substring(start, end), Links.makeString(sElement));
				break;
			}
			default: {
				result = regTmp.replace(regTmp.substring(start, end), "\n" + sElement.toString());
			}
		}
		return result;
	}

	public static String replaceWithSlash(String s) {
		s = Pattern.compile("\"").matcher(s).replaceAll("");
		String regexStr = "([-.+?^$(){}])";
		StringBuffer buffer = new StringBuffer();
		Pattern p = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		while (m.find()) {
			m.appendReplacement(buffer, "\\\\$1");
		}
		m.appendTail(buffer);
		return buffer.toString();
	}

	/**
	 * @return the homeDataObjsList
	 */
	public static List<HomeDataObj> getHomeDataObjsList() {
		List<HomeDataObj> homeDataObjsList = new ArrayList<>();
		try {
			JsonObject parser = getParser().getAsJsonObject();
			for (Map.Entry<String, JsonElement> simpleObj : parser.entrySet()) {
				Gson gson = new Gson();
				String json = gson.toJson(simpleObj.getValue());
				// создаем java-объект из базы json
				homeDataObjsList.add(gson.fromJson(json, HomeDataObj.class));
			}
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}

		return homeDataObjsList;
	}

	@Override
	public BotLibrary copy() {
		return null;
	}

	public HomeData() {
		super();
	}

	public static @Nonnull JsonElement getParser() {
		Reader reader;
		JsonElement parser = new JsonObject();

		try {
			// собираем данные по востребованным объектам из файла
			reader = Files.newBufferedReader(Path.of(path).toAbsolutePath());
			// парсим все данные как один объект
			parser = JsonParser.parseReader(reader);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return parser;
	}
}
