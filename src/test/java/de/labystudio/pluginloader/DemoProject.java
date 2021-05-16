package de.labystudio.pluginloader;

import de.labystudio.pluginloader.plugin.Plugin;

import java.io.File;

public class DemoProject implements Core {

    public DemoProject() {
        // Create plugin loader
        PluginLoader pluginLoader = new PluginLoader(this, new File("plugins"),
                ClassLoader.getSystemClassLoader(), System.out::println);

        // Load all plugins in directory
        pluginLoader.loadPluginsInDirectory();

        // Unload a plugin
        Plugin testPlugin = pluginLoader.getPlugin("TestPlugin");
        pluginLoader.unloadPlugin(testPlugin);

        // Load single jar file
        pluginLoader.loadPlugin(testPlugin.getJarFile());
    }

}
