package de.labystudio.pluginloader.plugin.meta;

import java.io.File;

/**
 * Depending plugin holder.
 * This plugin is waiting for all plugins in the depends list to load
 *
 * @author LabyStudio
 */
public class DependingPlugin {

    /**
     * List of all plugins that should load first
     */
    private final String[] depends;

    /**
     * Meta information of the plugin of the json
     */
    private final PluginMeta meta;

    /**
     * Plugin jar file
     */
    private final File file;

    /**
     * Create depending plugin
     *
     * @param depends List of all plugins that should load first
     * @param meta    Meta information of the plugin of the json
     * @param file    Plugin jar file
     */
    public DependingPlugin(String[] depends, PluginMeta meta, File file) {
        this.depends = depends;
        this.meta = meta;
        this.file = file;
    }

    /**
     * Get list of all plugins that should load first
     *
     * @return List of all plugins that should load first
     */
    public String[] getDepends() {
        return depends;
    }

    /**
     * Get meta information of the plugin of the json
     *
     * @return Meta information of the plugin of the json
     */
    public PluginMeta getMeta() {
        return meta;
    }

    /**
     * Get plugin jar file
     *
     * @return Plugin jar file
     */
    public File getFile() {
        return file;
    }
}
