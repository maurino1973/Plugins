package eu.unifiedviews.plugins.transformer.filesfilter;


public class FilesFilterConfig_V1 {

     public static final String SYMBOLIC_NAME = "FIXED_SYMBOLIC_NAME";

    public static final String VIRTUAL_PATH = "VIRTUAL_PATH";

    private String predicate = SYMBOLIC_NAME;

    private String object = ".*";

    private boolean useRegExp = true;

    public FilesFilterConfig_V1() {
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public boolean isUseRegExp() {
        return useRegExp;
    }

    public void setUseRegExp(boolean useRegExp) {
        this.useRegExp = useRegExp;
    }

}
