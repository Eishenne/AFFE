package at.webCrawler;

import java.util.ArrayList;

public class CrawlerBehaviour {
    private int delay;
    private ArrayList<String> siteBlacklist;


    public CrawlerBehaviour(int delay, ArrayList<String> siteBlacklist) {
        this.delay = delay;
        this.siteBlacklist = siteBlacklist;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public ArrayList<String> getSiteBlacklist() {
        return siteBlacklist;
    }

    public void setSiteBlacklist(ArrayList<String> siteBlacklist) {
        this.siteBlacklist = siteBlacklist;
    }
}
