package eu.unifiedviews.plugins.extractor.rdffromfile;

/**
 * Interface provides information about extracted triples.
 * 
 * @author Jiri Tomes
 */
public interface TripleCounter {

    /**
     * Returns count of extracted triples.
     * 
     * @return count of extracted triples.
     */
    public long getTripleCount();

    /**
     * Returns true if there is no triples, false otherwise.
     * 
     * @return true if there is no triples, false otherwise.
     */
    public boolean isEmpty();

    /**
     * Set count of extracted triples to 0.
     */
    public void reset();
}
