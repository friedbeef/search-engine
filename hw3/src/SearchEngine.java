import Util.UrlListUtils.UrlListUtils;
import component.component1.DocumentInfo;
import component.component3.Lexicon;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class SearchEngine {
    public static void main(String[] args) {
        List<DocumentInfo> pageTable = UrlListUtils.loadDocumentInfoList("document_info.ser");
        int docCount = pageTable.size();
        System.out.println(docCount);
        double avgDocLen = 0;
        for(DocumentInfo docInfo : pageTable) {
            avgDocLen += docInfo.termCount; // 先累加所有的termCount
        }
        avgDocLen /= docCount;
        System.out.println(avgDocLen);
    }
//    private static Map<String, Lexicon> lexiconMap = LexiconUtils.readLexiconFromFile("lexicon.bin");
//    private static List<DocumentInfo> pageTable = UrlListUtils.loadDocumentInfoList("document_info.ser");
//    private static RandomAccessFile metadataFile;
//    private static RandomAccessFile indexFile;
//    private static int docCount = pageTable.size();
//    private static double avgDocLen;
//    private static final double k1 = 1.5;
//    private static final double b = 0.75;
//    private static final int resSize = 3;
//    static {
//        for(DocumentInfo docInfo: pageTable){
//            avgDocLen += (double) (docInfo.termCount/docCount);
//        }
//        try {
//            metadataFile = new RandomAccessFile("metadata.bin", "r");
//            indexFile = new RandomAccessFile("inverted_index.bin", "r");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }
    public static PriorityQueue<Document> queryProcessorAND(String query, Map<String, Lexicon> lexiconMap,
                                                          RandomAccessFile metadataFile, RandomAccessFile indexFile,
                                                          List<DocumentInfo> pageTable, int docCount,
                                                          double avgDocLen, double k1, double B, int resSize
                                                          ) throws IOException {
        PriorityQueue<Document> topResults = new PriorityQueue<>(Comparator.comparingDouble(a -> a.score));
        String[] terms = query.split("&");
        int n = terms.length;

        List<InvertedList> lists = new ArrayList<>();
        for (String term : terms) {
            if(!lexiconMap.containsKey(term)){
                System.out.println(term + "doesn't exist in lexicon");
                return topResults;
            }
            lists.add(ListAccessAPI.openList(term, lexiconMap, metadataFile));
            System.out.println("term = " + term + " list opened");
        }
        lists.sort(Comparator.comparingInt(a -> lexiconMap.get(a.term).docFrequency));

        int maxDocID = lists.get(0).metadata.lastDocIds[lists.get(0).metadata.lastDocIds.length-1];
        int did = 0, d = 0;
        while(did < maxDocID){
            //get next post from the shortest list
            did = ListAccessAPI.nextGEQ(did, lists.get(0), indexFile);
            // see if you find entries with same docID in other lists
            for (int i = 1; i < n && (d = ListAccessAPI.nextGEQ(did, lists.get(i), indexFile)) == did; i++);

            if(d > did) did = d;
            else if (d == -1){// no such element exists
                break;
            }
            else{
                double score = 0d;
                for (int i = 0; i < n; i++) {
                    score += ListAccessAPI.getScore(lists.get(i), pageTable, indexFile, lexiconMap.get(lists.get(i).term), docCount, k1, B, avgDocLen, did);
                }
                //If score large enough, insert into top-k heap
                if(topResults.size() < resSize){
                    topResults.add(new Document(did, score));
                }else{
                    if(score > topResults.peek().score){
                        topResults.poll();
                        topResults.add(new Document(did, score));
                    }
                }
                // increase did to search for next post
                did++;
            }
        }
        return topResults;
    }
    public static PriorityQueue<Document> queryProcessorOR(String query, Map<String, Lexicon> lexiconMap,
                                                             RandomAccessFile metadataFile, RandomAccessFile indexFile,
                                                             List<DocumentInfo> pageTable, int docCount,
                                                             double avgDocLen, double k1, double B, int resSize
    ) throws IOException {
        PriorityQueue<Document> topResults = new PriorityQueue<>((a, b) -> Double.compare(a.score, b.score));
        String[] terms = query.split("\\|");
        int n = terms.length;

        List<InvertedList> lists = new ArrayList<>();
        for (String term : terms) {
            if(!lexiconMap.containsKey(term)){
                System.out.println(term + "doesn't exist in lexicon");
                return topResults;
            }
            lists.add(ListAccessAPI.openList(term, lexiconMap, metadataFile));
            System.out.println("term = " + term + " list opened");
        }
        lists.sort(Comparator.comparingInt(a -> lexiconMap.get(a.term).docFrequency));

        // process one inverted list at a time, from shortest to longest list
        Map<Integer,Double> table = new HashMap<>(); // docID: Impact Score
        for (int i = 0; i < n; i++) {
            InvertedList list = lists.get(i);
            // 遍历倒排列表，更新哈希表
            ListAccessAPI.updateTable(table, list, indexFile, pageTable, lexiconMap.get(list.term), docCount, k1, B, avgDocLen);
        }

        //If score large enough, insert into top-k heap
        for(Map.Entry<Integer,Double> entry: table.entrySet()){
            int did = entry.getKey();
            double score = entry.getValue();
            if(topResults.size() < resSize){
                topResults.add(new Document(did, score));
            }else{
                if(score > topResults.peek().score){
                    topResults.poll();
                    topResults.add(new Document(did, score));
                }
            }
        }
        return topResults;
    }
//    public static PriorityQueue<Document> queryProcessing(String query) throws IOException {
//        PriorityQueue<Document> topResults = new PriorityQueue<>((a, b) -> Double.compare(a.score, b.score));
//        String[] terms = query.split("&");
//        int n = terms.length;
//
//        List<InvertedList> lists = new ArrayList<>();
//        for (String term : terms) {
//            if(!lexiconMap.containsKey(term)){
//                System.out.println(term + "doesn't exist in lexicon");
//                return topResults;
//            }
//            lists.add(ListAccessAPI.openList(term, lexiconMap, metadataFile));
//        }
//        lists.sort(Comparator.comparingInt(a -> lexiconMap.get(a.term).docFrequency));
//
//        int maxDocID = lists.get(0).metadata.lastDocIds[lists.get(0).metadata.lastDocIds.length-1];
//        int did = 0, d = 0;
//        while(did < maxDocID){
//            //get next post from the shortest list
//            did = ListAccessAPI.nextGEQ(0, lists.get(0), indexFile);
//
//            // see if you find entries with same docID in other lists
//            for (int i = 1; i < n && (d = ListAccessAPI.nextGEQ(did, lists.get(i), indexFile)) == did; i++);
//
//            if(d > did) did = d;
//            else if (d == -1){// no such element exists
//                break;
//            }
//            else{
//                int score = 0;
//                for (int i = 0; i < n; i++) {
//                    score += ListAccessAPI.getScore(lists.get(i), pageTable, indexFile, lexiconMap.get(lists.get(i).term), docCount, k1, b, avgDocLen, did);
//                }
//                //If score large enough, insert into top-k heap
//                if(topResults.size() < resSize || score > topResults.peek().score){
//                    topResults.add(new Document(did, score));
//                }
//                // increase did to search for next post
//                did++;
//            }
//        }
//        return topResults;
//    }
}

