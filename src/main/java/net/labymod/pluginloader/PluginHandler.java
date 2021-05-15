package net.labymod.pluginloader;

import net.labymod.pluginloader.plugin.JavaPlugin;
import net.labymod.pluginloader.plugin.Plugin;
import net.labymod.pluginloader.plugin.PluginClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginHandler {

    private final List<JavaPlugin> plugins = new ArrayList<>();

    private final Map<String, JavaPlugin> enabledPlugins = new HashMap<>();

    private final File pluginDirectory;

    private final Core core;
    private final PluginLoader pluginLoader;

    public PluginHandler(Core core, File pluginDirectory) {
        this.core = core;
        this.pluginDirectory = pluginDirectory;
        this.pluginLoader = new PluginLoader(core, this, pluginDirectory);
    }

    public void init() {
        this.pluginLoader.loadPluginsInFolder();

        // registerRawPlugin( "Test Plugin", TestPlugin.class );
    }

    public PluginClassLoader createClassLoader(ClassLoader parentClassLoader, Plugin pluginInfo, File file) throws Exception {
        File dataFolder = new File(this.pluginDirectory, pluginInfo.getName());
        return new PluginClassLoader(parentClassLoader,
                this.core, this, pluginInfo, dataFolder, file);
    }

    public void registerPlugin(Plugin pluginInfo, PluginClassLoader classLoader) {
        try {
            JavaPlugin plugin = classLoader.newInstance();

            this.core.log("Enabling addon " + pluginInfo.getName());

            plugin.onEnable();

            this.plugins.add(plugin);
            this.enabledPlugins.put(pluginInfo.getName(), plugin);
        } catch (Throwable e) {
            e.printStackTrace();
            this.core.log("Error while loading plugin " + pluginInfo.getName() + ": " + e.getMessage());
        }
    }

    private void registerRawPlugin(String pluginName, Class<?> clazz) {
        File dataFolder = new File(this.pluginDirectory, pluginName);
        Plugin pluginInfo = new Plugin(pluginName, clazz.getName());

        try {
            JavaPlugin plugin = (JavaPlugin) clazz.newInstance();
            plugin.init(this.core, this, pluginInfo, dataFolder, null);
            plugin.onEnable();

            this.plugins.add(plugin);
            this.enabledPlugins.put(pluginInfo.getName(), plugin);
        } catch (InstantiationException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void unregisterPlugin(JavaPlugin plugin) {
        plugin.onDisable();

        this.plugins.remove(plugin);
        this.enabledPlugins.remove(plugin.getPluginInfo().getName());
    }

    public boolean isEnabled(String pluginName) {
        return this.enabledPlugins.containsKey(pluginName);
    }

    public JavaPlugin getPlugin(String pluginName) {
        return this.enabledPlugins.get(pluginName);
    }

    public PluginLoader getPluginLoader() {
        return pluginLoader;
    }

    public List<JavaPlugin> getPlugins() {
        return plugins;
    }
}
