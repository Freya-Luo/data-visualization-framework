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
    private String frameworkName;
    private String instruction;
    private int stage;

    private final String FRAMEWORK_BASE_NAME = "Text Sentiment Analysis Framework";
    private final String PLUGIN_BASE_NAME = "Text Sentiment Analysis of ";
    private final String EMPTY_DATA_RETURNED = "No data in this time range, please reselect parameters.";

    public FrameworkImpl() {
        this.stage = 0;
        this.instruction = "";
        this.dataPlugins = new ArrayList<>();
        this.visualPlugins = new ArrayList<>();
        this.currentVisualPlugins = new ArrayList<>();
        this.contents = new ArrayList<>();
        this.frameworkName = FRAMEWORK_BASE_NAME;
    }

    /**
     * Add data plugins to the list dataPlugins
     * @param dataPlugins
     */
    public void registerDataPlugins(List<DataPlugin> dataPlugins) {
        this.dataPlugins.addAll(dataPlugins);
    }

    public List<DataPlugin> getRegisteredDataPlugins() {
        return this.dataPlugins;
    }

    /**
     * Add visual plugins to the list visualPlugins
     * @param visualPlugins
     */
    public void registerVisualPlugins(List<VisualPlugin> visualPlugins) {
        this.visualPlugins.addAll(visualPlugins);
    }

    /**
     * Reset the state of framework and clear the list
     */
    public void reset() {
        this.stage = 0;
        this.instruction = "";
        this.currentVisualPlugins.clear();
        this.currentDataPlugin = null;
        this.contents.clear();
        this.frameworkName = FRAMEWORK_BASE_NAME;
        for(DataPlugin dp : dataPlugins) {
            dp.reset();
        }
    }

    /**
     * Init currentDataPlugin
     */
    public void initDataPlugin(DataPlugin dp) {
        if (currentDataPlugin != dp) {
            currentDataPlugin = dp;
        }
        if (language == null) {
            language = createLanguage();
        }
        this.stage = 1;
    }

    /**
     * Init currentVisualPlugins
     */
    public void initVisualPlugin(List<VisualPlugin> vps) {
        currentVisualPlugins.clear();
        currentVisualPlugins.addAll(vps);

        if (language == null) {
            language = createLanguage();
        }
        this.stage = 2;
    }

    public void init(DataPlugin dp, List<VisualPlugin> vps) {
        if (currentDataPlugin == null || currentDataPlugin != dp) {
            currentDataPlugin = dp;
        }
        currentVisualPlugins.clear();
        currentVisualPlugins.addAll(vps);
        language = createLanguage();
        this.frameworkName = PLUGIN_BASE_NAME + currentDataPlugin.getPluginName();
        this.stage = 1;
    }

    /**
     * Fetch the data with parameters
     * @param paramsMap
     */
    public void fetchData(Map<String, String> paramsMap) {
        this.instruction = "";
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
            if (this.currentDataPlugin.getErrorMsg().equals("")) {
                this.instruction = EMPTY_DATA_RETURNED;
            }
            return;
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

    /**
     * Create Google NLP LanguageServiceClient
     * @return
     */
    public LanguageServiceClient createLanguage() {
        try {
            language = LanguageServiceClient.create();
            return language;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Analyze the sentiment using Google NLP
     * @return Sentiment with scores
     */
    public Sentiment analyzeSentimentText(String text) {
        // [START language_sentiment_text]
        // Instantiate the Language client com.google.cloud.language.v1.LanguageServiceClient
        Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();
        AnalyzeSentimentResponse response = language.analyzeSentiment(doc);
        Sentiment sentiment = response.getDocumentSentiment();
        return sentiment;
    }

    public String getFrameworkName() {
        return this.frameworkName;
    }

    /**
     * @return true if the current data plugin exists
     */
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

    public String[] getCurrentVisualPluginNames() {
        List<String> res = new ArrayList<>();
        for(VisualPlugin vp : currentVisualPlugins) {
            res.add(vp.getPluginName());
            System.out.println(vp.getPluginName() + "ll");
        }
        return res.toArray(new String[0]);
    }

    public String getInfo() {
        if (this.currentDataPlugin != null){
            if (!this.currentDataPlugin.getErrorMsg().equals("")) {
                this.instruction = this.currentDataPlugin.getErrorMsg();
            }
        }
        return this.instruction;
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
