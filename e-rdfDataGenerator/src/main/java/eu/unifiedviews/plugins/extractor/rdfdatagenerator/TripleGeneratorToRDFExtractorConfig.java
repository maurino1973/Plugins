package eu.unifiedviews.plugins.extractor.rdfdatagenerator;

public class TripleGeneratorToRDFExtractorConfig {

    private int tripleCount = 1000000;

    private int commitSize = 50000;

    private String outputGraphSymbolicName = "E-RDFDataGenerator/output" + String.valueOf(new java.util.Random().nextInt(100));

    public int getTripleCount() {
        return tripleCount;
    }

    public void setTripleCount(int tripletCount) {
        this.tripleCount = tripletCount;
    }

    public int getCommitSize() {
        return commitSize;
    }

    public void setCommitSize(int commitSize) {
        this.commitSize = commitSize;
    }

    public String getOutputGraphSymbolicName() {
        return outputGraphSymbolicName;
    }

    public void setOutputGraphSymbolicName(String outputGraphSymbolicName) {
        this.outputGraphSymbolicName = outputGraphSymbolicName;
    }
}
