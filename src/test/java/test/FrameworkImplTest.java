package test;

import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.gson.JsonObject;
import com.jzhangdeveloper.newsapi.net.NewsAPI;
import com.jzhangdeveloper.newsapi.net.NewsAPIClient;
import com.jzhangdeveloper.newsapi.net.NewsAPIResponse;
import com.jzhangdeveloper.newsapi.params.EverythingParams;
import edu.cmu.cs.cs214.hw6.framework.core.*;
import edu.cmu.cs.cs214.hw6.plugin.dataplugin.NewsPlugin;
import edu.cmu.cs.cs214.hw6.plugin.dataplugin.TwitterPlugin;
import edu.cmu.cs.cs214.hw6.plugin.visualplugin.BarPlugin;
import edu.cmu.cs.cs214.hw6.plugin.visualplugin.PiePlugin;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import twitter4j.*;

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
        //LanguageServiceClient language1 = mock(LanguageServiceClient.class);
        language = mock(LanguageServiceClient.class);
        try (MockedStatic<LanguageServiceClient> utilities = mockStatic(LanguageServiceClient.class)) {
            utilities.when(LanguageServiceClient::create).thenReturn(language);
        }
        f = new FrameworkImpl();
        try {
            language = LanguageServiceClient.create();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //currentDtaPlugin = mock(List.class);
        currentDtaPlugin = new ArrayList<>();
        DataPlugin twPlugin = mock(TwitterPlugin.class);
        currentDtaPlugin.add(twPlugin);
        currentVisPlugin = new ArrayList<>();
        currentVisPlugin.add(new PiePlugin());
        currentVisPlugin.add(new BarPlugin());
        f.registerDataPlugins(currentDtaPlugin);
        f.registerVisualPlugins(currentVisPlugin);
        doNothing().when(twPlugin).getDataFromParams();

        f.init(currentDtaPlugin.get(0), currentVisPlugin);

    }
    /*@Test
    public void fetchDataTestTwitter() {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("from", "11:13");
        paramsMap.put("to", "16:15");
        paramsMap.put("dataNumber", "5");

        Twitter twitterMock = mock(Twitter.class);
        Paging pageMock = mock(Paging.class);
        Status statusMock = mock(Status.class);
        List<Status> statusesMock = new ArrayList<>();
        statusesMock.add(statusMock);
        try {
            when(twitterMock.getHomeTimeline(pageMock)).thenReturn((ResponseList<Status>) statusesMock);
        }catch (TwitterException e) {
            e.printStackTrace();
        }

        currentDtaPlugin = new ArrayList<>();
        //DataPlugin twPlugin = mock(TwitterPlugin.class);
        DataPlugin twPlugin = new TwitterPlugin();
        currentDtaPlugin.add(twPlugin);
        currentVisPlugin = new ArrayList<>();
        currentVisPlugin.add(new PiePlugin());
        currentVisPlugin.add(new BarPlugin());
        f.registerDataPlugins(currentDtaPlugin);
        f.registerVisualPlugins(currentVisPlugin);
        //doNothing().when(twPlugin).getDataFromParams();
        f.init(currentDtaPlugin.get(0), currentVisPlugin);

        f.fetchData(paramsMap);
        List<Content> contentsMock = new ArrayList<>();
        contentsMock.add(new Content("wwww", new Date("Mon Nov 22 23:45:02 EST 2021")));
        f.setContents(contentsMock);
        List<Content> content = f.getContents();
        assertEquals(content, f.getContents());
    }*/

    @Test
    public void fetchDataTestNews() throws IOException, InterruptedException {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("from", "2021-11-11");
        paramsMap.put("to", "2021-11-13");
        paramsMap.put("sources", "bbc-news");
        Map<String, String> params = EverythingParams.newBuilder()
                .setLanguage("en").setFrom("2021-11-11").setTo("2021-11-13").setSources("bbc-news")
                .build();
        NewsPlugin nwsPlugin = mock(NewsPlugin.class);
        NewsAPI.Builder builderMock = mock(NewsAPI.Builder.class);
        NewsAPIClient newsMock = mock(NewsAPIClient.class);
        NewsAPIResponse newRespMock = mock(NewsAPIResponse.class);
        //DataPlugin nwsPlugin = mock(NewsPlugin.class, withSettings().useConstructor(builderMock, searchService));
        //doNothing().when(nwsPlugin).setBuilder(builderMock);
        //doNothing().when(nwsPlugin).setNewsApiClient(newsMock);
        //doNothing().when(nwsPlugin).setup(paramsMap);
        //doNothing().when(nwsPlugin).getDataFromParams();
        nwsPlugin.setBuilder(builderMock);
        nwsPlugin.setNewsApiClient(newsMock);
        System.out.println("======"+nwsPlugin.getNewsApiClient());

        currentDtaPlugin = new ArrayList<>();
        currentDtaPlugin.add(nwsPlugin);
        currentVisPlugin = new ArrayList<>();
        currentVisPlugin.add(new PiePlugin());
        currentVisPlugin.add(new BarPlugin());
        f.registerDataPlugins(currentDtaPlugin);
        f.registerVisualPlugins(currentVisPlugin);

        when(newsMock.getEverything(params)).thenReturn(newRespMock);
        when(newRespMock.getBodyAsJson()).thenAnswer(
                invocation -> {
                    JsonObject jsonMock = new JsonObject();
                    JsonObject jsonMock2 = new JsonObject();
                    jsonMock2.addProperty("publishedAt", "2021-11-10");
                    jsonMock2.addProperty("description", "ohh");
                    jsonMock.add("articles", jsonMock2);
                    return jsonMock;
                }
        );
        f.init(nwsPlugin, currentVisPlugin);
        f.fetchData(paramsMap);
        List<Content> contentsMock = new ArrayList<>();
        contentsMock.add(new Content("ohh", new Date("Mon Nov 22 23:45:02 EST 2021")));
        //f.setContents(contentsMock);
        List<Content> content = f.getContents();
        verify(nwsPlugin).setup(paramsMap);
        verify(nwsPlugin).getDataFromParams();
        verify(nwsPlugin).setBuilder(builderMock);
        verify(nwsPlugin).setNewsApiClient(newsMock);
        System.out.println("======"+nwsPlugin.getNewsApiClient());
        verify(newsMock).getEverything(params);
        verify(newRespMock).getBodyAsJson();
        verify(nwsPlugin).getContents();
        assertEquals(contentsMock.get(0).getText(), f.getContents().get(0).getText());

    }

    /*@Test
    public void analyzeTest() {
        List<Content> contentsMock = new ArrayList<>();
        contentsMock.add(new Content("wwww", new Date("Mon Nov 22 23:45:02 EST 2021")));
        f.setContents(contentsMock);
        Document doc = mock(Document.class);
        AnalyzeSentimentResponse resp = mock(AnalyzeSentimentResponse.class);
        Sentiment sentimentMock = mock(Sentiment.class);
        when(language.analyzeSentiment(doc)).thenReturn(resp);
        when(resp.getDocumentSentiment()).thenReturn(sentimentMock);
        when(sentimentMock.getScore()).thenReturn(0.9f);
        f.analyze();
        //System.out.println("test:"+f.analyzeSentimentText(contentsMock.get(0).getText()).getScore());
        //System.out.println();
        assertEquals(0.9f, f.getVisualizedScores()[0], 0.0f);
    }*/
}
