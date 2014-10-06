package eu.unifiedviews.plugins.loader.metadatatockan;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class MetadataToCkanConfig_V1 {
    private String ckanApiUrl = "http://localhost:5000/api/3/action/";

    private String ckanApiKey = "";

    public String getCkanApiUrl() {
        return ckanApiUrl;
    }

    public void setCkanApiUrl(String ckanApiUrl) {
        this.ckanApiUrl = ckanApiUrl;
    }

    public String getCkanApiKey() {
        return ckanApiKey;
    }

    public void setCkanApiKey(String ckanApiKey) {
        this.ckanApiKey = ckanApiKey;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
