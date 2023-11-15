import Util.MetadataUtils.Metadata;

/**
 * @author Hanlin Wang
 */
public class InvertedList {
    String term;
    Metadata metadata;
    int targetBlockIdx;
    int targetIdxInBlock;

    public InvertedList(String term, Metadata metadata) {
        this.term = term;
        this.metadata = metadata;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public int getTargetBlockIdx() {
        return targetBlockIdx;
    }

    public void setTargetBlockIdx(int targetBlockIdx) {
        this.targetBlockIdx = targetBlockIdx;
    }

    public int getTargetIdxInBlock() {
        return targetIdxInBlock;
    }

    public void setTargetIdxInBlock(int targetIdxInBlock) {
        this.targetIdxInBlock = targetIdxInBlock;
    }

    @Override
    public String toString() {
        return "Term: " + term + ", metatdata: " + metadata;
    }
}
