package net.labymod.pluginloader.plugin;

import net.labymod.pluginloader.plugin.Plugin;

import java.io.File;

public class DependingPlugin {

    private final String[] depends;
    private final Plugin plugin;
    private final File file;

    public DependingPlugin(String[] depends, Plugin plugin, File file) {
        this.depends = depends;
        this.plugin = plugin;
        this.file = file;
    }

    public String[] getDepends() {
        return depends;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public File getFile() {
        return file;
    }
}
