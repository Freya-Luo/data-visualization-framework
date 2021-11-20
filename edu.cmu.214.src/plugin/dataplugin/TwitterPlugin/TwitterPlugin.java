package plugin.dataplugin.TwitterPlugin;

import framework.core.Content;
import plugin.dataplugin.DataPlugin;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.DirectMessage;
import twitter4j.DirectMessageList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TwitterPlugin implements DataPlugin {
    private static final String dataPluginName = "Twitter";
    private String userName;
    private int dataNumber;
    private Date from;
    private Date to;
    private List<Content> pluginData;
    private static final int MAX_NUMBER = 30;

    public TwitterPlugin() {
        this.pluginData = new ArrayList<>();
    }
    @Override
    public String getPluginName() {
        return this.dataPluginName;
    }
    @Override
    public void setup(Map<String, String> paramsMap) throws ParseException {
        this.userName = paramsMap.get("userName");
        this.dataNumber = Integer.parseInt(paramsMap.get("dataNumber"));
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        this.from = format.parse(paramsMap.get("from"));
        this.to = format.parse(paramsMap.get("to"));
    }

    @Override
    public void getDataFromParams() {
        Twitter twitter = new TwitterFactory().getInstance();
        try {
            String cursor = null;
            DirectMessageList messages;
            do {
                System.out.println("* cursor:" + cursor);
                messages = cursor == null ? twitter.getDirectMessages(this.dataNumber) : twitter.getDirectMessages(this.dataNumber, cursor);
                for (DirectMessage message : messages) {
                    System.out.println("From: " + message.getSenderId() + " id:" + message.getId()
                            + " [" + message.getCreatedAt() + "]"
                            + " - " + message.getText());
                    System.out.println("raw[" + message + "]");
                }
                cursor = messages.getNextCursor();
            } while (messages.size() > 0 && cursor != null);
            System.out.println("done.");
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get messages: " + te.getMessage());
            //System.exit(-1);
        }
        // convert to Content type and filter the time period
        for (DirectMessage message : messages) {
            Date timeStamp = message.getCreatedAt();
            if (timeStamp.after(this.from) && timeStamp.before((this.to))) {
                this.pluginData.add(toContent(message));
            }
        }
    }

    public Content toContent(DirectMessage message) {
        Content c = new Content(message.getText(), message.getCreatedAt());
        return c;
    }
    @Override
    public void parseData() {
        //
        return;
    }

    public List<Content> getPluginData() {
        return pluginData;
    }
}
