package Util.BlockUtils;

import Util.VarByteUtils.VarByte;
import component.component1.Posting;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hanlin Wang
 */
public class BlockUtils {
    public static void compressBlock(List<Posting> postings, RandomAccessFile indexFile,
                                      List<Integer> lastDocIds, List<Long> docIdBlockStarts,
                                      List<Integer> docIdBlockSizes, List<Long> freqBlockStarts,
                                      List<Integer> freqBlockSizes) throws IOException {

        long docIdBlockStart = indexFile.getFilePointer();
        int docIdBlockSize = compressDocIds(postings, indexFile);
        long freqBlockStart = indexFile.getFilePointer();
        int freqBlockSize = compressFreqs(postings, indexFile);

        // 更新元数据列表
        lastDocIds.add(postings.get(postings.size() - 1).docID);
        docIdBlockStarts.add(docIdBlockStart);
        docIdBlockSizes.add(docIdBlockSize);
        freqBlockStarts.add(freqBlockStart);
        freqBlockSizes.add(freqBlockSize);
    }
    public static int compressDocIds(List<Posting> postings, RandomAccessFile indexFile) throws IOException {
        int lastDocId = 0;
        int blockSize = 0;
        for (Posting posting : postings) {
            int delta = posting.docID - lastDocId; // 差分编码
            VarByte.writeVarInt(indexFile, delta); // 写入压缩的差分值
            lastDocId = posting.docID;
            blockSize++; // 可以是字节大小或者压缩后的实体数
        }
        return blockSize; // 这里返回的是实体数量，也可以是压缩后的字节大小
    }

    public static int compressFreqs(List<Posting> postings, RandomAccessFile indexFile) throws IOException {
        int blockSize = 0;
        for (Posting posting : postings) {
            VarByte.writeVarInt(indexFile, posting.freq); // 写入压缩的频率
            blockSize++; // 同上，可表示实体数量或字节大小
        }
        return blockSize;
    }

    public static List<Integer> decompressDocIds(RandomAccessFile indexFile, int blockSize) throws IOException {
        List<Integer> docIds = new ArrayList<>(blockSize);
        int lastDocId = 0;
        for (int i = 0; i < blockSize; i++) {
            int delta = VarByte.readVarInt(indexFile); // 读取压缩的差分值
            lastDocId += delta; // 恢复原始文档ID
            docIds.add(lastDocId);
        }
        return docIds;
    }

    public static List<Integer> decompressFreqs(RandomAccessFile indexFile, int blockSize) throws IOException {
        List<Integer> freqs = new ArrayList<>(blockSize);
        for (int i = 0; i < blockSize; i++) {
            int freq = VarByte.readVarInt(indexFile); // 读取压缩的频率
            freqs.add(freq);
        }
        return freqs;
    }
}
