# Plugin Loader
Load and unload classes dynamically in your project

### Example core
```java
package de.labystudio.pluginloader;

import de.labystudio.pluginloader.plugin.Plugin;

import java.io.File;

public class DemoProject implements Core {

    public DemoProject() {
        // Create plugin loader
        PluginLoader pluginLoader = new PluginLoader(this, new File("plugins"), System.out::println);

        // Load all plugins in directory
        pluginLoader.loadPluginsInDirectory();

        // Unload a plugin
        Plugin testPlugin = pluginLoader.getPlugin("TestPlugin");
        pluginLoader.unloadPlugin(testPlugin);

        // Load single jar file
        pluginLoader.loadPlugin(testPlugin.getJarFile());
    }

}
```

### Example plugin

```java
package de.labystudio.pluginloader;

import de.labystudio.pluginloader.plugin.Plugin;

public class TestPlugin extends Plugin {

    @Override
    public void onEnable() {
        // Enable your plugin...
    }

    @Override
    public void onDisable() {
        // Disable your plugin...
    }
    
}
```

### Add to gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compile 'com.github.LabyStudio:pluginloader:1.0.2'
}
```