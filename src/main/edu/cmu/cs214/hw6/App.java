package main.edu.cmu.cs214.hw6;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import fi.iki.elonen.NanoHTTPD;

import main.edu.cmu.cs214.hw6.framework.core.DataPlugin;
import main.edu.cmu.cs214.hw6.framework.core.FrameworkImpl;
import main.edu.cmu.cs214.hw6.framework.gui.State;
import main.edu.cmu.cs214.hw6.framework.core.VisualPlugin;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class App extends NanoHTTPD {
    public static void main(String[] args) {
        try {
            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.OFF);
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
        this.framework.registerDataPlugins(dataPlugins);
        this.framework.registerVisualPlugins(visualPlugins);
        Handlebars handlebars = new Handlebars();
        System.out.println("pwd: "+ System.getProperty("user.dir"));
        this.template = handlebars.compile("main");

        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            String uri = session.getUri();
            Map<String, String> params = session.getParms();
            if (uri.equals("/plugin")) {
                this.framework.init(dataPlugins.get(Integer.parseInt(params.get("i"))), visualPlugins);
            } else if (uri.equals("/analyze")){
                if (this.framework.hasDataPlugin()) {
                    this.framework.analyze();
                }
            }
            // Extract the view-specific data from the game and apply it to the template.
            State frameworkState = State.forFramework(this.framework);
            String HTML = this.template.apply(frameworkState);
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