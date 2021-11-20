import framework.core.Content;
import framework.gui.VisualPlugin;

import java.util.Date;
import java.util.List;

public class BarPlugin implements VisualPlugin {
    private List<Content> contents;
    private List<Date> timeStamps;
    private List<Float> scores;
    private static final name = "Bar Chart";

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
        return this.timeStamps;
    }

    public List<Float> getScores(){
        return this.scores;
    }


    public String getPluginName() {
        return "";
    }
}