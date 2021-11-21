package main.edu.cmu.cs214.hw6.plugin.dataplugin;

import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import main.edu.cmu.cs214.hw6.framework.core.Content;
import main.edu.cmu.cs214.hw6.framework.core.DataPlugin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NewsPlugin implements DataPlugin {
    private final String name = "News";
    private final String language = "en";
    private String from;
    private String to;
    private String sources;
    private List<Content> articles;
    private final NewsApiClient newsApiClient;
    private String msg;

    public NewsPlugin() {
        newsApiClient = new NewsApiClient("6ef5b2367fca4b1b863d2060b68d944a");
        articles = new ArrayList<>();
    }

    public void setup(Map<String, String> paramsMap) {
        from = paramsMap.get("from");
        to = paramsMap.get("to");
        sources = paramsMap.get("sources");
    }

    private EverythingRequest buildQuery() {
        return new EverythingRequest.Builder()
                .language(language)
                .from(from)
                .to(to)
                .sources(sources)
                .build();
    }

    private void collectContentData(ArticleResponse response) throws ParseException {
        for(Article a: response.getArticles()) {
            Date publishedAt =new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss'Z'").parse(a.getPublishedAt());
            articles.add(new Content(a.getDescription(), publishedAt));
        }

        for(Content c: articles) {
            System.out.println(c.getText());
        }
    }


    public void getDataFromParams(){
        newsApiClient.getEverything(
                buildQuery(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                        try{
                            collectContentData(response);
                        }catch (ParseException p) {
                            p.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println(throwable.getMessage());
                    }
                }
        );
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
