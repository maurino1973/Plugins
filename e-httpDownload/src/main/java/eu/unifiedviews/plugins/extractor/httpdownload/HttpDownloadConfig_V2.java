package eu.unifiedviews.plugins.extractor.httpdownload;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpDownloadConfig_V2 {

    private int connectionTimeout = 2000;

    private int readTimeout = 2000;

    private Map<String, String> symbolicNameToURIMap;

    private Map<String, String> symbolicNameToVirtualPathMap;
    
    // DPUTemplateConfig must provide public non-parametric constructor
    public HttpDownloadConfig_V2() {
        this.symbolicNameToURIMap = new LinkedHashMap<>();
    }

    public Map<String, String> getSymbolicNameToURIMap() {
        return symbolicNameToURIMap;
    }

    public void setSymbolicNameToURIMap(Map<String, String> symbolicNameToURIMap) {
        this.symbolicNameToURIMap = symbolicNameToURIMap;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Map<String, String> getSymbolicNameToVirtualPathMap() {
        return symbolicNameToVirtualPathMap;
    }

    public void setSymbolicNameToVirtualPathMap(Map<String, String> symbolicNameToVirtualPathMap) {
        this.symbolicNameToVirtualPathMap = symbolicNameToVirtualPathMap;
    }
}
