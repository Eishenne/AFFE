package at.webCrawler;

public class SearchTarget {
    int id;
    String keyword;
    int relevanz;
    int fk_targetid;

    public SearchTarget(int id, String keyword, int relevanz, int fk_targetid) {
        this.id = id;
        this.keyword = keyword;
        this.relevanz = relevanz;
        this.fk_targetid = fk_targetid;
    }

    public int getIdSearch() {
        return id;
    }

    public void setIdSearch(int id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getRelevanz() {
        return relevanz;
    }

    public void setRelevanz(int relevanz) {
        this.relevanz = relevanz;
    }

    public int getFk_targetid() {
        return fk_targetid;
    }

    public void setFk_targetid(int fk_targetid) {
        this.fk_targetid = fk_targetid;
    }
}
