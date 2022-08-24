package bot.dompp.storage;

public enum MyPath {
    DATA("\\storage\\data.json");

    private String path;

    /**
     * 
     */
    private MyPath(String path) {
        this.path = path;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }
}
