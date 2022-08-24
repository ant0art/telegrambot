package bot.dompp.storage;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import bot.dompp.storage.HomeData.HomeDataObj.Shedule;

public class HomeData extends BotLibrary {
    private static Logger logger = LoggerFactory.getLogger(HomeData.class);

    private static String path = MyPath.DATA.getPath();

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

        public class Shedule {
            List<DayOfWork> daysOfWork;

            public Shedule() {
                this.daysOfWork = new ArrayList<>();
            }

            public class DayOfWork {
                private String day;
                private String start;
                private String end;


                /**
                 * @return the day
                 */
                public String getDay() {
                    return day;
                }


                /**
                 * @param day the day to set
                 */
                public void setDay(String day) {
                    this.day = day;
                }


                /**
                 * @return the start
                 */
                public String getStart() {
                    return start;
                }


                /**
                 * @param start the start to set
                 */
                public void setStart(String start) {
                    this.start = start;
                }


                /**
                 * @return the end
                 */
                public String getEnd() {
                    return end;
                }


                /**
                 * @param end the end to set
                 */
                public void setEnd(String end) {
                    this.end = end;
                }


                public DayOfWork() {
                    this.day = null;
                    this.start = null;
                    this.end = null;
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
                Map<String, String> example = new LinkedHashMap<>();
                for (DayOfWork dayOfWork : dayOfWorks) {
                    logger.info(String.format(
                            "This object consists of: day-\"%s\", start - \"%s\", end - \"%s\"",
                            dayOfWork.day, dayOfWork.start, dayOfWork.end));
                    if (dayOfWork.start == null) {
                        example.put(dayOfWork.day, "выходной");
                    } else {
                        example.put(dayOfWork.day, dayOfWork.start + "-" + dayOfWork.end);
                    }
                }
                Map<String, String> entry = new LinkedHashMap<>();

                int count1 = 0; // этот счётчик отвечает за необходимость дефиса
                int count2 = 0; // этот отвечает за запятую
                // перебираем карту примера
                for (Map.Entry<String, String> pair : example.entrySet()) {
                    logger.info(String.format("Start checking from pair KEY = %s pair VALUE = %s",
                            pair.getKey(), pair.getValue()));
                    if (pair.getKey() != null) {
                        if (entry.containsKey(pair.getValue())) {
                            logger.info(String.format("Счётчик count = %d", count1));
                            if (count1 >= 1) {
                                String tm = entry.get(pair.getValue());
                                logger.info(String.format("String final var  %s by key %s", tm,
                                        pair.getValue()));
                                tm = tm.substring(0, tm.length() - 2);
                                entry.put(pair.getValue(), tm += pair.getKey());
                                count1++;
                            } else {
                                String tm = entry.get(pair.getValue());
                                logger.info(String.format("String final var  %s by key %s", tm,
                                        pair.getValue()));
                                entry.put(pair.getValue(), tm += "-" + pair.getKey());
                                count1++;
                            }
                        } else {
                            count2++;
                            entry.put(pair.getValue(), pair.getKey());
                            if (count2 > 0) {
                                count1 = 0;
                            }
                        }
                    }
                }
                StringBuilder sBuilder = new StringBuilder("\n");
                for (Map.Entry<String, String> pair : entry.entrySet()) {
                    sBuilder.append(pair.getValue()).append(":").append(pair.getKey()).append("\n");
                }

                return sBuilder.toString();
            }

        }


        public String[] getKeys() {
            return keys;
        }

        public void setKeys(String[] keys) {
            this.keys = keys;
        }

        /**
         * @return the regex
         */
        public String[] getRegex() {
            return regex;
        }

        /**
         * @param regex the regex to set
         */
        public void setRegex(String[] regex) {
            this.regex = regex;
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @param title the title to set
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * @return the data
         */
        public JsonObject getData() {
            return data;
        }

        /**
         * @param data the data to set
         */
        public void setData(JsonObject data) {
            this.data = data;
        }

        /**
         * @return the tpl
         */
        public String[] getTpl() {
            return tpl;
        }

        /**
         * @param tpl the tpl to set
         */
        public void setTpl(String[] tpl) {
            this.tpl = tpl;
        }

    }

    public static HomeDataObj hasMatch(String userMessage) {
        HomeDataObj homeDataObj = null;
        for (HomeDataObj obj : HomeData.getHomeDataObjsList()) {
            for (int i = 0; i < obj.getRegex().length; i++) {
                Pattern pattern = Pattern.compile(obj.getRegex()[i], Pattern.CASE_INSENSITIVE);
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

    public static String matchAnswer(HomeDataObj homeDataObj) {
        String templateAnswer = "";
        StringBuilder sBuilder = new StringBuilder("*").append(homeDataObj.getTitle()).append("*\n");
        // получаем все объекты data, которые могут быть использованы в шаблоне сообщения
        JsonObject dataObj = homeDataObj.getData();
        try {
            // перебираем все пары объекта data
            logger.info("Start checking elements of data");
            // получаем все значения из шаблона ответа на сообщение пользвателю
            for (int i = 0; i < homeDataObj.getTpl().length; i++) {
                logger.info("Start checking elements of template");

                String s = homeDataObj.getTpl()[i];
                logger.info(String.format("%d element of template is - %s ", i, s));
                String tmp = "";
                String regexStr = ".*\\{\\{.+?}}[\\s\\S\\n]*";
                // если в строке шаблона найдено совпадение, то работаем дальше с этой
                // строкой совпадения
                logger.info(String.format("Regex is - %s %nIs there any match? - %s",
                regexStr, Pattern.matches(regexStr, s)));
                if (Pattern.matches(regexStr, s)) {
                    logger.info("Match found, go next");
                    regexStr = "\\{\\{.+?}}";
                    Pattern p = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
                    Matcher m = p.matcher(s);
                    while (m.find()) {
                        // возвращаем во временной строке результат совпадения, чтобы
                        // сравнить с ключами data
                        tmp = s.substring(m.start() + 2, m.end() - 2);

                        logger.info(String.format(
                        "Temp string for checking with key-strings of data: %s",
                        tmp));

                        for (Map.Entry<String, JsonElement> dEntry2 : dataObj.entrySet()) {
                            logger.info("Start checking data-map secondly");

                            JsonElement sElement = dEntry2.getValue();
                            logger.info(String.format("sElement is: %s", sElement));

                            // если в шаблоне regex выражение совпадет с ключем data, едем
                            // дальше
                            if (tmp.equals(dEntry2.getKey())) {
                                logger.info("Show this log if true");

                                // тут мы понимаем, есть ли совпадение. А значит пришло
                                // время
                                // определить тип объекта перед нами
                                // JsonArray: lonlat, phones, shedule
                                logger.info(String.format("sElement is JsonElement? %s",
                                        sElement.isJsonObject()));
                                logger.info("WE WERE HERE!");
                                switch (tmp) {
                                    case "lonlat": {
                                        logger.info(String.format("Array of Long with logitude & latitude: %s, shown class- %s",sElement, sElement.getClass()));

                                        sBuilder.append(s.replace(s.substring(m.start(), m.end()),
                                                        sElement.toString()));
                                        break;
                                    }
                                    case "phones": {
                                        logger.info(String.format(
                                        "Array of phones: %s, shown class - %s",
                                        sElement, sElement.getClass()));
                                        sBuilder.append(s.replace(s.substring(m.start(), m.end()),
                                                        sElement.toString()));
                                        break;
                                    }
                                    case "schedule": {
                                        logger.info(String.format(
                                        "Array of shedule: %s, shown class - %s",
                                        sElement, sElement.getClass()));
                                        logger.info(String.format("schedule is %s",
                                                Shedule.makeString(sElement)));
                                        sBuilder.append(s.replace(s.substring(m.start(), m.end()),
                                                        Shedule.makeString(sElement)));
                                        break;
                                    }
                                    default: {
                                        sBuilder.append(s.replace(s.substring(m.start(), m.end()),
                                                sElement.toString()));
                                    }
                                }
                                templateAnswer = sBuilder.toString();
                            }
                        }
                    }
                } else {
                    sBuilder.append(s);
                    logger.info(String.format("S.Builder now is that: %s", sBuilder.toString()));
                    templateAnswer = sBuilder.toString();

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        templateAnswer = Pattern.compile("\"").matcher(templateAnswer).replaceAll("");
        

        String regexStr = "([-.+?^$(){}])";
        StringBuffer buffer = new StringBuffer();
        Pattern p = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(templateAnswer);
        while (m.find()) {
            m.appendReplacement(buffer, "\\\\$1");
        }
        m.appendTail(buffer);
        templateAnswer = buffer.toString();

        return templateAnswer;

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

    public static JsonElement getParser() {
        Reader reader;
        JsonElement parser = null;
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
