package edu.cmu.cs.cs214.hw6.framework.core;

import com.jzhangdeveloper.newsapi.net.NewsAPI;
import com.jzhangdeveloper.newsapi.net.NewsAPIClient;

import java.util.List;
import java.util.Map;

public interface DataPlugin {
    void setup(Map<String, String> paramsMap);
    void getDataFromParams();
    String getPluginName();
    List<Content> getContents();
    String getErrorMsg();
}
