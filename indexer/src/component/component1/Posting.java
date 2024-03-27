package component.component1;

/**
 * @author Hanlin Wang
 */
public class Posting implements Comparable<Posting>{
    public String word;
    public int docID;
    public int freq;

    public Posting(String word, Integer docID, Integer freq) {
        this.word = word;
        this.docID = docID;
        this.freq = freq;
    }

    @Override
    public int compareTo(Posting other) {
        int wordComparison = this.word.compareTo(other.word);
        if (wordComparison != 0) {
            return wordComparison;
        }
        if (this.docID != other.docID) {
            return Integer.compare(this.docID, other.docID);
        }
        return Integer.compare(this.freq, other.freq);
    }

    public String getWord() {
        return word;
    }

    public int getDocID() {
        return docID;
    }

    public int getFreq() {
        return freq;
    }

    @Override
    public String toString() {
        return word + " " + docID + " " + freq;
    }

}
