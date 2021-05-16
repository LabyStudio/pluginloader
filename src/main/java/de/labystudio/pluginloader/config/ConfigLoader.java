package de.labystudio.pluginloader.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Json config loader
 *
 * @param <T> Storage class
 * @author LabyStudio
 */
public class ConfigLoader<T> {

    /**
     * Gson to serialize and deserialize json
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Config file location
     */
    private final File file;

    /**
     * Storage class type
     */
    private final Class<?> type;

    /**
     * Create config loader
     *
     * @param file Config file location
     * @param type Storage class type
     */
    public ConfigLoader(File file, Class<?> type) {
        this.file = file;
        this.type = type;
    }

    /**
     * Load config file.
     * It will create a new file with default values if it does not exists
     *
     * @return The storage class
     */
    public T load() throws Exception {
        T config;
        if (this.file.exists()) {
            config = read();
        } else {
            config = (T) this.type.getConstructor().newInstance();
        }
        write(config);
        return config;
    }

    /**
     * Read the json file
     *
     * @return Storage class
     */
    private T read() throws Exception {
        FileReader fileReader = new FileReader(this.file);
        T config = (T) GSON.fromJson(fileReader, this.type);
        fileReader.close();
        return config;
    }

    /**
     * Write storage class to json
     *
     * @param config Storage class
     */
    private void write(T config) throws Exception {
        FileWriter fileWriter = new FileWriter(this.file);
        GSON.toJson(config, fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }

}
