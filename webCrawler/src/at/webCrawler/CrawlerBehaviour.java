package at.webCrawler;

import java.util.ArrayList;

public class CrawlerBehaviour {
    private int delay;
    private ArrayList<String> SiteBlacklist;


    public CrawlerBehaviour(int delay, ArrayList<String> siteBlacklist) {
        this.delay = delay;
        SiteBlacklist = siteBlacklist;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public ArrayList<String> getSiteBlacklist() {
        return SiteBlacklist;
    }

    public void setSiteBlacklist(ArrayList<String> siteBlacklist) {
        SiteBlacklist = siteBlacklist;
    }
}
