package plugin.dataplugin;

import framework.plugins.Plugin;

import java.text.ParseException;
import java.util.Map;

public interface DataPlugin extends Plugin {
    public void setup(Map<String, String> paramsMap) throws ParseException;
    public void getDataFromParams();
    public void parseData();
}
