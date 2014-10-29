package eu.unifiedviews.plugins.transformer.unzipper;

/**
 * @author Škoda Petr
 */
public class UnZipperConfig_V1 {

    /**
     * If true then symbolic name of output files
     * is not prefixed with symbolic name of input file.
     */
    public boolean notPrefixed = false;

    public UnZipperConfig_V1() {
    }

    public boolean isNotPrefixed() {
        return notPrefixed;
    }

    public void setNotPrefixed(boolean notPrefixed) {
        this.notPrefixed = notPrefixed;
    }

}
