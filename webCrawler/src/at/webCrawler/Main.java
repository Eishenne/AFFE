package at.webCrawler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import static at.webCrawler.DataBaseFunction.newTarget;
import static at.webCrawler.DataBaseFunction.writeTargetToTargetlist;

public class Main {
    /*
    Allgemeines / Systematik
        - Class erstellen für SQL Sätze (url, titel, description) sodass diese als ganzes angesprochen/übergeben wird
        - öffnen und schliessen der connection ??? wann, wie ???

    Ablauf Roadmap
        //*** Start Hauptprogramm
        //eine URL wird dem Programm gegeben oder in ersten Satz der DB geschrieben
        //crawler holt satz von SQL-DB.target (id)
        //wenn Datum nicht vorhanden oder (DatumMM + nextvisit) < currentDate
        //*** Start Website auswerten
        //Website holen
        //Daten für vollständigen Satz erstellen
        //-URL holen
        //-title holen
        //-description holen
        //-keywords holen
        //-relevanz holen
        //*** Start Daten in DB ablegen
        //DB.target - URL Satz löschen
        //DB.target - vollständigen Satz schreiben
        //DB.searchresult - vollständigen Satz zu jedem keyword ablegen
        //*** Start Programmende
        //DB Verbindung schliessen wenn noch vorhanden


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
        DB.target - URL Satz löschen
        DB.target - vollständigen Satz schreiben
        DB.searchresult - vollständigen Satz zu jedem keyword ablegen
        *** Start Programmende
        DB Verbindung schliessen wenn noch vorhanden

     */


    public static void main(String[] args) throws IOException {
        //*** Start Vorbereitung
        String targetUrl;
        //browser erzeugen
        final WebClient webClient = new WebClient();

        //eine URL wird dem Programm gegeben oder in ersten Satz der DB geschrieben und als wert übergeben
        targetUrl = "https://www.laendlejob.at";

        //-crawler holt satz von SQL-DB.target (id)


        //wenn Datum nicht vorhanden oder (DatumMM + nextvisit) < currentDate
        //-website laden
        final HtmlPage page = webClient.getPage(targetUrl);
        //https://htmlunit.sourceforge.io/gettingStarted.html


        //*** Start Hauptprogramm
        System.out.println("AFFE - Welcome to AdvancedFileFindEntity" + " " + page.getTitleText());
        //title
        String title = page.getTitleText();
        //description
        String description = "";
        //heading
        String heading = "";
        //keywords
        ArrayList<String> keywordList = new ArrayList<String>();






        //Html - <head> aus page lesen
        //-DomNode d ist eine Liste aller HtmlTags auf page
        //-die Children sind untergeordnete HtmlTags (zB.: meta, title, link)
        for (DomNode d : page.getHead().getChildren()) {
            if (d.getLocalName() != null) {
                //--schreibt den HtmlTag
                System.out.println("name: " + d.getLocalName());
                //---für die Anzahl seiner Parameter(n.xxxAttributes())
                for (int i = 0; i < d.getAttributes().getLength(); i++) {
                    Node n = d.getAttributes().item(i);
                    //---schreibt Node-Typ / Parameter-Bezeichnung / Parameter-Wert
                    System.out.println(n.getNodeType() + " / " + n.getNodeName() + " / " + n.getNodeValue());

                }
            }
        }


        //Seitenstruktur ausgeben
        System.out.println("********************************");
        printDomTree("", page.getBody());
        System.out.println("********************************");

        //Überschrift ausgeben
        printResultHeader(page.getBody(), page.getBaseURL());





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
                        if (newTarget(baseUrl.toURI().resolve(n.getNodeValue()).toString())) {
                            //DB row schreiben wenn URL nicht vorhanden
                            writeTargetToTargetlist(baseUrl.toURI().resolve(n.getNodeValue()).toString());
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

    public static void SearchAndCreateTarget() {

    }

}
