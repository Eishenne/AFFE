package at.webCrawler.parsers;


import org.apache.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RobotstxtParser {

    /**
     * Mainmethod for parsing Robots.txt. From here the different methods are executed.
     *
     * @param base Link to website/source of the robots.txt
     */
    public static void analyzeRobotsTxt(String base) {
        //robots.txt von Seite (base) holen
        String inputFile = getRobotsTxtFile(base);
        //crawlerspezifische Anweisungen von einander trennen
        List<String> groupList = searchAndCreateGroups(inputFile, "User-agent:");

        //-crawlerspezifische Anweisungen filtern
        for (String x : groupList) {
//            System.out.println("analyze: " + x);
            if (x.startsWith("AFFE") || x.startsWith(" AFFE")) {
                //Anweisungen für AFFE auflisten
                //Anweisungen anhand linereturn aufteilen
                splitRobotsTxt(x);
                //aufgeteilte Anweisungen filtern und verarbeiten
            }
            if (x.startsWith("*") || x.startsWith(" *")) {
                //Anweisungen für alle crawler auflisten
                //Anweisungen anhand linereturn aufteilen
                splitRobotsTxt(x);
                //aufgeteilte Anweisungen filtern und verarbeiten
            }
        }
    }

    /**
     * uses the java.net.HttpClient to receive the robots.txt from a website.
     *
     * @param base Link to websites robots.txt
     * @return robots.txt as a String
     */
    private static String getRobotsTxtFile(String base) {
        //-Client für Webdateien
        HttpClient client = HttpClient.newBuilder()
                .version(java.net.http.HttpClient.Version.HTTP_1_1)
                .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        //URL Sachen
        URI robotsTxtUri = URI.create("http://www.beispieluri.com");
        String robotsTxtContent = "";

        //robots.txt
        //-robots.txt Anfrage vorbereiten
        //--URL zur robots.txt erzeugen
        robotsTxtUri = URI.create(base + "/robots.txt");
//            System.out.println("robots.txt URI = " + robotsTxtUri);
        //--robots.txt Anfrage beschreiben
        HttpRequest request = HttpRequest.newBuilder()
                .uri(robotsTxtUri)
                .timeout(Duration.ofMinutes(2))
                .build();
        try {
            //-robots.txt Anfrage durchführen (+Auswahl Rückgabeform)
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //-robots.txt Anfrage Erfolg prüfen und Ergebnis verarbeiten
            if (response.statusCode() == HttpStatus.SC_OK) {
//                System.out.println("Ziel :" + robotsTxtUri);
                //--robots.txt in String füllen
                robotsTxtContent = response.body();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
//        System.out.println(robotsTxtContent);
        return robotsTxtContent;
    }

    /**
     * Splits a String into parts. Every Part is indicated by the separator.
     *
     * @param fullTxt   the entire string which needs to be split into groups
     * @param separator indicates the beginning and end of a group
     * @return the List of groups found
     */
    private static List<String> searchAndCreateGroups(String fullTxt, String separator) {
        String inputText = fullTxt;
        String txtGroup = "";
        ArrayList<String> groupList = new ArrayList<>();

        String[] orderList = inputText.split(separator);
        for (int i = 0; i < orderList.length; i++) {
            txtGroup = orderList[i];
            groupList.add(txtGroup);
//            System.out.println("*****************************************");
//            System.out.println("Anweisung #" + i + " : ");
//            System.out.println(txtGroup);
        }
        return groupList;
    }

    /**
     * Splits String into separate Strings for each line. Every line gets analyzed for its content.
     *
     * @param robotsTxtContent String that needs to be split per line.
     */
    private static void splitRobotsTxt(String robotsTxtContent) {
        Scanner scanner = new Scanner(robotsTxtContent);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                continue;
            }
            analyzeLineFormat(line);
        }
        scanner.close();
    }

    /**
     * Analyzes a line of the robots.txt to check what the content wants to control.
     * Should only handle lines that are relevant for us or every crawler.
     *
     * @param line a single line of the robots.txt
     */
    private static void analyzeLineFormat(String line) {
        //unformatierter Eintrag
        String entry = "";
        //formatierter Eintrag zum anbinden an nextTarget
        String formatedEntry = "";
        //crawlerdelay
        int delay = 0;

        if (line.toLowerCase().startsWith("disallow")) {
            //Pfad welcher nicht erlaubt ist
            entry = line.substring(9).trim();
            //Form der Pfadangabe verarbeiten
            // */entry
            if (entry.startsWith("*")) {
//                System.out.println("So gehts rein: " + entry);
                entry = entry.replace("*", "%");
//                System.out.println("So kommts raus: " + entry);
            }
            // /*entry
            if (entry.startsWith("/*")) {
//                System.out.println("So gehts rein: " + entry);
                entry = entry.replace("/*", "%");
//                System.out.println("So kommts raus: " + entry);
            }
            // /entry*
            if (entry.endsWith("*")) {
//                System.out.println("So gehts rein: " + entry);
                entry = entry.replace("*", "%");
//                System.out.println("So kommts raus: " + entry);
            }
//            System.out.println("***********************************");
            //nach formatierung den entry zur Blacklist hinzufügen
        }
        //delay ermitteln
        if (line.toLowerCase().startsWith("crawl-delay:")) {
            System.out.println("delay : " + line);
            delay = Integer.parseInt(line.substring(12).trim());
            System.out.println("delay bekannt : " + delay);
        }
    }

    private static String[] whateverRobotBlacklist(String robotsTxt) {

        String[] robotBlacklist = new String[]{};

        //Zeilenweise Anweisungen aufarbeiten
        //keine robots.txt oder robots.txt leer
        if ((robotsTxt == null) || (robotsTxt.length() == 0)) {
            //alles erlaubt
        }
        //erste line enthält <!document HTML dingsda ===> das ist kein JimBeam

        return robotBlacklist;
    }


}
