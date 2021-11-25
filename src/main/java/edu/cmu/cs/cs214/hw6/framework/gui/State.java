package edu.cmu.cs.cs214.hw6.framework.gui;

import edu.cmu.cs.cs214.hw6.framework.core.DataPlugin;
import edu.cmu.cs.cs214.hw6.framework.core.FrameworkImpl;
import edu.cmu.cs.cs214.hw6.framework.core.VisualPlugin;

import java.util.Date;
import java.util.List;

public class State {
    private final String name;
    private final String currentPluginName;
    private final String[] currentVisualPluginNames;
    private final PluginInfo[] pluginInfos;
    private final Float[] scores;
    private final Date[] timestamps;
    private final int stage;
    private String info;

    public State(String name, String currentPluginName, String[] currentVisualPluginNames, PluginInfo[] pluginInfos,
                 Float[] scores, Date[] timestamps, int stage, String info) {
        this.name = name;
        this.currentPluginName = currentPluginName;
        this.currentVisualPluginNames = currentVisualPluginNames.clone();
        this.pluginInfos = pluginInfos;
        this.scores = scores;
        this.timestamps = timestamps;
        this.stage = stage;
        this.info = info;
    }

    public static State forFramework(FrameworkImpl framework) {
        String name = framework.getFrameworkName();
        String currentPluginName = framework.getCurrentDataPluginName();
        String[] currentVisualPluginNames = framework.getCurrentVisualPluginNames();
        Float[] scores = framework.getVisualizedScores();
        Date[] timestamps = framework.getVisualizedTimeStamps();
        String info = framework.getInfo();
        PluginInfo[] pluginInfos = getDataPlugins(framework);
        int stage = framework.getStage();

        return new State(name, currentPluginName, currentVisualPluginNames, pluginInfos,
                scores, timestamps, stage, info);
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

    public String getName() {
        return this.name;
    }

    public PluginInfo[] getPluginInfos() {
        return this.pluginInfos;
    }

    public int getStage() {
        return this.stage;
    }

    public String getInfo() { return this.info;}

    public String getCurrentPluginName() {
        return this.currentPluginName;
    }

    public String[] getCurrentVisualPluginNames() {
        for(String vp: currentVisualPluginNames) {
           System.out.println(vp);
        }
        return this.currentVisualPluginNames;
    }

    public Float[] getScores() {

        return scores;
    }

    public Date[] getTimestamps() {
        return timestamps;
    }
}
