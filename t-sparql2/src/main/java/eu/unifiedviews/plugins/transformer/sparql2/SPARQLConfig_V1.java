package eu.unifiedviews.plugins.transformer.sparql2;

import java.util.LinkedList;
import java.util.List;

/**
 * SPARQL transformer configuration.
 *
 * @author Jiri Tomes
 * @author tknap
 */
public class SPARQLConfig_V1 {

    private List<SPARQLQueryPair> queryPairs;

    private String outputGraphSymbolicName = "T-SPARQL/output" + String.valueOf(new java.util.Random().nextInt(100));

    public SPARQLConfig_V1() {
        this.queryPairs = new LinkedList<>();
    }

    public SPARQLConfig_V1(String query, boolean isContructType) {
        this.queryPairs = new LinkedList<>();
        this.queryPairs.add(new SPARQLQueryPair(query, isContructType));
    }

    public SPARQLConfig_V1(List<SPARQLQueryPair> queryPairs) {
        this.queryPairs = queryPairs;
    }

    /**
     * Returns collection of {@link SPARQLQueryPair} instance.
     *
     * @return collection of {@link SPARQLQueryPair} instance.
     */
    public List<SPARQLQueryPair> getQueryPairs() {
        return queryPairs;
    }

    /**
     * Returns true, if DPU configuration is valid, false otherwise.
     *
     * @return true, if DPU configuration is valid, false otherwise.
     */
    public boolean isValid() {
        return queryPairs != null;
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
