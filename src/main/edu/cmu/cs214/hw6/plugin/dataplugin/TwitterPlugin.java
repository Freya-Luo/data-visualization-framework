package main.edu.cmu.cs214.hw6.plugin.dataplugin;

import main.edu.cmu.cs214.hw6.framework.core.Content;
import main.edu.cmu.cs214.hw6.framework.core.DataPlugin;

import main.edu.cmu.cs214.hw6.framework.gui.State;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TwitterPlugin implements DataPlugin {
    private final String dataPluginName = "Twitter";
    private static final String KEY = "2gtaEsk6BWB16WqFmZn4OrhjL";
    private static final String KEYSECRET = "6boeybAMeIRv6Ve4yc6l8AVqz1DVmrsRkDQwE0Mkz6OTgQpB0j";
    private static final String ACCESSTOKENSECRET = "ovG21pPnN0TEsd2mIH7L7DreBu0eziyGj86g8NtQ6hOCi";
    private static final String ACCESSTOKEN = "1102438736640598018-xNvuL2WBfnUm1Tc6FjSOAUePu2tX9H";
    private static final int MAX_NUMBER = 50;

    private int dataNumber;
    private Date from;
    private Date to;
    private List<Content> pluginData;
    private String msg;

    public TwitterPlugin() {
        this.pluginData = new ArrayList<>();
    }

    public String getPluginName() {
        return this.dataPluginName;
    }

    public void setup(Map<String, String> paramsMap) {
        this.dataNumber = Integer.parseInt(paramsMap.get("dataNumber"));
        //this.dataNumber = 50;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            this.from = format.parse(paramsMap.get("from"));
            this.to = format.parse(paramsMap.get("to"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Twitter getTwitter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(KEY)
                .setOAuthConsumerSecret(KEYSECRET)
                .setOAuthAccessToken(ACCESSTOKEN)
                .setOAuthAccessTokenSecret(ACCESSTOKENSECRET);

        TwitterFactory tf = new TwitterFactory(cb.build());
        return tf.getInstance();
    }
    public  void getDataFromParams() {
        List<Status> statuses = new ArrayList<>();
        int pages = this.dataNumber / MAX_NUMBER;
        int rest = this.dataNumber % MAX_NUMBER;
        Paging p = new Paging(1, MAX_NUMBER);
        Twitter twitter = getTwitter();
        String user = "";

        try {
            for(int i = 0; i < pages; i++) {
                p.setPage(i+1);
                statuses.addAll(twitter.getUserTimeline(user, p));
            }
            p.setPage(pages + 1);
            statuses.addAll(twitter.getUserTimeline(user, p));
            System.out.println("Showing @" + user + "'s user timeline.");
            for (Status status : statuses) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                Date timeStamp = status.getCreatedAt();
                if (timeStamp.after(this.from) && timeStamp.before((this.to))) {
                    this.pluginData.add(toContent(status));
                }
            }

        } catch (TwitterException e) {
            setErrorMsg("Failed to get timeline: " + e.getMessage());
            System.out.println("Failed to get timeline: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /*public void getDataFromParams() {
        // oauth for twitter
        Twitter twitter = getTwitter();
        //Twitter twitter = new TwitterFactory().getInstance();
        try {
            String cursor = null;
            DirectMessageList messages;
            do {
                messages = cursor == null ? twitter.getDirectMessages(this.dataNumber) : twitter.getDirectMessages(this.dataNumber, cursor);
                System.out.println("size:"+messages.size());
                for (DirectMessage message : messages) {
                    System.out.println("From: " + message.getSenderId() + " id:" + message.getId()
                            + " [" + message.getCreatedAt() + "]"
                            + " - " + message.getText());
                    System.out.println("raw[" + message + "]");
                }
                cursor = messages.getNextCursor();
            } while (messages.size() > 0 && cursor != null);
            System.out.println("done.");
            // convert to Content type and filter the time period
            for (DirectMessage message : messages) {
                Date timeStamp = message.getCreatedAt();
                //if (timeStamp.after(this.from) && timeStamp.before((this.to))) {
                    this.pluginData.add(toContent(message));
                //}
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get messages: " + te.getMessage());
            //System.exit(-1);
        }

    }*/

    public Content toContent(Status s) {
        Content c = new Content(s.getText(), s.getCreatedAt());
        return c;
    }

    public List<Content> getContents() {
        return pluginData;
    }

    public void setErrorMsg(String msg) {
        this.msg = msg;
    }

    public String getErrorMsg() {
        return this.msg;
    }
}