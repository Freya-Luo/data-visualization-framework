package main.edu.cmu.cs214.hw6;

import com.github.jknack.handlebars.Template;
import fi.iki.elonen.NanoHTTPD;

import main.edu.cmu.cs214.hw6.framework.core.DataPlugin;
import main.edu.cmu.cs214.hw6.framework.core.FrameworkImpl;
import main.edu.cmu.cs214.hw6.framework.gui.VisualPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class App extends NanoHTTPD {
    public static void main(String[] args) {
        try {
            new App();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    private FrameworkImpl framework;
    private List<DataPlugin> dataPlugins;
    private List<VisualPlugin> visualPlugins;
    private Template template;

    public App() throws IOException {
        super(8080);

        this.framework = new FrameworkImpl();
        dataPlugins = loadDataPlugins();
        visualPlugins = loadVisualPlugins();

    }

    @Override
    public Response serve(IHTTPSession session) {
        try {

            String HTML = this.template.apply("gameplay");
            return newFixedLengthResponse(HTML);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<DataPlugin> loadDataPlugins() {
        ServiceLoader<DataPlugin> dataPlugins = ServiceLoader.load(DataPlugin.class);
        List<DataPlugin> res = new ArrayList<>();
        for (DataPlugin plugin : dataPlugins) {
            System.out.println("Loaded plugin " + plugin.getPluginName());
            res.add(plugin);
        }
        return res;
    }

    private static List<VisualPlugin> loadVisualPlugins() {
        ServiceLoader<VisualPlugin> visualPlugins = ServiceLoader.load(VisualPlugin.class);
        List<VisualPlugin> res = new ArrayList<>();
        for (VisualPlugin plugin : visualPlugins) {
            System.out.println("Loaded plugin " + plugin.getPluginName());
            res.add(plugin);
        }
        return res;
    }
}