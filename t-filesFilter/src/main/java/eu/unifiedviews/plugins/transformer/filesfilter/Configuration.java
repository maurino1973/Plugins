package eu.unifiedviews.plugins.transformer.filesfilter;


public class Configuration {

    private String predicate = FixedPredicates.SYMBOLIC_NAME;

    private String object = "*";

    /**
     * If false then {@link #predicate} represents one of {@link FixedPredicates}
     * values and should be evaluated at runtime.
     */
    private boolean customPredicate = false;

    private boolean useRegExp = true;

    // TODO we can utilize also some option like: is part of string

    public Configuration() {
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

    public boolean isCustomPredicate() {
        return customPredicate;
    }

    public void setCustomPredicate(boolean isCustom) {
        this.customPredicate = isCustom;
    }

    public boolean isUseRegExp() {
        return useRegExp;
    }

    public void setUseRegExp(boolean useRegExp) {
        this.useRegExp = useRegExp;
    }

}
