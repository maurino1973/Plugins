package eu.unifiedviews.plugins.extractor.uploadtofiles;

import java.util.LinkedHashMap;
import java.util.Map;

public class UploadToFilesConfig_V1 {

    private Map<String, String> symbolicNameToURIMap;
    
    private Map<String, String> symbolicNameToVirtualPathMap;

    // DPUTemplateConfig must provide public non-parametric constructor
    public UploadToFilesConfig_V1() {
        this.symbolicNameToURIMap = new LinkedHashMap<>();
        this.symbolicNameToVirtualPathMap = new LinkedHashMap<>();
    }

    public Map<String, String> getSymbolicNameToURIMap() {
        return symbolicNameToURIMap;
    }

    public void setSymbolicNameToURIMap(Map<String, String> symbolicNameToURIMap) {
        this.symbolicNameToURIMap = symbolicNameToURIMap;
    }

	public Map<String, String> getSymbolicNameToVirtualPathMap() {
		return symbolicNameToVirtualPathMap;
	}

	public void setSymbolicNameToVirtualPathMap(
			Map<String, String> symbolicNameToVirtualPathMap) {
		this.symbolicNameToVirtualPathMap = symbolicNameToVirtualPathMap;
	}
}
