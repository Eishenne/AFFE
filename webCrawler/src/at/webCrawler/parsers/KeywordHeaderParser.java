package at.webCrawler.parsers;

import at.webCrawler.Main;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.HashMap;

public class KeywordHeaderParser {
    public static void analyzeKeywordHeaderTag(String currentURL, HtmlPage page, HashMap<String, Integer> keywords) {
        System.out.println(currentURL + page.getUrl() + keywords);
        analyzeElementForHeadlineTag(page.getBody(), keywords);
    }

    private static void analyzeElementForHeadlineTag(DomNode htmlElement, HashMap<String, Integer> keywords){
        String nodeName = htmlElement.getNodeName();
        if (nodeName.equals("h1")){
            System.out.println("analyzeElementForHeadlineTag " + nodeName + " \"" + htmlElement.getTextContent() + "\"");
            Main.registerKeywords(htmlElement.getTextContent(), 3, keywords);
        }
        if (nodeName.equals("h2")){
            System.out.println("analyzeElementForHeadlineTag " + nodeName + " \"" + htmlElement.getTextContent() + "\"");
            Main.registerKeywords(htmlElement.getTextContent(), 2, keywords);
        }
        if (nodeName.equals("h3")){
            System.out.println("analyzeElementForHeadlineTag " + nodeName + " \"" + htmlElement.getTextContent() + "\"");
            Main.registerKeywords(htmlElement.getTextContent(), 1, keywords);
        }

        for (DomNode d: htmlElement.getChildren()) {
            analyzeElementForHeadlineTag(d, keywords);
        }
    }
}