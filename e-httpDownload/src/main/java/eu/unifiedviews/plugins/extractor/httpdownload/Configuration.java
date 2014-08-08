package eu.unifiedviews.plugins.extractor.httpdownload;

import java.net.URL;

public class Configuration {

    private URL URL = null;

    private String target = "/file";

    /**
     * Number of attempts to try before failure, -1 for infinite.
     */
    private int retryCount = -1;

    private int retryDelay = 1000;

    public URL getURL() {
        return URL;
    }

    public void setURL(URL URL) {
        this.URL = URL;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }
    
}
