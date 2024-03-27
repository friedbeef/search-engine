package component.component3;

import Util.LexiconUtils.LexiconUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hanlin Wang
 */
public class Lexicon implements Serializable {
    public static void main(String[] args) {
        // 创建并填充lexicon
        Map<String, Lexicon> lexicon = new HashMap<>();
        lexicon.put("term1", new Lexicon(1L,1L,13,11,1L));
        lexicon.put("term2", new Lexicon(2L,2L,3123,4,5L));


        // 写入文件
        String filePath = "lexicon.dat";
        LexiconUtils.writeLexiconToFile(lexicon, filePath);

        // 从文件读取
        Map<String, Lexicon> readLexicon = LexiconUtils.readLexiconFromFile(filePath);

        // 验证读取结果
        if (readLexicon != null) {
            for (Map.Entry<String, Lexicon> entry : readLexicon.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue().toString());
            }
        }
    }

    public long startOffset;
    public long endOffset;
    public int numBlocks;
    public int docFrequency; // 表示包含此词的文档总数
    public long metadataOffset;

    public Lexicon(long startOffset, long endOffset, int numBlocks, int docFrequency, long metadataOffset) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.numBlocks = numBlocks;
        this.docFrequency = docFrequency;
        this.metadataOffset = metadataOffset;
    }

    @Override
    public String toString() {
        return "startOffset: " + startOffset +
                ", endOffset: " +  endOffset +
                ", numBlocks: " + numBlocks +
                ", docFrequency: " + docFrequency +
                ", metadataOffset: " + metadataOffset;
    }
}
