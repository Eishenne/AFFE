package at.webCrawler;

public class SearchTarget {
    int idSearch;
    String keyword;
    int relevanz;
    int FK_targetid;

    public SearchTarget(int idSearch, String keyword, int relevanz, int FK_targetid) {
        this.idSearch = idSearch;
        this.keyword = keyword;
        this.relevanz = relevanz;
        this.FK_targetid = FK_targetid;
    }

    public int getIdSearch() {
        return idSearch;
    }

    public void setIdSearch(int idSearch) {
        this.idSearch = idSearch;
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

    public int getFK_targetid() {
        return FK_targetid;
    }

    public void setFK_targetid(int FK_targetid) {
        this.FK_targetid = FK_targetid;
    }
}
