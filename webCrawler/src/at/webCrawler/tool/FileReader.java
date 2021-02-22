package at.webCrawler.tool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {
    public static String readTextFile(String dateipfad) {
        //File Inhalt lesen
        FileInputStream fis = null;
        String currentLine = "";
        String text = null;

        try {
            fis = new FileInputStream(dateipfad);
        } catch (FileNotFoundException fnfe) {
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
        } else {
            //TODO: Log-Eintrag erzeugen
            System.out.println("Der Dateipfad ist falsch.");
        }
//        System.out.println(text);
        return text;
    }

    public static List<String> readBlacklistKeyword(String dateipfad) {
        ArrayList<String> wordlist = new ArrayList<>();
        String text = readTextFile(dateipfad);

        String[] wordArray = text.split(",");
        for (int i = 0; i < wordArray.length; i++) {
            String word = wordArray[i];
            word = word.trim();
            wordlist.add(word);
//            System.out.println(word);
        }
        return wordlist;
    }
}
