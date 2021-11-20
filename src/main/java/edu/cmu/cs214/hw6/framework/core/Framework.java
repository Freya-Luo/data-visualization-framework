package main.java.edu.cmu.cs214.hw6.framework.core;

import main.java.edu.cmu.cs214.hw6.framework.gui.VisualPlugin;

import java.util.List;

public interface Framework {
    void registerDataPlugins(DataPlugin dataPlugins);
    void registerVisualPlugins(List<VisualPlugin> visualPlugins);
    void analyze();
    List<Content> getData();
};
