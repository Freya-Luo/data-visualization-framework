package test;

import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import edu.cmu.cs.cs214.hw6.framework.core.*;
import edu.cmu.cs.cs214.hw6.plugin.dataplugin.TwitterPlugin;
import edu.cmu.cs.cs214.hw6.plugin.visualplugin.BarPlugin;
import edu.cmu.cs.cs214.hw6.plugin.visualplugin.PiePlugin;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.mockito.Mock;
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
    @Test
    public void fetchDataTest() {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("from", "01:24");
        paramsMap.put("to", "13:24");
        paramsMap.put("dataNumber", "5");

        f.fetchData(paramsMap);
        List<Content> contentsMock = new ArrayList<>();
        contentsMock.add(new Content("wwww", new Date("Mon Nov 22 23:45:02 EST 2021")));
        f.setContents(contentsMock);
        List<Content> content = f.getContents();
        assertEquals(content, f.getContents());
    }
    @Test
    public void analyzeTest() {
        List<Content> contentsMock = new ArrayList<>();
        contentsMock.add(new Content("wwww", new Date("Mon Nov 22 23:45:02 EST 2021")));
        f.setContents(contentsMock);
        AnalyzeSentimentResponse resp = mock(AnalyzeSentimentResponse.class);
        Sentiment sentimentMock = mock(Sentiment.class);
        when(resp.getDocumentSentiment()).thenReturn(sentimentMock);
        when(sentimentMock.getScore()).thenReturn(0.3f);
        f.analyze();
        //System.out.println("test:"+f.analyzeSentimentText(contentsMock.get(0).getText()).getScore());
        //System.out.println();
        assertEquals(0.3f, f.getVisualizedScores()[0], 0.0f);
    }
}
