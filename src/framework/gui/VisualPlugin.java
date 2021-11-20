package framework.gui;


import framework.core.Content;

import java.util.Date;
import java.util.List;

public interface VisualPlugin {
    String getPluginName();
    void setContents(List<Content>contents);
    List<Date> getTimeStamps();
    List<Float> getScores();
}
