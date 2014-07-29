package eu.unifiedviews.plugins.transformer.rdftofiles;

/**
 *
 * @author Škoda Petr
 */
public class Ontology {
    
    private Ontology() { 
        
    }
    
    /**
     * Indicates that some resource were created from other resource(s).
     */    
    public static final String PREDICATE_TRANFORM_FROM = 
            "http://unifiedviews.cz/resource/domain/t-rdf-to-files/source";
    
}
