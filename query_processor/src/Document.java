class Document {
    int docID;
    double score;
    public Document(int docID, double score) {
        this.docID = docID;
        this.score = score;
    }

    @Override
    public String toString() {
        return "Document{" +
                "docID=" + docID +
                ", score=" + score +
                '}';
    }
}