import Util.LexiconUtils.LexiconUtils;
import Util.UrlListUtils.UrlListUtils;
import component.component1.DocumentInfo;
import component.component3.Lexicon;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * @author Hanlin Wang
 */
public class Test {
    public static void main(String[] args) {
        Map<String, Lexicon> lexiconMap = LexiconUtils.readLexiconFromFile("lexicon.bin");
        List<DocumentInfo> pageTable = UrlListUtils.loadDocumentInfoList("document_info.ser");
        RandomAccessFile metadataFile;
        RandomAccessFile indexFile;
        int docCount = pageTable.size();
        System.out.println("docCount = " + docCount);
        double avgDocLen = 0;
        double k1 = 1.5;
        double B = 0.75;
        int resSize = 10;
        for(DocumentInfo docInfo : pageTable) {
            avgDocLen += docInfo.termCount; // 先累加所有的termCount
        }
        avgDocLen /= docCount;
        System.out.println("avgDocLen = " + avgDocLen);
        try {
            metadataFile = new RandomAccessFile("metadata.bin", "r");
            indexFile = new RandomAccessFile("inverted_index.bin", "r");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        Scanner scanner = new Scanner(System.in);
//        while (true) {
//            System.out.print("Enter query (press Enter to stop): ");
//            String query = scanner.nextLine();
//            // 当输入为空字符串时，停止循环
//            if (query.isEmpty()) {
//                break;
//            }
//            // 处理输入
//            System.out.println("You entered: " + query);
//            PriorityQueue<Document> pq = null;
//            try {
//                long startTime = System.currentTimeMillis(); // 记录开始时间
//                pq = SearchEngine.queryProcessorAND(query, lexiconMap,
//                        metadataFile, indexFile,
//                        pageTable, docCount,
//                        avgDocLen, k1, B, resSize);
////                pq = SearchEngine.queryProcessorOR(query,  lexiconMap,
////                        metadataFile, indexFile,
////                        pageTable, docCount,
////                avgDocLen, k1, B, resSize);
//                long endTime = System.currentTimeMillis(); // 记录结束时间
//                double duration = (endTime - startTime) / 1000.0; // 计算运行时长，转换为秒
//                System.out.println("Query processing took " + duration + " seconds.");
//                while(!pq.isEmpty()){
//                    Document doc = pq.poll();
//                    System.out.println("URL: " + pageTable.get(doc.docID).url + ", score = " + doc.score);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        System.out.println("Program stopped.");
//        scanner.close();
//
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter query type (AND/OR, press Enter to stop): ");
            String queryType = scanner.nextLine().trim().toUpperCase();

            // 当输入为空字符串时，停止循环
            if (queryType.isEmpty()) {
                break;
            }

            if (!queryType.equals("AND") && !queryType.equals("OR")) {
                System.out.println("Invalid query type. Please enter 'AND' or 'OR'.");
                continue;
            }

            System.out.print("Enter query: ");
            String query = scanner.nextLine();

            // 处理输入
            System.out.println("You entered: " + query);
            PriorityQueue<Document> pq = null;
            try {
                long startTime = System.currentTimeMillis(); // 记录开始时间

                if (queryType.equals("AND")) {
                    pq = SearchEngine.queryProcessorAND(query, lexiconMap,
                            metadataFile, indexFile,
                            pageTable, docCount,
                            avgDocLen, k1, B, resSize);
                } else if (queryType.equals("OR")) {
                    pq = SearchEngine.queryProcessorOR(query, lexiconMap,
                            metadataFile, indexFile,
                            pageTable, docCount,
                            avgDocLen, k1, B, resSize);
                }

                long endTime = System.currentTimeMillis(); // 记录结束时间
                double duration = (endTime - startTime) / 1000.0; // 计算运行时长，转换为秒
                System.out.println("Query processing took " + duration + " seconds.");

                while (!pq.isEmpty()) {
                    Document doc = pq.poll();
                    System.out.println("URL: " + pageTable.get(doc.docID).url + ", score = " + doc.score);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Program stopped.");
        scanner.close();
    }
}
