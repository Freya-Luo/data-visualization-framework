package main.edu.cmu.cs214.hw6.framework.gui;

import main.edu.cmu.cs214.hw6.framework.core.DataPlugin;
import main.edu.cmu.cs214.hw6.framework.core.FrameworkImpl;

import java.util.List;

public class State {
    private final String name;
    private final PluginInfo[] pluginInfos;

    public State(String name, PluginInfo[] pluginInfos) {
        this.name = name;
        this.pluginInfos = pluginInfos;
    }

    public static State forFramework(FrameworkImpl framework) {
        String name = framework.getFrameworkName();
        PluginInfo[] pluginInfos = getDataPlugins(framework);
        return new State(name, pluginInfos);
    }

    private static PluginInfo[] getDataPlugins(FrameworkImpl framework) {
        List<DataPlugin> plugins = framework.getRegisteredDataPlugins();
        PluginInfo[] pluginInfos = new PluginInfo[plugins.size()];
        for (int i=0; i < plugins.size(); i++){
            String link = "/plugin?i="+ i;
            pluginInfos[i] = new PluginInfo(plugins.get(i).getPluginName(), link);
        }
        return pluginInfos;
    }
}
