package at.webCrawler;

import at.webCrawler.parsers.KeywordHeaderParser;
import at.webCrawler.parsers.KeywordMetaParser;
import at.webCrawler.parsers.UrlParser;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.HttpStatus;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.net.http.HttpClient;

public class Main {
    //TODO: java.sql.* anpassen auf benötigte SQL imports und diese einzeln importieren
    //TODO: import der ProgrammKlassen oder qualifiziert aufrufen. Aktuell wird beides gemacht. Warum?
    // Was ist besser?
    // TODO: import Node oder DomNode. Alternative siehe KeywordHeaderParser
    public static void main(String[] args) throws IOException {
        WebClient webClient = new WebClient();

        HttpClient client = HttpClient.newBuilder()
                .version(java.net.http.HttpClient.Version.HTTP_1_1)
                .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        int countReadPages = 0;
        boolean stop = false;
        while (!stop) {
            System.gc();
            printMemory();
//            String nextURL = DataBaseFunction.readDB_nextTarget();
            String nextURL = "https://www.wetator.org/";
            int targetId = getTargetId(nextURL);
            //TODO: robots.txt lesen und berücksichtigen

            URI destination = URI.create(nextURL + "robots.txt");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(destination)
                    .timeout(Duration.ofMinutes(2))
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if(response.statusCode() == HttpStatus.SC_OK) {
                    System.out.println("Ziel :" + destination);
                    System.out.println(response.body());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setCssEnabled(false);
            try {
                System.out.println("Load URL: " + nextURL);
                System.out.println("Hello " + nextURL + "\nWelcome to AFFE!");
                HtmlPage page = webClient.getPage(nextURL);

                System.out.println("Hier ist jetzt das erste p :" + createDescription(page.getBaseURL(), page.getBody(), targetId));

                //analyzePage(nextURL, page, targetId);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                DataBaseFunction.updateTargetNextVisit(targetId, "Exception", "");
            } catch (Error err) {
                err.printStackTrace();
                DataBaseFunction.updateTargetNextVisit(targetId, err.getClass().getSimpleName(), "");
            } finally {
                //objekte leeren und freimachen für GarbageCollector
                webClient.getCache().clear();
                webClient.close();
                webClient = new WebClient();
            }
//           catch (UnknownHostException uhe) {
//                System.out.println(uhe.getMessage());
//                updateTargetNextVisit(targetId, "UnknownHostException", "");
//            } catch (IllegalArgumentException iae) {
//                System.out.println(iae.getMessage());
//                updateTargetNextVisit(targetId, "IllegalArgumentException", "");
//            }catch (NullPointerException npe){
//                System.out.println(npe.getMessage());
//                updateTargetNextVisit(targetId,"NullPointerException", "");
//            } catch (ClassCastException cce) {
//                System.out.println(cce.getMessage());
//                updateTargetNextVisit(targetId, "ClassCastException", "");
//            } catch (FailingHttpStatusCodeException fhsce) {
//                System.out.println(fhsce.getMessage());
//                updateTargetNextVisit(targetId, "FailingHttpStatusCodeException", "");
//            }

            // TODO: 12.01.2021 Define a practical Exit statement

            stop = false;

            if (countReadPages >= 1) {
                stop = true;
            } else {
                ++countReadPages;
            }

        }
    }


    public static int getTargetId(String currentURL) {
        int targetId = DataBaseFunction.readTargetId(currentURL);
        return targetId;
    }


    public static void analyzePage(String currentURL, HtmlPage page, int targetId) {
        HashMap<String, Integer> keywords = new HashMap<>();

        UrlParser.analyzeHyperlinks(page.getBaseURL(), page.getBody());
        String description = analyzeDescription(page.getBaseURL(), page.getHead(), targetId);
        String title = analyzePageTitle(targetId, page.getBaseURL(), page.getHead(), keywords);
        KeywordMetaParser.analyzeKeywordMetaTag(currentURL, page, keywords);
        KeywordHeaderParser.analyzeKeywordHeaderTag(currentURL, page, keywords);
        clearAndRegisterKeywords(targetId, keywords);
        DataBaseFunction.updateTargetNextVisit(targetId, title, description);
    }

    /**
     * reads title of a website, extends keywordlist and returns the longest title found on website.
     * Form 1: <title> content </title>
     * Form 2: <meta name"title" content=" content ">
     *
     * @param targetId    id der currentURL in DB_target
     * @param currentURL  URL der aktuellen Webseite
     * @param htmlElement zu durchsuchende DomNode-Elemente
     * @param keywords    HashMap<String, Integer> Liste aller Keywords in currentURL
     * @return title als String
     */
    public static String analyzePageTitle(int targetId, URL currentURL, DomNode htmlElement, HashMap<String, Integer> keywords) {
        String title = "";
        String title1 = "";
        String title2 = "";

        for (DomNode d : htmlElement.getChildren()) {
            if (d.getLocalName() != null) {
                //Form 1
                if (d.getLocalName().equals("title")) {
                    //Konsolenausgabe: Inhalt des HtmlTag
//                    System.out.println("Title: " + d.getTextContent());
                    title1 = d.getTextContent();
                    //Titel in Keywörter aufteilen
                    registerKeywords(d.getTextContent(), 5, keywords);
                }

                //Form 2
                if (d.getLocalName().equals("meta")) {
                    //---für die Anzahl seiner Parameter/Attributes
                    for (int i = 0; i < d.getAttributes().getLength(); i++) {
                        Node n = d.getAttributes().item(i);
                        if ((n.getNodeName().equals("name")) && n.getNodeValue().equals("title")) {
                            for (int j = 0; j < d.getAttributes().getLength(); j++) {
                                Node m = d.getAttributes().item(i++);
                                if (m.getNodeName().equals("content")) {
                                    //Konsolenausgabe: Inhalt des HtmlTag
//                                    System.out.println("contentNode: " + m.getNodeName());
//                                    System.out.println("wert: " + m.getNodeValue());
                                    title2 = m.getNodeValue();
                                    registerKeywords(title, 5, keywords);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (title1.length() > title2.length()) {
            title = title1;
        } else {
            title = title2;
        }
        return title;
    }


    /**
     * searches htmlElement for a description and returns the longest description found.
     * TODO: 12.01.2021 Generate description in case no description is defined on website
     *
     * @param currentURL  URL of currently scanned website
     * @param htmlElement DomNode which gets scanned for description
     * @param targetId    id of URL in DB_target
     * @return description as String
     */
    public static String analyzeDescription(URL currentURL, DomNode htmlElement, int targetId) {
        String descriptionText = "";
        String description1 = "";
        String description2 = "";

        for (DomNode d : htmlElement.getChildren()) {
            if (d.getLocalName() != null) {
                //Form 1
                //description aus <meta name="description" content="beschreibung der seite">
                if (d.getLocalName().equals("meta")) {
                    //---für die Anzahl seiner Parameter/Attributes
                    for (int i = 0; i < d.getAttributes().getLength(); i++) {
                        Node n = d.getAttributes().item(i);
                        if ((n.getNodeName().equals("name")) && n.getNodeValue().equals("description")) {
                            for (int j = 0; j < d.getAttributes().getLength(); j++) {
                                Node m = d.getAttributes().item(i++);
                                if (m.getNodeName().equals("content")) {
                                    //Konsolenausgabe: Inhalt des HtmlTag
//                                    System.out.println("contentNode: " + m.getNodeName());
//                                    System.out.println("wert: " + m.getNodeValue());
                                    description1 = m.getNodeValue();
                                }
                            }
                        }
                    }
                }
                //Form 2
                //description aus <description="">
                if (d.getLocalName().equals("description")) {
                    //Konsolenausgabe: Inhalt des HtmlTag
//                    System.out.println("Beschreibung: " + d.getTextContent());
                    description2 = d.getTextContent();
                }
            }
        }
        //Auswahl der längsten description zum ablegen in DB
        if (description1.length() > description2.length()) {
            descriptionText = description1;
        } else {
            descriptionText = description2;
        }
        //keine description auf Seite vorhanden
        if (descriptionText.length() < 1) {
            System.out.println("Keine Beschreibung gefunden auf " + currentURL + ".");
            descriptionText = "Fehler: Seite prüfen.";
            //TODO: generate description in case none is found
        }
        return descriptionText;
    }


    /**
     * clears old keywords of targetId = FK_targetId at DB-searchresult and writes all keywords from keywordlist into
     * DB.searchresult
     *
     * @param targetId id of URL in DB.target
     * @param keywords list of keywords found on currently analyzed website
     */
    public static void clearAndRegisterKeywords(int targetId, HashMap<String, Integer> keywords) {
        DataBaseFunction.clearKeywords(targetId);
        for (String k : keywords.keySet()) {
            //--keyword, relevanz, targetId an SQL übergeben
            DataBaseFunction.writeKeyword(k, keywords.get(k), targetId);
        }
    }

    /**
     * formats and checks whether a word is to be registered as a keyword. Duplicate entrys are filtered out.
     *
     * @param textForKeywords String to be checked
     * @param relevanz        given importance of the keyword-source
     * @param keywords        list of keywords found for that website
     */
    public static void registerKeywords(String textForKeywords, int relevanz, HashMap<String, Integer> keywords) {
        //Blacklist Bindewörter
        //TODO: Blacklist aus externem Log/Dokument entsprechend Seitensprache / meta charset?
        List<String> disabledKeywords = java.util.Arrays.asList(
                new String[]{
                        //deutsch
                        //Pronomen
                        "ich", "du", "er", "sie", "es", "wir", "ihr", "sie",
                        "meiner", "deiner", "seiner", "ihrer", "unser", "euer",
                        "mir", "dir", "ihm", "uns", "euch", "ihnen",
                        "mich", "dich", "ihn",
                        //Artikel
                        "der", "die", "das", "den", "des", "dem",
                        //Fragewörter
                        "wer", "wie", "was", "wieso", "weshalb", "warum", "wo", "wann", "wessen", "wem", "wohin",
                        "woran", "woher", "wieviele", "wieviel",
                        "ist", "sein", "und", "oder", "auf", "nach", "oben", "hinten", "links", "rechts", "rechte",
                        "oft", "zum", "zur", "bei", "kommt", "mit", "alle", "dass", "dein", "deine",
                        "meine", "seine", "diese", "dieser", "klein", "kleine", "kleiner", "kleinen", "große", "als",
                        "von", "vom", "voll", "mal", "ersten", "oder", "ihre", "ihrer", "über", "uns", "also",
                        "grenznah", "frau", "stunde", "inner", "schnell", "bereich", "werden", "sind", "durch",
                        "können", "jetzt", "aus", "unser", "unsere", "unserer", "less", "such", "finden", "bis",
                        "nicht", "beim", "auch", "sich", "ein", "eine", "eins", "einen", "einer", "einem", "kein",
                        "keines", "keiner", "mehr", "noch", "doch", "", "%---%",
                        //english
                        //Pronomen
                        //Artikel
                        //Fragewörter
                        "i", "you", "he", "she", "it", "we", "them", "they", "why", "when", "how", "where", "what",
                        "which", "was", "for", "other", "there", "these", "those", "their", "they", "them", "are",
                        "who", "is", "the", "this", "that", "he", "she", "his", "her", "have", "minute", "hours",
                        "and", "with", "back", "eye", "reveal", "us", "our", "said", "–relaunch", "make", "long", "or",
                        "sold", "told", "big", "medium", "small", "its", "seeing", "rocket", "cancel", "here",
                        "backed", "your", "you", "all", "children", "use", "on", "off", "replay", "share", "pizza",
                        "best", "forever", "left", "right", "rights", "out", "top", "from", "more", "get", "first",
                        "second", "third", "can", "may", "form", "know", "now", "no", "time", "will", "into", "free",
                        "across", "corss", "one", "like", "keep", "most", "need", "changing", "next", "through",
                        "real", "better", "similar", "wir", "see", "any", "life", "show", "usage", "find", "outside",
                        "overview", "stay", "footer", "improve", "change", "here", "home", "exercise", "shield",
                        "open", "whom", "want", "full", "only", "listen", "east", "over", "useful", "quick", "easy",
                        "middle", "please", "some", "trust", "thank", "thanks", "than", "payment", "end", "made",
                        "white", "way", "increase", "decrease", "reach", "reaching", "between", "should", "house",
                        "hard", "but", "smart", "decision", "become", "becomes", "self", "behalf", "high", "family",
                        "love", "good", "grow", "buy", "button", "did", "take", "fraud", "own", "thing", "things",
                        "think", "thinks", "down", "up", "side", "%'s", "%´s", "%`s", "'ve", "´ve", "%`ve"});

        //alles was kein Schriftzeichen ist entfernen, ö.ä,ü bleiben bestehen
        textForKeywords = textForKeywords.replaceAll("[\\p{Punct}]+", " ")
                .replaceAll("[\\p{Digit}]+", " ") //alle Zahlen ...
                .replaceAll("\n", " ")            //linebreak entfernen
                .replaceAll("[ ]+", " ");
        String[] newKeywords = textForKeywords.split(" ");
        for (String word : newKeywords) {
            word = word.toLowerCase().trim();
            if ((word.length() > 2) && (!disabledKeywords.contains(word))) {
                if ((!keywords.containsKey(word)) || (relevanz > keywords.get(word))) {
//                    System.out.println("registerKeywords(" + word + ", " + relevanz + ")");
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
     * lookup a href and h2 in htmlElement, prints them into console if found (rekursiv)
     *
     * @param htmlElement contains all Elements from a specific part of a html website
     * @param baseUrl     is the URL where the htmlElements are from
     */
    public static void printResultHeader(DomNode htmlElement, URL baseUrl) {
        //alle h2 überschriften auslesen
        if (htmlElement.getNodeName().equals("h2")) {
            System.out.println("h2:" + htmlElement.getTextContent());
        }

        //alle Links auslesen
        //-LinkTag finden
        if (htmlElement.getNodeName().equals("a")) {
            for (int i = 0; i < htmlElement.getAttributes().getLength(); i++) {
                Node n = htmlElement.getAttributes().item(i);
                //--Link finden
                if (n.getNodeName().equals("href")) {
                    try {
                        System.out.println("a: " + baseUrl.toURI().resolve(n.getNodeValue()));
                        //DB auf vorhandene URL prüfen
//                        if (DataBaseFunction.newTarget(baseUrl.toURI().resolve(n.getNodeValue()).toString())) {
//                            //DB row schreiben wenn URL nicht vorhanden
//                            DataBaseFunction.writeTargetToTargetlist(baseUrl.toURI().resolve(n.getNodeValue()).toString());
//                        }
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

    /**
     * lists information about memory of JAVA-VirtualMachine
     * TODO: prüfen ob es mit Bytes oder KBytes arbeitet -> mb = 1024 * 1024 * 1024
     */
    public static void printMemory() {
        Runtime runtime = Runtime.getRuntime();
        NumberFormat format = NumberFormat.getInstance();
        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        long mb = 1024 * 1024;
        sb.append("Used memory: " + ((runtime.totalMemory() - runtime.freeMemory()) / (float) mb) + " Megabyte");
//        sb.append("free memory: " + format.format(freeMemory / 1024) + "\n");
//        sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "\n");i
//        sb.append("max memory: " + format.format(maxMemory / 1024) + "\n");
//        sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");
        System.out.println(sb);
    }

    /**
     * looks up content of the first <p></p> Element and returns it
     *
     * @param currentURL  website to be searched
     * @param htmlElement page from the current URL
     * @param targetId    DB.target id of current URL
     * @return alternativ description
     */
    public static String createDescription(URL currentURL, DomNode htmlElement, int targetId) {

        for (DomNode d : htmlElement.getChildren()) {
            if (d.getLocalName() != null) {
                //Element welches wir suchen
                if (d.getLocalName().equals("p")) {
                    return d.getTextContent();
                }
            }
            String description = createDescription(currentURL, d, targetId);
            if (description != null) {
                return description;
            }
        }
        return null;
    }


    public static String analyzeRobotsTxt(URL currentURL, DomNode htmlElement, int targetId) {
        return "hallo";
    }
}
