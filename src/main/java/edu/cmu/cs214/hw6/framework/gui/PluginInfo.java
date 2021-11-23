package main.java.edu.cmu.cs214.hw6.framework.gui;

public class PluginInfo {
    private final String name;
    private final String link;

    public PluginInfo(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}
