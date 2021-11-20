package framework.core;
import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.AnalyzeEntitySentimentRequest;
import com.google.cloud.language.v1.AnalyzeEntitySentimentResponse;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.AnalyzeSyntaxRequest;
import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import com.google.cloud.language.v1.ClassificationCategory;
import com.google.cloud.language.v1.ClassifyTextRequest;
import com.google.cloud.language.v1.ClassifyTextResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.EntityMention;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.Token;
import framework.gui.VisualPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AnalyzeSentiment implements Framework{
    private LanguageServiceClient language;
    private List<Content> contents;
    private List<DataPlugin> dataPlugins;
    private List<VisualPlugin> visualPlugins;
    private DataPlugin currentDataPlugin;
    private List<VisualPlugin> currentVisualPlugins;

    public AnalyzeSentiment(List<Content> initialContents) throws IOException {
        language = LanguageServiceClient.create();
        this.contents = new ArrayList<>();
        this.contents.addAll(initialContents);

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
