package de.labystudio.pluginloader.plugin;

import de.labystudio.pluginloader.Core;
import de.labystudio.pluginloader.PluginLoader;
import de.labystudio.pluginloader.plugin.meta.PluginMeta;

import java.io.File;

/**
 * Abstract plugin instance.
 * Extend this class on your main instance of your dynamic project.
 *
 * @author LabyStudio
 */
public abstract class Plugin {

    /**
     * The instance of the core project
     */
    protected Core core;

    /**
     * Plugin loader instance
     */
    protected PluginLoader pluginLoader;

    /**
     * Plugin meta information from the json
     */
    protected PluginMeta pluginMeta;

    /**
     * Data folder of the plugin for configuration files
     */
    protected File dataDirectory;

    /**
     * The loaded jar file of the plugin
     */
    protected File jarFile;

    /**
     * This function is called when the plugin is loaded.
     * Overwrite this method to handle the initialization of your project
     */
    public void onEnable() {
    }

    /**
     * This function is called when the plugin is unloaded.
     * Overwrite this method to handle the unloading of your project
     */
    public void onDisable() {
    }

    /**
     * Create instance of the plugin
     */
    public Plugin() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof PluginClassLoader)) {
            throw new RuntimeException("Plugin was loaded from wrong classloader: " + getClass().getName());
        } else {
            ((PluginClassLoader) classLoader).initialize(this);
        }
    }

    /**
     * Initialize the plugin
     *
     * @param core          The instance of the core project
     * @param pluginLoader  Plugin loader instance
     * @param meta          Plugin meta information from the json
     * @param dataDirectory Data directory of the plugin for configuration files
     * @param jarFile       The loaded jar file of the plugin
     */
    public void init(Core core, PluginLoader pluginLoader, PluginMeta meta, File dataDirectory, File jarFile) {
        this.core = core;
        this.pluginLoader = pluginLoader;
        this.pluginMeta = meta;
        this.dataDirectory = dataDirectory;
        this.jarFile = jarFile;
    }

    /**
     * Get the plugin meta information from the json
     *
     * @return Plugin meta information from the json
     */
    public PluginMeta getPluginInfo() {
        return pluginMeta;
    }

    /**
     * Get the loaded jar file of the plugin
     *
     * @return The loaded jar file of the plugin
     */
    public File getJarFile() {
        return jarFile;
    }

    /**
     * Plugin loader instance
     *
     * @return Get the plugin loader instance
     */
    public PluginLoader getPluginLoader() {
        return pluginLoader;
    }

    /**
     * Get data directory of the plugin for configuration files
     *
     * @return Data directory of the plugin for configuration files
     */
    public File getDataDirectory() {
        return dataDirectory;
    }
}
