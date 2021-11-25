package test;

import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jzhangdeveloper.newsapi.net.NewsAPI;
import com.jzhangdeveloper.newsapi.net.NewsAPIClient;
import com.jzhangdeveloper.newsapi.net.NewsAPIResponse;
import com.jzhangdeveloper.newsapi.params.EverythingParams;
import edu.cmu.cs.cs214.hw6.framework.core.*;
import edu.cmu.cs.cs214.hw6.plugin.dataplugin.NewsPlugin;
import edu.cmu.cs.cs214.hw6.plugin.visualplugin.BarPlugin;
import edu.cmu.cs.cs214.hw6.plugin.visualplugin.PiePlugin;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;

public class FrameworkImplTest {

    private FrameworkImpl f;
    private List<DataPlugin> currentDtaPlugin;
    private List<VisualPlugin> currentVisPlugin;
    private LanguageServiceClient language;
    @Before
    public void setup() {
        language = mock(LanguageServiceClient.class);
        try (MockedStatic<LanguageServiceClient> utilities = mockStatic(LanguageServiceClient.class)) {
            utilities.when(LanguageServiceClient::create).thenReturn(language);
        }
        f = new FrameworkImpl();
    }

    @Test
    public void fetchDataTestNews() throws IOException, InterruptedException {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("from", "2021-11-11");
        paramsMap.put("to", "2021-11-13");
        paramsMap.put("sources", "bbc-news");
        Map<String, String> params = EverythingParams.newBuilder()
                .setLanguage("en").setFrom("2021-11-11").setTo("2021-11-13").setSources("bbc-news")
                .build();
        NewsPlugin nwsPlugin = new NewsPlugin();
        NewsAPI.Builder builderMock = mock(NewsAPI.Builder.class);
        NewsAPIClient newsMock = mock(NewsAPIClient.class);
        NewsAPIResponse newRespMock = mock(NewsAPIResponse.class);
        nwsPlugin.setBuilder(builderMock);
        nwsPlugin.setNewsApiClient(newsMock);

        currentDtaPlugin = new ArrayList<>();
        currentDtaPlugin.add(nwsPlugin);
        currentVisPlugin = new ArrayList<>();
        currentVisPlugin.add(new PiePlugin());
        currentVisPlugin.add(new BarPlugin());
        f.registerDataPlugins(currentDtaPlugin);
        f.registerVisualPlugins(currentVisPlugin);

        when(newsMock.getEverything(any(Map.class))).thenReturn(newRespMock);
        when(newRespMock.getBodyAsJson()).thenAnswer(
                invocation -> {
                    JsonObject jsonMock = new JsonObject();
                    JsonObject jsonMock2 = new JsonObject();
                    jsonMock2.addProperty("publishedAt", "2021-11-10T19:23:00Z");
                    jsonMock2.addProperty("description", "ohh");
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(jsonMock2);
                    jsonMock.add("articles", jsonArray);
                    return jsonMock;
                }
        );
        f.initDataPlugin(nwsPlugin);
        f.initVisualPlugin(currentVisPlugin);
        f.fetchData(paramsMap);
        List<Content> contentsMock = new ArrayList<>();
        contentsMock.add(new Content("ohh", new Date("Mon Nov 22 23:45:02 EST 2021")));
        List<Content> content = f.getContents();
        verify(newsMock).getEverything(params);
        verify(newRespMock).getBodyAsJson();
        assertEquals(contentsMock.get(0).getText(), f.getContents().get(0).getText());

    }

    @Test
    public void analyzeTest() {
        List<Content> contentsMock = new ArrayList<>();
        contentsMock.add(new Content("im so happy", new Date("Mon Nov 22 23:45:02 EST 2021")));
        f.setContents(contentsMock);

        AnalyzeSentimentResponse resp = mock(AnalyzeSentimentResponse.class);
        Sentiment sentimentMock = mock(Sentiment.class);
        when(language.analyzeSentiment(any(Document.class))).thenReturn(resp);
        when(resp.getDocumentSentiment()).thenReturn(sentimentMock);
        when(sentimentMock.getScore()).thenReturn(0.6f);

        currentDtaPlugin = new ArrayList<>();
        NewsPlugin nwsPlugin = new NewsPlugin();
        currentDtaPlugin.add(nwsPlugin);
        currentVisPlugin = new ArrayList<>();
        currentVisPlugin.add(new PiePlugin());
        currentVisPlugin.add(new BarPlugin());
        f.registerDataPlugins(currentDtaPlugin);
        f.registerVisualPlugins(currentVisPlugin);
        f.initDataPlugin(nwsPlugin);
        f.initVisualPlugin(currentVisPlugin);
        //f.init(nwsPlugin, currentVisPlugin);
        f.setLanguage(language);
        f.analyze();
        verify(language).analyzeSentiment(any(Document.class));
        verify(resp).getDocumentSentiment();
        verify(sentimentMock).getScore();

        assertEquals(0.6f, f.getVisualizedScores()[0], 0.0f);
    }
}
