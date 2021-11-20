package framework.core;

import framework.gui.VisualPlugin;

import java.util.List;

public interface Framework {
    void registerDataPlugins(List<DataPlugin> dataPlugins);
    void registerVisualPlugins(List<VisualPlugin> visualPlugins);
}
