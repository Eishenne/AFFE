package at.webCrawler.parsers;

import at.webCrawler.Main;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.w3c.dom.Node;

import java.util.HashMap;

public class KeywordMetaParser {
    public static void analyzeKeywordMetaTag(String currentURL, HtmlPage page, HashMap<String, Integer> keywords) {
        System.out.println(currentURL + page.getUrl() + keywords);

        for (DomNode d : page.getHead().getChildren()) {
            if (d.getNodeName().equals("meta")) {
                boolean keywordsFound = false;
                String contentOfSite = "";

                for (int i = 0; i < d.getAttributes().getLength(); i++) {
                    Node n = d.getAttributes().item(i);
                    //keyword meta-tag finden
                    if (n.getNodeName().equals("name") && n.getNodeValue().equals("keywords")) {
                        keywordsFound = true;
                    }
                    if (n.getNodeName().equals("content")) {
                        contentOfSite = n.getNodeValue();
                    }
                }
                if (keywordsFound) {
                    System.out.println("Keywords: " + contentOfSite);
                    Main.registerKeywords(contentOfSite, 4, keywords);
                }
            }
        }
    }
}
