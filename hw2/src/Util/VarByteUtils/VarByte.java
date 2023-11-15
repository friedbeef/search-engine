package Util.VarByteUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VarByte {

    public static void main(String[] args) throws IOException {
        String testFileName = "varintlong_test.bin";
        try {
            // 需要测试的整数和长整型数组
            int[] testInts = {5, 127, 128, 16385};
            long[] testLongs = {5L, 127L, 128L, 16385L, (1L << 35)};

            // 写入测试数据
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(testFileName))) {
                for (int value : testInts) {
                    writeVarInt(dos, value);
                }
                for (long value : testLongs) {
                    writeVarLong(dos, value);
                }
            }

            // 读取并验证测试数据
            try (DataInputStream dis = new DataInputStream(new FileInputStream(testFileName))) {
                for (int originalValue : testInts) {
                    int readValue = readVarInt(dis);
                    if (originalValue != readValue) {
                        System.out.println("Mismatch! Original: " + originalValue + ", Read: " + readValue);
                    } else {
                        System.out.println("Int read correctly: " + readValue);
                    }
                }
                for (long originalValue : testLongs) {
                    long readValue = readVarLong(dis);
                    if (originalValue != readValue) {
                        System.out.println("Mismatch! Original: " + originalValue + ", Read: " + readValue);
                    } else {
                        System.out.println("Long read correctly: " + readValue);
                    }
                }
            }

            // 删除测试文件
            new File(testFileName).delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
//        List<int[]> pairs = new ArrayList<>();
//        pairs.add(new int[]{33549, 1});
////        pairs.add(new int[]{12, 1});
////        pairs.add(new int[]{17, 1});
////        pairs.add(new int[]{167, 1});
////        pairs.add(new int[]{4561, 2});
////        pairs.add(new int[]{789321, 3});
////        pairs.add(new int[]{1011123, 4});
//
//        // 应用偏移量 offset
//        for (int i = pairs.size() - 1; i > 0; i--) {
//            pairs.get(i)[0] -= pairs.get(i - 1)[0];
//        }
//        try (FileOutputStream fos = new FileOutputStream("encoded_data_test.bin")) {
//            for (int[] pair : pairs) {
//                int bytesWritten = writeVarByte(fos, pair[0]);
//                writeVarByte(fos, pair[1]);
//            }
//        }
//        System.out.println("The data has been encoded and written to encoded_data.bin");
//
//        try (FileInputStream fis = new FileInputStream("encoded_data_test.bin")) {
//            int previousDocID = 0;
//
//            while (fis.available() > 0) {
//                int docID = readVarByte(fis) + previousDocID;
//                int freq = readVarByte(fis);
//
//                System.out.println("docID: " + docID + ", freq: " + freq);
//
//                previousDocID = docID;
//            }
//        }
    }
    public static int readVarInt(DataInputStream dis) throws IOException {
        int value = 0;
        int i = 0;
        int b;
        while (((b = dis.readByte()) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
        }
        return value | (b << i);
    }

    public static int readVarInt(RandomAccessFile file) throws IOException {
        int value = 0;
        int i = 0;
        byte b;
        do {
            b = file.readByte();
            value |= (b & 0x7F) << i;
            i += 7;
        } while (b < 0); // 如果最高位是1，继续读取
        return value;
    }
    public static long readVarLong(DataInputStream dis) throws IOException {
        long value = 0;
        int i = 0;
        long b;
        while (((b = dis.readByte()) & 0x80L) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
        }
        return value | (b << i);
    }
    public static long readVarLong(RandomAccessFile file) throws IOException {
        long value = 0;
        int i = 0;
        long b;
        while (((b = file.readByte()) & 0x80L) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
        }
        return value | (b << i);
    }
    public static void writeVarInt(DataOutputStream dos, int value) throws IOException {
        while ((value & 0xFFFFFF80) != 0L) {
            dos.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        dos.writeByte(value & 0x7F);
    }
    public static void writeVarInt(RandomAccessFile file, int value) throws IOException {
        while ((value & 0xFFFFFF80) != 0L) {
            file.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        file.writeByte(value & 0x7F);
    }

    public static void writeVarLong(DataOutputStream dos, long value) throws IOException {
        while ((value & 0xFFFFFFFFFFFFFF80L) != 0L) {
            dos.writeByte(((int) value & 0x7F) | 0x80);
            value >>>= 7;
        }
        dos.writeByte((int) value & 0x7F);
    }
    public static void writeVarLong(RandomAccessFile file, long value) throws IOException {
        while ((value & 0xFFFFFFFFFFFFFF80L) != 0L) {
            file.writeByte(((int) value & 0x7F) | 0x80);
            value >>>= 7;
        }
        file.writeByte((int) value & 0x7F);
    }


}
