package de.labystudio.pluginloader.plugin;

import de.labystudio.pluginloader.plugin.meta.PluginMeta;
import de.labystudio.pluginloader.Core;
import de.labystudio.pluginloader.PluginLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Plugin class loader
 *
 * @author LabyStudio
 */
public class PluginClassLoader extends URLClassLoader {

    /**
     * The instance of the core project
     */
    private final Core core;

    /**
     * Plugin loader to load the source classes
     */
    private final PluginLoader pluginLoader;

    /**
     * Plugin meta information from the plugin json
     */
    private final PluginMeta meta;

    /**
     * Data directory location to store config files of the plugin
     */
    private final File dataDirectory;

    /**
     * Jar file location
     */
    private final File file;

    /**
     * Plugin instance
     */
    private Plugin plugin;

    /**
     * Create plugin class loader
     *
     * @param parent        Parent class loader
     * @param core          The instance of the core project
     * @param pluginLoader  Plugin loader to load the source classes
     * @param meta          Plugin meta information from the plugin json
     * @param dataDirectory Data directory location to store config files of the plugin
     * @param file          Jar file location
     */
    public PluginClassLoader(ClassLoader parent, Core core, PluginLoader pluginLoader, PluginMeta meta, File dataDirectory, File file) throws MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);

        this.core = core;
        this.pluginLoader = pluginLoader;
        this.meta = meta;
        this.dataDirectory = dataDirectory;
        this.file = file;
    }

    /**
     * Create new instance of the plugin
     *
     * @return Created plugin instance
     */
    public synchronized Plugin newInstance() throws Exception {
        @SuppressWarnings("unchecked")
        Class<? extends Plugin> clazz = (Class<? extends Plugin>) Class.forName(meta.getMain(), true, this);
        Class<? extends Plugin> subClass = clazz.asSubclass(Plugin.class);

        return (this.plugin = subClass.getConstructor().newInstance());
    }

    /**
     * Initialize the plugin
     *
     * @param plugin Plugin instance
     */
    synchronized void initialize(Plugin plugin) {
        plugin.init(this.core, this.pluginLoader, this.meta, this.dataDirectory, this.file);
    }

    /**
     * Get plugin instance
     *
     * @return Plugin instance
     */
    public Plugin getPlugin() {
        return plugin;
    }
}
