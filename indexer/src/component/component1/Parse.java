package component.component1;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


public class Parse {
    private static final Pattern TERM_PATTERN = Pattern.compile("\\p{L}+");

    public static List<DocumentInfo> docInfoList = new ArrayList<>();
    private static int docID = -1;
    /**
     * Parsing terms then sort and write to temp files
     * @param srcFilePath path of data
     * @param tempFilePath path of the file to write temp files
     */
    public static void parsing(String srcFilePath, String tempFilePath, String dataFilePath) {
        int termCounter = 0;
        String currentURL = null;
        List<Posting> buffer = new ArrayList<>();
        BufferedReader br = null;
        boolean insideText = false; // if inside <TEXT>
        boolean isFirstLineInsideText = false; // if it's the first line of <TEXT>（URL）

        Map<String,Integer> freq = new HashMap<>();// 每个文档中单词的出现次数

        int tempFileCount = 0;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFilePath), StandardCharsets.UTF_8));
            String line;

            while ((line = br.readLine()) != null) {
                // start of a DOC
                if (line.contains("<TEXT>")) {
                    insideText = true;
                    isFirstLineInsideText = true;
                    continue;
                }

                // end of a DOC
                if (line.contains("</TEXT>")) {
                    insideText = false;

                    // freq --> postings
                    for(Map.Entry<String, Integer> entry: freq.entrySet()){
                        buffer.add(new Posting(entry.getKey(), docID, entry.getValue()));
                    }
                    freq = new HashMap<>(); // reset freq

                    if (buffer.size() > 10000000) { // check buffer size and write to temp files
                        Collections.sort(buffer);
                        writeBufferToTempFile(buffer, tempFilePath + "temp" + tempFileCount++ + ".txt");
                        buffer.clear();
                    }

                    // save doc info
                    docInfoList.add(new DocumentInfo(docID, currentURL, termCounter));
                    termCounter = 0;
                    currentURL = null;
                    continue;
                }

                // inside <TEXT>
                if (insideText) {
                    // first line (URL) of <TEXT>
                    if (isFirstLineInsideText) {
                        docID++;
                        currentURL = line.trim();
                        isFirstLineInsideText = false;
                        continue;
                    }
                    // extract term
                    Matcher matcher = TERM_PATTERN.matcher(line);
                    while (matcher.find()) {
                        String term = matcher.group().toLowerCase();
                        freq.put(term,freq.getOrDefault(term,0)+1);
                        termCounter++;
                    }
                }
            }
            saveDocumentInfoList(docInfoList, dataFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Write postings in buffer to file
     */
    public static void writeBufferToTempFile(List<Posting> buffer, String filePath) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));

            int size = buffer.size();
            for (int i = 0; i < size; i++) {
                Posting p = buffer.get(i);
                bw.write(p.toString());

                if (i != size - 1) {
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<DocumentInfo> getDocInfoList() {
        return docInfoList;
    }

    public static void saveDocumentInfoList(List<DocumentInfo> docInfoList, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(docInfoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readLinesFromFile(String filePath, int linesToRead) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader((new FileInputStream(filePath)), StandardCharsets.UTF_8))) {
            String line;
            int cnt = 0;
            while ((line = br.readLine()) != null && cnt < linesToRead) {
                System.out.println(line);
                cnt++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        String srcFilePath = "msmarco-docs.trec";
        String tempFilePath = "/Users/hanlin/Desktop/temp1/";
        String dataFilePath = "document_info_test.ser";
        //readLinesFromFile(srcFilePath, 100);

        parsing(srcFilePath, tempFilePath, dataFilePath);

    }
}
