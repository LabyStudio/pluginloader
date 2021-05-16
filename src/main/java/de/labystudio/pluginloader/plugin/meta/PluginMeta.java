package de.labystudio.pluginloader.plugin.meta;

/**
 * Plugin meta information from the plugin json
 *
 * @author LabyStudio
 */
public class PluginMeta {

    /**
     * Unique name of the plugin
     */
    private final String name;

    /**
     * Main class path
     */
    private final String main;

    /**
     * Author names
     */
    private String[] authors;

    /**
     * Plugin description
     */
    private String description;

    /**
     * Names of depending plugins. All plugin names in this list will load first.
     */
    private String[] depends;

    /**
     * Create plugin meta instance
     *
     * @param name Unique name of the plugin
     * @param main Main class path
     */
    public PluginMeta(String name, String main) {
        this.name = name;
        this.main = main;
    }

    /**
     * Get unique name oft the plugin
     *
     * @return Unique plugin name
     */
    public String getName() {
        return name;
    }

    /**
     * Get main class path
     *
     * @return Main class path
     */
    public String getMain() {
        return main;
    }

    /**
     * Get author names
     *
     * @return Author names
     */
    public String[] getAuthors() {
        return authors;
    }

    /**
     * Get description of the plugin
     *
     * @return Plugin description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get names of depending plugins. All plugin names in this list will load first.
     *
     * @return Names of depending plugin
     */
    public String[] getDepends() {
        return depends;
    }
}
