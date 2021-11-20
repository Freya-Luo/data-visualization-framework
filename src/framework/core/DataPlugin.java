package framework.core;


import java.util.List;
import java.util.Map;

public interface DataPlugin {
    void setup(Map<String, String> paramsMap);
    void getDataFromParams();
    String getPluginName();
    List<Content> getContents();
}
