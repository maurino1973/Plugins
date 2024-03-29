package eu.unifiedviews.plugins.extractor.rdffromsparql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For given SELECT/CONTRUCT/DESCRIBE query you can split it for 2 parts -
 * prefixes and rest of query.
 * Support for {@link SPARQLQueryValidator}.
 * 
 * @author Jiri Tomes
 */
public class QueryPart {

    private String query;

    private int prefixEndIndex;

    /**
     * Create new instance of {@link QueryPart} based on given SPARQL query.
     * 
     * @param query
     *            SPARQL query you can analyze - split to prefixes and rest.
     */
    public QueryPart(String query) {
        setQuery(query);
        setPrefixEndIndex();

    }

    /**
     * Find out and set index, when prefixes in query ends.
     */
    private void setPrefixEndIndex() {
        String regex = ".*prefix\\s+[\\w-_]+[:]\\s*[<]http://[\\w:/\\.#-_]+[>][\\s]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query.toLowerCase());

        boolean hasResult = matcher.find();

        if (hasResult) {

            prefixEndIndex = matcher.end();

            while (matcher.find()) {
                prefixEndIndex = matcher.end();
            }

        } else {
            prefixEndIndex = 0;
        }
    }

    /**
     * Returns string representation of query without defined prefixes.
     * 
     * @return String representation of query without defined prefixes.
     */
    public String getQueryWithoutPrefixes() {
        return query.substring(prefixEndIndex, query.length());
    }

    /**
     * Returns all defined prefixes in given query.
     * 
     * @return all defined prefixes in given query.
     */
    public String getQueryPrefixes() {
        return query.substring(0, prefixEndIndex);
    }

    /**
     * Returns original SPARQL query.
     * 
     * @return string representation of query.
     */
    public String getQuery() {
        return query;
    }

    private void setQuery(String newQuery) {
        this.query = newQuery;
    }

    /**
     * Return one of enum as query type - SELECT, CONTRUCT, DESCRIBE, UNKNOWN.
     * 
     * @return one possible of enum {@link SPARQLQueryType}.
     */
    public SPARQLQueryType getSPARQLQueryType() {

        String myQyery = getQueryWithoutPrefixes().toLowerCase();

        SPARQLQueryType myType = SPARQLQueryType.UNKNOWN;

        if (myQyery.startsWith("select")) {
            myType = SPARQLQueryType.SELECT;
        } else if (myQyery.startsWith("construct")) {
            myType = SPARQLQueryType.CONSTRUCT;
        } else if (myQyery.startsWith("describe")) {
            myType = SPARQLQueryType.DESCRIBE;
        }
        return myType;
    }
}
