package eu.unifiedviews.plugins.extractor.httpdownload;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsExtractor
public class HttpDownload extends ConfigurableBase<HttpDownloadConfig_V1> implements ConfigDialogProvider<HttpDownloadConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(HttpDownload.class);

    private HttpDownloadConfig_V2 configInternal;

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public HttpDownload() {
        super(HttpDownloadConfig_V1.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        if (configInternal == null) {
            configInternal = migrateConfig(config);
        }

        Map<String, String> symbolicNameToURIMap = configInternal.getSymbolicNameToURIMap();
        Map<String, String> symbolicNameToVirtualPathMap = configInternal.getSymbolicNameToVirtualPathMap();

        int connectionTimeout = configInternal.getConnectionTimeout();
        int readTimeout = configInternal.getReadTimeout();
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: files to download: %d, connectionTimeout: %d, readTimeout: %d", symbolicNameToURIMap.size(), connectionTimeout, readTimeout);
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);
        LOG.info(shortMessage + " " + longMessage);

        boolean shouldContinue = !dpuContext.canceled();
        VirtualPathHelper virtualPathHelper = VirtualPathHelpers.create(filesOutput);
        try {
            for (String symbolicName : symbolicNameToURIMap.keySet()) {
                if (!shouldContinue) {
                    break;
                }

                String downloadedFilename = null;
                File downloadedFile = null;
                String downloadFromLocation = null;
                try {
                    downloadedFilename = filesOutput.addNewFile(symbolicName);
                    downloadedFile = new File(URI.create(downloadedFilename));
                    downloadFromLocation = symbolicNameToURIMap.get(symbolicName);
                    URL downloadFromLocationURL = new URL(downloadFromLocation);
                    FileUtils.copyURLToFile(downloadFromLocationURL, downloadedFile, connectionTimeout, readTimeout);
                    if (dpuContext.isDebugging()) {
                        LOG.debug("Downloaded " + symbolicName + " from " + downloadFromLocation + " to " + downloadedFilename);
                    }
                    if (symbolicNameToVirtualPathMap.containsKey(symbolicName)) {
                        virtualPathHelper.setVirtualPath(symbolicName, symbolicNameToVirtualPathMap.get(symbolicName));
                    } else {
                        virtualPathHelper.setVirtualPath(symbolicName, downloadFromLocationURL.getPath());
                    }
                } catch (IOException | DataUnitException ex) {
                    throw new DPUException("Error when downloading. Symbolic name " + symbolicName + " from location " + downloadFromLocation + " could not be saved to " + downloadedFilename, ex);
                }
                shouldContinue = !dpuContext.canceled();
            }
        } finally {
            try {
                virtualPathHelper.close();
            } catch (DataUnitException ex) {
                LOG.warn("Error in close", ex);
            }
        }
    }

    @Override
    public AbstractConfigDialog<HttpDownloadConfig_V1> getConfigurationDialog() {
        return new HttpDownloadVaadinDialog();
    }

    private HttpDownloadConfig_V2 migrateConfig(HttpDownloadConfig_V1 oldConfig) throws DPUException {
        HttpDownloadConfig_V2 resultConfig = new HttpDownloadConfig_V2();
        Map<String, String> symbolicNameToURIMap = new LinkedHashMap<>();
        Map<String, String> symbolicNameToVirtualPathMap = new LinkedHashMap<>();
        String oldConfigSymbolicName = oldConfig.getTarget();
        symbolicNameToURIMap.put(oldConfigSymbolicName, oldConfig.getURL().toString());
        symbolicNameToVirtualPathMap.put(oldConfigSymbolicName, oldConfigSymbolicName);
        resultConfig.setSymbolicNameToURIMap(symbolicNameToURIMap);
        resultConfig.setSymbolicNameToVirtualPathMap(symbolicNameToVirtualPathMap);
        return resultConfig;
    }
}
