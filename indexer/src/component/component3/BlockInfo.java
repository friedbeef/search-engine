package component.component3;

public class BlockInfo {
    public int docIDBlockSize;
    public int freqBlockSize;
    public int lastDocID;

    BlockInfo(int docIDBlockSize, int freqBlockSize, int lastDocID) {
        this.docIDBlockSize = docIDBlockSize;
        this.freqBlockSize = freqBlockSize;
        this.lastDocID = lastDocID;
    }

    public int getLastDocID() {
        return lastDocID;
    }
}