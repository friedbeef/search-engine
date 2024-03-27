import Main.InvertedIndex;
import Util.BlockUtils.BlockUtils;
import Util.LexiconUtils.LexiconUtils;
import Util.MetadataUtils.Metadata;
import Util.MetadataUtils.MetadataUtils;
import component.component1.DocumentInfo;
import component.component3.Lexicon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class ListAccessAPI {
    public static void main(String[] args) {
        String metadataPath = "metadata.bin";
        String lexiconFilePath = "lexicon.bin";
        String indexPath = "inverted_index.bin";
        try {
            RandomAccessFile indexFile = new RandomAccessFile(indexPath, "r");
            RandomAccessFile metadataFile = new RandomAccessFile(metadataPath, "r");
            Map<String, Lexicon> lexiconMap = LexiconUtils.readLexiconFromFile(lexiconFilePath);
            String[] terms = {"a","b","c"};
            int n = terms.length;

            List<InvertedList> lists = new ArrayList<>();
            for (int i = 0; i < n; i++) {

                lists.add(ListAccessAPI.openList(terms[i], lexiconMap, metadataFile));
            }

            lists.sort(Comparator.comparingInt(a -> lexiconMap.get(a.term).docFrequency));

            for(InvertedList list: lists){
                System.out.println(list);
                System.out.println(nextGEQ(7, list, indexFile));
                System.out.println(getFreq(list, indexFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * open list for term t for reading
     */
    public static InvertedList openList(String term, Map<String, Lexicon> lexiconMap, RandomAccessFile metadataFile) throws IOException {
        //decompress metadata
        Lexicon lexicon = lexiconMap.get(term);
        metadataFile.seek(lexicon.metadataOffset);
        Metadata metadata = MetadataUtils.decompressMetadata(metadataFile);
        return new InvertedList(term, metadata);
    }

    /**
     * find the next posting in list lp with docID >= k
     * @return return its docID, return value > MAXDID if none exists.
     */
    public static int nextGEQ(int k, InvertedList invertedList, RandomAccessFile indexFile) throws IOException {
        Metadata metadata = invertedList.getMetadata();
        int[] lastDocIds = metadata.lastDocIds;

        int blockIdx = 0, idxInBlock = 0;
        while (lastDocIds[blockIdx] < k) {
            blockIdx++; // skip blocks
            if (blockIdx >= lastDocIds.length) { // if no such element exists
                return -1; // using -1 to indicate no such element
            }
        }

        // uncompress the specific block
        indexFile.seek(metadata.docIdBlockStarts[blockIdx]);
        List<Integer> docIdBlock = BlockUtils.decompressDocIds(indexFile, metadata.docIdBlockSizes[blockIdx]);

        while (docIdBlock.get(idxInBlock) < k) {
            idxInBlock++; // move to the next docID within the block
            // Note: It is assumed that 'idxInBlock' will not exceed 'docIdBlock.length' because 'last[block] >= k'
        }
        invertedList.setTargetBlockIdx(blockIdx);
        invertedList.setTargetIdxInBlock(idxInBlock);
        return docIdBlock.get(idxInBlock); // return the found docID
    }

    /**
     * get the impact score of the current posting in list lp
     */
    public static double getScore(InvertedList invertedList, List<DocumentInfo> pageTable, RandomAccessFile indexFile, Lexicon lexicon, int docCount, double k1, double b, double avgDocLen, int did) throws IOException {
        int numOfDocsContainTerm = lexicon.docFrequency;
        int termFreqInDoc = getFreq(invertedList, indexFile);
        int docLen = pageTable.get(did).termCount;
        double idf = Math.log((docCount - numOfDocsContainTerm + 0.5) / (numOfDocsContainTerm + 0.5));
        double termFrequencyFactor = ((k1 + 1) * termFreqInDoc) / (k1 * ((1 - b) + b * (docLen / (double) avgDocLen)) + termFreqInDoc);
        return idf * termFrequencyFactor;
    }
    public static double getScore(int termFreqInDoc, List<DocumentInfo> pageTable, Lexicon lexicon, int docCount, double k1, double b, double avgDocLen, int did) throws IOException {
        int numOfDocsContainTerm = lexicon.docFrequency;
        int docLen = pageTable.get(did).termCount;
        double idf = Math.log((docCount - numOfDocsContainTerm + 0.5) / (numOfDocsContainTerm + 0.5));
        double termFrequencyFactor = ((k1 + 1) * termFreqInDoc) / (k1 * ((1 - b) + b * (docLen / (double) avgDocLen)) + termFreqInDoc);
        return idf * termFrequencyFactor;
    }
    /**
     * get the freq of the current posting in list lp
     */
    public static int getFreq(InvertedList invertedList, RandomAccessFile indexFile) throws IOException {
        Metadata metadata = invertedList.getMetadata();

        // uncompress the specific block
        indexFile.seek(metadata.freqBlockStarts[invertedList.getTargetBlockIdx()]);
        List<Integer> freqBlock = BlockUtils.decompressFreqs(indexFile, metadata.freqBlockSizes[invertedList.getTargetBlockIdx()]);

        return freqBlock.get(invertedList.getTargetIdxInBlock());
    }

    public static void updateTable(Map<Integer,Double> table, InvertedList list,
                                   RandomAccessFile indexFile, List<DocumentInfo> pageTable,
                                   Lexicon lexicon, int docCount,
                                   double k1, double B, double avgDocLen) throws IOException {
        Metadata metadata = list.getMetadata();
        // 每一个docID
        List<Integer> docIdBlock = new ArrayList<>();
        List<Integer> freqBlock = new ArrayList<>();
        int blockIdx = 0;
        while(blockIdx < metadata.lastDocIds.length){
            indexFile.seek(metadata.docIdBlockStarts[blockIdx]);
            docIdBlock = BlockUtils.decompressDocIds(indexFile, metadata.docIdBlockSizes[blockIdx]);
            indexFile.seek(metadata.freqBlockStarts[blockIdx]);
            freqBlock = BlockUtils.decompressFreqs(indexFile, metadata.freqBlockSizes[blockIdx]);

            for(int idxInBlock = 0; idxInBlock < docIdBlock.size(); idxInBlock++){
                int docId = docIdBlock.get(idxInBlock);
                int freq = freqBlock.get(idxInBlock);
                double score = getScore(freq, pageTable, lexicon, docCount, k1, B, avgDocLen, docId);
                table.put(docId, table.getOrDefault(docId,0d) + score);
            }
            blockIdx++;
        }
    }
}
