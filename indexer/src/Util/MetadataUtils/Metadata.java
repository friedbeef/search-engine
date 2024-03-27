package Util.MetadataUtils;

public class Metadata {
        public int[] lastDocIds;
        public long[] docIdBlockStarts;
        public int[] docIdBlockSizes;
        public long[] freqBlockStarts;
        public int[] freqBlockSizes;

        public Metadata(int[] lastDocIds, long[] docIdBlockStarts, int[] docIdBlockSizes, long[] freqBlockStarts, int[] freqBlockSizes) {
            this.lastDocIds = lastDocIds;
            this.docIdBlockStarts = docIdBlockStarts;
            this.docIdBlockSizes = docIdBlockSizes;
            this.freqBlockStarts = freqBlockStarts;
            this.freqBlockSizes = freqBlockSizes;
        }
    }