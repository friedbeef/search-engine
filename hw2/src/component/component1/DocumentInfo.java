package component.component1;

import java.io.Serializable;

public class DocumentInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    public int docID;
    public String url;
    public int termCount;

    public DocumentInfo(int docID, String url, int termCount) {
        this.docID = docID;
        this.url = url;
        this.termCount = termCount;
    }

    @Override
    public String toString() {
        return "DocID: " + docID + ", URL: " + url + ", TermCount: " + termCount;
    }
}

