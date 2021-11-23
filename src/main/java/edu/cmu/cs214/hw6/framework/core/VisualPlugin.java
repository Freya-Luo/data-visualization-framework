package main.java.edu.cmu.cs214.hw6.framework.core;


import java.util.Date;
import java.util.List;

public interface VisualPlugin {
    String getPluginName();
    void setContents(List<Content>contents);
    List<Date> getTimeStamps();
    List<Float> getScores();
}
