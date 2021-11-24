package test;

import com.google.cloud.language.v1.*;
import edu.cmu.cs.cs214.hw6.framework.core.*;
import edu.cmu.cs214.hw6.plugin.dataplugin.NewsPlugin;
import edu.cmu.cs.cs214.hw6.plugin.dataplugin.TwitterPlugin;
import edu.cmu.cs.cs214.hw6.plugin.visualplugin.BarPlugin;
import edu.cmu.cs.cs214.hw6.plugin.visualplugin.PiePlugin;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.Document.Type;
import org.checkerframework.checker.units.qual.C;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;

public class FrameworkImplTest {
    private Framework f;
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
        contentsMock.add(new Content("hhhh", new Date("Mon Nov 22 23:45:02 EST 2021")));
        f.setContents(contentsMock);
        List<Content> content = f.getContents();
        assertEquals(content, f.getContents());
    }
    @Test
    public void analyzeTest() {
        List<Content> contentsMock = new ArrayList<>();
        contentsMock.add(new Content("hhhh", new Date("Mon Nov 22 23:45:02 EST 2021")));
        f.setContents(contentsMock);
        f.analyze();
        AnalyzeSentimentResponse resp = mock(AnalyzeSentimentResponse.class);
        Sentiment sentimentMock = mock(Sentiment.class);
        when(resp.getDocumentSentiment()).thenReturn(sentimentMock);
        when(sentimentMock.getScore()).thenReturn(0.5f);
        f.analyze();
        assertEquals(0.5f, f.getVisualizedScores());
    }
}
