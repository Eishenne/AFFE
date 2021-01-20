package at.webCrawler.tool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {
    public static List<String> blackListKeyword() {
        //File Inhalt lesen
        FileInputStream fis = null;
        ArrayList<String> wordList = new ArrayList<>();

        try {
            fis = new FileInputStream("C:\\Users\\DCV\\Desktop\\webcrawlerDeutsch.txt");
        } catch (
                FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        if (fis != null) {          //wenn File nicht existiert, soll Code nicht weiter ausgeführt werden
            Scanner sc = new Scanner(fis);

            while (sc.hasNext()) {
                String currentLine = sc.nextLine();
                if (currentLine.startsWith("//")) {
                    continue;
                }
                if (currentLine.isEmpty()) {
                    continue;
                }
                String[] wordArray = currentLine.split(","); //woher woran wie wann warum weshalb weswegen

                for (int i = 0; i < wordArray.length; i++) {
                    String word = wordArray[i];
                    word = word.trim();
                    wordList.add(word);
                    System.out.println(word);
                }

            }
            try {
                fis.close();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return wordList;
    }
}


