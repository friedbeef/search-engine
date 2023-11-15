package component.component3;

import Util.BlockUtils.BlockUtils;
import Util.LexiconUtils.LexiconUtils;
import Util.MetadataUtils.Metadata;
import Util.MetadataUtils.MetadataUtils;
import Util.VarByteUtils.VarByte;
import component.component1.Posting;

import java.io.*;
import java.util.*;

public class Reformat {
    public static void main(String[] args) {
        // 输出文件和元数据文件的路径
        //String sortedFilePath = "/Users/hanlin/Desktop/merged.txt";
        String sortedFilePath = "/Users/hanlin/Desktop/merge/merged1.txt";
        String invertedIndexPath = "inverted_index_test.bin";
        String metadataPath = "metadata_test.bin";
        String lexiconFilePath = "lexicon_testc.bin";

        // 执行压缩转换
        convertToCompressedIndex(sortedFilePath, invertedIndexPath, metadataPath, lexiconFilePath);

//        // 读取和验证词典文件
//        Map<String, Lexicon> readLexicon = LexiconUtils.readLexiconFromFile(lexiconFilePath);
//
//        // 读取和验证元数据文件
//        try (RandomAccessFile metadataFile = new RandomAccessFile(metadataPath, "r");
//             RandomAccessFile indexFile = new RandomAccessFile(invertedIndexPath, "r")) {
//            for (Map.Entry<String, Lexicon> entry : readLexicon.entrySet()) {
//                Lexicon lexicon = entry.getValue();
//                System.out.println(entry.getKey() + " - " + lexicon);
//                metadataFile.seek(lexicon.metadataOffset);
//                Metadata metadata = MetadataUtils.decompressMetadata(metadataFile);
//                System.out.println("metadata.lastDocIds = " + Arrays.toString(metadata.lastDocIds));
//                System.out.println("metadata.docIdBlockSizes = " + Arrays.toString(metadata.docIdBlockSizes));
//                indexFile.seek(lexicon.startOffset);
//                List<Integer> docIds = BlockUtils.decompressDocIds(indexFile, metadata.docIdBlockSizes[0]);
//                System.out.println(docIds);
//                indexFile.seek(metadata.freqBlockStarts[1]);
//                List<Integer> freqs = BlockUtils.decompressFreqs(indexFile, metadata.freqBlockSizes[1]);
//                System.out.println(freqs);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    private static final int BLOCK_SIZE = 128;
    public static Map<String, Lexicon> lexicon = new HashMap<>();
    public static Map<String, Lexicon> getLexicon() {
        return lexicon;
    }

    public static void convertToCompressedIndex(String sortedFilePath, String invertedIndexPath, String metadataPath, String lexiconFilePath) {

        try (
                BufferedReader br = new BufferedReader(new FileReader(sortedFilePath));
                RandomAccessFile indexFile = new RandomAccessFile(invertedIndexPath, "rw");
                RandomAccessFile metadataFile = new RandomAccessFile(metadataPath, "rw")
        ) {
            String line;
            List<Posting> currBlock = new ArrayList<>(BLOCK_SIZE);
            String currTerm = "";
            int totalDocsForTerm = 0;
            int numBlocks = 0;

            // metadata
            List<Integer> lastDocIds = new ArrayList<>();
            List<Long> docIdBlockStarts = new ArrayList<>();
            List<Integer> docIdBlockSizes = new ArrayList<>();
            List<Long> freqBlockStarts = new ArrayList<>();
            List<Integer> freqBlockSizes = new ArrayList<>();

            // inverted list starting position
            long lastListOffset = 0;

            while ((line = br.readLine()) != null) {
                // extract term
                if(line.trim().isEmpty()) continue;
                String[] parts = line.split(" ");
                String term = parts[0];

                // 如果遇到新的词项
                if (!currTerm.equals(term)) {
                    // 处理并压缩当前块（如果非空）
                    if (!currBlock.isEmpty()) {
                        numBlocks++;
                        BlockUtils.compressBlock(currBlock, indexFile, lastDocIds, docIdBlockStarts, docIdBlockSizes, freqBlockStarts, freqBlockSizes);
                        currBlock.clear();
                    }
                    // 写入元数据
                    if (!currTerm.isEmpty()) {
                        writeMetadata(currTerm, totalDocsForTerm, indexFile, metadataPath, metadataFile, numBlocks, lastListOffset, lastDocIds, docIdBlockStarts, docIdBlockSizes, freqBlockStarts, freqBlockSizes);
                    }
                    // 重置当前词项和文档计数
                    currTerm = term;
                    totalDocsForTerm = 0;
                    numBlocks = 0;
                    // 清除元数据跟踪列表
                    lastDocIds.clear();
                    docIdBlockStarts.clear();
                    docIdBlockSizes.clear();
                    freqBlockStarts.clear();
                    freqBlockSizes.clear();
                    // 更新列表起始位置
                    lastListOffset = indexFile.getFilePointer();
                }
                // Add to current block
                currBlock.add(new Posting(term, Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                totalDocsForTerm++;

                // block is full, compress
                if (currBlock.size() == BLOCK_SIZE) {
                    numBlocks++;
                    BlockUtils.compressBlock(currBlock, indexFile, lastDocIds, docIdBlockStarts, docIdBlockSizes, freqBlockStarts, freqBlockSizes);
                    currBlock.clear();
                }
            }
            // last block of last term
            if (!currBlock.isEmpty()) {
                numBlocks++;
                BlockUtils.compressBlock(currBlock, indexFile, lastDocIds, docIdBlockStarts, docIdBlockSizes, freqBlockStarts, freqBlockSizes);
            }
            // metadata for last term
            writeMetadata(currTerm, totalDocsForTerm, indexFile, metadataPath, metadataFile, numBlocks, lastListOffset,  lastDocIds, docIdBlockStarts, docIdBlockSizes, freqBlockStarts, freqBlockSizes);


            LexiconUtils.writeLexiconToFile(lexicon, lexiconFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeMetadata(String term, int totalDocsForTerm,
                                      RandomAccessFile indexFile, String metadataFilePath,
                                      RandomAccessFile metadataFile, int numBlocks,
                                      long lastListOffset,
                                      List<Integer> lastDocIds, List<Long> docIdBlockStarts,
                                      List<Integer> docIdBlockSizes, List<Long> freqBlockStarts,
                                      List<Integer> freqBlockSizes) throws IOException {
        // 添加词项到词典
        Lexicon lexiconEntry = new Lexicon(
                lastListOffset, // startOffset
                indexFile.getFilePointer(), // endOffset
                numBlocks, // numBlocks
                totalDocsForTerm, // docFrequency
                metadataFile.getFilePointer()); // metadataOffset
        lexicon.put(term, lexiconEntry);

        // 写入压缩的元数据
        // metadata读取时需不需要额外信息？
        MetadataUtils.compressMetadata(
                metadataFile,
                lastDocIds.stream().mapToInt(i->i).toArray(),
                docIdBlockStarts.stream().mapToLong(l->l).toArray(),
                docIdBlockSizes.stream().mapToInt(i->i).toArray(),
                freqBlockStarts.stream().mapToLong(l->l).toArray(),
                freqBlockSizes.stream().mapToInt(i->i).toArray()
        );

    }
}
