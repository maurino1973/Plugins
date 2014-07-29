package eu.unifiedviews.plugins.trandformer.sparqlselect;

public class Configuration {

    private String targetPath = "/out.csv";

    private String query = "SELECT ?s ?p ?o WHERE {?s ?p ?o }";

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    
}
