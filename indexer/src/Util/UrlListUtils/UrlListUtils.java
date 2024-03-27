package Util.UrlListUtils;

import component.component1.DocumentInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hanlin Wang
 */
public class UrlListUtils {
    public static void main(String[] args) {
        List<DocumentInfo> docInfoList = new ArrayList<>();

        // 添加示例文档信息到列表
        docInfoList.add(new DocumentInfo(1, "http://example.com/1", 100));
        docInfoList.add(new DocumentInfo(2, "http://example.com/2", 200));
        docInfoList.add(new DocumentInfo(3, "http://example.com/3", 300));

        // 假设 docInfoList 已经被填充数据
        String filename = "document_info_test.ser"; // 使用 .ser 作为序列化文件的扩展名是一种惯例

        // 保存列表到文件
        saveDocumentInfoList(docInfoList, filename);

        // 从文件加载列表
        List<DocumentInfo> loadedDocInfoList = loadDocumentInfoList(filename);
        if (loadedDocInfoList != null) {
            // 输出加载的列表，确认数据
            for (DocumentInfo docInfo : loadedDocInfoList) {
                System.out.println(docInfo);
            }
        }
    }
    public static void saveDocumentInfoList(List<DocumentInfo> docInfoList, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(docInfoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<DocumentInfo> loadDocumentInfoList(String filename) {
        List<DocumentInfo> docInfoList = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            docInfoList = (List<DocumentInfo>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return docInfoList;
    }
}
