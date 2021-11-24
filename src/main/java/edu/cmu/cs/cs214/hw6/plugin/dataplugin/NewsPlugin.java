package edu.cmu.cs.cs214.hw6.plugin.dataplugin;

import com.google.gson.JsonObject;
import com.jzhangdeveloper.newsapi.models.Everything;
import com.jzhangdeveloper.newsapi.net.NewsAPI;
import com.jzhangdeveloper.newsapi.net.NewsAPIClient;
import com.jzhangdeveloper.newsapi.net.NewsAPIResponse;
import com.jzhangdeveloper.newsapi.params.EverythingParams;
import edu.cmu.cs.cs214.hw6.framework.core.Content;
import edu.cmu.cs.cs214.hw6.framework.core.DataPlugin;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
    private String msg;

    public NewsPlugin() {
        builder = NewsAPI.newClientBuilder();
        newsApiClient = builder.setApiKey("6ef5b2367fca4b1b863d2060b68d944a").build();
        articles = new ArrayList<>();
    }

    public void setup(Map<String, String> paramsMap) {
        from = paramsMap.get("from");
        to = paramsMap.get("to");
        sources = paramsMap.get("sources");
        //System.out.println(sources);
    }
    //private EverythingRequest buildQuery() {
//        return new EverythingRequest.Builder()
//                .language(language)
//                .from(from)
//                .to(to)
//                .sources(sources)
//                .build();
 //   }

//    private void collectContentData(ArticleResponse response) throws ParseException {
//        for(Article a: response.getArticles()) {
//            Date publishedAt =new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss'Z'").parse(a.getPublishedAt());
//            articles.add(new Content(a.getDescription(), publishedAt));
//        }
//
//        for(Content c: articles) {
//            System.out.println(c.getText());
//        }
//    }


    public void getDataFromParams(){
        //CountDownLatch countDownLatch = new CountDownLatch(1);
//        Map<String, String> params = EverythingParams.newBuilder()
//                .setLanguage(language).setFrom(from).setTo(to)
//                .build();
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        try {
//            NewsAPIResponse response  = newsApiClient.getEverything(params);
//            Everything everything = response.getBody();
////            JsonObject obj = response.getBodyAsJson();
//            System.out.println(everything.count() + " " + everything.getStatus());
//            countDownLatch.countDown();
//            countDownLatch.await(1L, TimeUnit.SECONDS);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }

//                buildQuery(),
//                new NewsApiClient.ArticlesResponseCallback() {
//                    @Override
//                    public void onSuccess(ArticleResponse response) {
//                        try{
//                            collectContentData(response);
//                            countDownLatch.countDown();
//                        }catch (ParseException p) {
//                            p.printStackTrace();
//                        }
//                    }
//                    @Override
//                    public void onFailure(Throwable throwable) {
//                        System.out.println(throwable.getMessage());
//                    }
//                }
       // );
//        try {
//            countDownLatch.await(1L, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public String getPluginName() {
        return this.name;
    }

    public List<Content> getContents() {
        return articles;
    }

    public void setErrorMsg(String msg) {
        this.msg = msg;
    }

    public String getErrorMsg() {
        return this.msg;
    }
}
