package de.labystudio.pluginloader;

import com.google.gson.Gson;
import de.labystudio.pluginloader.plugin.Plugin;
import de.labystudio.pluginloader.plugin.PluginClassLoader;
import de.labystudio.pluginloader.plugin.meta.DependingPlugin;
import de.labystudio.pluginloader.plugin.meta.PluginMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Plugin loader to load the source classes
 *
 * @author LabyStudio
 */
public class PluginLoader {

    /**
     * Gson instance to load the meta file
     */
    private static final Gson GSON = new Gson();

    /**
     * The instance of the core project
     */
    private final Core core;

    /**
     * Plugin directory. All jar files in this directory are loadable.
     */
    private final File directory;

    /**
     * Core class loader
     */
    private final ClassLoader classLoader;

    /**
     * Plugin logger interface
     */
    private final PluginLoaderLogger logger;

    /**
     * Plugin loading queue. All plugins in this list are waiting for the depending plugin to load.
     */
    private final List<DependingPlugin> dependingPluginList = new ArrayList<>();

    /**
     * List of all loaded plugins
     */
    private final Map<String, Plugin> plugins = new HashMap<>();

    /**
     * Plugin loader to load or unload classes
     *
     * @param core        The instance of the core project
     * @param directory   Plugin directory. All jar files in this directory are loadable
     * @param classLoader Core class loader
     * @param logger      Plugin message logger
     */
    public PluginLoader(Core core, File directory, ClassLoader classLoader, PluginLoaderLogger logger) {
        this.core = core;
        this.directory = directory;
        this.classLoader = classLoader;
        this.logger = logger;
    }

    /**
     * Unload given plugin
     *
     * @param plugin Plugin to unload
     */
    public void unloadPlugin(Plugin plugin) {
        plugin.onDisable();

        this.plugins.remove(plugin.getPluginInfo().getName());
    }

    /**
     * Check if the given plugin is loaded
     *
     * @param pluginName Name of the plugin to check
     * @return Returns true if plugin is loaded
     */
    public boolean isLoaded(String pluginName) {
        return this.plugins.containsKey(pluginName);
    }

    /**
     * Get plugin by name
     *
     * @param pluginName Name of the plugin
     * @return Plugin with given name. Returns null when if plugin is not loaded.
     */
    public Plugin getPlugin(String pluginName) {
        return this.plugins.get(pluginName);
    }

    /**
     * Load all jar files in the plugin directory
     */
    public void loadPluginsInDirectory() {
        if (!this.directory.exists()) {
            this.directory.mkdir();
        }

        // Load plugins
        File[] dirFiles = this.directory.listFiles();
        if (dirFiles != null) {
            for (File pluginFile : dirFiles) {
                if (!pluginFile.getName().endsWith(".jar"))
                    continue;

                loadPlugin(pluginFile);
            }
        }
    }

    /**
     * Load given jar file
     *
     * @param pluginFile Plugin jar file to load
     */
    public void loadPlugin(File pluginFile) {
        try {
            JarFile jarFile = new JarFile(pluginFile);

            if (jarFile.getJarEntry("plugin.json") == null) {
                this.logger.log("Invalid plugin jar found: " + pluginFile.getName());
                jarFile.close();
                return;
            }

            JarEntry pluginJsonFile = jarFile.getJarEntry("plugin.json");

            StringBuilder json = new StringBuilder();
            Scanner scanner = new Scanner(jarFile.getInputStream(pluginJsonFile));
            while (scanner.hasNextLine()) {
                json.append(scanner.nextLine());
            }
            scanner.close();

            PluginMeta pluginMeta = GSON.fromJson(json.toString(), PluginMeta.class);
            PluginClassLoader classLoader = createClassLoader(this.classLoader, pluginMeta, pluginFile);

            String[] depends = pluginMeta.getDepends();
            if (depends == null || depends.length == 0) {
                // Register plugin
                loadPlugin(pluginMeta, classLoader);
            } else {
                this.dependingPluginList.add(new DependingPlugin(depends, pluginMeta, pluginFile));
            }

            jarFile.close();

            // Load depending plugins
            loadDependingPlugins(classLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Unload and load given plugin
     *
     * @param plugin Plugin to reload
     */
    public void reloadPlugin(Plugin plugin) throws Exception {
        ClassLoader parentClassLoader = plugin.getClass().getClassLoader().getParent();

        // Unload plugin
        unloadPlugin(plugin);

        // Load plugin with previous parent class loader
        loadPlugin(plugin.getPluginInfo(), createClassLoader(parentClassLoader, plugin.getPluginInfo(), plugin.getJarFile()));
    }

    /**
     * Iterate all plugins in the depending plugin list and check if it is ready to load
     *
     * @param parentClassLoader Parent class loader of the last loaded plugin
     */
    private void loadDependingPlugins(PluginClassLoader parentClassLoader) {
        Iterator<DependingPlugin> iterator = this.dependingPluginList.iterator();

        while (iterator.hasNext()) {
            // Get next depending plugin
            DependingPlugin dependingPlugin = iterator.next();

            boolean readyToLoad = true;
            for (String depend : dependingPlugin.getDepends()) {
                if (!isLoaded(depend)) {
                    readyToLoad = false;
                    break;
                }
            }

            // Register plugin if all depending plugins are loaded
            if (readyToLoad) {
                PluginMeta meta = dependingPlugin.getMeta();

                try {
                    PluginClassLoader classLoader = createClassLoader(parentClassLoader, meta, dependingPlugin.getFile());
                    loadPlugin(meta, classLoader);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                iterator.remove();
            }
        }
    }

    /**
     * Create plugin instance of given plugin meta and load it
     *
     * @param meta        Plugin meta from the json
     * @param classLoader Plugin class loader context
     */
    private void loadPlugin(PluginMeta meta, PluginClassLoader classLoader) {
        try {
            Plugin plugin = classLoader.newInstance();

            this.logger.log("Enabling addon " + meta.getName());
            plugin.onEnable();

            this.plugins.put(meta.getName(), plugin);
        } catch (Throwable e) {
            e.printStackTrace();
            this.logger.log("Error while loading plugin " + meta.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Create plugin class loader with given parent class loader
     *
     * @param parentClassLoader Parent class loader
     * @param meta              Plugin meta information from the json
     * @param file              Plugin jar file
     * @return Plugin class loader
     * @throws Exception Exception during class loader instantiation
     */
    private PluginClassLoader createClassLoader(ClassLoader parentClassLoader, PluginMeta meta, File file) throws Exception {
        File dataFolder = new File(this.directory, meta.getName());
        return new PluginClassLoader(parentClassLoader, this.core, this, meta, dataFolder, file);
    }

    /**
     * Get logger interface for the plugin loader
     *
     * @return Logger interface
     */
    public PluginLoaderLogger getLogger() {
        return logger;
    }
}
