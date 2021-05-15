package net.labymod.pluginloader.plugin;

import net.labymod.pluginloader.Core;
import net.labymod.pluginloader.PluginHandler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader {

    private final Core core;
    private final PluginHandler pluginHandler;
    private final Plugin pluginInfo;
    private final File dataFolder;
    private final File file;

    private JavaPlugin plugin;

    public PluginClassLoader(ClassLoader parent, Core core, PluginHandler pluginHandler, Plugin pluginInfo, File dataFolder, File file) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        super(new URL[]{file.toURI().toURL()}, parent);

        this.core = core;
        this.pluginHandler = pluginHandler;
        this.pluginInfo = pluginInfo;
        this.dataFolder = dataFolder;
        this.file = file;
    }

    public synchronized JavaPlugin newInstance() throws Exception {
        @SuppressWarnings("unchecked")
        Class<? extends JavaPlugin> clazz = (Class<? extends JavaPlugin>) Class.forName(pluginInfo.getMain(), true, this);
        Class<? extends JavaPlugin> subClass = clazz.asSubclass(JavaPlugin.class);

        return (this.plugin = subClass.getConstructor().newInstance());
    }

    synchronized void initialize(JavaPlugin javaPlugin) {
        javaPlugin.init(this.core, this.pluginHandler, this.pluginInfo, this.dataFolder, this.file);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
