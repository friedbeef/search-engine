package Util.MetadataUtils;

import Util.VarByteUtils.VarByte;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MetadataUtils {
    public static void main(String[] args) {
        try {
            // 打开文件以进行读写
            RandomAccessFile metadataFile = new RandomAccessFile("metadata.bin", "rw");

            // 准备测试数据
            int[] lastDocIds1 = {1, 3, 6};
            long[] docIdBlockStarts1 = {0, 10, 20};
            int[] docIdBlockSizes1 = {10, 10, 10};
            long[] freqBlockStarts1 = {30, 40, 50};
            int[] freqBlockSizes1 = {10, 10, 10};

            int[] lastDocIds2 = {7, 9, 12};
            long[] docIdBlockStarts2 = {60, 70, 80};
            int[] docIdBlockSizes2 = {10, 10, 10};
            long[] freqBlockStarts2 = {90, 100, 110};
            int[] freqBlockSizes2 = {10, 10, 10};

            // 写入第一组数据并记录起始位置
            long pointer1 = metadataFile.getFilePointer();
            compressMetadata(metadataFile, lastDocIds1, docIdBlockStarts1, docIdBlockSizes1, freqBlockStarts1, freqBlockSizes1);

            // 写入第二组数据并记录起始位置
            long pointer2 = metadataFile.getFilePointer();
            compressMetadata(metadataFile, lastDocIds2, docIdBlockStarts2, docIdBlockSizes2, freqBlockStarts2, freqBlockSizes2);

            // 读取并验证第一组数据
            metadataFile.seek(pointer1);
            Metadata metadata1 = decompressMetadata(metadataFile);
            System.out.println("First metadata block: " + Arrays.toString(metadata1.lastDocIds));
            System.out.println("metadata1.docIdBlockStarts = " + Arrays.toString(metadata1.docIdBlockStarts));
            System.out.println("metadata1.docIdBlockSizes = " + Arrays.toString(metadata1.docIdBlockSizes));
            System.out.println("metadata1.freqBlockStarts = " + Arrays.toString(metadata1.freqBlockStarts));
            System.out.println("metadata1.freqBlockSizes = " + Arrays.toString(metadata1.freqBlockSizes));

            // 读取并验证第二组数据
            metadataFile.seek(pointer2);
            Metadata metadata2 = decompressMetadata(metadataFile);
            System.out.println("Second metadata block: " + Arrays.toString(metadata2.lastDocIds));
            System.out.println("metadata2.docIdBlockStarts = " + Arrays.toString(metadata2.docIdBlockStarts));
            System.out.println("metadata2.docIdBlockSizes = " + Arrays.toString(metadata2.docIdBlockSizes));
            System.out.println("metadata2.freqBlockStarts = " + Arrays.toString(metadata2.freqBlockStarts));
            System.out.println("metadata2.freqBlockSizes = " + Arrays.toString(metadata2.freqBlockSizes));
            // 关闭文件
            metadataFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void compressMetadata(
            RandomAccessFile metadataFile,
            int[] lastDocIds,
            long[] docIdBlockStarts,
            int[] docIdBlockSizes,
            long[] freqBlockStarts,
            int[] freqBlockSizes) throws IOException {

        // 写入块的数量
        metadataFile.writeInt(lastDocIds.length);

        // 压缩最后一个docID的偏移
        int lastDocId = 0;
        for (int docId : lastDocIds) {
            VarByte.writeVarInt(metadataFile, docId - lastDocId);
            lastDocId = docId;
        }

        // 压缩docID块起始位置的偏移
        long lastStart = 0;
        for (long start : docIdBlockStarts) {
            VarByte.writeVarLong(metadataFile, start - lastStart);
            lastStart = start;
        }

        // 压缩docID块大小
        for (int size : docIdBlockSizes) {
            VarByte.writeVarInt(metadataFile, size);
        }

        // 压缩freq块起始位置的偏移
        lastStart = 0;
        for (long start : freqBlockStarts) {
            VarByte.writeVarLong(metadataFile, start - lastStart);
            lastStart = start;
        }

        // 压缩freq块大小
        for (int size : freqBlockSizes) {
            VarByte.writeVarInt(metadataFile, size);
        }
    }

    public static Metadata decompressMetadata(RandomAccessFile metadataFile) throws IOException {
        // 读取块的数量
        int blocksCount = metadataFile.readInt();

        int[] lastDocIds = new int[blocksCount];
        long[] docIdBlockStarts = new long[blocksCount];
        int[] docIdBlockSizes = new int[blocksCount];
        long[] freqBlockStarts = new long[blocksCount];
        int[] freqBlockSizes = new int[blocksCount];

        // 解压最后一个docID的偏移
        int lastDocId = 0;
        for (int i = 0; i < blocksCount; i++) {
            lastDocIds[i] = lastDocId += VarByte.readVarInt(metadataFile);
        }

        // 解压docID块起始位置的偏移
        long lastStart = 0;
        for (int i = 0; i < blocksCount; i++) {
            docIdBlockStarts[i] = lastStart += VarByte.readVarLong(metadataFile);
        }

        // 解压docID块大小
        for (int i = 0; i < blocksCount; i++) {
            docIdBlockSizes[i] = VarByte.readVarInt(metadataFile);
        }

        // 解压freq块起始位置的偏移
        lastStart = 0;
        for (int i = 0; i < blocksCount; i++) {
            freqBlockStarts[i] = lastStart += VarByte.readVarLong(metadataFile);
        }

        // 解压freq块大小
        for (int i = 0; i < blocksCount; i++) {
            freqBlockSizes[i] = VarByte.readVarInt(metadataFile);
        }

        return new Metadata(lastDocIds, docIdBlockStarts, docIdBlockSizes, freqBlockStarts, freqBlockSizes);
    }

}
