package main.edu.cmu.cs214.hw6.framework.core;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import main.edu.cmu.cs214.hw6.framework.gui.VisualPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FrameworkImpl implements Framework{
    private LanguageServiceClient language;
    private List<Content> contents;
    private List<DataPlugin> dataPlugins;
    private List<VisualPlugin> visualPlugins;
    private DataPlugin currentDataPlugin;
    private List<VisualPlugin> currentVisualPlugins;

    public FrameworkImpl() throws IOException {
        language = LanguageServiceClient.create();
        this.dataPlugins = new ArrayList<>();
        this.visualPlugins = new ArrayList<>();
    }

    public void registerDataPlugins(DataPlugin dataPlugins) {
        this.dataPlugins.add(dataPlugins);
    }

    public void registerVisualPlugins(List<VisualPlugin> visualPlugins) {
        this.visualPlugins.addAll(visualPlugins);
    }

    public void init(DataPlugin dp, List<VisualPlugin> vps) {
        if (currentDataPlugin != dp) {
            currentDataPlugin = dp;
        }
        this.contents = dp.getContents();

        currentVisualPlugins.clear();
        currentVisualPlugins.addAll(vps);
    }

    public void analyze() {
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

    public Sentiment analyzeSentimentText(String text) throws Exception {
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
}
