package ue.unifiedviews.plugins.extractors.http.files;

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
    public static final String PREDICATE_DOWNLOADED_FROM = 
            "http://unifiedviews.cz/resource/domain/e-http-file/downloadedFrom";    
    
}
