import fi.iki.elonen.NanoHTTPD;

import plugin.dataplugin.NewsPlugin.NewsPlugin;

import java.io.IOException;
import java.util.HashMap;

public class App extends NanoHTTPD {
    public static void main(String[] args) {
        NewsPlugin np = new NewsPlugin();
        np.setup(new HashMap<>());
        np.getDataFromParams();

    }

    public App() throws IOException {
        super(8080);
    }
}