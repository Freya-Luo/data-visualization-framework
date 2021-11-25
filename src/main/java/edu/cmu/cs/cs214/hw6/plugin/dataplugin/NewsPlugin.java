package edu.cmu.cs.cs214.hw6.plugin.dataplugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jzhangdeveloper.newsapi.net.NewsAPI;
import com.jzhangdeveloper.newsapi.net.NewsAPIClient;
import com.jzhangdeveloper.newsapi.params.EverythingParams;
import edu.cmu.cs.cs214.hw6.framework.core.Content;
import edu.cmu.cs.cs214.hw6.framework.core.DataPlugin;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NewsPlugin implements DataPlugin {
    private final String name = "News";
    private final String language = "en";
    private String from;
    private String to;
    private String sources;
    private List<Content> articles;
    private NewsAPI.Builder builder;
    private NewsAPIClient newsApiClient;
    private String msg = "";
    private final String DATE_ERR_MSG = "Please provide a valid date range.";

    public NewsPlugin() {
        builder = NewsAPI.newClientBuilder();
        newsApiClient = builder.setApiKey("6ef5b2367fca4b1b863d2060b68d944a").build();
        articles = new ArrayList<>();
    }

    /**
     * Reset the state of data plugin and msg
     */
    public void reset() {
        articles.clear();
        this.msg = "";
    }

    /**
     * Set up the data plugin with params from user chosen e.g. bbc-news
     * @param paramsMap the time period and sources of data
     */
    public void setup(Map<String, String> paramsMap) {
        from = paramsMap.get("from");
        to = paramsMap.get("to");
        sources = paramsMap.get("sources");
        articles.clear();
        this.msg = "";
    }

    /**
     * Check if the parameters are valid
     * @return true if date is not null
     * and from date is bigger than to date
     */
    private boolean checkParams() {
        if (from.equals("") || to.equals("")) {
            this.msg = DATE_ERR_MSG;
            return false;
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = format.parse(from);
            Date date2 = format.parse(to);

            if (date1.compareTo(date2) > 0) {
                this.msg = DATE_ERR_MSG;
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Fetch News data via API
     */
    public void getDataFromParams(){
        if (!checkParams()) return;
        Map<String, String> params = EverythingParams.newBuilder()
                .setLanguage(language).setFrom(from).setTo(to).setSources(sources)
                .build();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            JsonObject response  = newsApiClient.getEverything(params).getBodyAsJson();
            JsonArray data = response.get("articles").getAsJsonArray();
            for(JsonElement a: data){
                JsonObject aObj = a.getAsJsonObject();
                String pubAt = aObj.get("publishedAt").getAsString();
                String des = aObj.get("description").getAsString();
                Date publishedAt =new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss'Z'").parse(pubAt);
                articles.add(new Content(des, publishedAt));
            }
            countDownLatch.countDown();
            countDownLatch.await(1L, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }


    public String getPluginName() {
        return this.name;
    }

    public List<Content> getContents() {
        return articles;
    }

    public String getErrorMsg() {
        return this.msg;
    }

    public void setNewsApiClient(NewsAPIClient newsApiClient) {
        this.newsApiClient = newsApiClient;
    }

    public void setBuilder(NewsAPI.Builder build) {
        this.builder = build;
    }

    public NewsAPIClient getNewsApiClient() {
        return this.newsApiClient;
    }
}
