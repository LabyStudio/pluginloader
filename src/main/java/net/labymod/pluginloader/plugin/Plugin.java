package net.labymod.pluginloader.plugin;

import net.labymod.pluginloader.interfaces.Core;
import net.labymod.pluginloader.PluginLoader;
import net.labymod.pluginloader.plugin.meta.PluginMeta;

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
    private PluginMeta pluginMeta;

    /**
     * Data folder of the plugin for configuration files
     */
    private File dataFolder;

    /**
     * The loaded jar file of the plugin
     */
    private File jarFile;

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
    private Plugin() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof PluginClassLoader)) {
            this.pluginLoader.getLogger().log("A plugin was loaded from wrong classloader: " + getClass().getName());
        } else {
            ((PluginClassLoader) classLoader).initialize(this);
        }
    }

    /**
     * Initialize the plugin
     *
     * @param core         The instance of the core project
     * @param pluginLoader Plugin loader instance
     * @param meta         Plugin meta information from the json
     * @param dataFolder   Data folder of the plugin for configuration files
     * @param jarFile      The loaded jar file of the plugin
     */
    public void init(Core core, PluginLoader pluginLoader, PluginMeta meta, File dataFolder, File jarFile) {
        this.core = core;
        this.pluginLoader = pluginLoader;
        this.pluginMeta = meta;
        this.dataFolder = dataFolder;
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
     * Get data folder of the plugin for configuration files
     *
     * @return Data folder of the plugin for configuration files
     */
    public File getDataFolder() {
        return dataFolder;
    }
}
