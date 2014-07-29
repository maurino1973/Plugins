package eu.unifiedviews.plugins.transformers.zipper;

/**
 *
 * @author Škoda Petr
 */
public class Ontology {
    
    private Ontology() {
        
    }
    
    /**
     * States that given file were packed into zip file.
     */
    public static String PREDICATE_CONTAINS_FILE = 
             "http://unifiedviews.cz/resource/domain/t-zipper/source";
    
}
