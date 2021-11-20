package framework.core;

import framework.gui.VisualPlugin;

import java.util.List;

public interface Framework {
    void registerDataPlugins(DataPlugin dataPlugins);
    void registerVisualPlugins(List<VisualPlugin> visualPlugins);
    void analyze();
    List<Content> getData();
};
