package main.edu.cmu.cs214.hw6.framework.gui;


import main.edu.cmu.cs214.hw6.framework.core.Content;
import java.util.Date;
import java.util.List;

public interface VisualPlugin {
    String getPluginName();
    void setContents(List<Content>contents);
    List<Date> getTimeStamps();
    List<Float> getScores();
}
