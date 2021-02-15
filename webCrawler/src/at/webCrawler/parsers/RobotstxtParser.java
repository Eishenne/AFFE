package at.webCrawler.parsers;


import org.apache.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

public class RobotstxtParser {

    /**
     * Mainmethod for parsing Robots.txt. From here the different methods used are started.
     * @param base Link to website/source of the robots.txt
     */
    public static void analyzeRobotsTxt(String base) {
        String file = getRobotsTxtFile(base);
        splitRobotsTxt(file);

    }


    /**
     * uses the java.net.HttpClient to receive the robots.txt from a website.
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
        //-robots.txt holen
        //---URL zur robots.txt erzeugen
        robotsTxtUri = URI.create(base + "/robots.txt");
//            System.out.println("robots.txt URI = " + robotsTxtUri);

        //-robots.txt Anfrage beschreiben
        HttpRequest request = HttpRequest.newBuilder()
                .uri(robotsTxtUri)
                .timeout(Duration.ofMinutes(2))
                .build();
        try {
            //robots.txt Anfrage durchführen (+Auswahl Rückgabeform)
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //robots.txt Anfrage Erfolg prüfen und Ergebnis verarbeiten
            if (response.statusCode() == HttpStatus.SC_OK) {
//                System.out.println("Ziel :" + robotsTxtUri);
                //robots.txt in String füllen
                robotsTxtContent = response.body();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

//        System.out.println(robotsTxtContent);
        return robotsTxtContent;
    }

    /**
     * splits String into separate Strings for each line
     * @param robotsTxtContent robots.txt as a String
     */
    private static void splitRobotsTxt(String robotsTxtContent) {
        Scanner scanner = new Scanner(robotsTxtContent);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                continue;
            }
            //Zeile analysieren / einordnen und so
            System.out.println(line);
            analyzeLine(line);

        }
        scanner.close();
    }


    /**
     * analyzes a line of the robots.txt to check what the content wants to control
     * @param line a single line of the robots.txt
     */
    private static void analyzeLine(String line) {
        if (line.toLowerCase().startsWith("disallow")) {
            //Pfad welcher nicht erlaubt ist
            String entry = line.substring(8).trim();
            //
            //Form der Pfadangabe verarbeiten
            // */uri
            // /*uri
            // /*/uri
            // /uri*
            // /uri/*


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

    private static void createSiteBlacklist(String robotsTxt) {


    }
}
