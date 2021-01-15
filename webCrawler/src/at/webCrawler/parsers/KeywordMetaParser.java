package at.webCrawler.parsers;

import at.webCrawler.DataBaseFunction;
import at.webCrawler.Main;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.w3c.dom.Node;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

public class KeywordMetaParser {
    public static void analyzeKeywordMetaTag(String currentURL, HtmlPage page, HashMap<String, Integer> keywords) {
        // TODO: 12.01.2021 Search for page keywords and add as a keyword
        System.out.println(currentURL + page.getUrl() + keywords);

        for (DomNode d : page.getHead().getChildren()) {
            if (d.getNodeName().equals("meta")) {
                boolean keywordsFound = false;
                String contentOfSite = "";

                for (int i = 0; i < d.getAttributes().getLength(); i++) {
                    Node n = d.getAttributes().item(i);
                    //--Link finden
                    if (n.getNodeName().equals("name") && n.getNodeValue().equals("keywords")) {
                        keywordsFound = true;
                    }
                    if (n.getNodeName().equals("content")) {
                        contentOfSite = n.getNodeValue();
                    }
                }
                if (keywordsFound){
                    System.out.println("Keywords: " + contentOfSite);
                    Main.registerKeywords(contentOfSite,4, keywords);
                }
            }
        }
    }

    public static void printDomTree(String prefix, DomNode htmlElement) {
        System.out.println(prefix + htmlElement.getNodeName());
        for (DomNode d : htmlElement.getChildren()) {
            printDomTree(prefix + "  ", d);
        }
    }
/**
 * a href und h2 aus der Seite auslesen (REKURSIV)
 *
 * @param htmlElement contains all Elements from a specific part of a html website
 * @param baseUrl     is the URL where the htmlElements are from
 */
    public static void printResultHeader(DomNode htmlElement, URL baseUrl) {
        //überschriften auslesen
        if (htmlElement.getNodeName().equals("h2")) {
            System.out.println("h2:" + htmlElement.getTextContent());
        }

        //Links auslesen
        //-LinkTag finden
        if (htmlElement.getNodeName().equals("a")) {
            for (int i = 0; i < htmlElement.getAttributes().getLength(); i++) {
                Node n = htmlElement.getAttributes().item(i);
                //--Link finden
                if (n.getNodeName().equals("href")) {
                    try {
                        System.out.println("a: " + baseUrl.toURI().resolve(n.getNodeValue()));
                        //DB auf vorhandene URL prüfen
                        if (DataBaseFunction.newTarget(baseUrl.toURI().resolve(n.getNodeValue()).toString())) {
                            //DB row schreiben wenn URL nicht vorhanden
                            DataBaseFunction.writeTargetToTargetlist(baseUrl.toURI().resolve(n.getNodeValue()).toString());
                        }
                    } catch (URISyntaxException use) {
                        System.out.println(use.getMessage());
                    }
                }
            }
        }

        for (DomNode d : htmlElement.getChildren()) {
            printResultHeader(d, baseUrl);
        }
    }
}