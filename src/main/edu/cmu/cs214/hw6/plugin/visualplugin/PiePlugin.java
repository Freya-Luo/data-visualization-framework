package main.edu.cmu.cs214.hw6.plugin.visualplugin;

import main.edu.cmu.cs214.hw6.framework.core.Content;
import main.edu.cmu.cs214.hw6.framework.core.VisualPlugin;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PiePlugin implements VisualPlugin {
    private final String name = "pie chart";
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
