package framework.core;

import java.util.Date;

public class Content {
    private String text;
    private Date timeStamp;
    private Float score;

    public Content(String text, Date timeStamp, String location) {
        this.text = text;
        this.timeStamp = timeStamp;
        this.score = 0f;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float newScore) {
        this.score = newScore;
    }
    public String getText() {
        return text;
    }
}