package component.component1;

import java.io.*;
import java.util.HashMap;

public class TrecFileProcessor {

    // 序列化方法
    public static void serializeDocIndex(String trecFilePath, String serializedIndexPath) throws IOException {
            try {
                RandomAccessFile trecFile = new RandomAccessFile(trecFilePath, "r");
                HashMap<Integer, Long[]> indexMap = new HashMap<>();
                String line;
                boolean inText = false;
                long textStart = 0;
                int docID = 0;

                while ((line = trecFile.readLine()) != null) {

                    if (line.contains("<TEXT>")) {
                        inText = true;
                        String url = trecFile.readLine(); // 读取并跳过URL行
                        System.out.println("url = " + url);
                        textStart = trecFile.getFilePointer(); // 记录URL之后第一行的开头位置
                    } else if (line.contains("</TEXT>") && inText) {
                        long textEnd = trecFile.getFilePointer();
                        System.out.println("docID = " + docID);
                        indexMap.put(docID, new Long[]{textStart, textEnd});
                        docID++;
                        inText = false;
                    }
                }
                trecFile.close();
                // 序列化哈希表到文件
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serializedIndexPath));
                out.writeObject(indexMap);
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    // 反序列化方法
    public static HashMap<Integer, Long[]> deserializeDocIndex(String filePath) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));
        HashMap<Integer, Long[]> indexMap = (HashMap<Integer, Long[]>) in.readObject();
        in.close();
        return indexMap;
    }

    // 测试主方法
    public static void main(String[] args) {
        String trecFilePath = "msmarco-docs.trec";
        String serializedIndexPath = "document_index.ser";
        try {
            // 序列化文档索引
            serializeDocIndex(trecFilePath, serializedIndexPath);

            // 反序列化文档索引
            HashMap<Integer, Long[]> docIndex = deserializeDocIndex(serializedIndexPath);

            // 遍历并打印文档索引以验证内容
            for (Integer docID : docIndex.keySet()) {
                Long[] positions = docIndex.get(docID);
                System.out.println("DocID: " + docID + ", Start: " + positions[0] + ", End: " + positions[1]);
                if(docID == 10) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
