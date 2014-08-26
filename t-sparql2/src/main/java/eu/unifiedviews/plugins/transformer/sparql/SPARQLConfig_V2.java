package eu.unifiedviews.plugins.transformer.sparql;

import java.util.List;

/**
 * SPARQL transformer configuration.
 */
public class SPARQLConfig_V2 {

    private List<SPARQLQueryPair> queryPairs;

    private String outputGraphSymbolicName = "T-SPARQL/output" + String.valueOf(new java.util.Random().nextInt(100));

    public SPARQLConfig_V2() {
    }

    public List<SPARQLQueryPair> getQueryPairs() {
        return queryPairs;
    }

    public void setQueryPairs(List<SPARQLQueryPair> queryPairs) {
        this.queryPairs = queryPairs;
    }

    public String getOutputGraphSymbolicName() {
        return outputGraphSymbolicName;
    }

    public void setOutputGraphSymbolicName(String outputGraphSymbolicName) {
        this.outputGraphSymbolicName = outputGraphSymbolicName;
    }
}
