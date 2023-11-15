package Main;

import component.component1.DocumentInfo;
import component.component1.Parse;
import component.component2.Merge;
import component.component3.Lexicon;
import component.component3.Reformat;

import java.util.List;
import java.util.Map;

/**
 * @author Hanlin Wang
 */
public class InvertedIndex {
    public static List<DocumentInfo> docInfoList = null;
    public static Map<String, Lexicon> lexicon = null;
    public static void main(String[] args) {
        // Step 1
        String srcFilePath = "/Users/hanlin/Desktop/msmarco-docs.trec.gz";
        String tempFilePath = "/Users/hanlin/Desktop/temp/";
        // Parse.parsing(srcFilePath, tempFilePath);

        // Step 2
        String sortedFilePath = "/Users/hanlin/Desktop/merge/merged.txt";
        // Merge.mergeSortedTempFiles(tempFilePath, sortedFilePath);

        // Step 3
        String invertedIndexPath = "/Users/hanlin/Desktop/index/index.txt";

        // whole process
        // createInvertedIndex(srcFilePath, tempFilePath, sortedFilePath, invertedIndexPath);

    }
    public static void createInvertedIndex(String srcFilePath, String tempFilePath, String dataFilePath, String sortedFilePath, String invertedIndexPath){
        Parse.parsing(srcFilePath, tempFilePath, dataFilePath);
        Merge.mergeSortedTempFiles(tempFilePath, sortedFilePath);

        List<DocumentInfo> docInfoList = Parse.getDocInfoList();
        Map<String, Lexicon> lexicon = Reformat.getLexicon();
    }
}
