package component.component2;

import Util.FileUtils.FileIndexExtractor;

import java.io.*;
import java.util.*;

/**
 * @author Hanlin Wang
 */
public class Merge {

    public static void mergeSortedTempFiles(String tempFilePath, String dstFilePath) {
        PriorityQueue<MergedPosting> pq = new PriorityQueue<>();
        Map<Integer,BufferedReader> readers = new HashMap<>();
        FileIndexExtractor fileIndexExtractor = new FileIndexExtractor();
        try {
            // 为每个临时文件创建BufferedReader并读取第一个posting
            File tempDir = new File(tempFilePath);
            for (File tempFile : tempDir.listFiles()) {
                String tempFileName = tempFile.getName();
                if (tempFileName.startsWith("temp") && tempFileName.endsWith(".txt")) {
                    int fileIndex = fileIndexExtractor.extractNumber(tempFileName);

                    // Create br for each file
                    BufferedReader br = new BufferedReader(new FileReader(tempFile));
                    readers.put(fileIndex,br);

                    // Initialize heap, read first line of each file
                    String line = br.readLine();
                    if (line != null) {
                        pq.add(new MergedPosting(line, fileIndex));
                    }
                }
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(dstFilePath));

            while (!pq.isEmpty()) {
                MergedPosting current = pq.poll();
                // 将下一个posting根据fileIndex放入pq
                String nextLine = readers.get(current.fileIndex).readLine();
                if (nextLine != null) {
                    pq.add(new MergedPosting(nextLine, current.fileIndex));
                }

                bw.write(current.posting.toString());
                if (!pq.isEmpty()) bw.write(System.lineSeparator());
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (BufferedReader br : readers.values()) {
                if(br != null){
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        String tempFilePath = "/Users/hanlin/Desktop/temp1/";
        String dstFilePath = "/Users/hanlin/Desktop/merge1/merged.txt";
        mergeSortedTempFiles(tempFilePath, dstFilePath);
    }
}
