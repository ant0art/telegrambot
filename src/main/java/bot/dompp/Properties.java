package bot.dompp;

public enum Properties {
	BOT_NAME(""), BOT_TOKEN("");

	private final String value;

	private Properties(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}