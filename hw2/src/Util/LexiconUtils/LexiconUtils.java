package Util.LexiconUtils;

import component.component3.Lexicon;

import java.io.*;
import java.util.Map;

/**
 * @author Hanlin Wang
 */
public class LexiconUtils {
    public static void writeLexiconToFile(Map<String, Lexicon> lexicon, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(lexicon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Lexicon> readLexiconFromFile(String filePath) {
        Map<String, Lexicon> lexicon = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            lexicon = (Map<String, Lexicon>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return lexicon;
    }
}
