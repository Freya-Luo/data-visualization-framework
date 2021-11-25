package edu.cmu.cs.cs214.hw6.plugin.dataplugin;

import edu.cmu.cs.cs214.hw6.framework.core.Content;
import edu.cmu.cs.cs214.hw6.framework.core.DataPlugin;
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
    private int fromInt;
    private int toInt;
    private List<Content> pluginData;
    private String msg = "";
    private final String TIME_ERR_MSG = "Please select a valid time range.";
    private final String NUMBER_ERR_MSG = "Please type a valid number.";

    public TwitterPlugin() {
        this.pluginData = new ArrayList<>();
    }

    /**
     * Get the name of data plugin
     */
    public String getPluginName() {
        return this.dataPluginName;
    }

    /**
     * Check if the parameters are valid
     * @return true if number of data is positive number
     * and from date is bigger than to date
     */
    private boolean checkParams() {
        if (dataNumber <= 0) {
            this.msg = NUMBER_ERR_MSG;
            return false;
        }

        if (fromInt > toInt) {
            this.msg = TIME_ERR_MSG;
            return false;
        }
        return true;
    }

    /**
     * Reset the state of data plugin and msg
     */
    public void reset() {
        pluginData.clear();
        this.msg = "";
    }

    /**
     * Set up the data plugin with params from user chosen e.g. number of data
     * @param paramsMap the time period and number of data
     */
    public void setup(Map<String, String> paramsMap) {
        this.msg = "";
        this.pluginData.clear();
        if (paramsMap.get("from").equals("") || paramsMap.get("from").equals("")) {
            this.msg = TIME_ERR_MSG;
            return;
        }
        this.dataNumber = Integer.parseInt(paramsMap.get("dataNumber"));
        this.fromInt = Integer.parseInt(String.join("", paramsMap.get("from").split(":")));
        this.toInt = Integer.parseInt(String.join("", paramsMap.get("to").split(":")));
    }

    /**
     * Get the Twitter class using access token and secret
     */
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

    /**
     * Fetch Twitter data via API
     */
    public  void getDataFromParams() {
        if (!this.msg.equals("") || !checkParams()) return;
        List<Status> statuses = new ArrayList<>();
        int pages = this.dataNumber / MAX_NUMBER;
        int rest = this.dataNumber % MAX_NUMBER;
        Paging p = new Paging(1, MAX_NUMBER);
        Twitter twitter = getTwitter();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            for(int i = 0; i < pages; i++) {
                p.setPage(i+1);
                statuses.addAll(twitter.getHomeTimeline(p));
            }
            p.setPage(pages+1);
            statuses.addAll(twitter.getHomeTimeline(p).subList(0, rest));
            for (Status status : statuses) {
                System.out.println(status.getText());
                Date timeStamp = status.getCreatedAt();
                Calendar cal_all = Calendar.getInstance();
                cal_all.setTime(timeStamp);

                int hour = cal_all.get(Calendar.HOUR_OF_DAY);
                int min = cal_all.get(Calendar.MINUTE);
                int timeStampInt = hour*100 + min;
                if ((timeStampInt >= this.fromInt)  && (timeStampInt <= this.toInt)){
                    this.pluginData.add(toContent(status));
                }
                System.out.println(pluginData.size());
            }
            countDownLatch.countDown();
            countDownLatch.await(1L, TimeUnit.SECONDS);
        } catch (TwitterException|InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert Status to Content class
     * @return Content class
     */
    public Content toContent(Status s) {
        Content c = new Content(s.getText(), s.getCreatedAt());
        return c;
    }

    /**
     * @return List of Content
     */
    public List<Content> getContents() {
        return pluginData;
    }

    /**
     * @return msg
     */
    public String getErrorMsg() {
        return this.msg;
    }
}