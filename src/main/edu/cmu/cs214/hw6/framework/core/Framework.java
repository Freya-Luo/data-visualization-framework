package main.edu.cmu.cs214.hw6.framework.core;

import java.util.List;

public interface Framework {
    void registerDataPlugins(List<DataPlugin> dataPlugins);
    void registerVisualPlugins(List<VisualPlugin> visualPlugins);
    void analyze();
    List<Content> getData();
};
