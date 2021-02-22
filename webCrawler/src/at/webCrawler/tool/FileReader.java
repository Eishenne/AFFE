package at.webCrawler.tool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {
    public static String readTextFile(String dateipfad) {
        //File Inhalt lesen
        FileInputStream fis = null;
        String currentLine = "";
        String text = "";

        try {
            fis = new FileInputStream(dateipfad);
        } catch (
                FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        if (fis != null) {          //wenn File nicht existiert, soll Code nicht weiter ausgeführt werden
            Scanner sc = new Scanner(fis);
            while (sc.hasNext()) {
                currentLine = sc.nextLine();
                if (currentLine.startsWith("//")) {
                    continue;
                }
                if (currentLine.isEmpty()) {
                    continue;
                }
                text += currentLine;
                try {
                    fis.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return text;
    }

    public static List<String> readBlacklistKeyword(String dateipfad) {
        ArrayList<String> wordlist = new ArrayList<>();
        String text = readTextFile(dateipfad);
        if (text != null && text.length() > 0) {
            String[] wordArray = text.split(",");
            for (int i = 0; i < wordArray.length; i++) {
                String word = wordArray[i];
                word = word.trim();
                wordlist.add(word);
                System.out.println(word);
            }
        }
        return wordlist;
    }

    public static List<String> multipleFileReader(String mf) throws Exception {
        ArrayList<String> disabledKeywords = new ArrayList<>();
        String[] disabledKeywordsfile = {"where:\\Machintosh HD\\Users\\shenwari\\Desktop\\WebcrawlerDeutsch.txt",
                "where:\\Machintosh HD\\Users\\shenwari\\Desktop\\WebcrawlerEnglisch.txt"};
        for (String filename : disabledKeywordsfile) {
            disabledKeywords.addAll(FileReader.readBlacklistKeyword(filename));
        }
        if(disabledKeywords.size() == 0){
            throw new Exception("Blacklist Keywords wurden nicht gefunden. Bitte um die Überprüfung der Dateipfaden;");
        }
        return disabledKeywords;
    }

    public static void creatLogEntry(String LogEintrag) {
        try {
            FileWriter mywriter = new FileWriter("where:\\Machintosh HD\\Users\\shenwari\\Desktop\\txtfile.txt", true);
            mywriter.write(LogEintrag, 0, LogEintrag.length());
            mywriter.write("\n", 0, 1);
            mywriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred");
            e.printStackTrace();
        }
    }
}
