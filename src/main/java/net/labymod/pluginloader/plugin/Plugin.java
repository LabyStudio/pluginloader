package net.labymod.pluginloader.plugin;

public class Plugin {
    private final String name;
    private final String main;

    private String[] authors;
    private String description;

    private String prefix;

    private String[] depends;

    public Plugin(String name, String main) {
        this.name = name;
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public String getMain() {
        return main;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }

    public String getPrefix() {
        return prefix;
    }

    public String[] getDepends() {
        return depends;
    }
}
