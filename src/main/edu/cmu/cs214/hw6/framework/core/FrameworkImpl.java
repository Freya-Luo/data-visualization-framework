package main.edu.cmu.cs214.hw6.framework.core;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FrameworkImpl implements Framework{
    private LanguageServiceClient language;
    private List<Content> contents;
    private List<DataPlugin> dataPlugins;
    private List<VisualPlugin> visualPlugins;
    private DataPlugin currentDataPlugin;
    private List<VisualPlugin> currentVisualPlugins;
    private int stage;

    private final String FRAMEWORK_BASE_NAME = "A Data Visualization Framework";

    public FrameworkImpl() throws IOException {
        language = LanguageServiceClient.create();
        this.dataPlugins = new ArrayList<>();
        this.visualPlugins = new ArrayList<>();
        this.currentVisualPlugins = new ArrayList<>();
        this.stage = 0;
    }

    public void registerDataPlugins(List<DataPlugin> dataPlugins) {
        this.dataPlugins.addAll(dataPlugins);
    }

    public List<DataPlugin> getRegisteredDataPlugins() {
        return this.dataPlugins;
    }

    public void registerVisualPlugins(List<VisualPlugin> visualPlugins) {
        this.visualPlugins.addAll(visualPlugins);
    }

    public void init(DataPlugin dp, List<VisualPlugin> vps) {
        if (currentDataPlugin != dp) {
            currentDataPlugin = dp;
        }
        currentVisualPlugins.clear();
        currentVisualPlugins.addAll(vps);
        this.stage = 1;
    }

    public void getParams(Map<String, String> paramsMap) {
        currentDataPlugin.setup(paramsMap);
        currentDataPlugin.getDataFromParams();
        this.contents = currentDataPlugin.getContents();

        for(VisualPlugin vp : currentVisualPlugins) {
            vp.setContents(this.contents);
        }
        System.out.println(this.currentDataPlugin.getPluginName());
        System.out.println(this.contents.size());
    }

    public void analyze() {
        if (this.contents.size() <= 0) {
            System.out.println("No data to analyze!");
        }
        try {
            for (Content c : this.contents) {
                Sentiment res = analyzeSentimentText(c.getText());
                c.setScore(res.getScore());
            }
            System.out.println("Done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Sentiment analyzeSentimentText(String text) throws Exception {
        // [START language_sentiment_text]
        // Instantiate the Language client com.google.cloud.language.v1.LanguageServiceClient
        Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();
        AnalyzeSentimentResponse response = language.analyzeSentiment(doc);
        Sentiment sentiment = response.getDocumentSentiment();
        if (sentiment == null) {
            System.out.println("No sentiment found");
        } else {
            System.out.printf("Sentiment magnitude: %.3f\n", sentiment.getMagnitude());
            System.out.printf("Sentiment score: %.3f\n", sentiment.getScore());
        }
        return sentiment;
    }

    public List<Content> getData() {
        return this.contents;
    }

    public String getFrameworkName() {
        if (currentDataPlugin == null) {
            return FRAMEWORK_BASE_NAME;
        }
        String fwName = "Visualization of " + currentDataPlugin.getPluginName() + " Data";
        return fwName;
    }

    public boolean hasDataPlugin() {
        return this.currentDataPlugin != null;
    }

    public int getStage() {
        return this.stage;
    }

    public String getCurrentDataPluginName() {
        if (this.currentDataPlugin != null) {
            return this.currentDataPlugin.getPluginName();
        }
        return "";
    }

    public String getMsg() {
        if (this.currentDataPlugin != null) {
            return this.currentDataPlugin.getErrorMsg();
        }
        return "";
    }

    public Float[] getVisualizedScores() {
        if (currentVisualPlugins.size() > 0) {
            List<Float> scores = currentVisualPlugins.get(0).getScores();
            return scores.toArray(new Float[scores.size()]);
        }
        return new Float[0];
    }

    public Date[] getVisualizedTimeStamps() {
        if (currentVisualPlugins.size() > 0) {
            List<Date> dates = currentVisualPlugins.get(0).getTimeStamps();
            return dates.toArray(new Date[dates.size()]);
        }
        return new Date[0];
    }
}
