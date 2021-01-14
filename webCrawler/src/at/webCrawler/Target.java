package at.webCrawler;

import java.sql.Timestamp;

public class Target {
    int targetId;
    String targetUrl;
    Timestamp targetDate;
    int targetNextvisit;
    String targetTitle;
    String targetDescription;

    public Target(int targetId, String targetUrl, Timestamp targetDate, int targetNextvisit, String targetTitle, String targetDescription) {
        this.targetId = targetId;
        this.targetUrl = targetUrl;
        this.targetDate = targetDate;
        this.targetNextvisit = targetNextvisit;
        this.targetTitle = targetTitle;
        this.targetDescription = targetDescription;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public Timestamp getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Timestamp targetDate) {
        this.targetDate = targetDate;
    }

    public int getTargetNextvisit() {
        return targetNextvisit;
    }

    public void setTargetNextvisit(int targetNextvisit) {
        this.targetNextvisit = targetNextvisit;
    }

    public String getTargetTitle() {
        return targetTitle;
    }

    public void setTargetTitle(String targetTitle) {
        this.targetTitle = targetTitle;
    }

    public String getTargetDescription() {
        return targetDescription;
    }

    public void setTargetDescription(String targetDescription) {
        this.targetDescription = targetDescription;
    }
}