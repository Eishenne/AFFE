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
import java.net.UnknownHostException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        WebClient webClient = new WebClient();
        boolean stop = false;
        while (!stop) {
            String nextURL = readDB_nextTarget(stop);

            //browser erzeugen
            webClient.getCache().clear();
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            try {
                HtmlPage page = webClient.getPage(nextURL);
                analyzePage(nextURL, page);
            } catch (UnknownHostException uhe){
                // TODO: 19.01.2021 Clear not valid URL from target table
                System.out.println(uhe.getMessage());

            }catch (IllegalArgumentException iae){
                System.out.println(iae.getMessage());
            }

            // TODO: 12.01.2021 Define a practical Exit statement

            stop = true;
        }
    }

    public static String readDB_nextTarget(boolean stop) {
        //int controller = -1;  // index for entire DB up to date, <0 = not up to date, >ß = up to date
        String targetUrl = "https://htmlunit.sourceforge.io/gettingStarted.html";
        //String targetUrl = "https://www.laendlejob.at";
        //String targetUrl = "https://vol.at";
        //targetUrl = DataBaseFunction.readDbNextTarget();
        // TODO: 13.01.2021 url aus DB nicht aufrufbar / unterschiedliches Format -> Format anpassen

        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement = "select url from target order by lastupdate IS NULL DESC, lastupdate  limit 1";
            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                targetUrl = rs.getString(1);
                //controller = 1;
            }
            //controller for exit
//            if (controller > 0) {
//                stop = true;
//            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            DataBaseMaster.getInstance().closeDatabase();
        }

        return targetUrl;
    }

    public static int getTargetId(String currentURL) {
        int targetId = DataBaseFunction.readTargetId(currentURL);
        return targetId;
    }


    public static void analyzePage(String currentURL, HtmlPage page) {
        HashMap<String, Integer> keywords = new HashMap<>();

        int targetId = getTargetId(currentURL);
        UrlParser.analyzeHyperlinks(page.getBaseURL(), page.getBody());
        String description = analyzeDescription(page.getBaseURL(), page.getHead(), targetId);
        String title = analyzePageTitle(targetId, page.getBaseURL(), page.getHead(), keywords);
        KeywordMetaParser.analyzeKeywordMetaTag(currentURL, page, keywords);
        // TODO: 12.01.2021 Weitere Analyze for keyword generation
        clearAndRegisterKeywords(targetId, keywords);
        updateTargetNextVisit(targetId, title, description);
    }

    /**
     * erweitert keywords-Liste aus dem inhalt des title-tags. Es gibt 2 Formen des Title-Tags.
     * Form 1: <title> content </title>
     * Form 2: <meta name"title" content=" content ">
     *
     * @param currentURL  URL
     * @param htmlElement DomNode
     * @param keywords    HashMap<String, Integer>
     */
    public static String analyzePageTitle(int targetId, URL currentURL, DomNode htmlElement, HashMap<String, Integer> keywords) {
        String title = "";
        //Form 1
        for (DomNode d : htmlElement.getChildren()) {
            if (d.getLocalName() != null) {
                if (d.getLocalName().equals("title")) {
                    //--schreibt den Inhalt des HtmlTag
                    System.out.println("Title: " + d.getTextContent());
                    title = d.getTextContent();
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
//                                    System.out.println("contentNode: " + m.getNodeName());
//                                    System.out.println("wert: " + m.getNodeValue());
                                    title = m.getNodeValue();
                                    registerKeywords(title, 5, keywords);
                                }
                            }
                        }
                    }
                }
            }
//            if (title.equals("")) {
//                title = "kein_Titel_gefunden";
//            }
        }
        return title;
    }


    public static String analyzeDescription(URL currentURL, DomNode htmlElement, int targetId) {
        String descriptionText = "";

        for (DomNode d : htmlElement.getChildren()) {
            if (d.getLocalName() != null) {
                //description aus <meta name="description" content="beschreibung der seite">
                if (d.getLocalName().equals("meta")) {
                    //---für die Anzahl seiner Parameter/Attributes
                    for (int i = 0; i < d.getAttributes().getLength(); i++) {
                        Node n = d.getAttributes().item(i);
                        if ((n.getNodeName().equals("name")) && n.getNodeValue().equals("description")) {
                            for (int j = 0; j < d.getAttributes().getLength(); j++) {
                                Node m = d.getAttributes().item(i++);
                                if (m.getNodeName().equals("content")) {
//                                    System.out.println("contentNode: " + m.getNodeName());
//                                    System.out.println("wert: " + m.getNodeValue());
                                    descriptionText = m.getNodeValue();
                                }
                            }
                        }
                    }
                }
                if (d.getLocalName().equals("description")) {
                    //--schreibt den Inhalt des HtmlTag
//                    System.out.println("Beschreibung: " + d.getTextContent());
                    descriptionText = d.getTextContent();
                    //Titel in Keywörter aufteilen
                    //registerKeywords(d.getTextContent(), 5, keywords);

                }

                //descriptionText an DB übertragen
//                if (descriptionText.equals("")) {
//                    descriptionText = "keine_Beschreibung_gefunden";
//                }
            }

        }
        //DataBaseFunction.writeDB_Description(descriptionText, targetId);
        return descriptionText;
        // TODO: 12.01.2021 Generate Description in case no description defined on site
    }

    public static boolean updateTargetNextVisit(int targetId, String title, String description) {
//        if (description.length()>164) {
//            description = description.substring(0, 155);
//        }
//        if (title.length()>124) {
//            title = title.substring(0, 124);
//        }

        try {
            Connection con = DataBaseMaster.getInstance().getDbCon();
            String statement =
                    "UPDATE target " +
                            "SET title= ?, nextvisit= ?, description = ?, lastupdate = ?" +
                            "WHERE id= ?;";

            PreparedStatement ps = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, title);
            ps.setInt(2, 1440);
            ps.setString(3, description);
            ps.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(5, targetId);
            int rows = ps.executeUpdate();
            return true;
        } catch (SQLException exc) {
            exc.printStackTrace();
            return false;
        } /* finally {                                                                  ??????????????????????????
            con.closeDatabase();
            con.close();
        } */
    }


    public static void clearAndRegisterKeywords(int targetId, HashMap<String, Integer> keywords) {
        DataBaseFunction.clearKeywords(targetId);
        for (String k : keywords.keySet()) {
            //--keyword, relevanz, targetId an SQL übergeben
            DataBaseFunction.writeKeyword(k, keywords.get(k), targetId);
        }

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
}
