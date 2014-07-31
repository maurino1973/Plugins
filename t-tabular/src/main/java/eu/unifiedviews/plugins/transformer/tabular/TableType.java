package eu.unifiedviews.plugins.transformer.tabular;

import java.util.Arrays;
import java.util.List;

/**
 * Types of tables that we can extract from.
 * 
 * @author Škoda Petr
 */
public class TableType {
 
    private TableType() {
        
    }
    
    public static final String CSV = "csv";
    
    public static final String DBF = "dbf";
    
    /**
     * 
     * @return List with all supported types.
     */    
    public static List<String> getAll() {
        return Arrays.asList(CSV, DBF);
    }
    
}
