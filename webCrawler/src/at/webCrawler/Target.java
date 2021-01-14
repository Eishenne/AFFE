package at.webCrawler;

import java.sql.Timestamp;

public class Target {
    int id;
    String url;
    Timestamp date;
    int nextvisit;

    public Target(int id, String url, Timestamp date, int nextvisit) {
        this.id = id;
        this.url = url;
        this.date = date;
        this.nextvisit = nextvisit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public int getNextvisit() {
        return nextvisit;
    }

    public void setNextvisit(int nextvisit) {
        this.nextvisit = nextvisit;
    }
}
