package net.labymod.pluginloader.interfaces;

/**
 * Called from the the plugin loader to log messages in the main project
 *
 * @author LabyStudio
 */
public interface PluginLoaderLogger {
    /**
     * Log a message
     *
     * @param message Message to log
     */
    void log(String message);
}
