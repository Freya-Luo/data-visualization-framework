package plugin.visualplugin;

import framework.plugins.Plugin;

import java.util.Date;
import java.util.List;

public interface VisualPlugin extends Plugin {
    public void display(List<Date> timestamp, List<Double> scores);
}
