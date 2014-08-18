package eu.unifiedviews.plugins.transformer.sparql;

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

    private String SPARQL_Update_Query;

    boolean isConstructType;

    private String outputGraphSymbolicName = "T-SPARQL/output" + String.valueOf(new java.util.Random().nextInt(100));

    private boolean rewriteConstructToInsert = false;

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

    public String getSPARQL_Update_Query() {
        return SPARQL_Update_Query;
    }

    public void setSPARQL_Update_Query(String SPARQL_Update_Query) {
        this.SPARQL_Update_Query = SPARQL_Update_Query;
    }

    public boolean isIsConstructType() {
        return isConstructType;
    }

    public void setIsConstructType(boolean isConstructType) {
        this.isConstructType = isConstructType;
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

    public boolean isRewriteConstructToInsert() {
        return rewriteConstructToInsert;
    }

    public void setRewriteConstructToInsert(boolean rewriteConstructToInsert) {
        this.rewriteConstructToInsert = rewriteConstructToInsert;
    }

    /**
     * Fill missing configuration with default values.
     */
//    @Override
//    public void onDeserialize() {
//        if (SPARQL_Update_Query != null) {
//            queryPairs.add(new SPARQLQueryPair(SPARQL_Update_Query,
//                    isConstructType));
//        }
//    }

}
