package at.webCrawler.parsers;

import at.webCrawler.DataBaseFunction;
import com.gargoylesoftware.htmlunit.html.DomNode;
import org.w3c.dom.Node;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UrlParser {
    public static void analyzeHyperlinks(URL currentURL, DomNode htmlElement) {
        //Links auslesen
        //-LinkTag finden
        if (htmlElement.getNodeName().equals("a")) {
            for (int i = 0; i < htmlElement.getAttributes().getLength(); i++) {
                Node n = htmlElement.getAttributes().item(i);
                //--Link finden
                if (n.getNodeName().equals("href")) {
                    try {
                        String URLtext = currentURL.toURI().resolve(n.getNodeValue()).toString();
                        int hashmarkPos = URLtext.indexOf("#");
                        if (hashmarkPos > 0){
                            URLtext = URLtext.substring(0, hashmarkPos);
                        }
                        System.out.println("a: " + URLtext);
                        //DB auf vorhandene URL prüfen
                        int targetID = DataBaseFunction.readTargetId(URLtext);
                        if (targetID < 0) {
                            //DB row schreiben wenn URL nicht vorhanden
                            DataBaseFunction.writeTargetUrl(URLtext);
                        }
                    } catch (URISyntaxException use) {
                        System.out.println(use.getMessage());
                    }
                }
            }
        }

        for (DomNode d : htmlElement.getChildren()) {
            analyzeHyperlinks(currentURL, d);
        }
    }
}
