package bot.dompp.storage;

import java.util.Map;

public class EnvVars {
	
	static final Map<String, String> getenv = System.getenv();

	private EnvVars() {}

	public static final EnvVars INSTANCE = new EnvVars();

	public static String getVal(String key) {
		return getenv.get(key);
	}
}
