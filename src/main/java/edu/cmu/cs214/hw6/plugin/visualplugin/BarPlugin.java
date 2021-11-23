package main.java.edu.cmu.cs214.hw6.plugin.visualplugin;

import main.java.edu.cmu.cs214.hw6.framework.core.Content;
import main.java.edu.cmu.cs214.hw6.framework.core.VisualPlugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BarPlugin implements VisualPlugin {
    private final String name = "bar chart";
    private List<Content> contents = new ArrayList<>();
    private List<Date> timeStamps = new ArrayList<>();
    private List<Float> scores = new ArrayList<>();

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    private void getVisualizedData() {
        for(Content a : contents) {
            timeStamps.add(a.getTimeStamp());
            scores.add(a.getScore());
        }
    }

    public List<Date> getTimeStamps(){
        return timeStamps;
    }

    public List<Float> getScores(){
        return scores;
    }


    public String getPluginName() {
        return name;
    }
}
