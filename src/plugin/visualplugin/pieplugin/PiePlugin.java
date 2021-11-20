package plugin.visualplugin.pieplugin;

import framework.core.Content;
import framework.gui.VisualPlugin;

import java.util.Date;
import java.util.List;

public class PiePlugin implements VisualPlugin {
    private final String name = "pie chart";
    private List<Content> contents;
    private List<Date> timeStamps;
    private List<Float> scores;

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
