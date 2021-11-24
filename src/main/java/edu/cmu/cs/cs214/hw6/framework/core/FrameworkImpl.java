package edu.cmu.cs.cs214.hw6.framework.core;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

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

    public FrameworkImpl() {
        /*try {
            language = LanguageServiceClient.create();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        this.dataPlugins = new ArrayList<>();
        this.visualPlugins = new ArrayList<>();
        this.currentVisualPlugins = new ArrayList<>();
        this.contents = new ArrayList<>();
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

    public void init() {
        this.stage = 0;
        this.dataPlugins.clear();
        this.visualPlugins.clear();
        this.currentVisualPlugins.clear();
        this.contents.clear();
    }

    public void init(DataPlugin dp, List<VisualPlugin> vps) {
        if (currentDataPlugin != dp) {
            currentDataPlugin = dp;
        }
        currentVisualPlugins.clear();
        currentVisualPlugins.addAll(vps);
        language = createLanguage();
        this.stage = 1;
    }

    public void fetchData(Map<String, String> paramsMap) {
        currentDataPlugin.setup(paramsMap);
        currentDataPlugin.getDataFromParams();
        this.contents = currentDataPlugin.getContents();
    }

    public void setVisualData() {
        for(VisualPlugin vp : currentVisualPlugins) {
            vp.setContents(this.contents);
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LanguageServiceClient createLanguage() {
        try {
            language = LanguageServiceClient.create();
            return language;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Sentiment analyzeSentimentText(String text) {
        // [START language_sentiment_text]
        // Instantiate the Language client com.google.cloud.language.v1.LanguageServiceClient
        Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();
        AnalyzeSentimentResponse response = language.analyzeSentiment(doc);
        Sentiment sentiment = response.getDocumentSentiment();
        if (sentiment == null) {
            System.out.println("No sentiment found");
        }
        return sentiment;
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
        List<Float> scores = new ArrayList<>();
        if (this.contents != null) {
            for(Content c : this.contents) {
                scores.add(c.getScore());
            }
            return scores.toArray(new Float[scores.size()]);
        }
        return new Float[0];
    }

    public Date[] getVisualizedTimeStamps() {
        List<Date> timestamps = new ArrayList<>();
        if (this.contents != null) {
            for(Content c : this.contents) {
                timestamps.add(c.getTimeStamp());
            }
            return timestamps.toArray(new Date[timestamps.size()]);
        }
        return new Date[0];
    }

    public List<Content> getContents() {
        return this.contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }
     public void setLanguage(LanguageServiceClient language) {
        this.language = language;
     }
}
