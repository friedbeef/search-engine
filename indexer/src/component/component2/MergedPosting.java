package component.component2;

import component.component1.Posting;

public class MergedPosting implements Comparable<MergedPosting> {
    Posting posting;
    int fileIndex;

    MergedPosting(String line, int fileIndex) {
        String[] parts = line.split(" ");
        this.posting = new Posting(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        this.fileIndex = fileIndex;
    }

    @Override
    public int compareTo(MergedPosting other) {
        return this.posting.compareTo(other.posting);
    }

    @Override
    public String toString() {
        return "posting: " + posting.toString() + ", fileIndex: " + fileIndex;
    }
}