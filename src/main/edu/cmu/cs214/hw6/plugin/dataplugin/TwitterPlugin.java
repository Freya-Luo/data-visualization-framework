package main.edu.cmu.cs214.hw6.plugin.dataplugin;

import main.edu.cmu.cs214.hw6.framework.core.Content;
import main.edu.cmu.cs214.hw6.framework.core.DataPlugin;

import main.edu.cmu.cs214.hw6.framework.gui.State;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    private int fromInt;
    private int toInt;
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
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        try {
            this.from = format.parse(paramsMap.get("from"));
            Calendar cal_from = Calendar.getInstance();
            cal_from.setTime(this.from);
            int hour = cal_from.get(Calendar.HOUR);
            System.out.println("hour"+hour);
            int min = cal_from.get(Calendar.MINUTE);
            this.fromInt = hour*100 + min;

            this.to = format.parse(paramsMap.get("to"));
            Calendar cal_to = Calendar.getInstance();
            cal_to.setTime(this.to);
            int hour_to = cal_to.get(Calendar.HOUR);
            int min_to = cal_to.get(Calendar.MINUTE);
            this.toInt = hour_to*100 + min_to;
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
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            for(int i = 0; i < pages; i++) {
                p.setPage(i+1);
                statuses.addAll(twitter.getHomeTimeline( p));
            }
            p.setPage(pages+1);
            statuses.addAll(twitter.getHomeTimeline(p).subList(0, rest));;
            for (Status status : statuses) {
                //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                Date timeStamp = status.getCreatedAt();

                Calendar cal_all = Calendar.getInstance();
                //SimpleDateFormat format_date = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss'Z'");
                cal_all.setTime(timeStamp);
                int hour = cal_all.get(Calendar.HOUR);
                int min = cal_all.get(Calendar.MINUTE);
                int timeStampInt = hour*100 + min;
                System.out.println(timeStampInt);
                System.out.println(this.fromInt);
                System.out.println(this.toInt);
                if ((timeStampInt >= this.fromInt)  && (timeStampInt <= this.toInt)){
                    this.pluginData.add(toContent(status));
                }
            }
            countDownLatch.countDown();
            countDownLatch.await(1L, TimeUnit.SECONDS);
        } catch (TwitterException|InterruptedException e) {
            setErrorMsg("Failed to get timeline: " + e.getMessage());
            e.printStackTrace();
        }
    }

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