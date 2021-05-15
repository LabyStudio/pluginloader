package net.labymod.pluginloader;

import com.google.gson.Gson;
import net.labymod.pluginloader.plugin.DependingPlugin;
import net.labymod.pluginloader.plugin.Plugin;
import net.labymod.pluginloader.plugin.PluginClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {

    private static final Gson GSON = new Gson();

    private Core core;
    private PluginHandler pluginHandler;
    private final File directory;

    private final List<DependingPlugin> dependingPluginList = new ArrayList<>();

    public PluginLoader(Core core, PluginHandler pluginHandler, File directory) {
        this.core = core;
        this.pluginHandler = pluginHandler;
        this.directory = directory;
    }

    public void loadPluginsInFolder() {
        if (!this.directory.exists()) {
            this.directory.mkdir();
        }

        // Load plugins
        File[] dirFiles = this.directory.listFiles();
        if (dirFiles != null) {
            for (File pluginFile : dirFiles) {
                if (!pluginFile.getName().endsWith(".jar"))
                    continue;

                resolveJarFile(pluginFile);
            }
        }
    }

    public void resolveJarFile(File pluginFile) {
        try {
            JarFile jarFile = new JarFile(pluginFile);

            if (jarFile.getJarEntry("plugin.json") == null) {
                this.core.log("Invalid plugin jar found: " + pluginFile.getName());
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

            Plugin pluginInfo = GSON.fromJson(json.toString(), Plugin.class);
            PluginClassLoader classLoader = this.pluginHandler.createClassLoader(ClassLoader.getSystemClassLoader(), pluginInfo, pluginFile);

            String[] depends = pluginInfo.getDepends();
            if (depends == null || depends.length == 0) {
                // Register plugin
                this.pluginHandler.registerPlugin(pluginInfo, classLoader);
            } else {
                this.dependingPluginList.add(new DependingPlugin(depends, pluginInfo, pluginFile));
            }

            jarFile.close();

            // Load depending plugins
            loadDependingPlugins(classLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDependingPlugins(PluginClassLoader parentClassLoader) {
        Iterator<DependingPlugin> iterator = this.dependingPluginList.iterator();

        while (iterator.hasNext()) {
            // Get next depending plugin
            DependingPlugin dependingPlugin = iterator.next();

            boolean readyToLoad = true;
            for (String depend : dependingPlugin.getDepends()) {
                if (!this.pluginHandler.isEnabled(depend)) {
                    readyToLoad = false;
                    break;
                }
            }

            // Register plugin if all depending plugins are loaded
            if (readyToLoad) {
                Plugin plugin = dependingPlugin.getPlugin();

                try {
                    PluginClassLoader classLoader = this.pluginHandler.createClassLoader(parentClassLoader, plugin, dependingPlugin.getFile());
                    this.pluginHandler.registerPlugin(plugin, classLoader);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                iterator.remove();
            }
        }
    }

}
