package at.webCrawler;

import at.webCrawler.parsers.KeywordMetaParser;
import at.webCrawler.parsers.UrlParser;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class Main {
    /*
    Ablauf Roadmap
        *** Start Hauptprogramm
        eine URL wird dem Programm gegeben oder in ersten Satz der DB geschrieben
        crawler holt satz von SQL-DB.target (id)
        wenn Datum nicht vorhanden oder (DatumMM + nextvisit) < currentDate
        *** Start Website auswerten
        Website holen
        Daten für vollständigen Satz erstellen
        -URL holen
        -title holen
        -description holen
        -keywords holen
        -relevanz holen
        *** Start Daten in DB ablegen
        DB.target - URL Satz löschen
        DB.target - vollständigen Satz schreiben
        DB.searchresult - vollständigen Satz zu jedem keyword ablegen
        *** Start Programmende
        DB Verbindung schliessen wenn noch vorhanden


    Ablauf real
        *** Start Vorbereitung
        browser erzeugen
        eine URL wird dem Programm gegeben oder in ersten Satz der DB geschrieben

        *** Start Hauptprogramm
        crawler holt satz von SQL-DB.target (id)

        wenn Datum nicht vorhanden oder (DatumMM + nextvisit) < currentDate
        *** Start Website auswerten
        Website holen
        Daten für vollständigen Satz erstellen
        -URL holen
        -title holen
        -description holen
        -keywords holen
        -relevanz holen
        *** Start Daten in DB ablegen
        DB.target - vollständigen Satz schreiben
        DB.searchresult - vollständigen Satz zu jedem keyword ablegen
        *** Start Programmende
        DB Verbindung schliessen wenn noch vorhanden
     */

    public static void main(String[] args) throws IOException {
        WebClient webClient = new WebClient();
        boolean stop = false;
        while (!stop) {
            String nextURL = readDB_nextTarget();

            //browser erzeugen
            webClient.getCache().clear();
            HtmlPage page = webClient.getPage(nextURL);

            analyzePage(nextURL, page);

            // TODO: 12.01.2021 Define a practical Exit statement
            stop = true;
        }
    }

    public static String readDB_nextTarget() {
        String targetUrl = "https://htmlunit.sourceforge.io/gettingStarted.html";
        //String targetUrl = "https://www.laendlejob.at";
        //targetUrl = DataBaseFunction.readDbNextTarget();
        // TODO: 13.01.2021 url aus DB nicht aufrufbar / unterschiedliches Format -> Format anpassen


        // TODO: 12.01.2021 Read DB for the next URL
        return targetUrl;
    }

    public static int getTargetId(String currentURL) {
        int targetID = DataBaseFunction.readTargetId(currentURL);
        return targetID;
    }


    public static void analyzePage(String currentURL, HtmlPage page) {
        HashMap<String, Integer> keywords = new HashMap<>();

        int targetID = getTargetId(currentURL);
        UrlParser.analyzeHyperlinks(page.getBaseURL(), page.getBody());
        analyzeDescription(page.getBaseURL(), page.getHead(), targetID);
        analyzePageTitle(targetID, page.getBaseURL(), page.getHead(), keywords);
        KeywordMetaParser.analyzeKeywordMetaTag(currentURL, page, keywords);
        // TODO: 12.01.2021 Weitere Analyze for keyword generation
        clearAndRegisterKeywords(targetID, keywords);
        updateTargetNextVisit(targetID);
    }

    /**
     * erweitert keywords-Liste aus dem inhalt des title-tags. Es gibt 2 Formen des Title-Tags.
     * Form 1: <title> content </title>
     * Form 2: <meta name"title" content="content"> TODO: funktionalität herstellen
     *
     * @param currentURL  URL
     * @param htmlElement DomNode
     * @param keywords    HashMap<String, Integer>
     */
    public static void analyzePageTitle(int targetId, URL currentURL, DomNode htmlElement, HashMap<String, Integer> keywords) {
        //Html - <head> aus page lesen
        //-DomNode d ist eine Liste aller HtmlTags auf page
        //-die Children sind untergeordnete HtmlTags (zB.: meta, title, link)
        //Form 1
        for (DomNode d : htmlElement.getChildren()) {
            if ((d.getLocalName() != null) && (d.getLocalName().equals("title"))) {
                //--schreibt den Inhalt des HtmlTag
                System.out.println("Title: " + d.getTextContent());
                //Titel zu DB.target hinzufügen
                // TODO: methode Titel zu DB.target hinzufügen
                DataBaseFunction.writeDB_titel(d.getTextContent(), targetId);

                //Titel in Keywörter aufteilen
                registerKeywords(d.getTextContent(), 5, keywords);

            }
        }
        /*
        //Form 2
        if (htmlElement.getNodeName().equals("meta")) {
            for (int i = 0; i < htmlElement.getAttributes().getLength(); i++) {
                Node n = htmlElement.getAttributes().item(i);
                //--Link finden
                if (n.getNodeName().equals("name")) {
                    if (n.getNodeValue().equals("title")) {
                        if (n.getNodeName().equals("content")) {
                            try {
                                String stringText = currentURL.toURI().resolve(n.getNodeValue()).toString();
                                System.out.println("title: " + stringText);
                                //DB auf vorhandene URL prüfen
                                int targetID = DataBaseFunction.readTargetId(stringText);
                                if (targetID < 0) {
                                    //DB row schreiben wenn URL nicht vorhanden
                                    DataBaseFunction.writeTargetUrl(stringText);
                                }
                            } catch (URISyntaxException use) {
                                System.out.println(use.getMessage());
                            }
                        }
                    }
                }
            }
        }

        for (DomNode d : htmlElement.getChildren()) {
            analyzePageTitle(currentURL, htmlElement, keywords);;
        }

         */
    }



    public static void analyzeDescription(URL currentURL, DomNode htmlElement, int targetId) {
        // TODO: 12.01.2021 Search for description (MetaTag) and add to DB.target
        /*if (htmlElement.getNodeName().equals("meta")) {
            for (int i = 0; i < htmlElement.getAttributes().getLength(); i++) {
                Node n = htmlElement.getAttributes().item(i);
                //--Link finden
                if ((n.getNodeName().equals("name")) && (n.getNodeValue().toLowerCase().equals("description"))) {

                    // TODO: getNodeValue von Node welche auf name="description" folgt und content heisst

                    String descriptionText = htmlElement.getAttributes().item(i + 1).getNodeValue();
                    //--schreibt den Inhalt des HtmlTag
                    System.out.println("Description: " + descriptionText);
                    //Titel zu DB.target hinzufügen
                    //DataBaseFunction.writeDB_Description(descriptionText, targetId);

                    //DB auf vorhandene URL prüfen

                }
            }
        }*/
        if (htmlElement.getNodeName().equals("meta")) {
            for (int i = 0; i < htmlElement.getAttributes().getLength(); i++) {
                Node n = htmlElement.getAttributes().item(i);
                //--Link finden
                if ((n.getNodeName().equals("name")) && (n.getNodeValue().toLowerCase().equals("viewport"))) {

                    // TODO: getNodeValue von Node welche auf name="description" folgt und content heisst
                    System.out.println("yesss ?");
                    String descriptionText = htmlElement.getAttributes().item(i + 1).getNodeValue();
                    //--schreibt den Inhalt des HtmlTag
                    System.out.println("Description: " + descriptionText);

                }
            }
        }

        // TODO: 12.01.2021 Generate Description for the case no description defined on site
    }

    public static void updateTargetNextVisit(int targetId) {
        // TODO: 12.01.2021 Update DB target record
    }


    public static void clearAndRegisterKeywords(int targetId, HashMap<String, Integer> keywords) {
        // TODO: 12.01.2021 Remove kewords to an old target
    }

    public static void registerKeywords(String textForKeywords, int relevanz, HashMap<String, Integer> keywords) {
        List<String> disabledKeywords = java.util.Arrays.asList(
                new String[]{"er", "sie", "es", "and", "und", "dass", "with"});

        //alles was kein Schriftzeichen ist entfernen, ö.ä,ü bleiben bestehen
        textForKeywords = textForKeywords.replaceAll("[\\p{Punct}]+", " ")
                .replaceAll("[\\p{Digit}]+", " ") //alle Zahlen ...
                .replaceAll("\n", " ")            //linebreak entfernen
                .replaceAll("[ ]+", " ");
        String[] newKeywords = textForKeywords.split(" ");
        for (String word : newKeywords) {
            word = word.toLowerCase();
            if ((word.length() > 2) && (!disabledKeywords.contains(word))) {
                if ((!keywords.containsKey(word)) || (relevanz > keywords.get(word))) {
                    System.out.println("registerKeywords(" + word + ", " + relevanz + ")");
                    keywords.put(word, relevanz);
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
