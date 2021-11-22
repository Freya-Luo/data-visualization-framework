package main.edu.cmu.cs214.hw6.framework.core;

import java.util.List;
import java.util.Map;

public interface Framework {
    void registerDataPlugins(List<DataPlugin> dataPlugins);
    void registerVisualPlugins(List<VisualPlugin> visualPlugins);
    void analyze();
    void fetchData(Map<String, String> paramsMap);
    void setVisualData();
};
