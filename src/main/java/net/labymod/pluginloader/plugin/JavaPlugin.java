package net.labymod.pluginloader.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.labymod.pluginloader.Core;
import net.labymod.pluginloader.PluginHandler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public abstract class JavaPlugin {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected Core core;
    protected PluginHandler pluginHandler;

    private Plugin pluginInfo;

    private File dataFolder;
    private File jarFile;

    private File configFile;

    protected JsonObject config = new JsonObject();

    public void onEnable() {
    }

    public void onDisable() {
    }

    public JavaPlugin() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof PluginClassLoader)) {
            this.core.log("A plugin was loaded from wrong classloader: " + getClass().getName());
        } else {
            ((PluginClassLoader) classLoader).initialize(this);
        }
    }

    protected JavaPlugin(Core core, PluginHandler pluginHandler, Plugin pluginInfo, File dataFolder, File jarFile) {
        init(core, pluginHandler, pluginInfo, dataFolder, jarFile);
    }

    public void init(Core core, PluginHandler pluginHandler, Plugin pluginInfo, File dataFolder, File jarFile) {
        this.core = core;
        this.pluginHandler = pluginHandler;
        this.pluginInfo = pluginInfo;
        this.dataFolder = dataFolder;
        this.jarFile = jarFile;

        // Server configuration
        this.configFile = new File(dataFolder, "config.json");

        // Load config
        loadConfig();
    }

    /**
     * Load properties file
     *
     * @return Success
     */
    public boolean loadConfig() {
        try {
            if (this.configFile.exists()) {
                FileReader reader = new FileReader(this.configFile);
                this.config = GSON.fromJson(reader, JsonObject.class);
                reader.close();
            } else {
                onSetDefaultConfigValues(this.config);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Set the default values here
     *
     * @param config Config object
     */
    protected void onSetDefaultConfigValues(JsonObject config) {
    }

    /**
     * Save properties file
     *
     * @return Success
     */
    public boolean saveConfig() {
        try {
            // Create directory
            if (!this.dataFolder.exists()) {
                this.dataFolder.mkdirs();
            }

            FileWriter writer = new FileWriter(this.configFile);
            GSON.toJson(this.config, writer);
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Plugin getPluginInfo() {
        return pluginInfo;
    }

    public File getJarFile() {
        return jarFile;
    }

    public PluginHandler getPluginHandler() {
        return pluginHandler;
    }
}
